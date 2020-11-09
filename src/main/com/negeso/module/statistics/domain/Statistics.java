/*
 * @(#)$Statistics.java$
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
import java.sql.Statement;
import java.sql.Date;
import java.sql.Time;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;

/**
 * 
 * 
 * @version		$Revision: 1$
 * @author   		
 *
 */
public class Statistics implements DbObject {

	private static Logger logger = Logger.getLogger(Statistics.class);
	
	private static final String tableId = "stat_statistics";
    private static final int fieldCount = 9;
    
    public final static String selectFrom =
        "SELECT id, user_id, user_login, user_ip, hit_date, hit_time, page_name, referer, event, site_id" +
        " from " + tableId;
    
    private final static String findByIdSql =
        selectFrom + " where id = ?";
    
    private final static String updateSql =
        "UPDATE "+ tableId +" SET " +
        " id = ?,  user_id = ?, user_login = ?, user_ip = ?, hit_date = ?, hit_time = ?, " +
        " page_name = ?, referer = ?,  event = ? , site_id = ?" +
        "WHERE id = ?";
    
    private final static String updateSqlFilename =
        "UPDATE "+ tableId +" SET page_name = ? WHERE page_name = ?";
    
    private final static String insertSql =
        "INSERT INTO " + tableId +
        " (id, user_id, user_login, user_ip, hit_date, hit_time, page_name, referer, event, site_id) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String EVENT_PAGE_VISIT = "page_visit";

	private Long id;
    private Long userId;
    private String userLogin;
    private String userIp;
    private Date hitDate;
    private Time hitTime;
    private String pageName;
    private String referer;
    private String event;
    private Long siteId;
    
    /**
     * Constructors
     */
    public Statistics() {}
    
    public Statistics (Long id, Long userId, String userLogin, String userIp, Date hitDate, 
    		Time hitTime, String pageName, String referer, String event, Long siteId )  
    {
        this.id = id;
        this.userId = userId;
        this.userLogin = userLogin;
        this.userIp = userIp;
        this.hitDate = hitDate;
        this.hitTime = hitTime;
        this.pageName = pageName;
        this.referer = referer;
        this.event = event;
        this.siteId = siteId;        
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            userId = DBHelper.makeLong(rs.getLong("user_id"));
            userLogin = rs.getString("user_login");
            userIp = rs.getString("user_ip");
            hitDate = rs.getDate("hit_date");
            hitTime = rs.getTime("hit_time");
            pageName = rs.getString("page_name");
            referer = rs.getString("referer");
            event = rs.getString("event");
            siteId = rs.getLong("site_id");            
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
        try
        {
            PreparedStatement statement = con.prepareStatement(updateSql);
            saveIntoStatement(statement);
            statement.setLong(8, this.id.longValue());
            statement.execute();
            statement.close();
        }
        catch(SQLException e)
        {
            throw new CriticalException(e);
        }
    }

    

    public void updateFilename(String filenameOld) {
        logger.debug("+");        
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();            
            PreparedStatement statement = conn.prepareStatement(updateSqlFilename);            
            statement.setString(1, this.pageName);
            statement.setString(2, filenameOld);
            statement.execute();
            statement.close();
            logger.debug("-");            
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }   
    
    public void delete(Connection con) throws CriticalException {
    	Statement updateStatement = null;
        try
        {
            updateStatement = con.createStatement();
            updateStatement.executeUpdate("DELETE FROM " + tableId +
                    " WHERE id='" + this.id + "' ");
        }catch (SQLException e){
            throw new CriticalException(e);
        }finally{
        	DBHelper.close(updateStatement);
        }
    }
    
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
    	throws SQLException
    {
	    logger.debug("+");
	    stmt.setObject(1, id);
	    stmt.setObject(2, userId);
	    stmt.setString(3, userLogin);
	    stmt.setString(4, userIp);
	    stmt.setObject(5, hitDate);
	    stmt.setObject(6, hitTime);
	    stmt.setString(7, pageName);
	    stmt.setString(8, referer);
	    stmt.setString(9, event);
	    stmt.setObject(10,siteId);	    
	    logger.debug("-");
	    return stmt;
	}
    
    public String toString() {
        logger.debug("+");
        StringBuffer sb = new StringBuffer("Statistics [");
        sb.append("id=");
        sb.append(id);
        sb.append(" user_id=");
        sb.append(userId);
        sb.append(" user_login=");
        sb.append(userLogin);
        sb.append(" user_ip=");
        sb.append(userIp);
        sb.append(" hit_date=");
        sb.append(hitDate);
        sb.append(" hit_time=");
        sb.append(hitTime);
        sb.append(" page_name=");
        sb.append(pageName);
        sb.append(" referer=");
        sb.append(referer);
        sb.append(" event=");
        sb.append(event);
        sb.append(" site_id=");
        sb.append(siteId);
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
     * @return Returns the user_id.
     */
    public Long getUserId() {
        return userId;
    }
    /**
     * @param user_id The user_id to set.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * @return Returns the user_login.
     */
    public String getUserLogin() {
        return userLogin;
    }
    /**
     * @param user_login The user_login to set.
     */
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    /**
     * @return Returns the user_ip.
     */
    public String getUserIp() {
        return userIp;
    }
    /**
     * @param user_ip The user_ip to set.
     */
    public void setUser_ip(String userIp) {
        this.userIp = userIp;
    }

    /**
     * @return Returns the hit_date.
     */
    public Date getHitDate() {
        return hitDate;
    }
    /**
     * @param hit_date The hit_date to set.
     */
    public void setHitDate(Date hitDate) {
        this.hitDate = hitDate;
    }

    /**
     * @return Returns the hit_time.
     */
    public Time getHitTime() {
        return hitTime;
    }
    /**
     * @param hit_time The hit_time to set.
     */
    public void setHitTime(Time hitTime) {
        this.hitTime = hitTime;
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
        
    /**
     * @return Returns the referer.
     */
    public String getReferer() {
        return referer;
    }
    /**
     * @param referer The referer to set.
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }

    /**
     * @return Returns the event.
     */
    public String getEvent() {
        return event;
    }
    /**
     * @param event The event to set.
     */
    public void getEvent(String event) {
        this.event = event;
    }

	public void setEvent(String event) {
		this.event = event;
	}

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

}