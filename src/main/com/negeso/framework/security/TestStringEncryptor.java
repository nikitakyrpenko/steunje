/*
 * @(#)$Id: TestStringEncryptor.java,v 1.0, 2006-05-18 18:26:35Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.security;

import java.net.URLEncoder;

/**
 * 
 * Test code for StringEncryptor class. Code is 
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 1$
 *
 */
public class TestStringEncryptor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			// Step 1. Create encryptor with a secret word in constructor 
			StringEncryptor strEncryptor = new StringEncryptor("HelloEverybody!");
			
			// Step 2. Encrypt a sample login + marker (Promocean side)
			String marker = "negesopfp";
			String encrypted = strEncryptor.encrypt("bobby+" + marker);
			System.out.println("encrypted string: " + encrypted);
			System.out.println("encrypted string: " + URLEncoder.encode(encrypted, "utf8"));
	
			// Step 3. Decrypt a incoming string
			String decrypted = strEncryptor.decrypt(encrypted);
			System.out.println("decrypted string: " + decrypted);
			
			// Step 4. Validation
			if (decrypted.endsWith(marker)) {
				System.out.println("USER AUTHORIZED!!! Greetings!!");
			} else {
				System.out.println("ALARM!! INTRUDER ALERT!!! ALL SECURITY LOCKS ACTIVATED!!!");
			}
		} catch (Exception e) {
			System.out.println("error" + e.toString());
		}
	}
}
