package com.project.cal.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.project.cal.model.Items;

import static org.junit.Assert.*;

public class ItemsTest {
  
  public ItemsTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }

  @Test
  public void testToItem() throws Exception {
    System.out.println("toItem");
    ConstItem c = new ConstItem(23);
    assertTrue(Items.toItem("pi", null) instanceof PiItem);
    assertTrue(Items.toItem("+", c) instanceof AddItem);
    assertTrue(Items.toItem("+", c) instanceof OprItem);
    assertTrue(Items.toItem("*", null) instanceof MulItem);
    assertTrue(Items.toItem("(", new LeftItem()) instanceof LeftItem);
    ConstItem c1 = (ConstItem)Items.toItem("123.45", null);
    ConstItem c2 = (ConstItem)Items.toItem("123,45", null);
    assertEquals(new Double(c1.getValue()), new Double(123.45));
    assertEquals(new Double(c2.getValue()), new Double(123.45));
    System.out.println(c1.getValue());
    System.out.println(c2.getValue());
  }

  @Test(expected=Exception.class)
  public void testNotItem() throws Exception {
    System.out.println("Not a Item");
    Item token = Items.toItem("abc", null);
  }
}
