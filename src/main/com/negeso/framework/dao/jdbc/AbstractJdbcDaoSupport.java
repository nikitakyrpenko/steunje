/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.dao.jdbc;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
abstract public class AbstractJdbcDaoSupport extends JdbcDaoSupport {

	private DataFieldMaxValueIncrementer incrementer;
	
	protected DataFieldMaxValueIncrementer getIncrementer() {
		return this.incrementer;
	}

	public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
		this.incrementer = incrementer;
	}
	
}

