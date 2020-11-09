/*
 * @(#)List.java       @version	10.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;

/**
 * List domain incapsulation
 *
 * @author	Olexiy.Strashko
 * @author  Sergiy Oliynyk
 */
public class List implements DbObject {
    
    private static Logger logger = Logger.getLogger(List.class);

    public static final String tableId = "list";
    private static final int fieldCount = 11;

    private final static String selectFrom =
        " SELECT " +
        " id, name, lang_id, image_width, image_height," +
        " module_id, instance_name, template_id, parent_list_item_id," +
        " container_id FROM list";

    private final static String findByIdSql =
        List.selectFrom + " WHERE id = ?";

    private final static String findByParentListItemIdSql =
        List.selectFrom + " WHERE parent_list_item_id = ? ";

    private final static String updateSql =
        " UPDATE list" +
        " SET id = ?, name = ?, lang_id = ?, " +
        " image_width = ?, image_height = ?, module_id = ?," +
        " instance_name = ?, template_id = ?, parent_list_item_id = ?," +
        " container_id = ?," +
        " site_id = ?" +
        " WHERE id = ?";

    private final static String insertSql =
        " INSERT INTO list"
        + " (id, name, lang_id, image_width, image_height, "
        + " module_id, instance_name, template_id, parent_list_item_id,"
        + " container_id, site_id) "
        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /** Persistant properties */
    private Long id;
    private String name = "List";
    private Long langId;
    private int imageWidth;
    private int imageHeight;
    private Long moduleId;
    private String instanceName;
    private Long templateId;
    private Long parentListItemId;
    private Long containerId;

    /** Dynamic properties */
    private int defaultImageWidth;
    private int defaultImageHeight;

    /** Constants */
    public static final int DEFAULT_IMAGE_WIDTH = 100;
    public static final int DEFAULT_IMAGE_HEIGHT = 100;
    private static final String ENV_IMAGE_HEIGHT_ID = "list.image.height";
    private static final String ENV_IMAGE_WIDTH_ID = "list.image.width";
    
    /*
     * Constructors
     */
    public List() {}
    
    public List(
        Long id,
        String name,
        Long langId,
        int imageWidth,
        int imageHeight,
        Long moduleId,
        String instanceName,
        Long templateId,
        Long parentListItemId,
        Long containerId)
    {
        this.id = id;
        this.langId = langId;
        this.name = name;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.moduleId = moduleId;
        this.instanceName = instanceName;
        this.templateId = templateId;
        this.parentListItemId = parentListItemId;
        this.containerId = containerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            name = rs.getString("name");
            langId = DBHelper.makeLong(rs.getLong("lang_id"));
            imageWidth = rs.getInt("image_width");
            imageHeight = rs.getInt("image_height");
            moduleId = DBHelper.makeLong(rs.getLong("module_id"));
            instanceName = rs.getString("instance_name");
            templateId = DBHelper.makeLong(rs.getLong("template_id"));
            parentListItemId = DBHelper.makeLong(
                rs.getLong("parent_list_item_id"));
            containerId = DBHelper.makeLong(rs.getLong("container_id"));
            logger.debug("-");
            return id;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
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
        stmt.setInt(4, imageWidth);
        stmt.setInt(5, imageHeight);
        stmt.setObject(6, moduleId);
        stmt.setString(7, instanceName);
        stmt.setObject(8, templateId);
        stmt.setObject(9, parentListItemId);
        stmt.setObject(10, containerId);
        stmt.setObject(11, Env.getSiteId() );        
        logger.debug("-");
        return stmt;
    }

