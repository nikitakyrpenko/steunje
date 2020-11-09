/*
 * @(#)DBHelper.java       @version	16.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
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
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.negeso.framework.Env;

/**
 * DBHelper contains database-specific operations to help write light-weight
 * db-oriented code.
 *
 * @version 	$Revision: 36$
 * @author 	 	Olexiy Strashko
 */
public class DBHelper {
	
	private static Logger logger = Logger.getLogger(DBHelper.class);

    private static SessionFactory sessionFactory;
    
    private static DataSource dataSource;

    private static boolean DATABASE_MONITOR = false;
    
	/**
	 * Retrieves next value for id.
     * 
	 * @param con
	 * @param sequence
	 * @throws CriticalException
	 */
	public static Long getNextInsertId(Connection con, String sequence)
		throws CriticalException {
		logger.debug("+");
		try {
			Statement stmt = con.createStatement();
			ResultSet rs =
				stmt.executeQuery("SELECT nextval('" + sequence + "')");
			rs.next();
			long result = rs.getLong(1);
            close(rs);
            close(stmt);
			logger.debug("-");
			return new Long(result);
		} catch (SQLException ex) {
			logger.error("-", ex);
			throw new CriticalException(ex);
		}
	}
    
	/**
	 * Deletes object from database. SQL pattern: " DELETE FROM className WHERE id = ?".
	 *
	 *
	 * @param id - id of object to be deleted
	 * @param tableId table id of deleting object
	 * @throws CriticalException
	 */
	public static void deleteObject(String tableId, Long id, Logger log)
		throws CriticalException {
		Connection conn = null;
		try {
			conn = getConnection();
			deleteObject(conn, tableId, id, logger);
		} catch (SQLException ex) {
			logger.error("-", ex);
			throw new CriticalException(ex);
		} finally {
			close(conn);
		}
	}
    
	/**
	 * Deletes object from database. SQL pattern: " DELETE FROM className WHERE id = ?".
	 *
	 * @param con
	 * @param id - id of object to be deleted
	 * @param tableId table id of deleting object
	 * @throws CriticalException
	 */
	public static void deleteObject(
		Connection con,
		String tableId,
		Long id,
		Logger log)
		throws CriticalException {
		if (id == null) {
			logger.error("- Trying to delete record with NULL id");
			throw new CriticalException("Trying to delete record with NULL id");
		}
		PreparedStatement deleteStatement = null;
		String deleteStatementString =
			" DELETE FROM " + tableId + " WHERE id = ?";
		try {
			deleteStatement = con.prepareStatement(deleteStatementString);
			deleteStatement.setObject(1, id, Types.BIGINT);
			deleteStatement.execute();
		} catch (SQLException ex) {
			logger.error("-", ex);
			throw new CriticalException(ex);
		}
	}

	/**
	 * Deletes DbObject from database. SQL pattern: " DELETE FROM className WHERE id = ?".
	 *
	 * @param con
	 * @param dbObject dbobject to be deleted
	 * @throws CriticalException
	 */
	public static void deleteDbObject(
		Connection con,
		DbObject dbObject)
		throws CriticalException 
	{

		if (dbObject.getId() == null) {
			logger.error("- Trying to delete record with NULL id");
			throw new CriticalException("Trying to delete record with NULL id");
		}
		PreparedStatement deleteStatement = null;
		String deleteStatementString =
			" DELETE FROM " + dbObject.getTableId() + " WHERE id = ?";
		try {
			deleteStatement = con.prepareStatement(deleteStatementString);
			deleteStatement.setObject(1, dbObject.getId(), Types.BIGINT);
			deleteStatement.execute();
		} catch (SQLException ex) {
			logger.error("-", ex);
			throw new CriticalException(ex);
		}
	}

