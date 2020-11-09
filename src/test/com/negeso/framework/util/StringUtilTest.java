/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.util;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * @TODO
 * 
 * @author		Volodymyr Snigur
 * @version		$Revision: $
 *
 */
public class StringUtilTest extends TestCase {
	
	public StringUtilTest(String name) { 
        super(name);
	}
	
	protected void setUp() {
		//TODO
	}
	protected void tearDown() {
		//TODO
	}
	
	public void testJoin() {
		String[] testArray = null;
		String testSeparator = null;
		String result = StringUtil.join(testArray, testSeparator);
		assertNotNull(result);
		assertEquals(", ", result);
		
		testArray = new String[] {
			"This", "is", "my", "first", "JUnit", "test."
		};
		result = StringUtil.join(testArray, testSeparator);
		assertNotNull(result);
		assertEquals(", ", result);
		
		testSeparator = " ";
		result = StringUtil.join(testArray, testSeparator);
		assertNotNull(result);
		assertEquals("This is my first JUnit test.", result);
		
		testArray = null;
		result = StringUtil.join(testArray, testSeparator);
		assertNotNull(result);
		assertEquals("", result);
	}
	
	public void testValidateEmail() {
		String email = "volodymyr.snigur@negeso-ua.com";
		Assert.assertTrue(StringUtil.validateEmail(email));
		
		email = "volodymyr.snigur_negeso-ua.com";
		Assert.assertFalse(StringUtil.validateEmail(email));
		
		email = null;
		try { 
			Assert.assertFalse(StringUtil.validateEmail(email));
			fail("NullPointerException should be thrown");
		} catch (NullPointerException e) {
			// ok
		}
	}
	
	public void testIsInStringArray() {
		String[] inputStringArray = {"This", "is", "my", "first", "JUnit", "test."};
		String inputTested = "my";
		Assert.assertTrue(StringUtil.isInStringArray(inputStringArray, inputTested));
		
		inputTested = "CACTUS";
		Assert.assertFalse(StringUtil.isInStringArray(inputStringArray, inputTested));
		
		inputStringArray = new String[] {null, null};
		try { 
			StringUtil.isInStringArray(inputStringArray, inputTested);
			fail("NullPointerException should be thrown");
		} catch (NullPointerException e) {
			// ok
		}
		
		inputStringArray = new String[] {"This", "is"};
		inputTested = null;
		try { 
			StringUtil.isInStringArray(inputStringArray, inputTested);
			fail("NullPointerException should be thrown");
		} catch (NullPointerException e) {
			// ok
		}
	}
	public static Test suite() {
		return new TestSuite(StringUtilTest.class);
	}
}