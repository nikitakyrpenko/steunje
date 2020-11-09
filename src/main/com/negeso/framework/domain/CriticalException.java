/*
 * @(#)CriticalException.java       @version	19.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

/**
 * CriticalException. Thrown on errors which cause unfunctional 
 * state of applicaiton.
 *
 * @version 	19.12.2003
 * @author 	 	Olexiy Strashko
 */
public class CriticalException extends RuntimeException {

	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
	public CriticalException() {
		super();
	}

	/**
	 * @param message
	 */
	public CriticalException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CriticalException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CriticalException(String message, Throwable cause) {
		super(message, cause);
	}

}
