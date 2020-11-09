/*
 * @(#)$Id: RestoreFromArchiveCommand.java,v 1.20, 2006-09-13 14:00:46Z, Svetlana Bondar$
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.list.command.CreateLinksCommand;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.archive.domain.ArchivedListItem;

/**
 * @author Sergiy Oliynyk
 */
public class RestoreFromArchiveCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(RestoreFromArchiveCommand.class);

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
                restoreItems(conn, items, user);
            else
                restoreItem(conn, listItemId, user);
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
    private void checkPermission(User user, Long listContainerId, 
        Long listItemContainerId) throws AccessDeniedException
    {
        if (!SecurityGuard.canManage(user, listContainerId) ||
            !SecurityGuard.canManage(user, listItemContainerId))
            throw new AccessDeniedException("Forbidden");
    }

    /*
     * Restore multiple items
     */
    private void restoreItems(Connection conn, String items, User user)
        throws AccessDeniedException, Exception
    {
        logger.debug("+");
        if (items != null) {
            StringTokenizer st = new StringTokenizer(items, ";");
            while (st.hasMoreTokens()) {
                Long listItemId = Long.valueOf(st.nextToken());
                restoreItem(conn, listItemId, user);
            }
        }
        logger.debug("-");
    }

    /*
     * Restore one item
     */
    private void restoreItem(Connection conn, Long listItemId, User user)
        throws AccessDeniedException, Exception
    {
        logger.debug("+");
        ArchivedListItem ali = ArchivedListItem.getById(conn, listItemId);
        List list = List.findById(conn, ali.getListId());
        checkPermission(user, list.getContainerId(), ali.getContainerId());
        List.getById(conn, ali.getListId()); // check for existence of the list
        ListItem listItem = createListItem(ali);
        Article article = new Article();
        article.setLangId(ali.getArticleLangId());
        article.setHead(ali.getTeaserHead());
        article.setText(ali.getTeaserText());
        article.setContainerId(ali.isPublic() ? null : ali.getContainerId());
        listItem.setTeaserId(article.insert(conn));
        article.setHead(ali.getArticleHead());
        article.setText(ali.getArticleText());
        article.setContainerId(null);
        listItem.setArticleId(article.insert(conn));
        try {
            listItem.setCreatedBy(User.findById(ali.getCreatedBy()).getId());
            listItem.setLastModifiedBy(User.findById(
                ali.getLastModifiedBy()).getId());
        }
        catch (Exception ex) {
            logger.debug("User not found " + ex);
        }
        listItem.insert(conn);
        restoreLinks(conn, ali.getListItemId(), listItem);
        ali.delete(conn);
        logger.debug("-");
    }
    
    private ListItem createListItem(ArchivedListItem ali) { 
        logger.debug("+");
        ListItem listItem = new ListItem(null,
            ali.getListId(),
            ali.getTitle(),
            0, null, null,
            ali.getLink(), 
            ali.getImageLink(),
            ali.getDocumentLink(),
            ali.getThumbnailLink(),
            null,
            ali.getViewDate(),
            ali.getPublishDate(),
            ali.getExpiredDate(),
            null,
            ali.getCreatedDate(),
            null, 
            ali.getLastModifiedDate(),
            null,
            ali.getContainerId(),
            ali.getPerLangId());
        logger.debug("-");
        return listItem;
    }

    private final static String selectLinksSql =
        "select lang_id, list_id, container_id from news_archive " +
        "where list_item_link = ? and exists (select id from list where id = ? and site_id = ? )";

    private void restoreLinks(Connection conn, Long listItemId,
        ListItem listItem) throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        ListItem link = CreateLinksCommand.createLink(listItem);
        try {
            stmt = conn.prepareStatement(selectLinksSql);
            stmt.setObject(1, listItemId, Types.BIGINT);
            stmt.setObject(2, listItem.getListId(), Types.BIGINT);
            stmt.setObject(3, Env.getSiteId(), Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                link.setListId(DBHelper.makeLong(rs.getLong("list_id")));
                link.setContainerId(
                    DBHelper.makeLong(rs.getLong("container_id")));
                link.insert(conn);
            }
            rs.close();
            logger.debug("-");
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }
}
