/*
 * @(#)UnlinkCommand.java       @version	27.04.2004
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
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UnlinkCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(UnlinkCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ITEM_ID = "listItemId";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    /*
     * Essential rights: user must be a contributor of the item
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
            ListItem listItem = ListItem.getById(conn,
                request.getLong(INPUT_LIST_ITEM_ID));
            List list = List.getById(conn, listItem.getListId());
            checkPermission(user, list.getContainerId(), 
                listItem.getContainerId());
            unlink(conn, listItem);
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }

        GetListItemCommand command = new GetListItemCommand();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }
    
    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ITEM_ID) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation
     */
    private void checkPermission(User user, Long listContainerId,
        Long listItemContainerId) throws AccessDeniedException
    {
        if (!SecurityGuard.canContribute(user, listContainerId) ||
            !SecurityGuard.canView(user, listItemContainerId))
            throw new AccessDeniedException("Forbidden");
    }
    
    /**
     * Unlink this item from item in default language.
     * Makes a copy of articles of the item to which this has been linked.
     * @throws CriticalException
     */
    public void unlink(Connection conn, ListItem listItem)
        throws CriticalException
    {
        logger.debug("+");
        try {
            if (listItem.getTeaserId() != null) {
                Article summaryArticle = Article.findById(
                    listItem.getTeaserId());
                Article article = new Article();
                article.setLangId(summaryArticle.getLangId());
                article.setHead(summaryArticle.getHead());
                article.setText(summaryArticle.getText());
                article.insert();
                listItem.setTeaserId(article.getId());
            }
            if (listItem.getArticleId() != null) {
                Article detailsArticle = Article.findById(
                    listItem.getArticleId());
                Article article = new Article();
                article.setLangId(detailsArticle.getLangId());
                article.setHead(detailsArticle.getHead());
                article.setText(detailsArticle.getText());
                article.insert();
                listItem.setArticleId(article.getId());
            }
            listItem.setListItemLink(null);
            listItem.update(conn);
        }
        catch (ObjectNotFoundException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
    }
}
