/*
 * @(#)ChangeOrderCommand.java       @version  18.02.2004
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

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author      Sergiy Oliynyk
 */
public class ChangeOrderCommand extends AbstractCommand {

	private static Logger logger = Logger.getLogger(ChangeOrderCommand.class);

	// Input parameters
	public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_ACTION = "action";

    /*
     * Essential rights: user must be a manager of the container of the list
     */
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
		try {
            super.checkPermission(user);
            checkRequest(request); 
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
			ListItem listItem = ListItem.getById(conn,
                request.getLong(INPUT_LIST_ITEM_ID));
            List list = List.getById(conn, listItem.getListId());
            checkPermission(user, list.getContainerId(), 
                listItem.getContainerId());
            String action = request.getParameter(INPUT_ACTION);
			moveItem(conn, listItem, action);
            conn.commit();
		}
		catch(Exception ex) {
			logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
		}
        finally {
            DBHelper.close(conn);
        }

        GetListCommand command = new GetListCommand();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
	}

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ITEM_ID) == null ||
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
	private void moveItem(Connection conn, ListItem listItem, String action)
        throws CriticalException
    {
        logger.debug("+");
        if (action.equals("up"))
            listItem.moveUp(conn);
        else if (action.equals("down"))
            listItem.moveDown(conn);
        logger.debug("-");
    }
}
