/*
 * @(#)$Id: Voter.java,v 1.7, 2005-06-06 13:05:17Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.webpoll.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;

/**
 * @author Sergiy Oliynyk
 *
 */
public class Voter implements DbObject {

    private static Logger logger = Logger.getLogger(Voter.class);

    private static String tableId = "webpoll_voter";

    private static final String findByIdSql =
        " SELECT id, list_item_id, address, vote_time FROM " + tableId +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, list_item_id, address, vote_time) VALUES (?, ?, ?, ?)";

    private final static String updateSql =
        " UPDATE " + tableId +
        " SET id = ?, list_item_id = ?, address = ?, vote_time = ?" +
        " WHERE id = ?";

    private static final String findByListItemIdAndAddressSql =
        " SELECT id, list_item_id, address, vote_time FROM " + tableId + 
        " WHERE list_item_id = ? and address = ?";

    private static final int fieldCount = 4;

    private Long id;
    private Long listItemId;
    private String address;
    private Timestamp voteTime;

    // after this timeout the voting from the one ip-address is permited
    // default value is one hour
    private static final long timeOut = 
        1000 * Env.getIntProperty("webpoll.timeout", 60 * 60);

    public Voter() {}

    public Voter(Long id, Long listItemId, String address, 
        Timestamp voteTime)
    {
        this.id = id;
        this.listItemId = listItemId;
        this.address = address;
        this.voteTime = voteTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            listItemId = DBHelper.makeLong(rs.getLong("list_item_id"));
            address = rs.getString("address");
            voteTime = rs.getTimestamp("vote_time");
            logger.debug("-");
            return id;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }

    public Long insert(Connection con) throws CriticalException {
        return DBHelper.insertDbObject(con, this);
    }

    public void update(Connection con) throws CriticalException {
        DBHelper.updateDbObject(con, this);
    }

    public void delete(Connection con) throws CriticalException {
        DBHelper.deleteObject(
            con, this.getTableId(), this.getId(), logger);
    }

    public static Voter findByListItemIdAndAddress(
        Connection conn, Long listItemId, String address)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findByListItemIdAndAddressSql);
            stmt.setObject(1, listItemId, Types.BIGINT);
            stmt.setString(2, address);
            ResultSet rs = stmt.executeQuery();
            Voter voter = null;
            if (rs.next()) {
                voter = new Voter();
                voter.load(rs);
            }
            logger.debug("-");
            return voter;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally{
            DBHelper.close(stmt);
        }
    }

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException
    {
        logger.debug("+");
        stmt.setObject(1, id);
        stmt.setObject(2, listItemId);
        stmt.setString(3, address);
        stmt.setTimestamp(4, voteTime);
        logger.debug("-");
        return stmt;
    }

    public Long getListItemId() {
        return listItemId;
    }

    public void setListItemId(Long listItemId) {
        this.listItemId = listItemId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(Timestamp voteTime) {
        this.voteTime = voteTime;
    }
    
    public boolean canVote() {
        return (voteTime == null || 
            System.currentTimeMillis() - voteTime.getTime() > timeOut);
    }
}
