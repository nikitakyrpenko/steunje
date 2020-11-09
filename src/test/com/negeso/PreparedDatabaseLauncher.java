/*
 * @(#)$Id: PreparedDatabaseLauncher.java,v 1.0, 2007-01-10 13:29:33Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.negeso.framework.domain.DBHelper;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
public abstract class PreparedDatabaseLauncher {
	
	private static void executeQuery(String query) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			stmt.execute(query);
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(stmt);
			DBHelper.close(conn);
		}
	}
	
	public void runTest(String inQuery, String outQuery) throws Exception {
		executeQuery(inQuery);
		try {
			assertTest();
		} catch (Exception e) {
			throw e;
		} finally {
			executeQuery(outQuery);
		}
	}

	public void runTest(String query) throws Exception {
		executeQuery(query);
		try {
			assertTest();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void runTestAndClean(String query) throws Exception {
		
		try {
			assertTest();
		} catch (Exception e) {
			throw e;
		}finally{
			executeQuery(query);
		}
	}

	public void runTest(Class clazz, String inQueryFilename,
			String outQueryFilename) throws Exception {
		String inQuery = readQueryToString(clazz.getResourceAsStream(inQueryFilename));
		String outQuery = readQueryToString(clazz.getResourceAsStream(outQueryFilename));
		runTest(inQuery, outQuery);
	}
	
	private String readQueryToString(InputStream is) throws IOException {
		String s = null;
		try {
			int size = is.available();
			byte[] buf = new byte[size];
			is.read(buf);
			
			s = new String(buf);
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		return s;
	}
	
	protected abstract void assertTest() throws Exception;
	
}
