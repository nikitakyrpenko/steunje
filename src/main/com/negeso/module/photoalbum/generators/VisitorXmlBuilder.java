/*
 * @(#)$VisitorXmlBuilder.java$
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
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 *
 */
public class VisitorXmlBuilder extends AlbumXmlBuilder {
    
    private static Logger logger = Logger.getLogger(VisitorXmlBuilder.class);

    private static final VisitorXmlBuilder instance = new VisitorXmlBuilder();

    private VisitorXmlBuilder() {}

    public static VisitorXmlBuilder getInstance() {
        return instance;
    }

    static final String albumQuery =
        "select photo_album.id, name, photo_album.parent_id, src as th_src," +
        " max_width as th_width, max_height as th_height," +
        " article.id as article_id, article.head, article.text," +
        " mc_folder.container_id" +
        " from photo_album" +
        " left join image on photo_album.thumbnail_id = image.image_id" +
        " join article on photo_album.article_id = article.id" +
        " join mc_folder on (photo_album.mc_folder_id = mc_folder.id)" +
        " where photo_album.id = ?";

    private static final String albumsQuery =
        "select photo_album.id, name, src as th_src, max_width as th_width," +
        " max_height as th_height, article.id as article_id, article.head," +
        " article.text, mc_folder.container_id" +
        " from photo_album" +
        " left join image on photo_album.thumbnail_id = image.image_id" +
        " join article on photo_album.article_id = article.id" +
        " join mc_folder on (photo_album.mc_folder_id = mc_folder.id)" +
        " where mc_folder_id is not null and photo_album.parent_id";

    private static final String photosQuery =
        "select photo_album.id, name, mc_folder_id is null as is_photo," +
        " src, alt, max_width as img_width, max_height as img_height," +
        " (select src from image where image_id = thumbnail_id) as th_src," +
        " (select max_width from image where image_id = thumbnail_id)" +
        " as th_width," +
        " (select max_height from image where image_id = thumbnail_id)" +
        " as th_height, mc_folder.container_id," +
        " article.id as article_id, article.head, article.text" +
        " from photo_album" +
        " left join image on photo_album.image_id = image.image_id" +
        " left join mc_folder on (photo_album.mc_folder_id = mc_folder.id)" +
        " join article on photo_album.article_id = article.id" +
        " where photo_album.parent_id";

    protected Element buildPathToAlbum(Connection conn, Long id, 
        Element rootElement, User user) throws CriticalException
    {
        logger.debug("+");
        Element albumElement = null;
        PreparedStatement stmt = null;
        Document doc = rootElement.getOwnerDocument();
        try {
            stmt = conn.prepareStatement(albumQuery);
            Long parentId = getParentId(stmt, id, null, user);
            Long topId = parentId;
            Element parentElement = rootElement;
            Element childElement = null;
            do {
                parentId = topId;
                if (parentId != null) { // find the parent element
                    parentElement = Env.createDomElement(doc, "album");
                    topId = getParentId(stmt, parentId, parentElement, user);
                }
                else parentElement = rootElement;
                if (childElement != null)
                    parentElement.appendChild(childElement);
                ArrayList albums = getAlbums(conn, doc, parentId, user);
                for (int i = 0; i < albums.size(); i++) {
                    Element el = (Element)albums.get(i);
                    if (el.getAttribute("id").equals(String.valueOf(id)))
                        albumElement = el;
                    if (!el.equals(childElement))
                        parentElement.appendChild(el);
                }
                if (parentElement != rootElement)
                    parentElement.setAttribute("selected", "true");
                childElement = parentElement;
            }
            while (parentId != null);
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return albumElement;
    }

    private ArrayList getAlbums(Connection conn, Document doc, Long parentId, 
        User user) throws CriticalException
    {
        logger.debug("+");
        ArrayList albums = new ArrayList();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String query = albumsQuery + (parentId != null ? " =" + parentId : 
                " is null") + " order by name";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
                if (SecurityGuard.canView(user, containerId)) {
                    Element element = Env.createDomElement(doc, "album");
                    setItemAttributes(element, rs);
                    albums.add(element);
                }
            }
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return albums;
    }

    protected String getItemsQuery(Long parentId) {
        String query = photosQuery + (parentId != null ? " =" + parentId : 
            " is null") + " order by is_photo, name";
        return query;
    }

    protected void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("name"));
        itemElement.setAttribute("th_src", rs.getString("th_src"));
        itemElement.setAttribute("th_width", rs.getString("th_width"));
        itemElement.setAttribute("th_height", rs.getString("th_height"));
        if ("negeso:photo".equals(itemElement.getNodeName())) {
            itemElement.setAttribute("alt", rs.getString("alt"));
            itemElement.setAttribute("src", rs.getString("src"));
            itemElement.setAttribute("img_width", rs.getString("img_width"));
            itemElement.setAttribute("img_height", rs.getString("img_height"));
        }
        Element articleElement =
            Env.createDomElement(itemElement.getOwnerDocument(), "article");
        articleElement.setAttribute("id", rs.getString("article_id"));
        articleElement.setAttribute("head", rs.getString("head"));
        articleElement.setAttribute("text", rs.getString("text"));
        itemElement.appendChild(articleElement);
        logger.debug("-");
    }
}
