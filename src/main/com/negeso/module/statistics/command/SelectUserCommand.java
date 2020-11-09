/**
 * @(#)$Id: SelectUserCommand.java,v 1.0, 2006-01-25 15:33:58Z, Andrey Morskoy$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics.command;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.statistics.StatisticsXmlBuilder;

@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SelectUserCommand extends AbstractCommand{
    private static Logger logger = Logger.getLogger(SelectUserCommand.class);
    
    @ActiveModuleRequired
    public ResponseContext execute() {
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
        logger.error("+");
        if (!SecurityGuard.canEdit(request.getSession().getUser(), null)) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.debug("- Access denied");
            return response;
        }
      
        Element parentElement =   Xbuilder.createTopEl(
                                                    "page"
                                                    );
        
        
        try{
            StatisticsXmlBuilder.buildUsersList(
                parentElement,
                null,
                request
        );
        }
        catch( Exception e ){
            logger.error( "process user list exception" );
        }
        
        Document xmlDoc = parentElement.getOwnerDocument();
        Map resultMap = response.getResultMap();
        resultMap.put("xml", xmlDoc);
        response.setResultName(RESULT_SUCCESS);
        logger.debug("-");
        
        return response;
        
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.SITE_STATISTICS_MODULE;
	}

}
