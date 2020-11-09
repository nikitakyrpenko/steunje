/*
 * @(#)DeleteListItemCommand.java       @version	18.02.2004
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
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.command.SetSearchIndexDirty;
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
 * @author		Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DeleteListItemCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(DeleteListItemCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ID = "listId";
    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_LIST_ITEMS = "listItems";

    /*
     * Essential rights: user must be a manager 
     * of the list's container and item(s) container
     */
    @SetSearchIndexDirty
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

        GetListCommand command = new GetListCommand();
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
    private void checkPermission(User user, Long listContainerId, 
        Long listItemContainerId) throws AccessDeniedException
    {
        if (!SecurityGuard.canManage(user, listContainerId) ||
            !SecurityGuard.canManage(user, listItemContainerId))
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
        throws AccessDeniedException, CriticalException
    {
        logger.debug("+");
        ListItem listItem = ListItem.getById(conn, listItemId);
        List list = List.getById(conn, listItem.getListId());
        checkPermission(user, list.getContainerId(), 
            listItem.getContainerId());
        listItem.delete(conn);
        logger.debug("-");
    }
}