	/**
	 * 
	 * @param con
	 * @param dbObject
	 * @param idNumber
	 * @throws CriticalException
	 */
	public static void updateDbObject(
		Connection con,
		DbObject dbObject,
		int idNumber
	)
		throws CriticalException 
	{
		logger.debug("+");
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement( dbObject.getUpdateSql() );
			stmt = dbObject.saveIntoStatement( stmt );
			stmt.setLong( idNumber, dbObject.getId().longValue() );
			stmt.executeUpdate();
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		}
		finally {
			close(stmt);
		}
		logger.debug("-");
	}
    
	/**
	 * @param con
	 * @param dbObject
	 * @throws CriticalException
	 */
	public static void updateDbObject(
		Connection con,
		DbObject dbObject
	)
		throws CriticalException 
	{
		logger.debug("+");
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement( dbObject.getUpdateSql() );
			stmt = dbObject.saveIntoStatement( stmt );
			stmt.setLong( dbObject.getFieldCount() + 1, dbObject.getId().longValue() );
			stmt.executeUpdate();
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		} finally {
			close(stmt);
		}
		logger.debug("-");
	}
	
	/**
	 * @param con
	 * @param dbObject
	 * @return id of the new dbobject
	 * @throws CriticalException
	 */
	public static Long insertDbObject( 
		Connection con,
		DbObject dbObject
	) 
		throws CriticalException 
	{
		logger.debug( "+" );
		PreparedStatement stmt = null;
		try {
			// get the next insert order	
			dbObject.setId(
                    getNextInsertId(con, dbObject.getTableId() + "_id_seq") );
			stmt = con.prepareStatement( dbObject.getInsertSql() );
			stmt = dbObject.saveIntoStatement( stmt );
			stmt.execute();
			logger.debug("-");
			return dbObject.getId();
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		} finally {
			close(stmt);
		}
	}
    
    /**
     * Finds object by id (if id == null - return null)
     * 
     * @param con
     * @param dbObject
     * @param id
     * @return DbObject or null
     * @throws CriticalException
     */
	public static DbObject findDbObjectById(
		Connection con, DbObject dbObject, Long id
        ) throws CriticalException
    {
		logger.debug("+");
		if ( id == null ) {
            logger.debug("- id is null");
			return null;
		}
		PreparedStatement stmt = null;
		try{
			stmt = con.prepareStatement(dbObject.getFindByIdSql());
            stmt.setLong(1, id.longValue());  
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
    			logger.debug("- not found");
                return null;
            }
            dbObject.load(rs);
			logger.debug("-");
			return dbObject;
		} catch(SQLException e){
			logger.error("- error", e);
			throw new CriticalException(e);
		} finally{
			close(stmt);
		}
	}
	
	public static DbObject getDbObjectById(
		Connection con,
        DbObject dbObject,
        Long id
	    ) throws CriticalException, ObjectNotFoundException
	{
		logger.debug("+");
		DbObject object = findDbObjectById(con, dbObject, id);
		if (object == null){
			throw new ObjectNotFoundException(
				"Object:" + dbObject.getTableId()+ 
				" not found by id:" + id.longValue()  
			);
		}
		return object;
	}
    
    /**
     * Rollback current transaction. Catches SQLException 
     * whitch uses connection, in finally part".
     *
     * @param conn      The <code>Connection</code> to close
     */
    public static void rollback(Connection conn)
    {
        if ( conn != null ){
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("-error (unable to rollback transaction)", e);
            }
        }
    }
    
    /**
     * Helper method to read numeric foreign or primary keys from db as Long
     * (value object).
     * 
     * @return positive Long value or null
     */
    public static Long makeLong(long value) {
        return value > 0 ? new Long(value) : null;
    }
    
    /** Closes a connection. */
    public static void close(Connection conn) {
        logger.debug("+ -");
    	if (DATABASE_MONITOR) {
        	DatabaseMonitor.getInstance().closeConnectionEvent(conn, Thread.currentThread());
    	}
        try {
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception e) {
            logger.error("cannot close connection", e);
        }
    }
    
    /** Closes a statement. */
    public static void close(Statement stmt) {
        logger.debug("+ -");
        if (stmt != null) {
            try { stmt.close(); }
            catch (Exception e) { logger.error("cannot close statement", e); }
        }
    }
    
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
        close(rs);
        close(stmt);
		close(conn);
	}
    
    /** Closes a result set. */
    public static void close(ResultSet rst) {
    	close(rst, "not specified");
    }
    

    /** Closes a result set. */
    public static void close(ResultSet rst, String query) {
        logger.debug("+ -");
        if (rst != null) {
            try { rst.close(); }
            catch (Exception e) {
				logger.error("cannot close ResultSet", e);
				logger.error("error query : " + query );
			}
        }
    }
    
    /** Close a session */
	public static void close(Session session) {
        logger.debug("+ -");
		if ( session != null) {
    		try { session.close(); }
            catch(Exception e){ logger.error("cannot close session", e); }
        }
	}
    
    /** Not to be instantiated. */
    private DBHelper() { /* Prevent instantiation of the utility class */ }
    
    /** Works with legacy datasource. Do not call it from Spring-managed beans. */
    public static JdbcTemplate getJdbcTemplate() { return new JdbcTemplate(dataSource); }

    /** Works with legacy datasource. Do not call it from Spring-managed beans. */
    public static HibernateTemplate getHibernateTemplate() { return new HibernateTemplate(sessionFactory); }
    
    /** Works with legacy datasource. Do not call it from Spring-managed beans. */
    public static Session openSession() { return sessionFactory.openSession(); } //TODO SB:

    /** Works with legacy datasource. Do not call it from Spring-managed beans. */
    public static Connection getConnection() throws SQLException {
    	Connection conn = dataSource.getConnection();
    	if (DATABASE_MONITOR) {
        	DatabaseMonitor.getInstance().obtainConnectionEvent(conn, Thread.currentThread());
    	}
    	return conn;
    }
    
    public static void setSessionFactory(SessionFactory factory) { sessionFactory = factory; }
    
    //TODO delete this method
    public static SessionFactory getSessionFactory() { return sessionFactory; }
    
    public static void setDataSource(DataSource ds) { dataSource = ds; }

    public static void startDatabaseMonitor() {
    	logger.debug("+");
    	DatabaseMonitor dbMonitor = null;
		try {
    	// switch on database monitor
			DATABASE_MONITOR = Boolean.valueOf(Env.getProperty("database.monitor.enabled", "false"));
			if (DATABASE_MONITOR) {
    			// set interval value from configuration. When the value isn't
    			// defined in configuration or wrong number format then
    			// default interval value is used.
				dbMonitor = DatabaseMonitor.getInstance();
    			int interval = Integer.valueOf(Env.getProperty("database.monitor.log.interval"));
    			dbMonitor.setInterval(interval);
   				dbMonitor.requestStart();
			}
    		} catch (Exception e) {
    		DATABASE_MONITOR = false;
    		if (dbMonitor != null) {
    			dbMonitor.requestStop();
    		}
    	}
    	logger.debug("-");
    }    
    
    public static void stopDatabaseMonitor() {
    	logger.debug("+");
    	// switch off database monitor
    	if (DATABASE_MONITOR) {
        	DATABASE_MONITOR = false;
        	DatabaseMonitor.getInstance().requestStop();
    	}
    	logger.debug("-");
    }

}