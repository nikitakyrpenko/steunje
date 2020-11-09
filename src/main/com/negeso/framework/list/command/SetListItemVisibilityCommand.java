/*
 * @(#)$Id: SetListItemVisibilityCommand.java,v 1.0, 2005-12-05 10:08:18Z, Svetlana Bondar$
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
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetListItemVisibilityCommand extends AbstractCommand {
 
    private static Logger logger = Logger.getLogger(
        SetListItemVisibilityCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_IS_PUBLIC = "public";
    
    /*
     * Essential rights: user must be a manager of the container of the list
     */
    @SetSearchIndexDirty
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
            conn.setAutoCommit(false);
            ListItem listItem = ListItem.getById(conn,
                request.getLong(INPUT_LIST_ITEM_ID));
            checkPermission(user, listItem.getContainerId());
            String param = request.getParameter(INPUT_IS_PUBLIC);
            Article article = Article.findById(listItem.getTeaserId());
            // if article is public, its container id is null
            article.setContainerId(
                param.equals("true") ? null: listItem.getContainerId());
            article.update(conn);
            conn.commit();
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
        }
        catch (Exception ex) {
            response.setResultName(ex instanceof AccessDeniedException ? 
                RESULT_ACCESS_DENIED : RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        return response;
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ITEM_ID) == null ||
            request.getParameter(INPUT_IS_PUBLIC) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation
     */
    private void checkPermission(User user, Long listItemContainerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canContribute(user, listItemContainerId))
            throw new AccessDeniedException("Forbidden");
    }
}
