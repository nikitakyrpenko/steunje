/*
 * @(#)$AdminXmlBuilder.java$
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
public class AdminXmlBuilder extends AlbumXmlBuilder {
    
    private static Logger logger = Logger.getLogger(AdminXmlBuilder.class);

    private static final AdminXmlBuilder instance = new AdminXmlBuilder();

    private AdminXmlBuilder() {}

    public static AdminXmlBuilder getInstance() {
        return instance;
    }

    static final String albumQuery =
        "select photo_album.id, name, photo_album.parent_id," +
        " mc_folder_id is null as is_photo, src as th_src, alt," +
        " max_width as th_width, max_height as th_height, container_id" +
        " from photo_album" +
        " left join image on (photo_album.thumbnail_id = image.image_id) " +
        " join mc_folder on (photo_album.mc_folder_id = mc_folder.id)" +
        " where photo_album.id = ?";
    
    static final String albumsQuery =
        "select photo_album.id, name, container_id from photo_album" +
        " join mc_folder on (photo_album.mc_folder_id = mc_folder.id)" +
        " and photo_album.parent_id";

    private static final String photosQuery =
        "select photo_album.id, name, photo_album.parent_id," +
        " mc_folder_id is null as is_photo, src as th_src, alt," +
        " max_width as th_width, max_height as th_height, container_id" +
        " from photo_album" +
        " left join image on (photo_album.thumbnail_id = image.image_id)" +
        " left join mc_folder on (photo_album.mc_folder_id = mc_folder.id)" +
        " where photo_album.parent_id";

    /*
     * Builds a document with all albums. Uses to select the album.
     */
    public Document getDocument(Connection conn, User user) 
        throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, user));
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

    /*
     * Method returns a subtree with all albums
     */
    public Element getElement(Connection conn, Document doc, User user)
        throws CriticalException
    {
        logger.debug("+");
        Element albumElement = Env.createDomElement(doc, "photo_album");
        appendDescendantAlbums(conn, albumElement, null, user);
        logger.debug("-");
        return albumElement;
    }

    protected Element buildPathToAlbum(Connection conn, Long id,
        Element rootElement, User user) throws CriticalException
    {
        logger.debug("+");
        Element albumElement = null;
        PreparedStatement stmt = null;
        Document doc = rootElement.getOwnerDocument();
        try {
            stmt = conn.prepareStatement(albumQuery);
            albumElement = Env.createDomElement(doc, "album");
            Long parentId = getParentId(stmt, id, albumElement, user);
            Element element = albumElement;
            while (parentId != null) {
                Element upperElement = Env.createDomElement(doc, "album");
                upperElement.appendChild(element);
                upperElement.setAttribute("selected", "true");
                element = upperElement;
                parentId = getParentId(stmt, parentId, element, user);
            }
            rootElement.appendChild(element);
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

    private void appendDescendantAlbums(Connection conn, Element parentElement, 
        Long id, User user) throws CriticalException
    {
        logger.debug("+");
        Document doc = parentElement.getOwnerDocument();
        ArrayList nodes = getDescendantAlbums(conn, doc, id, user);
        for (int i = 0; i < nodes.size(); i++)
            parentElement.appendChild((Element)nodes.get(i));
        logger.debug("-");
    }

    /*
     * Returns a list of albums following to the album with specified id
     */
    private ArrayList getDescendantAlbums(Connection conn, Document doc, 
        Long parentId, User user) throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList nodes = new ArrayList();
        try {
            stmt = conn.createStatement();
            String query = albumsQuery + 
                (parentId != null ? " =" + parentId : " is null") + 
                " order by name";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "album");
                element.setAttribute("id", rs.getString("id"));
                element.setAttribute("name", rs.getString("name"));
                Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
                element.setAttribute("canView", String.valueOf(
                    SecurityGuard.canView(user, containerId)));
                nodes.add(element);
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
        for (int i = 0; i < nodes.size(); i++) {
            appendDescendantAlbums(conn, (Element)nodes.get(i), Long.valueOf(((
                Element)nodes.get(i)).getAttribute("id")), user);
        }
        logger.debug("-");
        return nodes;
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
        itemElement.setAttribute("parentId", rs.getString("parent_id"));
        itemElement.setAttribute("th_src", rs.getString("th_src"));
        itemElement.setAttribute("alt", rs.getString("alt"));
        itemElement.setAttribute("th_width", rs.getString("th_width"));
        itemElement.setAttribute("th_height", rs.getString("th_height"));
        logger.debug("-");
    }
}
