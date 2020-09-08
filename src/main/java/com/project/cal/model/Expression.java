package com.project.cal.model;


import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Type representing a mathematical expression.
 * An expression specified as a string on ordinary infix form and must be built up of the four arithmetic
 */ 
public class Expression
{
	
	private static Logger logger = LoggerFactory.getLogger(Expression.class);
	
  private static final String split = "+-*/(),";                // Separation characters for dividing an expression in items
  private String text;                                         
  private ArrayList<Item> list = new ArrayList<Item>();       
  private int args;                                           
  private int itemCounter;                                     
  

  public Expression(String text) throws Exception
  {
    try
    {
    	// scanning, which divides the string into items
      ArrayList<Item> items = toItems(text);     
      logger.info(items.toString());
      toText(items);                               
      logger.info(text);
   // parsing, which controls the expression for the syntax errors
      syntaxCheck(items);                          
      toPostform(items);   
      // counts the number of different variables
      countArgs();                                 
    }
    catch (Exception ex)
    {
      throw new Exception("Parse error");
    }
  }

  /**
   * @return The number of different variables
   */
  public int getArgs()
  {
    return args;
  }

  public String toString()
  {
    return text;
  }
  
  /**
   * Method that evaluates an expression. If the expression can not be evaluated the method raises an Exception.
   * @param arg The expression's arguments if it needs arguments
   * @return Value of the expression
   * @throws Exception If the expression can not be evaluated with the current arguments
   */
  public double getValue(double ... arg) throws Exception
  {
    try
    {
      Deque<Item> stack = new ArrayDeque<Item>();
      for (Item item : list)
      {
        if (item instanceof ArgItem) stack.push(item);
        else if (item instanceof FuncItem)
        {
          FuncItem func = (FuncItem)item;
          double[] x = new double[func.getCount()];
          for (int n = 0; n < x.length; ++n) x[n] = popValue(stack, arg);
          double y = func.getValue(x);
          stack.push(new ConstItem(y));
        }
        else if (item instanceof AddItem)
        {
          double x1 = popValue(stack, arg);
          double x2 = popValue(stack, arg);
          stack.push(new ConstItem(x2 + x1));
        }
        else if (item instanceof SubItem)
        {
          double x1 = popValue(stack, arg);
          double x2 = popValue(stack, arg);
          stack.push(new ConstItem(x2 - x1));
        }
        else if (item instanceof MulItem)
        {
          double x1 = popValue(stack, arg);
          double x2 = popValue(stack, arg);
          stack.push(new ConstItem(x2 * x1));
        }
        else if (item instanceof DivItem)
        {
          double x1 = popValue(stack, arg);
          double x2 = popValue(stack, arg);
          stack.push(new ConstItem(x2 / x1));
        }
        else if (item instanceof SignItem)
        {
          double x = popValue(stack, arg);
          stack.push(new ConstItem(-x));
        }
      }
      return popValue(stack, arg);
    }
    catch (Exception ex)
    {
    }
    throw new Exception("Evaluation error");
  }

  // Pops an argument from the stack - either an operator or a function.
  // If the argument is a constant, the method returns only the value.
  // If the argument is a variable that returns the function's argument corresponding to the variable index.
  private double popValue(Deque<Item> stack, double[] arg) throws Exception
  {
    Item item = stack.pop();
    if (item instanceof ConstItem) return ((ConstItem)item).getValue();
    if (item instanceof VarItem) return arg[((VarItem)item).getId()];
    throw new Exception("Illegal calculation...");
  }

  private void toPostform(ArrayList<Item> items)
  {
	;
    Deque<Item> stack = new ArrayDeque<Item>();
    for (Item item : items)
    {
    	
      if (item instanceof SignItem) stack.push(item);
      else if (item instanceof FuncItem) stack.push(item);
      else if (item instanceof OprItem) 
      {
        while (stack.size() > 0 && stack.peek().getPriority() <= item.getPriority()) list.add(stack.pop());
        stack.push(item);
      }
      else if (item instanceof LeftItem) stack.push(item);
      else if (item instanceof RightItem)
      {
        while (stack.size() > 0 && !(stack.peek() instanceof LeftItem)) list.add(stack.pop());
        if (stack.size() > 0) stack.pop();
      }
      else if (!(item instanceof SepItem)) stack.push(item);
    }
    while (stack.size() > 0) list.add(stack.pop());
  }

  private ArrayList<Item> toItems(String text) throws Exception
  {
    ArrayList<Item> items = new ArrayList<Item>();
    Item lastItem = null;
    StringTokenizer tk = new StringTokenizer(text, split, true);
    while (tk.hasMoreTokens())
    {
    	
      String elem = tk.nextToken().trim().toLowerCase();
      if (elem.length() > 0)
      {
        Item item = Items.toItem(elem, lastItem);
        if (!(item instanceof SkipItem)) items.add(item);
        lastItem = item;
      }
    }
    return items;
  }

  private void syntaxCheck(ArrayList<Item> items) throws Exception
  {
    itemCounter = 0;
    boolean error = false;
    try
    {
      error = !isExpression(items);
    }
    catch (Exception ex)
    {
      error = true;
    }
    if (error || itemCounter < items.size()) throw new Exception("Error. There are errors in one of you expressions");
  }

  private boolean isExpression(ArrayList<Item> items)
  {
    if (itemCounter >= items.size()) return false;
    if (!isArgument(items)) return false;
    if (isOperator(items)) return isExpression(items);
    return true;
  }

  private boolean isArgument(ArrayList<Item> items)
  {
    while (isSign(items)) ;
    if (isConst(items)) return true;
    if (isVar(items)) return true;
    if (isFunction(items)) return true;
    if (isLeft(items))
    {
      if (!isExpression(items)) return false;
      return isRight(items);
    }
    return false;
  }

  private boolean isFunction(ArrayList<Item> items)
  {
    if (!isFunc(items)) return false;
    if (!isLeft(items)) return true;
    if (!isArgumentList(items)) return false;
    return isRight(items);
  }

  private boolean isArgumentList(ArrayList<Item> items)
  {
    if (!isExpression(items)) return false;
    if (isKomma(items)) return isArgumentList(items);
    return true;
  }

  private boolean isOperator(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof OprItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private boolean isSign(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof SignItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private boolean isConst(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof ConstItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private boolean isVar(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof VarItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private boolean isLeft(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof LeftItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private boolean isRight(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof RightItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private boolean isFunc(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof FuncItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private boolean isKomma(ArrayList<Item> items)
  {
    if (itemCounter < items.size() && items.get(itemCounter) instanceof SepItem)
    {
      ++itemCounter;
      return true;
    }
    return false;
  }

  private void toText(ArrayList<Item> list)
  {
    if (list.size() == 0) text = "";
    else
    {
      StringBuilder builder = new StringBuilder(list.get(0).toString());
      for (int i = 1; i < list.size(); ++i)
      {
        builder.append(' ');
        builder.append(list.get(i).toString());
      }
      text = builder.toString();
    }
  }

  // Method, which counts the number of variables.
  private void countArgs()
  {
    ArrayList<VarItem> vars = new ArrayList();
    for (Item t : list) 
      if (t instanceof VarItem)
      {
        VarItem item = (VarItem)t;
        if (!vars.contains(item)) vars.add(item);
      }
    args = vars.size();
  }
}

