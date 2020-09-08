package com.project.cal.model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.project.cal.model.Expression;

import static org.junit.Assert.*;

public class ExpressionTest {
  private static final double epsilon = 1e-20;
  private static Expression e1;
  private static Expression e2;
  private static Expression e3;
  private static Expression e4;
  private static Expression e5;
  private static Expression e6;
  
  public ExpressionTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    System.out.println("Start ExpressionTest");
    try
    {
      e1 = new Expression("(e)");
      e2 = new Expression("Sqr(2)");
      e3 = new Expression("(x0 + x1) * x2");
      e4 = new Expression("Sqrt(x0 + x1)");
      e5 = new Expression("Exp(Ln(x0 + x1))");
    }
    catch (Exception ex)
    {
      System.out.println(ex);
    }
  }
  
  @AfterClass
  public static void tearDownClass() {
    System.out.println("End ExpressionTest");
  }
  
  @Before
  public void setUp() {
    System.out.println("Start Test method");
  }
  
  @After
  public void tearDown() {
    System.out.println("End Test method");
  }

  @Test
  public void testGetArgs() {
    System.out.println("getArgs");
    assertTrue(e1.getArgs() == 0);
    assertTrue(e2.getArgs() == 0);
    assertTrue(e3.getArgs() == 3);
    assertTrue(e4.getArgs() == 2);
    assertTrue(e5.getArgs() == 2);
  }

  @Test
  public void testGetValue() throws Exception {
    System.out.println("getValue");
    assertTrue((-(5 + 5)) == -10);
    assertTrue(Math.abs(e1.getValue() - Math.E) < epsilon);
    assertTrue(Math.abs(e2.getValue() - Math.pow(2,2)) < epsilon);
    assertTrue(Math.abs(e3.getValue(23, 17, 5) - 200) < epsilon);
    assertTrue(Math.abs(e4.getValue(13, 12) - 5) < epsilon);
    assertTrue(Math.abs(e5.getValue(123, 4567) - 4690) < epsilon);
  }
}
