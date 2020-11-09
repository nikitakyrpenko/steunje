/*
 * @(#)DeleteFromArchiveCommand.java       @version	24.04.2004
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
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.archive.domain.ArchivedListItem;

/**
 * @author Sergiy Oliynyk
 */
public class DeleteFromArchiveCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(DeleteFromArchiveCommand.class);

    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_LIST_ITEMS = "listItems";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    /*
     * Essential rights: user must be a manager of the item(s) containers
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
            Long listItemId = request.getLong(INPUT_LIST_ITEM_ID);
            String items = request.getParameter(INPUT_LIST_ITEMS);
            if (items != null && items.length() > 0)
                deleteItems(conn, items, user);
            else
                deleteItem(conn, listItemId, user);
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }

        GetArchiveCommand command = new GetArchiveCommand();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }
    
    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ITEM_ID) == null &&
            request.getParameter(INPUT_LIST_ITEMS) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation 
     */
    private void checkPermission(User user, Long listItemContainerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canManage(user, listItemContainerId))
            throw new AccessDeniedException("Forbidden");
    }

    /*
     * Delete multiple items
     */
    private void deleteItems(Connection conn, String items, User user)
        throws AccessDeniedException, Exception
    {
        logger.debug("+");
        if (items != null) {
            StringTokenizer st = new StringTokenizer(items, ";");
            while (st.hasMoreTokens()) {
                Long listItemId = Long.valueOf(st.nextToken());
                deleteItem(conn, listItemId, user);
            }
        }
        logger.debug("-");
    }

    /*
     * Delete one item
     */
    private void deleteItem(Connection conn, Long listItemId, User user)
        throws AccessDeniedException, Exception
    {
        logger.debug("+");
        ArchivedListItem listItem = ArchivedListItem.getById(conn, listItemId);
        checkPermission(user, listItem.getContainerId());
        listItem.delete(conn);
        logger.debug("-");
    }
}
