/*
 * @(#)$Id: PollOption.java,v 1.9, 2005-06-06 13:04:56Z, Stanislav Demchenko$
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

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;

/**
 * @author Sergiy Oliynyk
 *
 */
public class PollOption implements DbObject {

    private static Logger logger = Logger.getLogger(PollOption.class);

    private static String tableId = "webpoll";

    private static final String findByIdSql =
        "SELECT id, list_item_id, poll_id, counter FROM webpoll WHERE id = ? ";

    private static final String insertSql =
        "INSERT INTO webpoll " + 
        "(id, list_item_id, poll_id, counter) VALUES (?, ?, ?, ?)";

    private final static String updateSql =
        "UPDATE webpoll " +
        "SET id = ?, list_item_id = ?, poll_id = ?, counter = ? " +
        "WHERE id = ?";

    private static final String findByListItemIdSql =
        "SELECT id, list_item_id, poll_id, counter FROM webpoll " + 
        "WHERE list_item_id = ? ";

    private static final int fieldCount = 4;

    private Long id;
    private Long listItemId;
    private Long pollId;
    private int counter;

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            id = new Long(rs.getLong("id"));
            listItemId = DBHelper.makeLong(rs.getLong("list_item_id"));
            pollId = DBHelper.makeLong(rs.getLong("poll_id"));
            counter = rs.getInt("counter");
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
        DBHelper.deleteObject(con, tableId, id, logger);
    }

    public static PollOption findByListItemId(Connection conn, Long listItemId)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findByListItemIdSql);
            stmt.setLong(1, listItemId.longValue());
            ResultSet rs = stmt.executeQuery();
            PollOption option = null;
            if (rs.next()) {
                option = new PollOption();
                option.load(rs);
            }
            logger.debug("-");
            return option;
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
        stmt.setObject(3, pollId);
        stmt.setInt(4, counter);
        logger.debug("-");
        return stmt;
    }

    public Long getListItemId() {
        return listItemId;
    }

    public void setListItemId(Long listItemId) {
        this.listItemId = listItemId;
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }
    
    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
