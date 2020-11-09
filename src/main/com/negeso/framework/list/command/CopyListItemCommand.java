/*
 * @(#)CopyListitemCommand.java       @version    18.02.2004
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
import com.negeso.framework.domain.Article;
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
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CopyListItemCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(CopyListItemCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ID = "listId";
    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_LIST_ITEMS = "listItems";
    public static final String INPUT_MOVE_TO_ID = "moveToId";
    public static final String INPUT_IS_MOVE = "isMove";

    private boolean isMove = false;

    /*
     * Essential rights to move the item: user must be a manager 
     * of the list's container and item(s) container and be a contributor
     * of a container of selected list.
     * Essential rights to copy the item: user must be able to view a list 
     * and be a contributor of a container of selected list.
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
            Long listId = request.getLong(INPUT_LIST_ID);
            Long listItemId = request.getLong(INPUT_LIST_ITEM_ID);
            Long moveToListId = request.getLong(INPUT_MOVE_TO_ID);
            String items = request.getParameter(INPUT_LIST_ITEMS);
            isMove = "true".equals(request.getParameter(INPUT_IS_MOVE));
            List list = List.findById(conn, listId);
            List moveToList = List.findById(conn, moveToListId);
            checkPermission(user, list, moveToList);
            if (items != null && items.length() > 0)
                copyItems(conn, items, user, moveToListId);
            else
                copyItem(conn, listItemId, user, moveToListId);
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
            request.getParameter(INPUT_LIST_ITEMS) == null ||
            request.getParameter(INPUT_MOVE_TO_ID) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation
     */
    private void checkPermission(User user, List sourceList, List targetList)
        throws AccessDeniedException
    {
        logger.debug("+");
        if (isMove) {
            if (!SecurityGuard.canManage(user, sourceList.getContainerId()) ||
                !SecurityGuard.canContribute(user, targetList.getContainerId()))
                throw new AccessDeniedException("Forbidden");
        }
        else {
            if (!SecurityGuard.canView(user, sourceList.getContainerId()) ||
                !SecurityGuard.canContribute(user, targetList.getContainerId()))
                throw new AccessDeniedException("Forbidden");
        }
        logger.debug("-");
    }

    private void checkPermission(User user, ListItem item)
        throws AccessDeniedException
    {
        if (isMove) {
            if (!SecurityGuard.canManage(user, item.getContainerId()))
                throw new AccessDeniedException("Forbidden");
        }
        else {
            if (!SecurityGuard.canView(user, item.getContainerId()))
                throw new AccessDeniedException("Forbidden");
        }
    }

    /*
     * Copy (move) multiple items
     */
    private void copyItems(Connection conn, String items, User user,
        Long listId) throws AccessDeniedException, Exception
    {
        logger.debug("+");
        if (items != null) {
            StringTokenizer st = new StringTokenizer(items, ";");
            while (st.hasMoreTokens()) {
                Long listItemId = Long.valueOf(st.nextToken());
                copyItem(conn, listItemId, user, listId);
            }
        }
        logger.debug("-");
    }

    /*
     * Copy (move) one item
     */
    private void copyItem(Connection conn, Long listItemId, User user, 
        Long listId) throws AccessDeniedException, CriticalException
    {
        logger.debug("+");
        ListItem listItem = ListItem.getById(conn, listItemId);
        checkPermission(user, listItem);
        Long teaserId = listItem.getTeaserId();
        Long articleId = listItem.getArticleId();
        if (isMove) {
            listItem.setTeaserId(null);
            listItem.setArticleId(null);
            listItem.delete(conn);
        }
        else {
            try {
                Article article = Article.findById(conn, teaserId);
                teaserId = article.insert(conn);
                article = Article.findById(conn, articleId);
                articleId = article.insert(conn);
            }
            catch (Exception ex) {
                logger.debug("- Throwing new CriticalException");
                throw new CriticalException(ex);
            }
        }
        listItem.setListId(listId);
        listItem.setTeaserId(teaserId);
        listItem.setArticleId(articleId);
        listItem.insert(conn);
        logger.debug("-");
    }
}
