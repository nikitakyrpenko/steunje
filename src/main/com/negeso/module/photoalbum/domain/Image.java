/*
 * @(#)Image.java       @version 10.12.2003
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
 * @author Sergiy Oliynyk
 *
 */
public class Image implements DbObject {
    
    private static Logger logger = Logger.getLogger(Image.class);

    public static final String tableId = "image";
    private static final int fieldCount = 4;

    private final static String selectFrom =
        "SELECT image_id as id, src, max_width as width, max_height as height" +
        " from image";

    private final static String findByIdSql =
        selectFrom + " WHERE image_id = ?";

    private final static String updateSql =
        "UPDATE image " +
        " SET image_id = ?, src = ?, max_width = ?, max_height = ?" +
        " WHERE image_id = ?";

    private final static String insertSql =
        " INSERT INTO image (image_id, src, max_width, max_height)"+
        " VALUES (?, ?, ?, ?)";

    private Long id;
    private String src;
    private int width;
    private int height;

    public Image() {}
    
    public Image(Long id, String src, int width, int height) {
        this.id = id;
        this.src = src;
        this.width = width;
        this.height = height;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+"); 
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            src = rs.getString("src");
            width = rs.getInt("width");
            height = rs.getInt("height");
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return id;
    }

    public Long insert(Connection con) throws CriticalException {
        PreparedStatement stmt = null;
        try {
            id = DBHelper.getNextInsertId(con, "image_image_id_seq");
            stmt = con.prepareStatement(insertSql);
            saveIntoStatement(stmt);
            stmt.executeUpdate();
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return id;
    }

    public void update(Connection con) throws CriticalException {
		logger.debug("+");
		try {
			PreparedStatement stmt = con.prepareStatement(updateSql);
			stmt = saveIntoStatement( stmt );
			stmt.setObject(5, id);
			stmt.executeUpdate();
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		}
		logger.debug("-");
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
        stmt.setString(2, src);
        stmt.setInt(3, width);
        stmt.setInt(4, height);
        logger.debug("-");
        return stmt;
    }

    public static Image findById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        Image image = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                image = new Image();
                image.load(rs);
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
        return image;
    }

    public static Image getById(Connection conn, Long id)
       throws CriticalException
    {
        Image image = findById(conn, id);
        if (image == null)
            throw new CriticalException("Image not found by id=" + id);
        return image;
    }

    public Element getElement(Document doc) {
        logger.debug("+");
        Element imageElement = Env.createDomElement(doc, "image");
        imageElement.setAttribute("id", String.valueOf(id));
        imageElement.setAttribute("src", src);
        imageElement.setAttribute("width", String.valueOf(width));
        imageElement.setAttribute("height", String.valueOf(height));
        logger.debug("-");
        return imageElement;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Image [");
        sb.append("id=");
        sb.append(id);
        sb.append(" src=");
        sb.append(src);
        sb.append(" width=");
        sb.append(width);
        sb.append(" height=");
        sb.append(height);
        return sb.toString();
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSrc() {
        return src;
    }
    
    public void setSrc(String src) {
        this.src = src;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
}
