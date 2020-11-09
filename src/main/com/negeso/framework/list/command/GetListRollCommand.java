/*
 * @(#)$GetListRoll.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.command;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.generators.ListRollXmlBuilder;
import com.negeso.framework.security.AccessDeniedException;

/**
 * @author Sergiy Oliynyk
 *
 */
public class GetListRollCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(GetListRollCommand.class);

    // Input parameter
    public static final String INPUT_LIST_ID = "listId";

    //  Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    /*
     * Builds a set of lists that user has rights with a specified role
     */
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
            Long listId = request.getLong(INPUT_LIST_ID);
            Long moduleId = request.getLong("moduleId");
            ListRollXmlBuilder builder = ListRollXmlBuilder.getInstance();
            Document doc = builder.getDocument(conn, listId, user.getId(), moduleId);
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
            logger.error("- Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }

    protected void checkRequest(RequestContext request) 
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ID) == null)
            throw new CriticalException("Parameter missing");
    }
}
