/*
 * @(#)$Counter.java$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */


package com.negeso.module.statistics.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;


public class Counter implements DbObject {

	private static Logger logger = Logger.getLogger(Counter.class);
	
	private static final String tableId = "stat_counter";
    private static final int 	fieldCount = 4;    
    
    public final static String selectFrom =
        "SELECT id, page_id, is_counter, page_name" +
        " from " + tableId;
    
    private final static String findByIdSql =
        selectFrom + " where id = ?";
    
    private final static String findByPageNameSql =
        selectFrom + " WHERE page_name= ?";
    
    private final static String findByIdNameSql =
        selectFrom + " WHERE page_id= ?";
    
    private final static String updateSql =
        "UPDATE "+ tableId +" SET " +
        " id = ?, page_id = ?, is_counter = ?, page_name = ? " +
        "WHERE id = ?";
    
    private final static String insertSql =
        "INSERT INTO " + tableId +
        " (id, page_id, is_counter, page_name) " +
        "VALUES (?, ?, ?, ?)";

    private Long id;
    private Long pageId;
    private boolean isActive = false;
    private String pageName;
    
    /**
     * Constructors
     */
    public Counter() {}
    
    public Counter(
    		Long id, Long pageId, boolean isActive, String pageName
    )
    {
        this.id = id;
        this.pageId = pageId;
        this.isActive = isActive;
        this.pageName = pageName;
    }
    
    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            pageId = DBHelper.makeLong(rs.getLong("page_id"));
            isActive = rs.getBoolean("is_counter");
            pageName = rs.getString("page_name");
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

    public static void deleteById(Connection con, Long counterId) throws CriticalException {
        DBHelper.deleteObject(con, tableId, counterId, logger);
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
    	throws SQLException
    {
	    logger.debug("+");
	    stmt.setObject(1, id);
	    stmt.setObject(2, pageId);
	    stmt.setBoolean(3, isActive);
	    stmt.setString(4, pageName);
	    logger.debug("-");
	    return stmt;
	}
    
    public static Counter findById(Connection conn, Long id)
    throws CriticalException
	{
	    logger.debug("+");
	    PreparedStatement stmt = null;
	    Counter counter = null;
	    try {
	        stmt = conn.prepareStatement(findByIdSql);
	        stmt.setObject(1, id);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	        	counter = new Counter();
	        	counter.load(rs);
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
	    return counter;
	}

    public static Counter getById(Connection conn, Long id)
    throws CriticalException
	 {
	     logger.debug("+");
	     Counter counter = findById(conn, id);
	     if (counter == null)
	         throw new CriticalException("Counter not found [" + id + ']');
	     logger.debug("-");
	     return counter;
	 }
    
    public static Counter findByPageName(Connection conn, String pageName)
    	throws CriticalException
	{
	    logger.debug("+");
	    PreparedStatement stmt = null;
	    Counter counter = null;
	    try {
	        stmt = conn.prepareStatement(findByPageNameSql);
	        stmt.setObject(1, pageName);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	        	counter = new Counter();
	        	counter.load(rs);
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
	    return counter;
	}
    
    public static Counter findByPageId(Connection conn, Long pageId)
	throws CriticalException{
    logger.debug("+");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Counter counter = null;
    try {
        stmt = conn.prepareStatement(findByIdNameSql);
        stmt.setLong(1, pageId);
        rs = stmt.executeQuery();
        if (rs.next()) {
        	counter = new Counter();
        	counter.load(rs);
        }
    }
    catch (SQLException ex) {
        logger.error("- Throwing new CriticalException");
    }
    finally {
    	DBHelper.close(rs);
    	DBHelper.close(stmt);
    }
    logger.debug("-");
    return counter;
}

    public static Boolean isCounterActiveOnPage(Connection conn, Long pageId)
    	throws CriticalException
	{
		logger.debug("+");
		Counter counter = findByPageId(conn, pageId);

	    if (counter == null) {
	        throw new CriticalException("Statistics not found, pageId=" + pageId);
	    }
	    logger.debug("-");
	    if (counter.isActive()) {
	    	return true;
	    }
	    else {
	    	return false;
	    }
	}

    
    public String toString() {
        logger.debug("+");
        StringBuffer sb = new StringBuffer("Counter [");
        sb.append("id=");
        sb.append(id);
        sb.append(" page_id=");
        sb.append(pageId);
        sb.append(" is_counter=");
        sb.append(isActive);
        sb.append(" page_name=");
        sb.append(pageName);
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
    
    public String getFindByPageNameSql() {
        return findByPageNameSql;
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
     * @return Returns the pageId.
     */
    public Long getPageId() {
        return pageId;
    }
    /**
     * @param pageId The pageId to set.
     */
    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    /**
     * @return Returns the page_name.
     */
    public String getPageName() {
        return pageName;
    }
    /**
     * @param page_name The page_name to set.
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

	public void setActive(boolean active) {
		this.isActive = active;
	}
    
	public boolean isActive() {
        return isActive;
    }
}

