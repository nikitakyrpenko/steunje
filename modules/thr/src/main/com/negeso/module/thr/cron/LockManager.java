/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.thr.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class LockManager {
	
	private static final Logger logger = Logger.getLogger(LockManager.class);
	
	private static int hoursInterval = 1; 
	
	private static final String LOCK_INSERT_STMT =
	    "INSERT INTO core_lock (OBJECT_TYPE, OBJECT_KEY) VALUES (?, ?)";
	
	private static final String RELEASE_LOCK_STMT =
	    "DELETE FROM core_lock WHERE OBJECT_TYPE = ?";
	
	private static final String RELEASE_ERROR = "Failed release %s lock: ";
	
	public boolean getLock(String objectType, long objectKey) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean gotLock = false;

		try {
			conn = DBHelper.getConnection();
			releaseOldLock(conn, objectType);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(LOCK_INSERT_STMT);
			pstmt.setString(1, objectType);
			pstmt.setLong(2, objectKey);

			try {
				pstmt.executeUpdate();
				gotLock = true;
			} catch (SQLException ex) {
				; // a SQLException means a PK violation, which means an existing lock
			}
			conn.commit();
		} catch (SQLException e) {
			logger.error(e);
			DBHelper.rollback(conn);
		} finally {	
			DBHelper.close(pstmt);
			DBHelper.close(conn);
		}
	    return gotLock;
	}
	
	public boolean releaseLock(String objectType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBHelper.getConnection();
			pstmt = conn.prepareStatement(RELEASE_LOCK_STMT);
			pstmt.setString(1, objectType);
			int count = pstmt.executeUpdate();
			return (count > 0); // if we deleted anything, we released a lock.
		} catch (SQLException e) {
			logger.error(String.format(RELEASE_ERROR, objectType), e);
		} finally {
			DBHelper.close(pstmt);
			DBHelper.close(conn);
		}
		return false;
	}
	
	public void releaseOldLock(Connection conn, String objectType) {			
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(RELEASE_LOCK_STMT + " AND lock_time < now() - interval '" + hoursInterval + " hours'");
			pstmt.setString(1, objectType);			
			pstmt.execute();			
		} catch (SQLException e) {
			logger.error(String.format(RELEASE_ERROR, objectType), e);
		} finally {
			DBHelper.close(rs);
			DBHelper.close(pstmt);				
		}			
	}
	
}

