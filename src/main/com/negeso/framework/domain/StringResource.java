/*
 * @(#)StringResource.java       @version	18.05.2004
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

import org.apache.log4j.Logger;


import com.negeso.framework.Env;

/**
 * String resource processing.
 *
 * @version 	18.05.2004
 * @author 		Olexiy.Strashko
 */
public class StringResource {
	private static Logger logger = Logger.getLogger( StringResource.class );
	protected static String tableId = "string_resource";

	private final static String findByIdSql =
		" SELECT id, lang_id, text" +
		"   FROM " + StringResource.tableId +
		"   WHERE id = ? "
	;

	private final static String updateTextSql =
		" UPDATE " + StringResource.tableId +
		"   SET text = ? WHERE id = ? AND lang_id = ?"
	;

	private final static String insertTextSql =
		" INSERT INTO " + StringResource.tableId +
		" 	(id, lang_id, text)  VALUES (?, ?, ?)";
	;

	private final static String updateResourceIdSql =
		" UPDATE " + StringResource.tableId +
		"   SET id = ? WHERE id = ?"
	;

	private static StringResource instance = new StringResource(); 

	/**
	 * @param id
	 */
	private StringResource() {
	}
	
	public static StringResource get(){
		return instance;
	}

	/**
	 * 
	 * @param resourceId
	 * @param languageId
	 * @return
	 */
	public String getText(String resourceId, int languageId) 
		throws CriticalException
	{
		logger.debug( "+" );
		Connection conn = null;
		try {
			logger.debug("+ -");
            conn = DBHelper.getConnection();
			PreparedStatement findStatement = null;
			findStatement = conn.prepareStatement( findByIdSql );
			findStatement.setString( 1, resourceId );
			ResultSet rs = findStatement.executeQuery();
			while ( rs.next() ) {
				if (rs.getInt("lang_id") == languageId){
					logger.debug( "-" );
					return rs.getString("text");
				}
			}
			logger.debug( "- Message not found" );
			return resourceId;
		} catch ( SQLException ex ) {
			logger.error( ex, ex );
			throw new CriticalException( ex );
		}
		finally {
			DBHelper.close( conn );
		}
	}

	/**
	 * 
	 * @param resourceId
	 * @param languageId
	 * @return		oldValue if text was updated, null - if text was inserted.  
	 * @throws CriticalException
	 */
	public String setText(String resourceId, int languageId, String newValue) 
		throws CriticalException
	{
		logger.debug( "+" );
		
		Connection conn = null;
		try {
			logger.debug("+ -");
            conn = DBHelper.getConnection();
			PreparedStatement findStatement = null;
			findStatement = conn.prepareStatement( findByIdSql );
			findStatement.setString( 1, resourceId );
			ResultSet rs = findStatement.executeQuery();
			while ( rs.next() ) {
				if (rs.getInt("lang_id") == languageId){
					logger.debug( "-" );
					// UPDATE MESSAGE;
					PreparedStatement updateStmt = null;
					updateStmt = conn.prepareStatement( updateTextSql );
					updateStmt.setString( 1, newValue);
					updateStmt.setString( 2, resourceId);
					updateStmt.setInt( 3, languageId);
					updateStmt.executeUpdate();
					return rs.getString("text");
				}
			}
			logger.debug( "-" );
			// INSERT MESSAGE;
			PreparedStatement insertStmt = null;
			insertStmt = conn.prepareStatement( insertTextSql );
			insertStmt.setString( 1, resourceId);
			insertStmt.setInt( 2, languageId);
			insertStmt.setString( 3, newValue);
			insertStmt.executeUpdate();
			rs.close();
			logger.debug( "-" );
			return null;
		} catch ( SQLException ex ) {
			logger.debug( ex, ex );
			throw new CriticalException( ex );
		}
		finally {
			DBHelper.close( conn );
		}
	}

	/**
	 * 
	 * 
	 * @param oldResourceId
	 * @param newResourceId
	 * @return
	 * @throws CriticalException
	 */	
	public String updateResourceId(
		String oldResourceId, String newResourceId
	) 
		throws CriticalException
	{
		logger.debug( "+" );

		Connection conn = null;
		try {
			logger.debug("+ -");
            conn = DBHelper.getConnection();
			PreparedStatement updateStmt = conn.prepareStatement( 
				updateResourceIdSql 
			);
			updateStmt.setString( 1, newResourceId );
			updateStmt.setString( 2, oldResourceId );
			int updated = updateStmt.executeUpdate();
			logger.debug( "- updated " + updated );
			return null;
		} 
		catch ( SQLException ex ) {
			logger.debug( ex, ex );
			throw new CriticalException( ex );
		}
		finally {
			DBHelper.close( conn );
		}
	}
}
