/*
 * @(#)$Id: PollResultXmlBuilder.java,v 1.18, 2006-09-13 14:06:48Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.webpoll.generators;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.list.generators.ListXmlBuilder;
import com.negeso.framework.list.generators.XmlHelper;

/**
 * @author Sergiy Oliynyk
 */
public class PollResultXmlBuilder extends ListXmlBuilder {

    private static Logger logger = Logger.getLogger(PollResultXmlBuilder.class);

    protected static final String itemsQuery =
        "select list_item.id, list_id, title, link, image_link, " +
        "thumbnail_link, order_number, list_item.container_id, " +
        "article.head, article.text, " +
        "article.id as articleId, article.class as articleClass, " +
        "webpoll.counter as votes from list_item " +
        "left join webpoll on (list_item.id = webpoll.list_item_id) " +
        "left join article on (list_item.teaser_id = article.id) " +
        "where list_id = ? and list_item.site_id = ? " +
        " order by list_item.order_number";

    private int totalVotes;

    private PollResultXmlBuilder() {}

    public static ListXmlBuilder getInstance() {
        return new PollResultXmlBuilder();
    }

    public Element getElement(Connection conn, Document doc, Long listId, 
        Long userId, String listPath) throws CriticalException
    {
        logger.debug("+");
        totalVotes = getTotalVotes(conn, listPath);
        Element element = super.getElement(conn, doc, listId, userId, listPath);
        listPath = element.getAttribute("listPath");
        element.setAttribute("totalVotes", String.valueOf(totalVotes));
        element.setAttribute("type", "webpollresult");
        logger.debug("-");
        return element;
    }

    protected void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException, CriticalException
    {        
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("listId", rs.getString("list_id"));
        itemElement.setAttribute("orderNumber", rs.getString("order_number"));
        itemElement.setAttribute("title", rs.getString("title"));
        XmlHelper.setImageLinkAttribute(itemElement, rs.getString("image_link"));
        XmlHelper.setThumbnailLinkAttribute(itemElement,
            rs.getString("thumbnail_link"));
        if (rs.getString("text") != null)
            itemElement.appendChild(XmlHelper.getArticleElement(
                itemElement.getOwnerDocument(), "teaser", 
                rs.getLong("articleId"), rs.getString("articleClass"),
                rs.getString("head"), rs.getString("text")));
        itemElement.setAttribute("votes", rs.getString("votes"));
        int votes = rs.getInt("votes");
        long rating = totalVotes > 0 ? 
            Math.round((double)votes/totalVotes * 100) : 0;
        itemElement.setAttribute("rating", String.valueOf(rating));
        logger.debug("-");
    }
    
    protected String getItemsQuery() {
        return itemsQuery;
    }
    
    private int getTotalVotes(Connection conn, String listPath)
        throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        try {
            int pollListItemId = Integer.parseInt(listPath);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "select sum(counter) from webpoll where poll_id=" + 
                pollListItemId);
            int votes = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            logger.debug("-");
            return votes;
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
