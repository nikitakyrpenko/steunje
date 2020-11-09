/*
 * @(#)$Category.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.document.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.OrderControlHelper;
import com.negeso.framework.domain.Orderable;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class Category implements DbObject, Orderable {

    private static Logger logger = Logger.getLogger(Category.class);

    private static final String tableId = "dc_category";
    private static final int fieldCount = 7;

    public final static String selectFrom =
        "SELECT id, name, parent_id, lang_id, mc_folder_id, order_number, site_id" +
        " from dc_category where site_id = ? ORDER BY order_number";

    private final static String findByIdSql =
        "SELECT id, name, parent_id, lang_id, mc_folder_id, order_number, site_id" +
        " from dc_category where id = ?";

    private final static String updateSql =
        "UPDATE dc_category SET " +
        " id = ?,  name = ?, parent_id = ?, lang_id = ?, mc_folder_id = ?, order_number = ?, site_id = ? " +
        "WHERE id = ?";

    private final static String insertSql =
        "INSERT INTO dc_category" +
        " (id, name, parent_id, lang_id, mc_folder_id, order_number, site_id) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private Long id;
    private String name;
    private Long parentId;
    private Long langId;
    private Long mcFolderId;
    
    private Long orderNumber = new Long(0);

	//======================================================================
	// Helpers 
	//======================================================================
	// order controller
	private static OrderControlHelper orderController = 
		new OrderControlHelper(tableId, "parent_id", "1=1");
    
    /**
     * Constructors
     */
    public Category() {}
    
    public Category(Long id, String name, Long parentId, Long langId,
        Long mcFolderId, Long orderNumber)
    {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.langId = langId;
        this.mcFolderId = mcFolderId;
        this.orderNumber = orderNumber;
    }
    
    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            name = rs.getString("name");
            parentId = DBHelper.makeLong(rs.getLong("parent_id"));
            langId = DBHelper.makeLong(rs.getLong("lang_id"));
            mcFolderId = DBHelper.makeLong(rs.getLong("mc_folder_id"));
			
            this.orderNumber = new Long(rs.getLong("order_number"));            
            
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return id;
    }

    public Long insert(Connection con) throws CriticalException {
		logger.debug( "+" );
		try {    	
			// get the next insert order	
			this.setOrderNumber(
					Category.orderController.getNextInsertOrder(
				con, this.getParentId()
					)
			);					
			// get the next insert id	
			this.setId( DBHelper.getNextInsertId( 
					con, 
					this.getTableId() + "_id_seq" ) 
			);
			PreparedStatement stmt = null;
			stmt = con.prepareStatement( insertSql );
			stmt = this.saveIntoStatement( stmt );
			stmt.execute();
			// 	increment order of all siblings
			logger.debug("-");
			return this.getId();
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		}    	
    }

    public void update(Connection con) throws CriticalException {
        DBHelper.updateDbObject(con, this);
    }

    public void delete(Connection con) throws CriticalException {
        DBHelper.deleteObject(con, tableId, id, logger);
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException
    {
        logger.debug("+");
        stmt.setObject(1, id);
        stmt.setString(2, name);
        stmt.setObject(3, parentId);
        stmt.setObject(4, langId);
        stmt.setObject(5, mcFolderId);
        stmt.setObject(6, orderNumber);
        stmt.setObject(7, Env.getSiteId());
        logger.debug("-");
        return stmt;
    }

    public static Category findById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        Category category = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                category = new Category();
                category.load(rs);
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
        return category;
    }

    public static Category getById(Connection conn, Long id)
       throws CriticalException
    {
        logger.debug("+");
        Category category = findById(conn, id);
        if (category == null)
            throw new CriticalException("Category not found [" + id + ']');
        logger.debug("-");
        return category;
    }

    public String toString() {
        logger.debug("+");
        StringBuffer sb = new StringBuffer("Category [");
        sb.append("id=");
        sb.append(id);
        sb.append(" name=");
        sb.append(name);
        sb.append(" parentId=");
        sb.append(parentId);
        sb.append(" langId=");
        sb.append(langId);
        sb.append(" mcFolderId=");
        sb.append(mcFolderId);
        sb.append("]");
        logger.debug("-");
        return sb.toString();
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
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the langId
     */
    public Long getLangId() {
        return langId;
    }
    /**
     * @param langId The langId to set.
     */
    public void setLangId(Long langId) {
        this.langId = langId;
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
     * @return
     * @throws CriticalException 
     */
    public Folder getFolder() throws CriticalException {
        return Repository.get().getFolder(this.getMcFolderId());
    }
    
    public Long getContainerId() throws CriticalException {
        return this.getFolder().getContainerId();
    }
    
	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.Orderable#moveUp(java.sql.Connection)
	 */
	public Long moveUp(Connection con) throws CriticalException {
		logger.debug("+-");
		return Category.orderController.moveUp(
			con, this.getId(), this.getOrderNumber(), this.getParentId()
		);
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.Orderable#moveDown(java.sql.Connection)
	 */
	public Long moveDown(Connection con) throws CriticalException {
		logger.debug("+-");
		return Category.orderController.moveDown(
			con, this.getId(), this.getOrderNumber(), this.getParentId()
		);
	}    

	/**
	 * @return
	 */
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}	

	/**
	 * @return
	 */
	public Long getOrderNumber() {
		return orderNumber;
	}	
}

