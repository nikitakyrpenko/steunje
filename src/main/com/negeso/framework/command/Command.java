/*
 * @(#)Command.java  Created on 19.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.command;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public interface Command {
    /** Command completed successfully */
    public static final String RESULT_SUCCESS = "success";
    
    
    /** Command cannot be completed due to some error */
    public static final String RESULT_FAILURE = "failure";
    
    
    /** The user does not have permission to perform the command */
    public static final String RESULT_ACCESS_DENIED = "denied";
    
    
    /** The key to store W3C Document in the result map */
    public static final String OUTPUT_XML = "xml";
    
    
    /** The key to store a string with error description in the result map */
    public static final String OUTPUT_ERROR = "error";

    
    public ResponseContext execute();
	
	public void setRequestContext(RequestContext request);
    
    public RequestContext getRequestContext();
    
    public String getModuleName();
    
}
