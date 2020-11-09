/*
 * @(#)Id: TestHelper.java, 13.09.2007 11:11:33, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import com.negeso.framework.domain.DBHelper;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class TestPropertiesHelper {
	
	public static String DEFAULT_LANGUAGE = "en";
	
	private static LinkedHashMap<String, String> properties;
	
	private static String LANGUAGE_DB_NAME = "DEFAULT_LANGUAGE";
	
	private static TestPropertiesHelper instance = null;
	
	protected TestPropertiesHelper(){}
	
	public static TestPropertiesHelper getInstance(){
		if (instance == null){
			instance = new TestPropertiesHelper();
			instance.init();
		}
		return instance;
	}
	
	private static void init(){
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			String query = "SELECT * FROM core_property WHERE core_property.name = ?";
			stat = conn.prepareStatement(query);
			stat.setString(1, LANGUAGE_DB_NAME);
			rs = stat.executeQuery();
			rs.next();
			DEFAULT_LANGUAGE = rs.getString("value");
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			DBHelper.close(rs, stat, conn);
		}
	}
}
