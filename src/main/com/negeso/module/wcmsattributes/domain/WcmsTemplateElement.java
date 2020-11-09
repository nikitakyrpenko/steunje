/*
 * Created on 12.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DbObject;

/**
 * @author OLyebyedyev
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WcmsTemplateElement implements DbObject {
    private static Logger logger = Logger.getLogger(WcmsTemplateElement.class);
    
    Long id = null;
    Long templId = null;
    String typeName = null;
    Integer orderNum = null; 

    WcmsAttributeType elementType = null;
    Integer typeId = null;
    String attributeName = null;
    String helpText = null;
    
    
    /**
     * 
     */
    public WcmsTemplateElement() {
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
        throw new UnsupportedOperationException("This method is not implemented");
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
     */
    public Long load(ResultSet rs) throws CriticalException {
        throw new UnsupportedOperationException("This method is not implemented");

    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#insert(java.sql.Connection)
     */
    public Long insert(Connection con) throws CriticalException {
        // 
        throw new UnsupportedOperationException("This method is not implemented");
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#update(java.sql.Connection)
     */
    public void update(Connection con) throws CriticalException {
        throw new UnsupportedOperationException("This method is not implemented");

    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#delete(java.sql.Connection)
     */
    public void delete(Connection con) throws CriticalException {
        throw new UnsupportedOperationException("This method is not implemented");        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    public String getTableId() {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    public String getFindByIdSql() {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    public String getUpdateSql() {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    public String getInsertSql() {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    public int getFieldCount() {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException {
        throw new UnsupportedOperationException("This method is not implemented");
    }
}
