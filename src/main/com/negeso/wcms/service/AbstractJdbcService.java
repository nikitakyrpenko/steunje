/*
 * @(#)$Id: AbstractJdbcService.java,v 1.0, 2007-02-27 08:10:36Z, Olexiy Strashko$
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.wcms.service;

import java.sql.Connection;

/**
 * 
 * Abstract service, which use Jdbc connection for DB Layer access.
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 1$
 *
 */
public class AbstractJdbcService {
	
	Connection connection;

	/**
	 * @return Returns the connection.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection The connection to set.
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
