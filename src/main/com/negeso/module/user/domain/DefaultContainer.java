/*
 * @(#)$Id: DefaultContainer.java,v 1.1, 2007-03-22 17:27:28Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.user.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

/** DefaultContainer - singleton
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class DefaultContainer extends Container {
	
	private static Logger logger = Logger.getLogger(DefaultContainer.class);
	
	private static DefaultContainer instance;
	
	private final static String GET_DEFAULT_CONTAINER = "SELECT * FROM containers WHERE is_default = true" +
			" AND site_id = ? LIMIT 1"; 
	
	public synchronized static DefaultContainer getInstance() {
		if (instance == null) {
			instance = new DefaultContainer();
			try {
				instance.loadMe();
	    	} catch (Exception e) {
				logger.error("Can't load default container");
				throw new CriticalException(e);
			} 
		} 
		return instance;
	}
	
	private void loadMe() throws SQLException {
		Connection connection = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			connection = DBHelper.getConnection();
			st = connection.prepareStatement(GET_DEFAULT_CONTAINER);
			st.setLong(1, Env.getSiteId());
			rs = st.executeQuery();
			Validate.isTrue(rs.next());
			setId(new Long(rs.getLong("id")));
			setName(rs.getString("name"));
			setSiteId(rs.getLong("site_id")); 
		} finally {
			DBHelper.close(rs);
			DBHelper.close(st);
			DBHelper.close(connection);
		}
	}
	
	public static boolean isMyId(Long containerId) {
		return getInstance().getId().equals(containerId);
	}

}

