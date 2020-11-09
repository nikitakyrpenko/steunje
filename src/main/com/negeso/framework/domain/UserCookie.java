/*
 * @(#)Id: UserCookie.java, 25.09.2007 12:20:29, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;

/**
 * 
 * User's cookie properties
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class UserCookie {
	
	private static Logger logger = Logger.getLogger( User.class );

	private static final String tableId = "users_cookies";
	
	private static final String SQL_FIND_BY_UNIQ_ID = "SELECT * FROM " + tableId + " WHERE uniq_id = ?";

	private static final String SQL_INSERT = 
			"INSERT INTO " + tableId + "(" +
			"username," +
			"uniq_id," +
			"interface_language," +
			"last_login_date," +
			"site_id) " +
			"VALUES (?, ?, ?, ?, ?)";

	private static final String SQL_DELETE = "DELETE FROM " + tableId + " WHERE uniq_id = ?";
	
	/* user login */
    private String login = null;
    
    /* interface language */
    private String language;
    
    /* last login date */
    private Date lastLoginDate;
    
    /* unique identification number */
    private Long id_number;
    
    /* site id */
    private Long siteId = null;

	public UserCookie(String login, Long id_number, String lang, Date lastLoginDate, Long siteId){
		this.login = login;
		this.language = lang;
		this.id_number = id_number;
		this.lastLoginDate = lastLoginDate;
		this.siteId = siteId;
	}
	
	
	/**
     * Finds User by uniq_id. If object is not found - throws ObjectNotFoundException
     *
     * @param uniq_id
     * @return User if object is found, null - if object not found
     * @throws SQLException
     */
    public static UserCookie findById( Long uniq_id )
        throws CriticalException, ObjectNotFoundException
    {
        logger.debug( "+" );
        UserCookie result = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement( SQL_FIND_BY_UNIQ_ID );
            findStatement.setLong( 1, uniq_id.longValue() );
            rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                // Object not found throw exception
                throw new ObjectNotFoundException( "Object: " + UserCookie.tableId +
                    " not found by id: " + uniq_id );
            }
            result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.error( "-", ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }
    
    /**
     * Loads UserCookie by RecordSet.
     *
     * @see Env
     * @param rs
     * @return
     * @throws SQLException
     */
    public static UserCookie load( ResultSet rs )
        throws CriticalException
    {
        logger.debug( "+" );
        try {
            UserCookie result = new UserCookie(
                rs.getString("username"),
                rs.getLong("uniq_id"),
                rs.getString("interface_language"),
                rs.getTimestamp("last_login_date"),
                rs.getLong("site_id"));
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
    }
    
    public void insert() {
        logger.debug( "+" );
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            insert(conn);
        } catch ( SQLException ex ) {
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }
    
    /**
     * Insert UserCookie with transaction support
     * 
     * @param con
     * @return
     * @throws CriticalException
     */
    public void insert(Connection con) {
	    logger.debug( "+" );
	    PreparedStatement stmt = null;
	    try {
	        stmt = con.prepareStatement( SQL_INSERT );
	        stmt.setString(1, this.getLogin());
	        stmt.setLong(2, this.getUniqId());
	        stmt.setString(3, this.getInterfaceLanguage());
	        stmt.setTimestamp(4, this.getLastLoginDate() == null ? null : new Timestamp (this.getLastLoginDate().getTime()));
	        stmt.setLong(5, this.getSiteId());
	        stmt.execute();
		    logger.debug( "-" );
	    } catch ( SQLException ex ) {
	        logger.debug("- SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	        DBHelper.close( stmt );
	    }
	}
    
    /**
     * Delete UserCookie by uniq_number
     * 
     * @param con
     * @return
     * @throws CriticalException
     */
    public void delete() {
	    logger.debug( "+" );
	    Connection con = null;
	    PreparedStatement stmt = null;
	    try {
	    	con = DBHelper.getConnection();
	        stmt = con.prepareStatement( SQL_DELETE );
	        stmt.setLong(1, this.getUniqId());
	        stmt.execute();
		    logger.debug( "-" );
	    } catch ( SQLException ex ) {
	        logger.debug("- SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	        DBHelper.close( stmt );
	        DBHelper.close( con );
	    }
	}
    
    public void generateNewIdNumber(){
    	Random random = new Random();
    	this.id_number = Math.abs(random.nextLong());
    }
    
    public Long getSiteId() {
        return siteId;
    }    
    
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
    
    public String getLogin() {
        return login;
    }    
    
    public void setLogin(String login) {
        this.login = login;
    }
	
    public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	
	public Long getUniqId(){
		return this.id_number;
	}
	
	public String getInterfaceLanguage() {
        return language;
    }    
    
    public void setInterfaceLanguage(String lang) {
        this.language = lang;
    }
    
    public String toString () {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
}
