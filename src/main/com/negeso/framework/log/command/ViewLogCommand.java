/**
 * @(#)$Id: ViewLogCommand.java,v 1.3, 2006-05-09 15:53:01Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.log.command;

import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.log.LogXmlBuilder;
import com.negeso.framework.security.SecurityGuard;

public class ViewLogCommand extends AbstractCommand{
    private static Logger logger = Logger.getLogger(ViewLogCommand.class);
    public static final String OUTPUT_DOCUMENT = "stringBuffer";
    private static final String LOG_FILENAME = "log-filename";
    
    
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
       
        User user = request.getSession().getUser();
      
        if (  !SecurityGuard.isAdministrator(user) ) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.debug("- Access denied");
            return response;
        }
        
      
        StringBuffer buf =  LogXmlBuilder.buildLogBuffer( request.getParameter(LOG_FILENAME) );
        
        Map resultMap = response.getResultMap();
        resultMap.put(OUTPUT_DOCUMENT, buf);
        response.setResultName(RESULT_SUCCESS);
        logger.debug("-");
        
        return response;
    }

}
