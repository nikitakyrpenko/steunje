/*
 * @(#)ArchiveListXmlBuilder.java       @version    09.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.archive.generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 */
public class ArchiveXmlBuilder {
    
    private static final ArchiveXmlBuilder instance = new ArchiveXmlBuilder();
    
    private static Logger logger = Logger.getLogger(ArchiveXmlBuilder.class);
    
    private ArchiveXmlBuilder() {}

    public static ArchiveXmlBuilder getInstance() {
        return instance;
    }

    private static final String query = 
        "select * from news_archive where list_id = ? order by id";

    public Document getDocument(Connection conn, Long listId, Long userId)
        throws CriticalException
    {
        logger.debug("+");
        try {
            Document doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, listId, userId));
            logger.debug("-");
            return doc;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }

    /**
     * Builds the XML for the archive of news.
     * @param listId Identifier of the list from which items were moved to
     * the archive
     * @param userId Identifier of the currect user
     * @throws CriticalException
     */
    protected Element getElement(Connection conn, Document doc, Long listId, 
        Long userId) throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        Element listElement = Env.createDomElement(doc, "newsArchive");
        listElement.setAttribute("listId", String.valueOf(listId));
        try {
            stmt = conn.prepareStatement(query);
            stmt.setLong(1, listId.longValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
                if (SecurityGuard.canView(userId, containerId)) {
                    Element itemElement = Env.createDomElement(
                        listElement.getOwnerDocument(), "archivedListItem");
                    setItemAttributes(itemElement, rs);
                    XmlHelper.setRightsAttributes(itemElement, userId, 
                        containerId);
                    listElement.appendChild(itemElement);
                }
            }
            rs.close();
            stmt.close();
            logger.debug("-");
            return listElement;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }
    
    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("listId", rs.getString("list_id"));
        itemElement.setAttribute("title", rs.getString("title"));
        itemElement.setAttribute("linked", rs.getString("list_item_link"));
        XmlHelper.setDateAttribute(itemElement, "viewDate",
            rs.getTimestamp("view_date"));
        XmlHelper.setDateAttribute(itemElement, "publishDate",
            rs.getTimestamp("publish_date"));
        XmlHelper.setDateAttribute(itemElement, "expiredDate",
            rs.getString("expired_date"));
        logger.debug("-");
    }
}
