/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.imp.extension;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class ImportException extends Exception {

	private static final long serialVersionUID = -8316550634344600761L;

	public ImportException(String message) {
		super(message);
	}
	
	public ImportException(Throwable cause) {
		super(cause);
	}
	
	public ImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
