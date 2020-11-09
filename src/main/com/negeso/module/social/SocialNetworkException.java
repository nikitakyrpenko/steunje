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
package com.negeso.module.social;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SocialNetworkException extends Exception{

	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
	public SocialNetworkException() {
		super();
	}

	/**
	 * @param message
	 */
	public SocialNetworkException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SocialNetworkException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SocialNetworkException(String message, Throwable cause) {
		super(message, cause);
	}

}

