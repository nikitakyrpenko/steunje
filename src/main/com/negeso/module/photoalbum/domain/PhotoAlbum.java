/*
 * @(#)PhotoAlbum.java       @version 10.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.photoalbum.domain;

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
import com.negeso.framework.domain.DbObject;

/**
 * Photo Album
 *
 * @author  Sergiy Oliynyk
 */
public class PhotoAlbum implements DbObject {

    private static Logger logger = Logger.getLogger(PhotoAlbum.class);

    public static final String tableId = "photo_album";
    private static final int fieldCount = 8;

    private final static String selectFrom =
        " SELECT" +
        " id, name, lang_id, parent_id, article_id, mc_folder_id, image_id," +
        " thumbnail_id FROM photo_album";

    private final static String findByIdSql =
        PhotoAlbum.selectFrom + " WHERE id = ?";
    
    private final static String findBySrcThumbnailSql =
        " SELECT src FROM image where image_id = ?";
    
    private final static String updateSql =
        " UPDATE photo_album" +
        " SET id = ?, name = ?, lang_id = ?, parent_id = ?, article_id = ?," +
        " mc_folder_id = ?, image_id = ?, thumbnail_id = ?" +
        " WHERE id = ?";

    private final static String insertSql =
        " INSERT INTO photo_album" +
        " (id, name, lang_id, parent_id, article_id, mc_folder_id, " +
        " image_id, thumbnail_id)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /** Persistant properties */
    private Long id;
    private String name;
    private Long langId;
    private Long parentId;
    private Long articleId;
    private Long mcFolderId;
    private Long imageId;
    private Long thumbnailId;

    public PhotoAlbum() {}

    public PhotoAlbum(Long id, String name, Long langId, Long parentId, 
        Long articleId, Long mcFolderId, Long imageId, Long thumbnailId)
    {
        this.id = id;
        this.name = name;
        this.langId = langId;
        this.parentId = parentId;
        this.articleId = articleId;
        this.mcFolderId = mcFolderId;
        this.imageId = imageId;
        this.thumbnailId = thumbnailId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+"); 
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            name = rs.getString("name");
            langId = DBHelper.makeLong(rs.getLong("lang_id"));
            parentId = DBHelper.makeLong(rs.getLong("parent_id"));
            articleId = DBHelper.makeLong(rs.getLong("article_id"));
            mcFolderId = DBHelper.makeLong(rs.getLong("mc_folder_id"));
            imageId = DBHelper.makeLong(rs.getLong("image_id"));
            thumbnailId = DBHelper.makeLong(rs.getLong("thumbnail_id"));
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return id;
    }

    public Long insert(Connection con) throws CriticalException {
        return DBHelper.insertDbObject(con, this);
    }

    public void update(Connection con) throws CriticalException {
        DBHelper.updateDbObject(con, this);
    }

    public void delete(Connection con) throws CriticalException {
        DBHelper.deleteObject(con, tableId, id, logger);
    }

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }
    
    public static String getFindBySrcThumbnailSql() {
		return findBySrcThumbnailSql;
	}
 
    public String getUpdateSql() {
        return updateSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException
    {
        logger.debug("+");
        stmt.setObject(1, id);
        stmt.setString(2, name);
        stmt.setObject(3, langId);
        stmt.setObject(4, parentId);
        stmt.setObject(5, articleId); 
        stmt.setObject(6, mcFolderId);
        stmt.setObject(7, imageId);
        stmt.setObject(8, thumbnailId);
        logger.debug("-");
        return stmt;
    }

    public static PhotoAlbum findById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        PhotoAlbum album = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                album = new PhotoAlbum();
                album.load(rs);
            }
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return album;
    }
    
    public static String findBySrcThumbnail(Connection conn, Long id)
	    throws CriticalException
	{
	    logger.debug("+");
	    PreparedStatement stmt = null;
	    String thumbnail = null; 
	    try {
	        stmt = conn.prepareStatement(findBySrcThumbnailSql);
	        stmt.setObject(1, id);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {	            
	        	thumbnail = rs.getString("src");
	        }
	    }
	    catch (SQLException ex) {
	        logger.error("- Throwing new CriticalException");
	        throw new CriticalException(ex);
	    }
	    finally {
	        DBHelper.close(stmt);
	    }
	    logger.debug("-");
	    return thumbnail;
	}    

    public static PhotoAlbum getById(Connection conn, Long id)
       throws CriticalException
    {
        PhotoAlbum album = findById(conn, id);
        if (album == null)
            throw new CriticalException("Album not found by id=" + id);
        return album;
    }
    
    public static String getBySrcThumbnail(Connection conn, Long id)
	    throws CriticalException
	 {
	     String thumbnail = findBySrcThumbnail(conn, id);
	     return thumbnail;
	 }
    
    public Element getElement(Document doc) {
        logger.debug("+");
        Element albumElement = Env.createDomElement(doc, mcFolderId != null ?
            "album" : "photo");
        albumElement.setAttribute("id", String.valueOf(id));
        albumElement.setAttribute("name", name);
        logger.debug("-");
        return albumElement;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("PhotoAlbum [");
        sb.append("id=");
        sb.append(id);
        sb.append(" name=");
        sb.append(name);
        sb.append(" langId=");
        sb.append(langId);
        sb.append(" parentId=");
        sb.append(parentId);
        sb.append(" articleId=");
        sb.append(articleId);
        sb.append(" mcFolderId=");
        sb.append(mcFolderId);
        sb.append(" imageId=");
        sb.append(imageId);
        sb.append(" thumbnailId=");
        sb.append(thumbnailId);
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLangId() {
        return langId;
    }

    public void setLangId(Long langId) {
        this.langId = langId;
    }

    /**
     * @return Returns the articleId.
     */
    public Long getArticleId() {
        return articleId;
    }
    /**
     * @param articleId The articleId to set.
     */
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
    /**
     * @return Returns the imageId.
     */
    public Long getImageId() {
        return imageId;
    }
    /**
     * @param imageId The imageId to set.
     */
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
    /**
     * @return Returns the mcFolderId.
     */
    public Long getMcFolderId() {
        return mcFolderId;
    }
    /**
     * @param mcFolderId The mcFolderId to set.
     */
    public void setMcFolderId(Long mcFolderId) {
        this.mcFolderId = mcFolderId;
    }
    /**
     * @return Returns the parentId.
     */
    public Long getParentId() {
        return parentId;
    }
    /**
     * @param parentId The parentId to set.
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    /**
     * @return Returns the thumbnailId.
     */
    public Long getThumbnailId() {
        return thumbnailId;
    }
    /**
     * @param thumbnailId The thumbnailId to set.
     */
    public void setThumbnailId(Long thumbnailId) {
        this.thumbnailId = thumbnailId;
    }
}
