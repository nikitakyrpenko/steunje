/*
 * @(#)Group.java  Created on 21.05.2004
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
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainObject;
import com.negeso.framework.domain.ObjectNotFoundException;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class Group extends DomainObject {

    protected static String tableId = "groups";
    protected static String sequenceId = "groups_id_seq";

    private final static String findByIdStatementString =
        " SELECT * FROM groups WHERE id = ?";
    
    
    private final static String updateStatementString =
        " UPDATE groups " +
        "   set id = ?, name = ?, role_id = ?, site_id = ? " +
        " WHERE id = ?";
    
    
    private final static String insertStatementString =
        " INSERT INTO groups(id, name, role_id, site_id) " +
        " VALUES (?, ?, ?, ?)";
    
    private final static String FIND_BY_USERID_QUERY = "SELECT * FROM members WHERE members.user_id = ?";
    
    private final static String FIND_GUEST_QUERY = "SELECT * FROM groups WHERE groups.role_id = 'guest'";
    
    /** Group name */
    private String name = null;
    
    
    /** The Role which the groups is based upon. */
    private String roleId;
    
    
    /**
     * The site which the group belongs to. Can be <code>null</code> for
     * simple sites (with no sites).
     */
    private Long siteId;
    
    
    private static Logger logger = Logger.getLogger( Group.class );
    
    
    public Group() {
        super(null);
    }
    

    public String getTableId() {
        return tableId;
    }

    
    public Long insert(Connection connection) throws SQLException  {
    	PreparedStatement insertStatement = null;
    	try {
	        setId( DBHelper.getNextInsertId( connection, sequenceId ) );
	        insertStatement = connection.prepareStatement( insertStatementString );
	        saveIntoStatement(insertStatement).execute();
	        return this.getId();
    	} catch (SQLException e) {
    		logger.error("SQL exveption - ", e);
    		throw e;
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
            updateStatement = conn.prepareStatement( updateStatementString );
            saveIntoStatement( updateStatement );
            updateStatement.setObject( 5, getId(), Types.BIGINT );
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
            stmt = conn.prepareStatement( findByIdStatementString );
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            if ( rs.next() == false ) {
                throw new ObjectNotFoundException(
                    "Object: " + getTableId() + " not found by id: " + id );
            }
            setId(new Long(rs.getLong("id")));
            setName(rs.getString("name"));
            setSiteId(makeLong(rs.getLong("site_id")));
            setRoleId(rs.getString("role_id"));
            logger.debug("-");
        } catch (SQLException ex) {
            logger.debug("-", ex);
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(conn);
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
        stmt.setObject( 3, getRoleId() );
        stmt.setObject( 4, getSiteId() );
        logger.debug( "-" );
        return stmt;
    }
    
    
    public Long getSiteId() {
        return siteId;
    }


    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
    

    public String getRoleId() {
        return roleId;
    }
    
    
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


    public String getName()
    {
        return name;
    }
    
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    public static Collection<Group> getGroups(Long siteId)
        throws CriticalException
    {
        logger.debug("+");
        ResultSet rs = null;
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            String sql =
                    " SELECT * FROM groups " +
                    " WHERE site_id = " + siteId +
                    " ORDER BY name";
            Collection<Group> groups = new LinkedList<Group>();
            rs = conn.createStatement().executeQuery(sql);
            while ( rs.next() ) {
                try {
                    Group group = new Group();
                    group = load(rs);
                    groups.add(group);
                } catch (Exception e) {
                    logger.error("Exception", e);
                }
            }
            logger.debug("-");
            return groups;
        } catch (SQLException ex) {
            logger.debug("-", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
    }
    
    public static Group getGuestGroup(){
    	logger.debug("+");
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    try {
	    	conn = DBHelper.getConnection();
	        stmt = conn.prepareStatement(FIND_GUEST_QUERY);
	        rs = stmt.executeQuery();
	        if (!rs.next()){
	        	logger.error("- cannot find guest group");
	        	return null;
	        }
	        logger.debug("-");
	        return load(rs);
	    } catch (SQLException ex) {
	        logger.debug("-", ex);
	        throw new CriticalException(ex);
	    }
	    finally {
	        DBHelper.close(rs, stmt, conn);
	    }
    }
    
    public static List<Group> findByUserId(Long userId) throws ObjectNotFoundException, CriticalException{
	    logger.debug("+");
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    List<Group> groups = new ArrayList<Group>();
	    try {
	        conn = DBHelper.getConnection();
	        stmt = conn.prepareStatement(FIND_BY_USERID_QUERY);
	        stmt.setLong(1, userId);
	        rs = stmt.executeQuery();
	        while(rs.next()){
	        	Group group = new Group();
	        	group.load(rs.getLong("group_id"));
	        	groups.add(group);
	        }
	        if (groups.size() == 0){
	        	groups.add(getGuestGroup());
	        }
	        logger.debug("-");
	        return groups;
	    } catch (SQLException ex) {
	        logger.debug("-", ex);
	        throw new CriticalException(ex);
	    }
	    finally {
	        DBHelper.close(rs, stmt, conn);
	    }
	}
    
    
    private static Group load(ResultSet rs)
        throws CriticalException
    {
        logger.debug( "+" );
        try {
            Group result = new Group(
                new Long( rs.getLong( "id" ) ),
                rs.getString( "name" ),
                rs.getString("role_id" ),
                makeLong(rs.getLong("site_id" ))
                );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
    }


    public Group(Long id, String name, String roleId, Long siteId) {
        super( id );
        this.id = id;
        this.name = name;
        this.roleId = roleId;
        this.siteId = siteId;
    }

    public void setContainers(Connection connection, Long[] containerIds) throws SQLException {
        logger.debug("+");
        PreparedStatement pstmt = null;
        try {
            connection.createStatement().executeUpdate(
                "DELETE FROM permissions WHERE group_id = " + id);
            pstmt = connection.prepareStatement(
                "INSERT INTO permissions (group_id, container_id) VALUES (?, ?)");
            for(int i = 0; i < containerIds.length; i++) {
                if(containerIds[i] != null){
                    pstmt.setObject(1, id);
                    pstmt.setObject(2, containerIds[i]);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error( "- SQLException", e );
            throw e;
        } finally {
            DBHelper.close( pstmt );
        }
    }
    
    public void setContainers(Long[] containerIds) throws CriticalException {
        logger.debug("+");
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            setContainers(conn, containerIds);
            conn.commit();
            logger.debug( "-" );
            return;
        } catch (SQLException ex) {
            logger.error( "- SQLException", ex );
            DBHelper.rollback(conn);
            throw new CriticalException(
                "Cannot update permisions for the container");
        } finally {
            DBHelper.close( conn );
        }
    }

    
    public void setVisitors(Connection connection, Long[] userIds) throws SQLException {
        logger.debug("+");
        PreparedStatement pstmt = null;
        try {
	        connection.createStatement().executeUpdate(
	            " DELETE FROM members" +
	            " WHERE " +
	            "   (SELECT user_list.type='visitor' " +
	            "    FROM user_list " +
	            "    WHERE user_list.id = members.user_id) " +
	            " AND group_id = " + id);
	        pstmt = connection.prepareStatement(
	            "INSERT INTO members (group_id, user_id) VALUES (?, ?)");
	        for(int i = 0; i < userIds.length; i++) {
	            if(userIds[i] != null){
	                pstmt.setObject(1, id);
	                pstmt.setObject(2, userIds[i]);
	                pstmt.addBatch();
	            }
	        }
	        pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error( "- SQLException", e);
            throw e;
		} finally {
			DBHelper.close(pstmt);
		}
        logger.debug( "-" );
    }

    public void setVisitors(Long[] userIds) throws CriticalException {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            setVisitors(conn, userIds);
            conn.commit();
            logger.debug( "-" );
        } catch (SQLException ex) {
            logger.error( "- SQLException", ex );
            DBHelper.rollback(conn);
            throw new CriticalException(
            "Cannot update permisions for the container");
        } finally {
            DBHelper.close( conn );
        }
    }


    public void setContributors(Long[] userIds) throws CriticalException {
        logger.debug("+");
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            conn.createStatement().executeUpdate(
                " DELETE FROM members" +
                " WHERE " +
                "   (SELECT NOT(user_list.type='visitor') " +
                "    FROM user_list " +
                "    WHERE user_list.id = members.user_id) " +
                " AND group_id = " + id);
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO members (group_id, user_id) VALUES (?, ?)");
            for(int i = 0; i < userIds.length; i++) {
                if(userIds[i] != null){
                    pstmt.setObject(1, id);
                    pstmt.setObject(2, userIds[i]);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
            logger.debug( "-" );
            return;
        } catch (SQLException ex) {
            logger.error( "- SQLException", ex );
            DBHelper.rollback(conn);
            throw new CriticalException(
                "Cannot update permisions for the container");
        } finally {
            DBHelper.close( conn );
        }
    }
    

    public Collection<String[]> getContainers() throws CriticalException
    {
        logger.debug("+");
        LinkedList<String[]> containers = new LinkedList<String[]>();
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                " SELECT containers.id, containers.name, " +
                "   (SELECT COUNT(*) > 0 FROM permissions " +
                "       WHERE permissions.container_id = containers.id " +
                "       AND permissions.group_id = " + id +
                "   ) AS linked " +
                " FROM containers WHERE containers.site_id = " + siteId +
                " ORDER by containers.name");
            while(rs.next()){
                try {
                    containers.add( new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getBoolean("linked") == true ? "true" : "false"
                        });
                } catch (Exception ex) {
                    logger.error( "CriticalException", ex );
                }
            }
            rs.close();
            logger.debug( "-" );
            return containers;
        } catch (SQLException ex) {
            logger.error( "-", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }


    public Collection<String[]> getContributors() throws CriticalException
    {
        logger.debug("+");
        LinkedList<String[]> users = new LinkedList<String[]>();
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                " SELECT user_list.id, user_list.username, " +
                "   (SELECT COUNT(*) > 0 FROM members " +
                "       WHERE members.user_id = user_list.id " +
                "       AND members.group_id = " + id +
                "   ) AS linked " +
                " FROM user_list " +
                " WHERE NOT (user_list.type = 'visitor') " +
                " AND user_list.site_id = " + siteId +
                " ORDER by user_list.username");
            while(rs.next()){
                try {
                    users.add( new String[]{
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getBoolean("linked") == true ? "true" : "false"
                        });
                } catch (Exception ex) {
                    logger.error( "CriticalException", ex );
                }
            }
            rs.close();
            logger.debug( "-" );
            return users;
        } catch (SQLException ex) {
            logger.error( "-", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }
    
    
    public Collection<String[]> getVisitiors() throws CriticalException
    {
        logger.debug("+");
        LinkedList<String[]> users = new LinkedList<String[]>();
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                " SELECT user_list.id, user_list.username, " +
                "   (SELECT COUNT(*) > 0 FROM members " +
                "       WHERE members.user_id = user_list.id " +
                "       AND members.group_id = " + id +
                "   ) AS linked " +
                " FROM user_list " +
                " WHERE user_list.type = 'visitor' " +
                " AND user_list.site_id = " + siteId +
                " ORDER by user_list.username");
            while(rs.next()){
                try {
                    users.add( new String[]{
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getBoolean("linked") == true ? "true" : "false"
                        });
                } catch (Exception ex) {
                    logger.error( "CriticalException", ex );
                }
            }
            rs.close();
            logger.debug( "-" );
            return users;
        } catch (SQLException ex) {
            logger.error( "-", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }

    public Collection<String[]> getLinkedVisitors() {
    	final Collection<String[]> allVisitors = getVisitiors();
    	Collection<String[]> linkedVisitors = new ArrayList<String[]>();   
    	for (String[] visitor: allVisitors) {
    		if (visitor[2].equalsIgnoreCase("true")) {
    			linkedVisitors.add(visitor);
    		}
    	}
    	return linkedVisitors;
    }
    
    
}