 /* @(#)$Id: ReorderCommand.java,v 1.0, 2006-04-04 15:31:01Z, Dmitry Dzifuta$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
 
package com.negeso.module.document.command;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.domain.DocumentEntry;

/**
 * @author      Dmitry Dzifuta
 */

public class ReorderCommand extends AbstractCommand {

	private static Logger logger = Logger.getLogger(ReorderCommand.class);

	// Input parameters
	public static final String INPUT_ITEM_ID = "itemId";
	public static final String INPUT_TYPE = "type";
    public static final String INPUT_ACTION = "reorder_action";

    /*
     * Essential rights: user must be a manager of the container of the list
     */
	public ResponseContext execute() {
		logger.debug("+");
		logger.debug("reorder command");
		RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
		try {
            super.checkPermission(user);
            checkRequest(request); 
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);

        	String action = request.getParameter(INPUT_ACTION);            
            if ("category".equals(request.getString(INPUT_TYPE, "category"))) {
            	logger.debug("category moving");
            	Category categoryItem = Category.getById(conn,
                        request.getLong(INPUT_ITEM_ID));
                moveCategoryItem(conn, categoryItem, action);          	
            }
            else {
            	logger.debug("document moving");
            	DocumentEntry documentItem = DocumentEntry.getById(conn,
                        request.getLong(INPUT_ITEM_ID));
            	logger.debug("document = " + documentItem);
                moveDocumentItem(conn, documentItem, action);          	
            }

/*            List list = List.getById(conn, listItem.getListId());
            checkPermission(user, list.getContainerId(), 
                listItem.getContainerId());*/
            conn.commit();
		}
		catch(Exception ex) {
			logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
		}
        finally {
            DBHelper.close(conn);
        }

        BrowseCategory command = new BrowseCategory();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
	}

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_ITEM_ID) == null ||
            request.getParameter(INPUT_ACTION) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation
     */
    private void checkPermission(User user, Long listContainerId, 
        Long listItemContainerId) throws AccessDeniedException
    {
        if (!SecurityGuard.canManage(user, listContainerId) ||
            !SecurityGuard.canView(user, listItemContainerId))
            throw new AccessDeniedException("Forbidden");
    }

	/**
	 * Moving the item of list, must be implemented in subclasses
	 * @param listItem item of list to be moved
	 */
	private void moveCategoryItem(Connection conn, Category categoryItem, String action)
        throws CriticalException
    {
        logger.debug("+");
        if (action.equals("up"))
        	categoryItem.moveUp(conn);
        else if (action.equals("down"))
        	categoryItem.moveDown(conn);
        logger.debug("-");
    }
	
	/**
	 * Moving the item of list, must be implemented in subclasses
	 * @param listItem item of list to be moved
	 */
	private void moveDocumentItem(Connection conn, DocumentEntry documentItem, String action)
        throws CriticalException
    {
        logger.debug("+");
        if (action.equals("up"))
        	documentItem.moveUp(conn);
        else if (action.equals("down"))
        	documentItem.moveDown(conn);
        logger.debug("-");
    }	
}
