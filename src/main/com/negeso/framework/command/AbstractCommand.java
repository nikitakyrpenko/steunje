/*
 * @(#)CheckFileExistsCommand.java  Created on 21.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.command;

import java.util.Collection;

import com.negeso.framework.command.error.ErrorsManager;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.list.command.ModuleConstants;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public abstract class AbstractCommand implements Command {
    
    /** RequestContext for this invocation of the command */
    private RequestContext requestContext = null;
    
    private ErrorsManager errorManager = new ErrorsManager();
    
    public void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    public RequestContext getRequestContext() {
        return this.requestContext;
    }

	public Collection<String> getErrors(){
		return errorManager.getErrors();
	}
	
	public boolean hasErrors() {
		return errorManager.hasErrors();
	}
	
	public String getModuleName(){
		return ModuleConstants.CORE_MODULE;
	}
}
