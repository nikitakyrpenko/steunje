/*
 * @(#)$Id: ModuleException.java,v 1.1, 2007-01-16 11:15:43Z, Svetlana Bondar$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 2$
 *
 */
public class ModuleException extends Exception {

	private static final long serialVersionUID = -4526626790814213052L;

	public ModuleException(String message) {
		super(message);
	}
	
	public ModuleException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
