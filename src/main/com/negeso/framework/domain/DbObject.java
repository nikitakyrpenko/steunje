/*
 * @(#)$Id: DbObject.java,v 1.4, 2004-12-10 14:29:57Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import com.negeso.framework.dao.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * Database object interface. 
 * 
 * @version		$Revision: 5$
 * @author		Olexiy Strashko
 * 
 */
public interface DbObject extends Entity {
    public Long load(ResultSet rs) throws CriticalException;
	public PreparedStatement saveIntoStatement( PreparedStatement stmt ) throws SQLException; 
    public Long insert(Connection con) throws CriticalException;
    public void update(Connection con) throws CriticalException;
    public void delete(Connection con) throws CriticalException;
    public String getTableId();
    public String getFindByIdSql();
    public String getUpdateSql();
    public String getInsertSql();
    public int getFieldCount();
}
