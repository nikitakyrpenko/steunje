/*
 * @(#)Role.java  Created on 19.05.2004
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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.Env;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class Role {

    private String id;

    protected static String tableId = "roles";

    private final static String findByIdStatementString =
        " SELECT * FROM roles WHERE id = ?";
    
    private final static String SQL_SELECT_ROLES = " SELECT * FROM roles ORDER BY name";
    
    /** Role name (E.g., "ADMINISTRATOR" */
    private String name = null;
    
    private static Logger logger = Logger.getLogger( Role.class );

    
    public Role(){ }
    
    
    public Role(String id, String name) {
        this.id = id;
        this.name = name;
    }
    

    public String getTableId() {
        return tableId;
    }
    
    public void load(String id)
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
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if ( rs.next() == false ) {
                throw new ObjectNotFoundException(
                    "Object: " + getTableId() + " not found by id: " + id );
            }
            id = rs.getString("id");
            name = rs.getString("name");
            logger.debug("-");
        } catch (SQLException ex) {
            logger.error("-", ex);
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(conn);
        }
    }
    

    /**
     * Loads Role by ResultSet. All blob fields from table are
     * passed throught Env.fixEncoding() method.
     *
     * @see Env
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Role load( ResultSet rs )
        throws CriticalException
    {
        logger.debug( "+" );
        try {
            Role result = new Role(
                rs.getString( "id" ),
                rs.getString( "name" ));
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
    }


    public String getId()
    {
        logger.debug("+ -");
        return id;
    }

    public String getName()
    {
        logger.debug("+ -");
        return name;
    }
    
    public static Collection getRoles() throws CriticalException
    {
        logger.debug("+");
        ResultSet rs = null;
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            ArrayList list = new ArrayList(10);
            rs = conn.createStatement().executeQuery(SQL_SELECT_ROLES);
            while ( rs.next() ) {
                list.add(Role.load(rs));
            }
            logger.debug("-");
            return list;
        } catch (SQLException ex) {
            logger.debug("-", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
    }
    
    
}
