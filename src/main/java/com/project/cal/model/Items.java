package com.project.cal.model;

import java.text.*;



public class Items {

	/*
	 * selected to type item is
	 * 
	 */
	public static Item toItem(String text, Item last) throws Exception {
		String elem = text.trim().toLowerCase();
		switch (elem) {
		case "+":
			if (!isSign(last))
				return new AddItem();
			else
				return new SkipItem();
		case "-":
			if (!isSign(last))
				return new SubItem();
			else
				return new SignItem();
		case "*":
			return new MulItem();
		case "/":
			return new DivItem();
		case "(":
			return new LeftItem();
		case ")":
			return new RightItem();
		case ",":
			return new SepItem();
		case "pi":
			return new PiItem();
		case "e":
			return new EItem();
		case "ln":
			return new LnItem();
		case "exp":
			return new ExpItem();
		case "log":
			return new LogItem();
		case "sqr":
			return new SqrItem();
		case "sqrt":
			return new SqrtToken();
		case "pow":
			return new PowItem();
		case "abs":
			return new AbsItem();
		case "factorial":
			return new FactorialItem();
		default:
			if (elem.length() > 0) {
				// elementet er en variabel
				if (elem.charAt(0) == 'x') {
					int id = 0;
					if (elem.length() > 1) {
						try {
							id = Integer.parseInt(elem.substring(1));
							return new VarItem(id);
						} catch (Exception ex) {
						}
					}
				} else {
					try {
						double value = Double.parseDouble(formatNumber(elem));
						return new ConstItem(value);
					} catch (Exception ex) {
					}
				}
			}
			break;
		}
		throw new Exception("Illegal Item");
	}
	
	
	 private static String formatNumber(String text)
	  {
	    DecimalFormat format = new DecimalFormat();
	    DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
	    return text.replace(symbols.getDecimalSeparator(), '.');
	  }
	 
