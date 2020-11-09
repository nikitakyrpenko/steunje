/*
 * @(#)$Id: AbstractCommand.java,v 1.0, 2005-12-05 10:08:10Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.command;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 */
public abstract class AbstractCommand extends 
    com.negeso.framework.command.AbstractCommand {

    abstract protected void checkRequest(RequestContext request)
        throws CriticalException;
    
    protected void checkPermission(User user) throws AccessDeniedException {
        if (!SecurityGuard.canContribute(user, null))
            throw new AccessDeniedException("Forbidden"); 
    }
}
