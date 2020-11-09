/*
 * @(#)$Id: ErrorsManager.java,v 1.1, 2007-04-02 12:14:41Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.command.error;

import java.util.ArrayList;
import java.util.Collection;

/** Container of errors
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class ErrorsManager implements ErrorContainer {
	
    /** Errors collection to simplify error processing  */
	private Collection<String> errors = null;

	/**
	 * Get errors for command
	 * 
	 * @return
	 */
	public Collection<String> getErrors(){
		if ( this.errors == null ){
			this.errors = new ArrayList<String>();
		}
		return this.errors;
	}
	
	/**
	 * Tests for errors
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		if ( this.errors == null ){
			return false;
		}
		if ( this.errors.isEmpty() ){
			return false;
		}
		return true;
	}

}

