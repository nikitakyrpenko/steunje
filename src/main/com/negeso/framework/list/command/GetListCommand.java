/*
 * @(#)GetListCommand.java       @version	26.10.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.command;

import java.sql.Connection;
import java.util.Map;
import org.w3c.dom.Document;
import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.generators.ListAdminXmlBuilder;
import com.negeso.framework.list.generators.ListXmlBuilder;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author 	Sergiy Oliynyk
 */
public class GetListCommand extends AbstractCommand {

	private static Logger logger = Logger.getLogger(GetListCommand.class);

	// Input parameters
	public static final String INPUT_LIST_ID = "listId";
    
    // Input parameters for tree list support
    public static final String INPUT_LIST_PATH = "listPath";
    public static final String INPUT_ROOT_LIST_ID = "rootListId";

	// Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";
    public static final String OUTPUT_TEMPLATE = "xsl";
    
    private List list;
 
	public ResponseContext execute() {
		logger.debug("+");
		ResponseContext response = new ResponseContext();
		RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
		try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            Long listId = request.getLong(INPUT_LIST_ID);
            Long rootListId = request.getLong(INPUT_ROOT_LIST_ID);
            if (rootListId != null)
                listId = rootListId;
            list = List.getById(conn, listId);
            checkPermission(user, list.getContainerId());
            ListXmlBuilder builder = ListAdminXmlBuilder.getInstance();
            String path = request.getParameter(INPUT_LIST_PATH);
            Document doc = builder.getDocument(conn, listId, user.getId(), path);
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            resultMap.put(OUTPUT_TEMPLATE, Templates.getListXsl(conn, list));
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
    
    /*
     * Checks permission to process this operation
     */
	protected void checkPermission(User user, Long listContainerId) 
        throws AccessDeniedException
    {
        if (!SecurityGuard.canView(user, listContainerId))
            throw new AccessDeniedException("Forbidden");
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getParameter(INPUT_LIST_ID) == null)
            throw new CriticalException("Missing parameter");
    }
}
