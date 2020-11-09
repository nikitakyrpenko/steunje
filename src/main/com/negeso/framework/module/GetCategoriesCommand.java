/*
 * @(#)GetCategoriesCommand.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.Env;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.module.CategoriesXmlBuilder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class GetCategoriesCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(GetCategoriesCommand.class);

    // Input parameters
    public static final String INPUT_MODULE_ID = "moduleId";
    public static final String INPUT_MODULE_NAME = "moduleName";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";
 
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            conn = DBHelper.getConnection();
            Long moduleId = null;
			if(request.getNonblankParameter(INPUT_MODULE_NAME)==null) {
				moduleId = request.getLong(INPUT_MODULE_ID);
				logger.info("moduleId: " + moduleId);
			} else {
	            String moduleName = request.getNonblankParameter(INPUT_MODULE_NAME);
	            logger.info("moduleName: " + moduleName);
	            if (moduleName != null){
	            	Module module = null;
	            	module = Module.findByName(conn, moduleName, Env.getSiteId().toString());
	            	moduleId = module.getId();
	            }
			}
            CategoriesXmlBuilder builder = CategoriesXmlBuilder.getInstance();
            Document doc = builder.getDocument(conn, moduleId, user.getId());
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(
                doc.getDocumentElement().getAttribute("module"));
            logger.debug("-");
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        return response;
    }
    
    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_MODULE_ID) == null)
            throw new CriticalException("Parameter missing");
    }
}