    private static List find(Connection conn, String query, Long id1, Long id2)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setObject(1, id1, Types.BIGINT);
            if (id2 != null)
                stmt.setObject(1, id1, Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            List l = null;
            if (rs.next()) {
                l = new List();
                l.load(rs);
            }
            logger.debug("-");
            return l;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    public static List findById(Connection conn, Long id)
        throws CriticalException
    {
        return find(conn, findByIdSql, id, null);
    }
    
    public static List findByParentListItemId(Connection conn, Long listItemId)
        throws CriticalException
    {
        return find(conn, findByParentListItemIdSql + " and site_id = " + Env.getSiteId(), listItemId, null);
    }            
    
    public static List findByCategory(Connection conn, Long langId, 
        Long templateId) throws CriticalException
    {
        return find(conn, findByIdSql, langId, templateId);
    }

    public static List getById(Connection conn, Long id)
       throws CriticalException
    {
        List l = find(conn, findByIdSql, id, null);
        if (l == null)
            throw new CriticalException("List not found by id=" + id);
        return l;
    }

    public static List getByParentListItemId(Connection conn, Long listItemId)
        throws CriticalException
    {
        List l = find(conn, findByParentListItemIdSql + " and site_id = " + Env.getSiteId(), listItemId, null);
        if (l == null)
            throw new CriticalException("List not found by parentListItemId=" + 
                listItemId);
        return l;
    }

    public Element getElement(Document doc) {
        logger.debug("+");
        Element listElement = Env.createDomElement(doc, "list");
        listElement.setAttribute("id", String.valueOf(id));
        listElement.setAttribute("name", name);
        listElement.setAttribute("imageWidth", String.valueOf(imageWidth));
        listElement.setAttribute("imageHeight", String.valueOf(imageHeight));
        listElement.setAttribute("instanceName", instanceName);
        listElement.setAttribute("templateId", String.valueOf(templateId));
        listElement.setAttribute("moduleId", String.valueOf(moduleId));
        listElement.setAttribute("parentListItemId", 
            String.valueOf(parentListItemId));
        listElement.setAttribute("containerId", String.valueOf(containerId));
        logger.debug("-");
        return listElement;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("List [");
        sb.append("id=");
        sb.append(this.id);
        sb.append(" name=");
        sb.append(name);
        sb.append(" langId=");
        sb.append(langId);
        sb.append(" imageWidth=");
        sb.append(imageWidth);
        sb.append(" imageHeight=");
        sb.append(imageHeight);
        sb.append(" moduleId=");
        sb.append(moduleId);
        sb.append(" instanceName=");
        sb.append(instanceName);
        sb.append(" templateId=");
        sb.append(templateId);
        sb.append(" parentListItemId=");
        sb.append(parentListItemId);
        sb.append(" defaultImageWidth=");
        sb.append(defaultImageWidth);
        sb.append(" defaultImageHeight=");
        sb.append(defaultImageHeight);
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String string) {
        name = string;
    }

    public Long getLangId() {
        return langId;
    }

    public void setLangId(Long langId) {
        this.langId = langId;
    }

    /**
     * The image width getter
     *
     * @return
     */
    public int getImageWidth() {
        if ( (imageHeight == 0) && (imageWidth == 0) ) {
            imageWidth = getDefaultImageWidth();
        }
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * The image height setter.
     *
     * @return
     */
    public int getImageHeight() {
        if ( (imageHeight == 0) && (imageWidth == 0) ) {
            imageHeight = getDefaultImageHeight();
        }
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    /**
     * @return moduleId
     */
    public Long getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId
     */
    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return
     */
    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * @return
     */
    public int getDefaultImageHeight() {
        if ((defaultImageHeight == 0)) {
            defaultImageHeight = Integer.parseInt(Env.getProperty(
                ENV_IMAGE_HEIGHT_ID, String.valueOf(DEFAULT_IMAGE_HEIGHT)));
        }
        return defaultImageHeight;
    }

    /**
     * @return
     */
    public int getDefaultImageWidth() {
        if ((defaultImageWidth == 0)) {
            defaultImageWidth = Integer.parseInt(Env.getProperty(
                ENV_IMAGE_WIDTH_ID, String.valueOf(DEFAULT_IMAGE_WIDTH)));
        }
        return defaultImageWidth;
    }

    public Long getParentListItemId() {
        return parentListItemId;
    }

    public void setParentListItemId(Long arg) {
        this.parentListItemId = arg;
    }

    public Long getContainerId() {
        return containerId;
    }
    
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }
}
