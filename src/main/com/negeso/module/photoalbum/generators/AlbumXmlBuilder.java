/*
 * @(#)$AlbumXmlBuilder.java$
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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.photoalbum.domain.PhotoAlbum;

/**
 * @author Sergiy Oliynyk
 *
 */
public abstract class AlbumXmlBuilder {

    private static Logger logger = Logger.getLogger(AlbumXmlBuilder.class);

    /*
     * Builds a document with a path to the specified album and a collection of
     * photos in the album.
     */
    public Document getDocument(Connection conn, Long id, User user)
        throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, id, user));
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

    public Element getElement(Connection conn, Document doc, Long id, 
        User user) throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null) {
            logger.warn("- Invalid argument");
            throw new CriticalException("Invalid argument");
        }
        Element rootElement = Env.createDomElement(doc, "photo_album");
        PhotoAlbum photoAlbum = null;
        if (id != null)
            photoAlbum = PhotoAlbum.findById(conn, id);
        if (photoAlbum != null) {
            Long parentId = (photoAlbum.getMcFolderId() == null) ? 
                photoAlbum.getParentId() : id;
            Element albumElement = buildPathToAlbum(conn, parentId, rootElement,
                user);
            addAlbumsAndPhotos(conn, albumElement, parentId, id, user);
            if (albumElement != null && albumElement != rootElement) {
                albumElement.setAttribute("selected", "true");
                setRightsAttributes(albumElement, photoAlbum.getMcFolderId(), 
                    user);
            }
            rootElement.setAttribute("albumId", parentId.toString());
        }
        else {
            addAlbumsAndPhotos(conn, rootElement, null, null, user);
            setRightsAttributes(rootElement, null, user);
        }
        logger.debug("-");
        return rootElement;
    }
    
    /*
     * Adds child nodes to the element that presents a photo album
     */
    protected void addAlbumsAndPhotos(Connection conn, Element parentElement, 
        Long parentId, Long photoId, User user) throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        if (parentElement != null) {
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(getItemsQuery(parentId));
                int pos = 0;
                while (rs.next()) {
                    Element element = Env.createDomElement(
                        parentElement.getOwnerDocument(),
                        rs.getBoolean("is_photo") ? "photo" : "album");
                    setItemAttributes(element, rs);
                    if (!rs.getBoolean("is_photo")) {
                        Long containerId = DBHelper.makeLong(
                            rs.getLong("container_id"));
                        if (user != null)
                            XmlHelper.setRightsAttributes(element, user.getId(), 
                               containerId);
                        if (!SecurityGuard.canView(user, containerId))
                            continue;
                    }
                    else pos++;
                    if (photoId != null && photoId.longValue() ==
                        rs.getLong("id"))
                    {
                        element.setAttribute("selected", "true");
                        element.setAttribute("position", String.valueOf(pos));
                    }
                    parentElement.appendChild(element);
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
        }
        logger.debug("-");
    }

    protected Long getParentId(PreparedStatement stmt, Long id,
        Element parentElement, User user) throws SQLException
    {
        logger.debug("+");
        Long parentId = null;
        stmt.setObject(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            parentId = DBHelper.makeLong(rs.getLong("parent_id"));
            Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
            if (parentElement != null) {
                setItemAttributes(parentElement, rs);
                parentElement.setAttribute("canView", String.valueOf(
                    SecurityGuard.canView(user, containerId)));
            }
        }
        else {
            logger.error("-Album not found with id=" + id);
            parentId = null;
        }
        rs.close();
        logger.debug("-");
        return parentId;
    }

    private void setRightsAttributes(Element albumElement, Long folderId, 
        User user) throws CriticalException
    {
        logger.debug("+");
        Folder folder = folderId != null ?
            Repository.get().getFolder(folderId) :
            Repository.get().getFolder("media/photo_album/");
        if (folder != null) {
            albumElement.setAttribute("canManage", String.valueOf(
                folder.canManage(user)));
            albumElement.setAttribute("canContribute", String.valueOf(
                folder.canEdit(user)));
            albumElement.setAttribute("canView", String.valueOf(
                folder.canView(user)));
            Long containerId = folder.getContainerId();
            if (containerId != null)
                albumElement.setAttribute("containerId", containerId.toString()); 
            if (folderId != null)
                albumElement.setAttribute("folderId", folderId.toString());
        }
        logger.debug("-");
    }

    protected abstract Element buildPathToAlbum(Connection conn, Long id,
        Element rootElement, User user) throws CriticalException;

    protected abstract String getItemsQuery(Long parentId);

    protected abstract void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException;
}
