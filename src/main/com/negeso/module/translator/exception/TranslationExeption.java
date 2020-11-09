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
package com.negeso.module.translator.exception;


/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */

public class TranslationExeption extends RuntimeException{
	
	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
	public TranslationExeption() {
		super();
	}

	/**
	 * @param message
	 */
	public TranslationExeption(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public TranslationExeption(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TranslationExeption(String message, Throwable cause) {
		super(message, cause);
	}
}

