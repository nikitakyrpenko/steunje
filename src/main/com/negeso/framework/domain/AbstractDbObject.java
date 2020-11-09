/*
 * @(#)$Id: AbstractDbObject.java,v 1.0, 2005-03-22 09:23:19Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
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

/**
 *
 * Abstract db object. Simplifies DbObject usage 
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 1$
 */

abstract public class AbstractDbObject implements DbObject {

    
    private Long id = null;
    
    /**
     * 
     */
    public AbstractDbObject() {
        super();
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getId()
     */
    public Long getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#setId(java.lang.Long)
     */
    public void setId(Long newId) {
        this.id = newId;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#insert(java.sql.Connection)
     */
    public Long insert(Connection con) throws CriticalException {
        return DBHelper.insertDbObject(con, this);
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#update(java.sql.Connection)
     */
    public void update(Connection con) throws CriticalException {
        DBHelper.updateDbObject(con, this);
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#delete(java.sql.Connection)
     */
    public void delete(Connection con) throws CriticalException {
        DBHelper.deleteDbObject(con, this);
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
     */
    abstract public Long load(ResultSet rs) throws CriticalException;

    
    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    abstract public PreparedStatement saveIntoStatement(PreparedStatement stmt) 
    	throws SQLException
    ;


    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    abstract public String getTableId();

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    abstract public String getFindByIdSql();

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    abstract public String getUpdateSql();

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    abstract public String getInsertSql();

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    abstract public int getFieldCount();
}
