/*
 * @(#)ListSearchProducer.java       @version    09.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.list.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.module.search.SearchEntry;
import com.negeso.module.search.SearchEntryProducer;
import com.negeso.module.search.SearchIndexCallBack;

/**
 * @author Sergiy Oliynyk
 * Class produces forming of search entries for lists.
 */
public class ListSearchProducer implements SearchEntryProducer {

    private static Logger logger = Logger.getLogger(ListSearchProducer.class);

    private static final String selectListItemsQuery =
            "select" +
                    " list_item.id as listItemId, list_item.title, last_modified_date," +
                    " a.text as summary_text, b.text as article_text," +
                    " a.lang_id, a.container_id" +
                    " from list, list_item, article as a, article as b" +
                    " where" +
                    " list_item.teaser_id = a.id" +
                    " and list_item.article_id = b.id" +
                    " and list_item.list_id = list.id" +
                    " and list.module_id in (?, ?) " +
                    " AND (list_item.expired_date > now() OR list_item.expired_date IS NULL )";

    private static final String selectArchivedItemsQuery =
            "select list_item_id, lang_id, title, teaser_text, article_text, " +
                    "last_modified_date, container_id, is_public " +
                    "from news_archive" +
                    " where site_id = ";

    @Override
    public void indexEntries(SearchIndexCallBack callBack) {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            addItemsFromLists(conn, callBack);
            addArchivedItems(conn, callBack);
        } catch (Exception ex) {
            logger.error("Cannot build search entries", ex);
        } finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
    }

    /**
     * @param conn    Connection
     * @throws CriticalException
     */
    private void addItemsFromLists(Connection conn, SearchIndexCallBack callBack)
            throws CriticalException {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(selectListItemsQuery);
            stmt.setLong(1, Env.getSite().getModuleId(ModuleConstants.NEWS));
            stmt.setLong(2, Env.getSite().getModuleId(ModuleConstants.FAQ));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("listItemId");
                String title = rs.getString("title");
                String summary = rs.getString("summary_text");
                String text = rs.getString("article_text");
                String langCode = Language.findById(
                        new Long(rs.getLong("lang_id"))).getCode();
                String uri = "searchnews_" + langCode + ".html?id=" + id;


                SearchEntry entry = new SearchEntry(null,uri, langCode,
                        title, getIndexingText(title, summary, text), null, null, null);

                Timestamp tm = rs.getTimestamp("last_modified_date");
                if (tm != null)
                    entry.setDate(tm.getTime());
                entry.setContainerId(DBHelper.makeLong(
                        rs.getLong("container_id")));
                callBack.index(entry);
            }
            rs.close();
        } catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }

    /*
     * @param conn Connection
     * @param entries The list in which SearchEntry objects are placing
     * @throws CriticalException
     */
    private void addArchivedItems(Connection conn, SearchIndexCallBack callBack)
            throws CriticalException {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectArchivedItemsQuery + +Env.getSiteId());
            while (rs.next()) {
                String id = rs.getString("list_item_id");
                String title = rs.getString("title");
                String langCode = Language.findById(
                        new Long(rs.getLong("lang_id"))).getCode();
                String summary = rs.getString("teaser_text");
                String text = rs.getString("article_text");
                String uri = "searchnews_" + langCode + ".html?id=" + id;

                SearchEntry entry = new SearchEntry(null, uri, langCode, title,
                        getIndexingText(title, summary, text), null, null, null);

                Timestamp tm = rs.getTimestamp("last_modified_date");

                if (tm != null) {
                    entry.setDate(tm.getTime());
                }

                if (!rs.getBoolean("is_public")) {
                    entry.setContainerId(DBHelper.makeLong(
                            rs.getLong("container_id")));
                }
                callBack.index(entry);
            }
        } catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }

    private String getIndexingText(String title, String summary, String text) {
        logger.debug("+");
        StringBuffer sb = new StringBuffer(title);
        sb.append(' ');
        sb.append(summary);
        sb.append(' ');
        sb.append(text);
        logger.debug("-");
        return sb.toString();
    }
}
