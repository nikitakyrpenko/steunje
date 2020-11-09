/**
 * @(#)$Id: ChooseStatisticsCommand.java,v 1.0, 2006-01-25 15:33:57Z, Andrey Morskoy$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.statistics.StatisticsXmlBuilder;

@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChooseStatisticsCommand extends AbstractCommand{
    private static Logger logger = Logger.getLogger(ChooseStatisticsCommand.class);
    public static final String OUTPUT_DOCUMENT = "xml";
    
    @ActiveModuleRequired
    public ResponseContext execute() {
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
        
        try {
            if (!SecurityGuard.canEdit(request.getSession().getUser(), null)) {
                response.setResultName(RESULT_ACCESS_DENIED);
                logger.debug("- Access denied");
                return response;
            }
          
            Element parentElement =   Xbuilder.createTopEl(
                                                        "stats_list"
                                                        );
            String formLanguage = request.getSession().getLanguageCode(); 
           
            Connection conn = null;
            Statement statement = null;
            try {
                conn = DBHelper.getConnection();
                statement = conn.createStatement();
                
                StatisticsXmlBuilder.buildStatisticsList(
                        parentElement,
                        statement,
                        request,
                        null);
                
            } catch (SQLException e) {
                logger.error("SQLException in Statistics " + e.getMessage() );
            } finally {
            	DBHelper.close(statement);
                DBHelper.close(conn);
            }
            
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, parentElement.getOwnerDocument());
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
            
        }
        catch (Exception e) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request ", e);
        }
        return response;
    }

	@Override
	public String getModuleName() {
		// TODO Auto-generated method stub
		return ModuleConstants.SITE_STATISTICS_MODULE;
	}
    
    

}
