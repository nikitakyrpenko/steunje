/*
 * @(#)$DocumentEntry.java$
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
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.OrderControlHelper;
import com.negeso.framework.list.generators.XmlHelper;


/**
 * @author Sergiy Oliynyk
 *
 */
public class DocumentEntry implements DbObject {

    private static Logger logger = Logger.getLogger(DocumentEntry.class);

    private static final String tableId = "dc_document";
    private static final int fieldCount = 10;

    public final static String selectFrom =
        "SELECT id, name, category_id, document_link, last_modified, owner," +
        " description, thumbnail_link, order_number from dc_document where site_id = ?";

    private final static String findByIdSql =
        "SELECT id, name, category_id, document_link, last_modified, owner," +
        " description, thumbnail_link, order_number from dc_document where id = ?";

    private final static String updateSql =
        "UPDATE dc_document set id = ?, name = ?, category_id = ?," +
        " document_link = ?, owner = ?, last_modified = ?, description = ?, thumbnail_link = ?, order_number = ?, site_id = ? " +
        " where id = ?";

    private final static String insertSql =
        "INSERT INTO dc_document" +
        " (id, name, category_id, document_link, owner, last_modified, " +
        " description, thumbnail_link, order_number, site_id)" +
        " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private Long id;
    private String name;
    private Long categoryId;
    private String documentLink;
    private String owner;
    private Timestamp lastModified;
    private String description;
    private String thumbnailLink;

    private Long orderNumber = new Long(0);

	//======================================================================
	// Helpers 
	//======================================================================
	// order controller
	private static OrderControlHelper orderController = 
		new OrderControlHelper(tableId, "category_id", "1=1");    
    
    /**
     * Constructors
     */
    public DocumentEntry() {}
    
    public DocumentEntry(Long id, String name, Long categoryId, String documentLink, 
        String owner, Timestamp lastModified, String description, String thumbnailLink, Long orderNumber)
    {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.documentLink = documentLink;
        this.owner = owner;
        this.lastModified = lastModified;
        this.description = description;
        this.thumbnailLink = thumbnailLink;
        this.orderNumber = orderNumber;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            name = rs.getString("name");
            categoryId = DBHelper.makeLong(rs.getLong("category_id"));
            documentLink = rs.getString("document_link");
            owner = rs.getString("owner");
            lastModified = rs.getTimestamp("last_modified");
            description = rs.getString("description");
            thumbnailLink = rs.getString("thumbnail_link");
            orderNumber = new Long(rs.getLong("order_number"));
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
					DocumentEntry.orderController.getNextInsertOrder(
				con, this.getCategoryId()
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
        stmt.setObject(3, categoryId);
        stmt.setString(4, documentLink);
        stmt.setString(5, owner);
        stmt.setTimestamp(6, lastModified);
        stmt.setString(7, description);
        stmt.setString(8, thumbnailLink);
        stmt.setObject(9, orderNumber);
        stmt.setObject(10, Env.getSiteId());
        
        logger.debug("-");
        return stmt;
    }

    public static DocumentEntry findById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        DocumentEntry entry = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                entry = new DocumentEntry();
                entry.load(rs);
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
        return entry;
    }

    public static DocumentEntry getById(Connection conn, Long id)
        throws CriticalException
    {
        DocumentEntry DocumentEntry = findById(conn, id);
        if (DocumentEntry == null)
            throw new CriticalException("DocumentEntry not found by id=" + id);
        return DocumentEntry;
    }

    public Element getElement(Document doc) {
        logger.debug("+");
        Element element = Env.createDomElement(doc, "document");
        element.setAttribute("id", String.valueOf(id));
        element.setAttribute("name", name);
        element.setAttribute("link", documentLink);
        element.setAttribute("owner", owner);
        element.setAttribute("description", description);
        element.setAttribute("thumbnail_link", thumbnailLink);
        XmlHelper.setDateAttribute(element, "lastModified", lastModified);
        logger.debug("-");
        return element;
    }

    public String toString() {
        logger.debug("+");
        StringBuffer sb = new StringBuffer("DocumentEntry [");
        sb.append("id=");
        sb.append(id);
        sb.append(" name=");
        sb.append(name);
        sb.append(" categoryId=");
        sb.append(categoryId);
        sb.append(" documentLink=");
        sb.append(documentLink);
        sb.append(" lastModified=");
        sb.append(lastModified);
        sb.append(" description=");
        sb.append(description);
        sb.append(" orderNumber=");
        sb.append(orderNumber);        
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
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return Returns the langId.
     */
    public Long getCategoryId() {
        return categoryId;
    }
    /**
     * @param langId The langId to set.
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    /**
     * @return Returns the lastModified.
     */
    public Timestamp getLastModified() {
        return lastModified;
    }
    /**
     * @param lastModified The lastModified to set.
     */
    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }
    /**
     * @return Returns the documentLink.
     */
    public String getDocumentLink() {
        return documentLink;
    }
    /**
     * @param documentLink The documentLink to set.
     */
    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
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
     * @return Returns the owner.
     */
    public String getOwner() {
        return owner;
    }
    /**
     * @param owner The owner to set.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get category domain
     * 
     * @param con
     * @return
     * @throws CriticalException 
     */
    public Category getCategory(Connection con) throws CriticalException {
        return Category.findById(con, this.getCategoryId());
    }

	public String getThumbnailLink() {
		return thumbnailLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}

    
	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.Orderable#moveUp(java.sql.Connection)
	 */
	public Long moveUp(Connection con) throws CriticalException {
		logger.debug("+-");
		return DocumentEntry.orderController.moveUp(
			con, this.getId(), this.getOrderNumber(), this.getCategoryId()
		);
	}
	
	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.Orderable#moveDown(java.sql.Connection)
	 */
	public Long moveDown(Connection con) throws CriticalException {
		logger.debug("+-");
		return DocumentEntry.orderController.moveDown(
			con, this.getId(), this.getOrderNumber(), this.getCategoryId()
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