	 private static boolean isSign(Item item) 
	  {
	    return (item == null) || (item instanceof OprItem) || (item instanceof LeftToken) || (item instanceof SignItem) || (item instanceof SkipItem);
	  }
	}

	/*
	 * The fundamental basis for a type of items. Each Item has a priority
	 * (precedence) that is used when an expression is converted. 
	 * 0 a number(constant), 
	 * variable 1 negative sign 
	 * 2 a function 
	 * 3 multiplication, division
	 * 4 addition, subtraction 
	 * 9 left parenthesis 
	 * 99 right parenthesis, comma
	 */

	interface Item {
		public int getPriority();
	}

	class SkipItem implements Item {
		public int getPriority() {
			return -1;
		}
	}

	abstract class ArgItem implements Item {
		public int getPriority() {
			return 0;
		}
	}


	interface OprItem extends Item {
		public double value(double arg1, double arg2);
	}
	
	abstract class FuncItem implements Item
	{
	  public int getPriority()
	  {
	    return 2;
	  }

	  public abstract int getCount();
	  public abstract double getValue(double ... x) throws Exception;
	}

	/**
	 * Represtent the + operator
	 */
	class AddItem implements OprItem
	{
	  public int getPriority()
	  {
	    return 4;
	  }

	  public double value(double arg1, double arg2)
	  {
	    return arg1 + arg2;
	  }

	  @Override
	  public String toString()
	  {
	    return "+";
	  }
	}

	// The subtraction operator
	class SubItem implements OprItem
	{
	  public int getPriority()
	  {
	    return 4;
	  }

	  public double value(double arg1, double arg2)
	  {
	    return arg1 - arg2;
	  }

	  public String toString()
	  {
	    return "-";
	  }
	}

	// The multiplication operator
	class MulItem implements OprItem
	{
	  public int getPriority()
	  {
	    return 3;
	  }

	  public double value(double arg1, double arg2)
	  {
	    return arg1 * arg2;
	  }

	  public String toString()
	  {
	    return "*";
	  }
	}

	// The division operator
	class DivItem implements OprItem
	{
	  public int getPriority()
	  {
	    return 3;
	  }

	  public double value(double arg1, double arg2)
	  {
	    return arg1 / arg2;
	  }

	  public String toString()
	  {
	    return "/";
	  }
	}

	// Sign
	class SignItem implements Item
	{
	  public int getPriority()
	  {
	    return 1;
	  }

	  public String toString()
	  {
	    return "-";
	  }
	}

	// Constant, that is a number that is a double.
	class ConstItem extends ArgItem
	{
	  private double value;

	  public ConstItem(double value)
	  {
	    this.value = value;
	  }

	  public double getValue()
	  {
	    return value;
	  }

	  public String toString()
	  {
	    return "" + value;
	  }
	}
	
	// Left parenthesis
	class LeftToken implements Item
	{
	  public int getPriority()
	  {
	    return 9;
	  }

	  public String toString()
	  {
	    return "(";
	  }
	}
	
	
	class RightToken implements Item
	{
	  public int getPriority()
	  {
	    return 99;
	  }

	  public String toString()
	  {
	    return ")";
	  }
	}


	// Variable that is identified by an index.
	class VarItem extends ArgItem implements Comparable<VarItem>
	{
	  private int id;

	  public VarItem(int id)
	  {
	    this.id = id;
	  }

	  public int getId()
	  {
	    return id;
	  }

	  public void setId(int id)
	  {
	    this.id = id;
	  }

	  public int compareTo(VarItem Item)
	  {
	    return id < Item.id ? -1 : id > Item.id ? 1 : 0;
	  }

	  public boolean equals(Object obj)
	  {
	    if (obj == null) return false;
	    if (obj.getClass() == getClass()) return ((VarItem)obj).id == id;
	    return false;
	  }

	  public String toString()
	  {
	    return "X" + id;
	  }
	}

	// Comma used as a separator in functions with several arguments.
	class SepItem implements Item
	{
	  public int getPriority()
	  {
	    return 99;
	  }

	  public String toString()
	  {
	    return ",";
	  }
	}

	// Left parenthesis
	class LeftItem implements Item
	{
	  public int getPriority()
	  {
	    return 9;
	  }

	  public String toString()
	  {
	    return "(";
	  }
	}

	// Right parenthesis
	class RightItem implements Item
	{
	  public int getPriority()
	  {
	    return 99;
	  }

	  public String toString()
	  {
	    return ")";
	  }
	}
	
	// Pi, constant function
	class PiItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 0;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("pi: Illegal argument...");
	    return Math.PI;
	  }

	  public String toString()
	  {
	    return "pi";
	  }
	}

	// E, constant function
	class EItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 0;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("e: Illegal argument...");
	    return Math.E;
	  }

	  public String toString()
	  {
	    return "e";
	  }
	}
	
	// Natural logarithm, logarithm of base e
	class LnItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 1;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("Ln(x): Illegal argument...");
	    return Math.log(x[0]);
	  }

	  public String toString()
	  {
	    return "Ln";
	  }
	}

	// Exponential function, the reverse of Ln
	class ExpItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 1;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("Exp(x): Illegal argument...");
	    return Math.exp(x[0]);
	  }

	  public String toString()
	  {
	    return "Exp";
	  }
	}

	// Logarithm, logarithm of base 10
	class LogItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 1;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("Log(x): Illegal argument...");
	    return Math.log10(x[0]);
	  }

	  public String toString()
	  {
	    return "Log";
	  }
	}
	
	// Square function
	class SqrItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 1;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("Sqr(x): Illegal argument...");
	    return x[0] * x[0];
	  }

	  public String toString()
	  {
	    return "Sqr";
	  }
	}
	
	// Square root function, the reverse of Sqr
	class SqrtToken extends FuncItem
	{
	  public int getCount()
	  {
	    return 1;
	  }

	  public double getValue(double... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("Sqrt(x): Illegal argument...");
	    return Math.sqrt(x[0]);
	  }

	  public String toString()
	  {
	    return "Sqrt";
	  }
	}

	
	// Absolut value
	class AbsItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 1;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("Abs(x): Illegal argument...");
	    return Math.abs(x[0]);
	  }

	  public String toString()
	  {
	    return "Abs";
	  }
	}

	
	// Power function
	class PowItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 2;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount()) throw new Exception("Pow(x1, x2): Illegal argument...");
	    return Math.pow(x[0], x[1]);
	  }

	  public String toString()
	  {
	    return "Pow";
	  }
	}
	
	// Implements the factorial function
	class FactorialItem extends FuncItem
	{
	  public int getCount()
	  {
	    return 1;
	  }

	  public double getValue(double ... x) throws Exception
	  {
	    if (x.length != getCount() || x[0] < 0) throw new Exception("Factorial(x), x >= 0: Illegal argument...");
	    int n = (int)x[0];
	    long u = 1;
	    for (int i = 2; i <= n; ++i) u *= i;
	    return u;
	  }

	  public String toString()
	  {
	    return "Factorial";
	  }

}
