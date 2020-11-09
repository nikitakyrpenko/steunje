/*
 * @(#)$Id: SelectPageTemplateCommand.java,v 1.3, 2007-01-16 16:05:10Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page.command;

import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.ResourceMap;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.security.SecurityGuard;


/**
 * 
 * 
 * @version     $Revision: 4$
 * @author      Stanislav Demchenko
 */
public class SelectPageTemplateCommand extends AbstractCommand{
    
    
    private static Logger logger =
        Logger.getLogger(SelectPageTemplateCommand.class);
    
    
    public ResponseContext execute()
    {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        Map<String, Object> resultMap = response.getResultMap();
        if(!SecurityGuard.canContribute(request.getSession().getUser(), null)) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- access denied");
            return response;
        }
        if (request.getParameter("special") != null) {
        	resultMap.put("xml", ResourceMap.getDom("TEMPLATE_SPECIAL_PAGE"));
        } else resultMap.put("xml", ResourceMap.getDom("TEMPLATE_NEW_PAGE"));
        response.setResultName(RESULT_SUCCESS);
        logger.debug("-");
        return response;
    }
    
    
}
