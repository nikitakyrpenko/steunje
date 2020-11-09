/*
 * @(#)GetModulesCommand.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.module.core.service.ModuleService;
/**
 * @author Sergiy Oliynyk
 *
 */
public class GetModulesCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(GetModulesCommand.class);

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";
 
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
        User user = request.getSession().getUser();
        try {
            super.checkPermission(user);
            ModuleService moduleService = (ModuleService)getRequestContext().getService("moduleService");
            Document doc = moduleService.getDocument(true, getRequestContext());
            Map<String, Object> resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        return response;
    }
    
    protected void checkRequest(RequestContext request)
       throws CriticalException {}
}
