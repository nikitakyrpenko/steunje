/*
 * @(#)$GetCategoriesTreeCommand.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.document.command;

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
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.document.generators.TreeXmlBuilder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class GetCategoriesTreeCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(
        GetCategoriesTreeCommand.class);

    public static final String INPUT_ID = "id";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            TreeXmlBuilder builder = TreeXmlBuilder.getInstance();
            Document doc = builder.getDocument(conn, user);
            Long id = request.getLong(INPUT_ID);
            if (id != null) {
                doc.getDocumentElement().setAttribute("categoryId",
                    id.toString());
            }
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
        }
        catch (AccessDeniedException ex) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied", ex);
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("-", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }

    protected void checkRequest(RequestContext request)
       throws CriticalException {}
}
