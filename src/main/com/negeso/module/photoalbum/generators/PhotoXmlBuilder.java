/*
 * @(#)$PhotoXmlBuilder.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.photoalbum.generators;

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

/**
 * @author Sergiy Oliynyk
 *
 */
public class PhotoXmlBuilder {
    
    private static Logger logger = Logger.getLogger(PhotoXmlBuilder.class);

    private static final PhotoXmlBuilder instance = new PhotoXmlBuilder();

    private PhotoXmlBuilder() {}

    public static PhotoXmlBuilder getInstance() {
        return instance;
    }

    private static final String photoQuery =
        "select photo_album.id, parent_id, name, src, alt," +
        " max_width as img_width, max_height as img_height," +
        " article.id as article_id, article.head, article.text" +
        " from photo_album" +
        " join image on photo_album.image_id = image.image_id" +
        " join article on photo_album.article_id = article.id" +
        " where photo_album.id=?";
    
    public Document getDocument(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, id));
            doc.getDocumentElement().setAttribute("xmlns:negeso", 
                Env.NEGESO_NAMESPACE);
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return doc;
    }

    public Element getElement(Connection conn, Document doc, Long id) 
        throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null) {
            logger.debug("- Invalid argument");
            throw new CriticalException("Invalid argument");
        }
        Element element = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(photoQuery);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                element = Env.createDomElement(doc, "photo");
                setItemAttributes(element, rs);
            }
            else {
                logger.warn("- Photo not found");
                throw new CriticalException("Photo not found by id=" + id);
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
        return element;
    }
    
    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("name"));
        itemElement.setAttribute("parent_id", rs.getString("parent_id"));
        itemElement.setAttribute("src", rs.getString("src"));
        itemElement.setAttribute("alt", rs.getString("alt"));
        itemElement.setAttribute("img_width", rs.getString("img_width"));
        itemElement.setAttribute("img_height", rs.getString("img_height"));
        Element articleElement =
            Env.createDomElement(itemElement.getOwnerDocument(), "article");
        articleElement.setAttribute("id", rs.getString("article_id"));
        articleElement.setAttribute("text", rs.getString("text"));
        itemElement.appendChild(articleElement);
        logger.debug("-");
    }
}
