/*
 * @(#)ParameterException.java       @version	24.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.generators;

import java.text.MessageFormat;

/**
 * Parameter exception
 *
 * @version 	24.12.2003
 * @author 	 	Olexiy Strashko
 */
public class ParameterException extends Exception {

	private static MessageFormat mFormat = new MessageFormat("{1}: Parameter <{0}> not found.");


	/**
	 * @param string
	 */
	public ParameterException(String message) {
		super(message);
	}
	
	public static ParameterException createFormatted(String parameterName, String place) {
		Object[] args = {parameterName, place}; 
		return new ParameterException(mFormat.format(args));
	}

}

