/*
 * @(#)ObjectNotFoundException.java       @version	15.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;


import java.text.MessageFormat;
/**
 * Exception which is thown by object finders then object not exist or
 * not found.  
 *  
 *
 * @version 	15.12.2003
 * @author 	 	Olexiy Strashko
 */
public class ObjectNotFoundException extends Exception {

	private static MessageFormat mFormat = new MessageFormat("Object <{0}> not found by id.");


	/**
	 * @param string
	 */
	public ObjectNotFoundException(String message) {
		super(message);
	}
	
	public static ObjectNotFoundException createFormatted(String objectId) {
		Object[] args = {objectId}; 
		return new ObjectNotFoundException(mFormat.format(args));
	}

}
