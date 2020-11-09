/*
 * @(#)$Id: PageExistsException.java,v 1.0, 2007-04-02 07:46:26Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu.command.creating.exception;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class PageExistsException extends Exception {

	public PageExistsException(String error) {
		super(error);
	}

	private static final long serialVersionUID = -3253589594616878743L;

}

