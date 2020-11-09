/*
 * @(#)Container.java  Created on 24.05.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainObject;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.Xbuilder;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class Container extends DomainObject {

    protected static String tableId = "containers";
    protected static String sequenceId = "containers_id_seq";

    private final static String SQL_FIND_BY_ID =
        " SELECT * FROM containers WHERE id = ? ";
    
    private final static String SQL_UPDATE =
        " UPDATE containers " +
        "   set id = ?, name = ?, site_id = ? " +
        " WHERE id = ?";
    
    private final static String SQL_INSERT =
        " INSERT INTO containers(id, name, site_id) " +
        " VALUES (?, ?, ?)";
    
    private final static String SELECT_ALL_ORDER_BY_ID =
        " SELECT * FROM " + tableId + " ORDER BY id ";
    
    /** Container name */
    private String name = null;
    
    
    /**
     * The Subsite which the container belongs to. Can be <code>null</code>
     * for simple sites (with no sites).
     */
    private Long siteId;
    
    
    private static Logger logger = Logger.getLogger( Container.class );
    
    
    public Container() {
        super(null);
    }
    
    
    protected Container(Long id, String name, Long siteId) {
        super( id );
        this.id = id;
        this.name = name;
        this.siteId = siteId;
    }
    

    public String getTableId() {
        return tableId;
    }

    public Long insert(Connection connection) throws SQLException {
        PreparedStatement insertStatement = null;
        try {
	        setId( DBHelper.getNextInsertId( connection, sequenceId ) );
				insertStatement = connection.prepareStatement( SQL_INSERT );
	        saveIntoStatement(insertStatement).execute();
	        return this.getId();
		} catch (SQLException e) {
			logger.error("SQL exception -", e);
			throw e;
		} finally {
			DBHelper.close(insertStatement);
		}
    }
    
    public Long insert() throws CriticalException {
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            return insert(conn);
        }
        catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }
    
    
    public void update() throws CriticalException {
        PreparedStatement updateStatement = null;
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            updateStatement = conn.prepareStatement( SQL_UPDATE );
            saveIntoStatement( updateStatement );
            updateStatement.setObject( 4, getId(), Types.BIGINT );
            updateStatement.execute();
        }
        catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }
    
    
    public void delete() throws CriticalException {
        logger.debug( "+" );
        DBHelper.deleteObject( this.getTableId(), this.getId(), logger );
        logger.debug( "-" );

    }
    
    public void delete(Connection conn) throws CriticalException {
    	logger.debug( "+" );
    	DBHelper.deleteObject(conn, this.getTableId(), this.getId(), logger );
    	logger.debug( "-" );
    }
    
    
    public void load(long id)
        throws ObjectNotFoundException, CriticalException
    {
        load(new Long(id));
    }
    
    
    public void load(Long id)
        throws ObjectNotFoundException, CriticalException
    {
        logger.debug("+");

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            stmt = conn.prepareStatement( SQL_FIND_BY_ID );
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            if ( rs.next() == false ) {
                throw new ObjectNotFoundException(
                    "Object: " + getTableId() + " not found by id: " + id );
            }
            this.id = new Long(rs.getLong("id"));
            this.name = rs.getString("name");
            this.siteId = makeLong(rs.getLong("site_id"));
            logger.debug("-");
        } catch (SQLException ex) {
            logger.debug("-", ex);
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(conn);
        }
    }
    
    
    public static Container load( ResultSet rs )
        throws CriticalException
    {
        logger.debug( "+" );
        try {
            Container result = new Container(
                new Long( rs.getLong( "id" ) ),
                rs.getString( "name" ),
                makeLong(rs.getLong("site_id" ))
                );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
    }
    
    
    /**
     * Save into PreparedStatement
     *
     * @throws SQLException
     */
    public PreparedStatement saveIntoStatement( PreparedStatement stmt )
        throws SQLException
    {
        logger.debug( "+" );
        stmt.setObject( 1, getId() );
        stmt.setString( 2, getName() );
        stmt.setObject( 3, getSiteId() );
        logger.debug( "-" );
        return stmt;
    }
    
    
    public Long getSiteId() {
        return siteId;
    }


    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
    

    public String getName()
    {
        return name;
    }
    
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    public static Collection getContainers(Long siteId)
        throws CriticalException
    {
        logger.debug("+");
        ResultSet rs = null;
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            Collection<Container> containers = new LinkedList<Container>();
            PreparedStatement pstmt = conn.prepareStatement(
                " SELECT * FROM containers " +                " WHERE site_id = ? " +                " ORDER BY name");
            pstmt.setObject(1, siteId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                try {
                    Container container = new Container();
                    container = load(rs);
                    containers.add(container);
                } catch (Exception e) {
                    logger.error("Exception", e);
                }
            }
            logger.debug("-");
            return containers;
        } catch (SQLException ex) {
            logger.debug("-", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
    }
    

    public Collection<String[]> getGroups() throws CriticalException
    {
        logger.debug("+");
        
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return getGroups(conn);
        } catch (SQLException ex) {
            logger.error( "-", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }
    
    public Collection<String[]> getGroups(Connection connection) throws SQLException  {
    	logger.debug("+");
    	ResultSet rs = null;
    	LinkedList<String[]> groups = new LinkedList<String[]>();
    	try {
	        rs = connection.createStatement().executeQuery(
	                " SELECT groups.*, " +
	                "   (SELECT COUNT(*) > 0 FROM permissions " +
	                "       WHERE permissions.group_id = groups.id " +
	                "       AND permissions.container_id = " + id +
	                "   ) AS linked " +
	                " FROM groups WHERE groups.site_id = " + siteId +
	                " ORDER by groups.name");
            while(rs.next()){
                try {
                    groups.add( new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getBoolean("linked") == true ? "true" : "false",
                        rs.getString("role_id")
                        });
                } catch (Exception ex) {
                    logger.error( "CriticalException", ex );
                }
            }
            logger.debug( "-" );            
            return groups;
    	} catch (SQLException e) {
    		logger.error("Can't get groups - ", e);
    		throw e;
    	} finally {
           DBHelper.close(rs);    		
    	}
    }
    
    /** Reads all containers, sorted by id, into an ordered ArrayList */
    public static synchronized ArrayList<Container> getContainers() throws SQLException
    {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        ArrayList<Container> containers = new ArrayList<Container>();
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement(SELECT_ALL_ORDER_BY_ID);
            rs = findStatement.executeQuery();
            while(rs.next()) {
                Container language = new Container(
                    new Long(rs.getLong("id")),
                    rs.getString("name"),
                    new Long(rs.getLong("site_id")));
                containers.add(language);
            }
            logger.debug("-");
            return containers;
        } finally {
            DBHelper.close(rs);
            DBHelper.close(findStatement);
            DBHelper.close(conn);
        }
    }
    
    public Collection<String[]> getLinkedGroups() throws SQLException  {
    	final Collection<String[]> allGroups = getGroups();
    	Collection<String[]> linkedGroups = new ArrayList<String[]>();
    	for (String[] group: allGroups) {
    		if (group[2].equalsIgnoreCase("true")) {
    			linkedGroups.add(group);
    		}
    	}
    	return linkedGroups; 
    }
    
    public Collection<String[]> getLinkedGroups(Connection connection) throws SQLException  {
    	final Collection<String[]> allGroups = getGroups(connection);
    	Collection<String[]> linkedGroups = new ArrayList<String[]>();
    	for (String[] group: allGroups) {
    		if (group[2].equalsIgnoreCase("true")) {
    			linkedGroups.add(group);
    		}
    	}
    	return linkedGroups; 
    }
    
    public void setGroups(Long[] groupIds) throws CriticalException {
        logger.debug("+");
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            conn.createStatement().executeUpdate(
                "DELETE FROM permissions WHERE container_id = " + id);
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO permissions (container_id, group_id) VALUES (?, ?)");
            for(int i = 0; i < groupIds.length; i++) {
                if(groupIds[i] != null){
                    pstmt.setObject(1, id);
                    pstmt.setObject(2, groupIds[i]);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
            logger.debug( "-" );
            return;
        } catch (SQLException ex) {
            logger.error( "- SQLException", ex );
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("- SQLException in rollback", e);
            }
            throw new CriticalException(
                "Cannot update permisions for the container");
        } finally {
            DBHelper.close( conn );
        }
    }

	public void addMe(Element parent) {
		Element containerElement =  Xbuilder.addEl(parent, "container", null);
		Xbuilder.setAttr(containerElement, "id", this.id);
		Xbuilder.setAttr(containerElement, "name", this.name);
	}
    
    /**
	 * @param elPage
	 * @param request
	 */
	public static void appendContainers(Element elPage) {
		Element contents = Xbuilder.addEl(elPage, "containers", null);
		try {
			ArrayList<Container> containers = Container.getContainers();
			if (containers != null && containers.size() > 0){
				contents.setAttribute("total", String.valueOf(containers.size()));
				Iterator<Container> iter = containers.iterator();
				while(iter.hasNext()){
					Element newContainer = Xbuilder.addEl(contents, "container", null);
					Container container = iter.next();
					newContainer.setAttribute("id", (container.getId() != null ? container.getId().toString() : null));
					newContainer.setAttribute("name", (container.getName() != null ? container.getName() : null));
				}
			}
		} catch (SQLException e) {
			logger.warn("- (no containers were founded). Tag containers is empty");
			e.printStackTrace();
		}
	}
	
	public static void appendContainers(Document elPage) {
		Element contents = Xbuilder.addEl(elPage, "containers", null);
		try {
			ArrayList<Container> containers = Container.getContainers();
			if (containers != null && containers.size() > 0){
				contents.setAttribute("total", String.valueOf(containers.size()));
				Iterator<Container> iter = containers.iterator();
				while(iter.hasNext()){
					Element newContainer = Xbuilder.addEl(contents, "container", null);
					Container container = iter.next();
					newContainer.setAttribute("id", (container.getId() != null ? container.getId().toString() : null));
					newContainer.setAttribute("name", (container.getName() != null ? container.getName() : null));
				}
			}
		} catch (SQLException e) {
			logger.warn("- (no containers were founded). Tag containers is empty");
			e.printStackTrace();
		}
	}
    
}
