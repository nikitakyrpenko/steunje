/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */

/**
 * Provides access to private members in classes.
 */
public class PrivateAccessor {
	
  public static Object getDeclaredPrivateField (Object o, String fieldName) {   
	 // Check we have valid arguments... 
    Assert.assertNotNull(o);
    Assert.assertNotNull(fieldName);
    
    // Go and find the private field... 
    final Field fields[] = o.getClass().getDeclaredFields();
    for (int i = 0; i < fields.length; ++i) {
      if (fieldName.equals(fields[i].getName())) {
        try {
          fields[i].setAccessible(true);
          return fields[i].get(o);
        } 
        catch (IllegalAccessException ex) {
          Assert.fail ("IllegalAccessException accessing " + fieldName);
        }
      }
    }
    Assert.fail ("Field '" + fieldName +"' not found");
    return null;
  }
  
  public static Object getPrivateField (Object o, String fieldName) {   
		 // Check we have valid arguments... 
	    Assert.assertNotNull(o);
	    Assert.assertNotNull(fieldName);
	    
	    // Go and find the private field... 
	    final Field fields[] = o.getClass().getFields();
	    for (int i = 0; i < fields.length; ++i) {
	      if (fieldName.equals(fields[i].getName())) {
	        try {
	          fields[i].setAccessible(true);
	          return fields[i].get(o);
	        } 
	        catch (IllegalAccessException ex) {
	          Assert.fail ("IllegalAccessException accessing " + fieldName);
	        }
	      }
	    }
	    Assert.fail ("Field '" + fieldName +"' not found");
	    return null;
	  }
  
  public static Object invokePrivateMethod (Object o, String methodName, Object[] params) {   
		 // Check we have valid arguments... 
	    Assert.assertNotNull(o);
	    Assert.assertNotNull(methodName);
	    Assert.assertNotNull(params);
	    
	    // Go and find the private method... 
	    final Method methods[] = o.getClass().getDeclaredMethods();
	    for (int i = 0; i < methods.length; ++i) {
	      if (methodName.equals(methods[i].getName())) {
	        try {
	          methods[i].setAccessible(true);
	          return methods[i].invoke(o, params);
	        } 
	        catch (IllegalAccessException ex) {
	          Assert.fail ("IllegalAccessException accessing " + methodName);
	        }
	        catch (InvocationTargetException ite) {
	        	Assert.fail ("InvocationTargetException accessing " + methodName);	        	
	        }
	      }
	    }
	    Assert.fail ("Method '" + methodName +"' not found");
	    return null;
	  }  
}
