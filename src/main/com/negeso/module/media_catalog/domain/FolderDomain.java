/*
 * @(#)$Id: FolderDomain.java,v 1.7, 2005-06-17 14:51:56Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.ObjectNotFoundException;

/**
 *
 * Folder domain object. (Secured)
 * 
 * @version		$Revision: 8$
 * @author		Olexiy Strashko
 * 
 */
public class FolderDomain implements DbObject  {
    
	private static Logger logger = Logger.getLogger( FolderDomain.class );

	private static String tableId = "mc_folder";
    
	private static final String selectFromSql = 
	    " SELECT mc_folder.id, mc_folder.path, " +
        " mc_folder.container_id, mc_folder.site_id, " +
	    " containers.name AS container_name " +
	    " FROM " + tableId + 
		" LEFT JOIN containers ON containers.id = mc_folder.container_id "
	;

	private static final String findByIdSql = 
	    selectFromSql + 
		" WHERE mc_folder.id = ? "
	;

	private static final String findByPathSql = 
	    selectFromSql + 
		" WHERE lower(path) = ? AND mc_folder.site_id = ?"
	;

	private static final String insertSql =
		" INSERT INTO "	+ 
		tableId + 
		" (id, path, container_id, site_id) " +
		" VALUES (?, ?, ?, ?)"
	;

	private final static String updateSql =
		" UPDATE " +
		tableId +
		" SET id=?, path=?, container_id=?, site_id=? " +
		" WHERE id = ?"
	;

    private static int fieldCount = 4;
	
    private Long id = null;
    //private Long parentId = null;
    private String path = null;
    private Long containerId = null;
    private String containerName = null;
    private Long siteId = null;

    /**
     * Id'finder
     * 
     * @param con
     * @param id
     * @return
     * @throws CriticalException
     */
	public static FolderDomain findById( Connection con, Long id ) 
		throws CriticalException
	{
	    return ( FolderDomain ) DBHelper.findDbObjectById( con, new FolderDomain(), id );
	}
    
	/**
	 * Path'finder
	 * 
	 * @param con
	 * @param path
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws CriticalException
	 */
	public static FolderDomain findByPath(
		Connection con, String path
	) 
		throws CriticalException
	{
		logger.debug("+");
		FolderDomain obj = null;
		PreparedStatement stmt = null; 
		try{
			stmt = con.prepareStatement( FolderDomain.findByPathSql );
			stmt.setString(1, path.toLowerCase());  
            stmt.setLong(2, Env.getSiteId());  
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				obj = new FolderDomain();
				obj.load(rs);
			}
			else{
				return null;
			}
			rs.close();
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
		finally{
			DBHelper.close(stmt);
		}
		logger.debug("-");
		return obj;
	}

	public static FolderDomain deleteFolders(Connection con, String path) 
		throws CriticalException
	{
		FolderDomain obj = null;
		if (path == null) {
			logger.error("Folder not existen");
			return obj;
		}
		logger.debug("+");
		PreparedStatement stmt = null; 
		String deleteStmtString =			
			" DELETE from mc_folder WHERE path like ? AND site_id = ?";
		;
		try{			
			stmt = con.prepareStatement(deleteStmtString);
			stmt.setString(1, path.toLowerCase()+"%");
			stmt.setLong(2, Env.getSiteId());
			stmt.execute();
		}
		catch(SQLException e){
			logger.error("-", e);
			//throw new CriticalException(e);
		}
		finally{
			DBHelper.close(stmt);
		}
		logger.debug("-");
		return obj;
	}	
	/**
     * 
     */
    public FolderDomain() {
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
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
     */
    public Long load(ResultSet rs) throws CriticalException {
		logger.debug( "+" );
		try{
			this.id = new Long(rs.getLong("id"));
			//this.parentId = new Long(rs.getLong("parent_id"));
			this.path = rs.getString("path"); 
            this.siteId = DBHelper.makeLong(rs.getLong("site_id")); 
			this.containerId = DBHelper.makeLong(rs.getLong("container_id"));
			this.containerName = rs.getString("container_name"); 
			return this.id;
		}
		catch(SQLException e){
			throw new CriticalException(e);
		}
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException 
    {
		logger.debug( "+" );
		// id
		stmt.setLong( 1, this.getId().longValue() );
		// path
		stmt.setString( 2, this.getPath() );
		// containerId
		if ( this.getContainerId() == null ){
		    stmt.setNull( 3, Types.BIGINT );
		}
		else{
			stmt.setLong( 3, this.getContainerId().longValue() );
		}
        // site_id
        stmt.setLong( 4, Env.getSiteId() );

        logger.debug( "-" );
		return stmt;
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
		DBHelper.deleteObject(
				con, this.getTableId(), this.getId(), logger
		);
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    public String getTableId() {
        return FolderDomain.tableId;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    public String getFindByIdSql() {
        return FolderDomain.findByIdSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    public String getUpdateSql() {
        return FolderDomain.updateSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    public String getInsertSql() {
        return FolderDomain.insertSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    public int getFieldCount() {
        return FolderDomain.fieldCount;
    }

    /**
     * @return Returns the containerId.
     */
    public Long getContainerId() {
        return containerId;
    }
    /**
     * @param containerId The containerId to set.
     */
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path; 
    }
    
    /**
     * @param path The path to set.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return Returns the containerName.
     */
    public String getContainerName() {
        return containerName;
    }
    /**
     * @return Returns the siteId.
     */
    public Long getSiteId() {
        return siteId;
    }
}
