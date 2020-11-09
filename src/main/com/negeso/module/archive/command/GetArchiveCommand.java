/*
 * @(#)ArchiveManageCommand.java       @version	24.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.archive.command;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.archive.generators.ArchiveXmlBuilder;

/**
 * @author Sergiy Oliynyk
 */
public class GetArchiveCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(GetArchiveCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ID = "listId";

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
            checkRequest(request);
            Long listId = request.getLong(INPUT_LIST_ID);
            conn = DBHelper.getConnection();
            Document doc = ArchiveXmlBuilder.getInstance().getDocument(
                conn, listId, user.getId());
            
            String backLinkTo = request.getString("backLinkTo","");
            doc.getDocumentElement().setAttribute("backLinkTo", backLinkTo);
            if("module".equals(backLinkTo))	doc.getDocumentElement().setAttribute(
            		"moduleId", String.valueOf(List.findById(conn, listId).getModuleId()));
            
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
        }
        catch (AccessDeniedException ex) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied", ex);
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
        if (request.getLong(INPUT_LIST_ID) == null)
            throw new CriticalException("Parameter missing");
    }
}
