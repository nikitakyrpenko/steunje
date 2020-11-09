/*
* @(#)$Id: GetMenuTreeCommand.java,v 1.0, 2006-01-25 15:33:58Z, Andrey Morskoy$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.statistics.command;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.page.explorer.PageExplorerXmlBuilder;
import com.negeso.framework.security.SecurityGuard;

/**
 *
 * Get Menu Tree Command
 * 
 * @version                $Revision: 1$
 * @author                 Svetlana Bondar
 * 
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GetMenuTreeCommand extends AbstractCommand {

	private static Logger logger = 
		Logger.getLogger(GetMenuTreeCommand.class);
	
	@ActiveModuleRequired
	public ResponseContext execute() {
		logger.debug("+");
        ResponseContext response = new ResponseContext();
        User user = getRequestContext().getSession().getUser();        
        
        if (!SecurityGuard.isContributor(user)) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- not a contributor");            
            return response;
        }
        

        Connection conn = null;
        
        try {
            conn = DBHelper.getConnection();

            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_XML, 
                PageExplorerXmlBuilder.getInstance().getDocument(
                    conn, user.getId()));        	
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }

        response.setResultName(RESULT_SUCCESS);
		logger.debug("-");
		return response;
	}

}


