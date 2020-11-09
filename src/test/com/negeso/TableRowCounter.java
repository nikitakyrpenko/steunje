/*
 * @(#)$Id: TableRowCounter.java,v 1.0, 2007-01-10 13:29:33Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;

import com.negeso.framework.domain.DBHelper;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class TableRowCounter {
	// for checking count of context XML nodes
	public static String getRowCount(String tableName)  throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT count(*) FROM " + tableName);
			Assert.assertNotNull(rs.next());
			return Integer.toString(rs.getInt(1));
		} finally {
			DBHelper.close(rs, stmt, conn);
		}
	}

}

