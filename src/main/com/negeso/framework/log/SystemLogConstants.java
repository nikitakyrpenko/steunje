/*
 * @(#)$Id: SystemLogConstants.java,v 1.0, 2006-11-27 07:48:34Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.log;

/**
 * 
 * @TODO
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 1$
 *
 */
public class SystemLogConstants {
	public static final String RESULT_OK = "OK";
	public static final String RESULT_ERROR = "ERROR";
	public static final String RESULT_FATAL = "FATAL";
	public static final String RESULT_WARNING = "WARNING";	
	public static final String EVENT_ADD = "Add";
	public static final String EVENT_UPDATE = "Update";
	public static final String EVENT_REMOVE = "Remove";
	public static final String EVENT_MERGE = "Merge";
	public static final String EVENT_SYSTEM = "System";

	public enum Result {OK, WARNING, ERROR, FATAL}
	public enum Event {CREATE, READ, UPDATE, INFO, DELETE}

    private SystemLogConstants() {
        // Constants class, no need to instantiate it
    }
}
