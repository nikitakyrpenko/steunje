/*
 * @(#)ArchiveListItemCommand.java       @version	21.04.2004
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.Env;
import com.negeso.framework.command.ProtectedCommand;
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
import com.negeso.module.archive.domain.ArchivedListItem;

/**
 * @author Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ArchiveListItemCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(ArchiveListItemCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_LIST_ITEMS = "listItems";

    // Output parameters
    public static final String OUTPUT_LIST_ID = "listId";

    /*
     * Essential rights: user must be a manager 
     * of the list's container and item(s) container
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
                moveItemsToArchive(conn, items, user);
            else
                moveItemToArchive(conn, listItemId, user);
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
     * Move multiple items to archive
     */
    private void moveItemsToArchive(Connection conn, String items, User user)
        throws AccessDeniedException, Exception
    {
        logger.debug("+");
        if (items != null) {
            // Move multiple items to archive table
            StringTokenizer st = new StringTokenizer(items, ";");
            while (st.hasMoreTokens()) {
                Long listItemId = Long.valueOf(st.nextToken());
                moveItemToArchive(conn, listItemId, user);
            }
        }
        logger.debug("-");
    }

    /*
     * Move one item to archive 
     */
    private void moveItemToArchive(Connection conn, Long listItemId, User user)
        throws AccessDeniedException, CriticalException
    {
        logger.debug("+");
        ListItem listItem = ListItem.getById(conn, listItemId);
        if (listItem.getListItemLink() != null) return;
        List list = List.getById(conn, listItem.getListId());
        checkPermission(user, list.getContainerId(), 
            listItem.getContainerId());
        ArchivedListItem archivedItem = createArchivedItem(listItem);
        archivedItem.setLangId(list.getLangId());
        archivedItem.insert(conn);
        moveLinksToArchive(conn, listItem);
        listItem.delete(conn);
        logger.debug("-");
    }

    private ArchivedListItem createArchivedItem(ListItem listItem)
        throws CriticalException 
    {
        logger.debug("+");
        ArchivedListItem arcListItem = new ArchivedListItem(
            null, null, listItem.getListId(), listItem.getId(),
            listItem.getListItemLink(), 
            listItem.getTitle(),
            listItem.getLink(),
            listItem.getImageLink(),
            listItem.getDocumentLink(), 
            listItem.getViewDate(),
            listItem.getPublishDate(),
            listItem.getExpiredDate(),
            listItem.getCreatedBy(),
            listItem.getCreatedDate(),
            listItem.getLastModifiedBy(),
            listItem.getLastModifiedDate(),
            listItem.getThumbnailLink(),
            null, null, null, null, null,
            listItem.getContainerId(), false,
            listItem.getPerLangId()
        );
        try {
            if (listItem.getTeaserId() != null) {
                Article article = Article.findById(listItem.getTeaserId());
                arcListItem.setArticleLangId(article.getLangId());
                arcListItem.setTeaserHead(article.getHead());
                arcListItem.setTeaserText(article.getText());
                arcListItem.setIsPublic(article.getContainerId() == null);
            }
            if (listItem.getArticleId() != null) {
                Article article = Article.findById(listItem.getArticleId());
                arcListItem.setArticleHead(article.getHead());
                arcListItem.setArticleText(article.getText());
            }
        }
        catch (ObjectNotFoundException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return arcListItem;
    }
    
    private final static String selectLinksSql =
        "select list_item.id, list.id as listId, lang_id from list_item " +
        "join list on (list_item.list_id = list.id and list.site_id = ? ) " +
        " where list_item_link = ?";
    
    /**
     * Puts to archive items that are linked to specified.
     * @throws CriticalException
     */
    private void moveLinksToArchive(Connection conn, ListItem listItem)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        ArchivedListItem archivedLink = createArchivedItem(listItem);
        archivedLink.setListItemLink(listItem.getId());
        try {
            stmt = conn.prepareStatement(selectLinksSql);
            stmt.setObject(1, Env.getSiteId(), Types.BIGINT);
            stmt.setObject(2, listItem.getId(), Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                archivedLink.setLangId(DBHelper.makeLong(rs.getLong("lang_id")));
                archivedLink.setListId(DBHelper.makeLong(rs.getLong("listId")));
                archivedLink.setListItemId(DBHelper.makeLong(rs.getLong("id")));
                archivedLink.insert(conn);
            }
            rs.close();
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }
}
