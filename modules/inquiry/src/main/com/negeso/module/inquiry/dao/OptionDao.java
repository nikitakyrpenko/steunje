/*
 * @(#)$Id: OptionDao.java,v 1.2, 2006-06-01 13:46:54Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.sorting.PositionSetter;
import com.negeso.framework.sorting.SortingTool;
import com.negeso.module.inquiry.DuplicateOptionException;
import com.negeso.module.inquiry.domain.Option;

/**
 * 
 *
 * @version     $Revision: 3$
 * @author      Stanislav Demchenko
 */
public class OptionDao implements PositionSetter {
    
    private static Logger logger = Logger.getLogger(OptionDao.class);

	private String optionsByQuestionIdSql = 
		" SELECT * FROM inq_option WHERE question_id=? ORDER BY position ";
	private String insertOptionSql = 
		" INSERT INTO inq_option "+
		" (id, question_id, title, position )" +
		" VALUES (?,?,?,?)";
	
    public Option loadOption(int id) {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM inq_option WHERE id=" + id);
            if(!rs.next()) {
                logger.warn("- cannot find row # " + id);
                return null;
            }
            logger.debug("-");
            return mapRow(rs);
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read a row", e);
        } finally {
            DBHelper.close(rs, stmt, conn);
        }
    }

    public void deleteOption(int id)
    {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM inq_option WHERE id = " + id);
            arrangePositions();
            logger.debug("-");
        } catch (SQLException e) {
            throw new RuntimeException("cannot delete a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stmt);
        }
    }

    public void insertOption(Option option) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBHelper.getConnection();
           	checkTitleNotPresentYet(conn, option);
            pstmt = conn.prepareStatement(
                " INSERT INTO inq_option (id,question_id,title,position) " +
                " VALUES (?,?,?,(SELECT MAX(position)+1 FROM inq_option)) ");
            Long id = DBHelper.getNextInsertId(conn, "inq_option_id_seq");
            pstmt.setInt(1, id.intValue());
            pstmt.setInt(2, option.getParentId());
            pstmt.setString(3, option.getTitle());
            pstmt.execute();
            option.setId(id.intValue());
            arrangePositions();
            logger.debug("-");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("cannot store a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(pstmt);
        }
        
    }

    public void updateOption(Option option) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBHelper.getConnection();
            checkTitleNotPresentYet(conn, option);
            pstmt = conn.prepareStatement(
                " UPDATE inq_option " +
                " SET question_id=?, title=?, position=? " +
                " WHERE id=? ");
            pstmt.setInt(1, option.getParentId());
            pstmt.setString(2, option.getTitle());
            pstmt.setInt(3, option.getPosition());
            pstmt.setInt(4, option.getId());
            pstmt.execute();
            logger.debug("-");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("cannot store a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(pstmt);
        }        
    }

    private void checkTitleNotPresentYet(Connection cn, Option o)
    throws SQLException {
        logger.debug("+");
        PreparedStatement st = null;
        try {
            st = cn.prepareStatement(
                " SELECT count(*) FROM inq_option " +
                " WHERE question_id = ? AND title = ? " + 
                (o.getId() == 0 ? "" : " AND id != " + o.getId()) );
            st.setInt(1, o.getParentId());
            st.setString(2, o.getTitle());
            ResultSet rs = st.executeQuery();
            rs.next();
            if (rs.getInt(1) != 0) {
                logger.warn("- throwing DuplicateOptionException");
                throw new DuplicateOptionException();
            }
            logger.debug("-");
        } finally {
            DBHelper.close(st);
        }
    }
    
    public List<Option> getOptionsByQuestionId(int id)
    {
        logger.debug("+");
        List<Option> result = new LinkedList<Option>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT * FROM inq_option WHERE question_id = " + id +
                " ORDER BY position ");
            while(rs.next()) {
                result.add(mapRow(rs));
            }
            logger.debug("-");
            return result;
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read rows", e);
        } finally {
            DBHelper.close(rs, stmt, conn);
        }
    }
    
    private Option mapRow(ResultSet rs)
    {
        logger.debug("+");
        try {
            Option q = new Option();
            q.setId(rs.getInt("id"));
            q.setParentId(rs.getInt("question_id"));
            q.setTitle(rs.getString("title"));
            q.setPosition(rs.getInt("position"));
            logger.debug("-");
            return q;
        } catch (SQLException e) {
            logger.error("- Exception", e);
            throw new RuntimeException("Cannot read a row", e);
        }
    }

    public void switchOptionPositions(int id1, int id2) {
        Option o1 = loadOption(id1);
        Option o2 = loadOption(id2);
        int pos1 = o1.getPosition();
        int pos2 = o2.getPosition();
        o1.setPosition(pos2);
        o2.setPosition(pos1);
        updateOption(o1);
        updateOption(o2);
        arrangePositions();
    }
    
    public boolean setPosition(int id, int position) {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            int counter =
                stmt.executeUpdate(
                    " UPDATE inq_option SET position = " + position +
                    " WHERE id = " + id);
            logger.debug("-");
            return (counter == 1);
        } catch (SQLException e) {
            logger.error("- SQLException", e);
            return false;
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stmt);
        }
    }

    private void arrangePositions() {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet sortable = null;
        try {
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sortable = stmt.executeQuery(
                " SELECT id, question_id, position " +
                " FROM inq_option ORDER BY question_id, position");
            if (sortable.next()) {
	            boolean success = SortingTool.reorderAll(sortable, this);
	            if (!success) {
	                logger.error("- Failed to arrange options");
	                throw new RuntimeException ("Failed to arrange options");
	            }
	            conn.commit();
            }
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            logger.error("- throwing Exception", e);
            throw new RuntimeException(e);
        } finally {
            DBHelper.close(sortable, stmt, conn);
        }
    }
	
	public void copyOptions(Connection conn, int prevQId, int newQId) throws Exception{
		PreparedStatement stat = conn.prepareStatement(optionsByQuestionIdSql);
		PreparedStatement stat2 = conn.prepareStatement(insertOptionSql);
		stat.setInt(1, prevQId);
		ResultSet res = null;
		Long id;
		res = stat.executeQuery();
		while(res.next()){
			 id = DBHelper.getNextInsertId(conn, "inq_option_id_seq");
			 stat2.setInt(1, id.intValue());
			 stat2.setInt(2, newQId);
			 stat2.setString(3, res.getString("title"));
			 stat2.setInt(4, res.getInt("position"));
			 stat2.executeUpdate();
		}
		stat2.close();
		stat.close();
	}
	
}

