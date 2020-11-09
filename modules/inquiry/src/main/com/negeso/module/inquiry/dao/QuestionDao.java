/*
 * @(#)$Id: QuestionDao.java,v 1.3, 2006-06-01 13:46:55Z, Anatoliy Pererva$
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.sorting.PositionSetter;
import com.negeso.framework.sorting.SortingTool;
import com.negeso.module.inquiry.DuplicateQuestionException;
import com.negeso.module.inquiry.domain.Question;
import com.negeso.module.inquiry.domain.QuestionType;

/**
 * 
 *
 * @version     $Revision: 4$
 * @author      Stanislav Demchenko
 */
public class QuestionDao implements PositionSetter{
    
    private static Logger logger = Logger.getLogger(QuestionDao.class);
	
	private static String questionsBySectionIdSql = 
		" SELECT * FROM inq_question WHERE section_id=? ORDER BY position ";
	private static String insertBQuestion =
		" INSERT INTO inq_question " +
		" (id, questionnaire_id, title, type, required, allow_remark, position, section_id, ao_height, ao_width, ao_multiline ) " +
		" VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0,0, true)";
	
	private static String departmentQuestionSql = 
		" SELECT * FROM inq_question WHERE questionnaire_id=? AND type='department' ORDER BY position ";
    
    public Question loadQuestion(int id)
    {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM inq_question WHERE id=" + id);
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

    public void deleteQuestion(int id)
    {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM inq_question WHERE id = " + id);
            arrangePositions();
            logger.debug("-");
        } catch (SQLException e) {
            throw new RuntimeException("cannot delete a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stmt);
        }
    }
    
    private static final String INSERT_STATEMENT =
        " INSERT INTO inq_question " +
        " (id,questionnaire_id,title,type,required,allow_remark," +
        " alternative,position, explanation, options_layout, ao_height, ao_width, ao_multiline, section_id) " +
        " VALUES" +
        " (?,?,?,?,?,?,?,(SELECT MAX(position)+1 FROM inq_question), ?, ?, ?, ?, ?, ? ) ";
    
    private PreparedStatement createInsertStatement(Long id, Connection conn,
    		Question question) throws SQLException {
    	PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT);
        stmt.setInt(1, id.intValue());
        stmt.setInt(2, question.getParentId());
        stmt.setString(3, question.getTitle());
        stmt.setString(4, question.getType().getName());
        stmt.setBoolean(5, question.getRequired());
        stmt.setBoolean(6, question.getAllowRemark());
        stmt.setString(7, question.getAlternative());
        stmt.setString(8, question.getExplanation());
        stmt.setString(9, question.getOptionsLayout());
        stmt.setInt(10, question.getAoHeight());
        stmt.setInt(11, question.getAoWidth());
        stmt.setBoolean(12, question.isAoMultiline());
		if(question.getSectionId()==null){
			stmt.setObject(13, null);
		}
		else{
			stmt.setLong(13, question.getSectionId());
		}
		return stmt;
    }
    
    public void insertQuestion(Question question) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBHelper.getConnection();
            checkTitleNotPresentYet(conn, question);
            Long id = DBHelper.getNextInsertId(conn, "inq_question_id_seq");
            pstmt = createInsertStatement(id, conn, question);
            pstmt.execute();
            question.setId(id.intValue());
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

    public void updateQuestion(Question question) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBHelper.getConnection();
            checkTitleNotPresentYet(conn, question);
            pstmt = conn.prepareStatement(
                " UPDATE inq_question " +
                " SET " +
                " questionnaire_id=?, title=?, type=?, required=?, allow_remark=?, " +
                " alternative=?, position=?, explanation=?, options_layout=?, ao_height=?, ao_width=?, ao_multiline=?, " +
                " section_id=? " +
                " WHERE id=? ");
            pstmt.setInt(1, question.getParentId());
            pstmt.setString(2, question.getTitle());
            pstmt.setString(3, question.getType().getName());
            pstmt.setBoolean(4, question.getRequired());
            pstmt.setBoolean(5, question.getAllowRemark());
            pstmt.setString(6, question.getAlternative());
            pstmt.setInt(7, question.getPosition());
            pstmt.setString(8, question.getExplanation());
            pstmt.setString(9, question.getOptionsLayout());
            pstmt.setInt(10, question.getAoHeight());
            pstmt.setInt(11, question.getAoWidth());
            pstmt.setBoolean(12, question.isAoMultiline());
			if(question.getSectionId()==null){
				pstmt.setObject(13, null);
			}
			else{
				pstmt.setLong(13, question.getSectionId());
			}
			pstmt.setInt(14, question.getId());
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

    private void checkTitleNotPresentYet(Connection cn, Question q)
    throws SQLException {
        logger.debug("+");
        PreparedStatement st = null;
        try {
            st = cn.prepareStatement(
                " SELECT count(*) FROM inq_question " +
                " WHERE questionnaire_id = ? AND title = ? " + 
                (q.getId() == 0 ? "" : " AND id != " + q.getId()) );
            st.setInt(1, q.getParentId());
            st.setString(2, q.getTitle());
            ResultSet rs = st.executeQuery();
            rs.next();
            if (rs.getInt(1) != 0) {
                logger.warn("- throwing DuplicateQuestionException");
                throw new DuplicateQuestionException();
            }
            logger.debug("-");
        } finally {
            DBHelper.close(st);
        }
    }
    
    private Question mapRow(ResultSet rs)
    {
        logger.debug("+");
        try {
            Question q = new Question();
            q.setId(rs.getInt("id"));
            q.setParentId(rs.getInt("questionnaire_id"));
            q.setTitle(rs.getString("title"));
			if(rs.getString("type").equals("")){}
            q.setType(QuestionType.getTypeByName(rs.getString("type")));
            q.setRequired(rs.getBoolean("required"));
            q.setAllowRemark(rs.getBoolean("allow_remark"));
            q.setAlternative(rs.getString("alternative"));
            q.setPosition(rs.getInt("position"));
            q.setExplanation(rs.getString("explanation"));
            q.setOptionsLayout(rs.getString("options_layout"));
            q.setAoHeight(rs.getInt("ao_height"));
            q.setAoWidth(rs.getInt("ao_width"));
            q.setAoMultiline(rs.getBoolean("ao_multiline"));
			if(rs.getObject("section_id")==null){
				q.setSectionId(null);
			}
			else{
				q.setSectionId(rs.getLong("section_id"));
			}
            logger.debug("-");
            return q;
        } catch (SQLException e) {
            logger.error("- Exception", e);
            throw new RuntimeException("Cannot read a row", e);
        }
    }

    public List<Question> getQuestionsByQuestionnaireId(int id)
    {
        logger.debug("+");
        List<Question> result = new LinkedList<Question>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT * FROM inq_question " +
                " WHERE questionnaire_id = " + id +
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

    public void switchQuestionPositions(int id1, int id2) {
        Question q1 = loadQuestion(id1);
        Question q2 = loadQuestion(id2);
        int pos1 = q1.getPosition();
        int pos2 = q2.getPosition();
        q1.setPosition(pos2);
        q2.setPosition(pos1);
        updateQuestion(q1);
        updateQuestion(q2);
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
                    " UPDATE inq_question SET position = " + position +
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
                " SELECT id, questionnaire_id, position " +
                " FROM inq_question ORDER BY questionnaire_id, position");
            
            if (sortable.next()) {
	            boolean success = SortingTool.reorderAll(sortable, this);
	            if (!success) {
	                logger.error("- Failed to arrange questions");
	                throw new RuntimeException ("Failed to arrange questions");
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
	
	public List getQuestionsBySectionId(int sectionId)throws Exception{
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		List list = new ArrayList();
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement(questionsBySectionIdSql);
			stat.setInt(1, sectionId);
			res = stat.executeQuery();
			while(res.next()){
				list.add(mapRow(res));
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception("Error while getting Questions By SectionId");
		}
		finally{
			DBHelper.close(res);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
		return list;
	}
	
	public void copyQuestions(Connection conn, int prevSectionId, int newSectionId, int questionnaireId) throws Exception{
		PreparedStatement stat = conn.prepareStatement(questionsBySectionIdSql);
		PreparedStatement stat2 = conn.prepareStatement(insertBQuestion);
		stat.setInt(1, prevSectionId);
		ResultSet res = null;
		Long id;
		res = stat.executeQuery();
		while(res.next()){
			id = DBHelper.getNextInsertId(conn, "inq_question_id_seq");
			stat2.setInt(1, id.intValue());
			stat2.setInt(2, questionnaireId);
			stat2.setString(3, res.getString("title"));
			stat2.setString(4, res.getString("type"));
			stat2.setBoolean(5, res.getBoolean("required"));
			stat2.setBoolean(6, res.getBoolean("allow_remark"));
			stat2.setInt(7, res.getInt("position"));
			stat2.setInt(8, newSectionId);
			stat2.executeUpdate();
			new OptionDao().copyOptions(conn, res.getInt("id"), id.intValue());
		}
		stat2.close();
		stat.close();
	}

	public void copyQuestions(Connection conn, int prevQuestionnaireId,
			int newQuestionnaireId) throws Exception {
		PreparedStatement selectStat = conn.prepareStatement(
				"SELECT * FROM inq_question WHERE questionnaire_id = ? ORDER BY position");
		PreparedStatement insertStat = null;
		selectStat.setInt(1, prevQuestionnaireId);
		ResultSet res = null;
		Long id;
		res = selectStat.executeQuery();
		while (res.next()) {
			Question question = mapRow(res);
			int prevId = question.getId();
			question.setParentId(newQuestionnaireId);
			id = DBHelper.getNextInsertId(conn, "inq_question_id_seq");
			insertStat = createInsertStatement(id, conn, question);
			insertStat.executeUpdate();
			new OptionDao().copyOptions(conn, prevId, id.intValue());
		}
		if (insertStat != null) {
			insertStat.close();
		}
		selectStat.close();
	}

	public void copyQuestion(int questionId, String title) {
		logger.debug("+");
		Connection conn = null;
		PreparedStatement selectStat = null;
		PreparedStatement insertStat = null;
		ResultSet rs = null;
		
		try {
			conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			selectStat = conn.prepareStatement("SELECT * FROM inq_question WHERE id = ?");
			selectStat.setInt(1, questionId);
			rs = selectStat.executeQuery();
            if (!rs.next()) {
                logger.warn("- cannot find question row # " + questionId);
                return;
            } 
			
            Question question = mapRow(rs);
            question.setTitle(title);
			Long id = DBHelper.getNextInsertId(conn, "inq_question_id_seq");
			insertStat = createInsertStatement(id, conn, question);
			insertStat.executeUpdate();
			new OptionDao().copyOptions(conn, questionId, id.intValue());
			conn.commit();
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Error while copy a question");
		}
		finally {
			try {
				conn.rollback();
			} catch (Exception e) {
				;
			}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
				;
			}
			DBHelper.close(rs);
			DBHelper.close(selectStat);
			DBHelper.close(insertStat);
			DBHelper.close(conn);
		}
		logger.debug("-");
	}
	
	public Question getDepartmentQuestion(int questionnaireId) throws Exception{
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		Question quest = null;
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement(departmentQuestionSql);
			stat.setInt(1, questionnaireId);
			res = stat.executeQuery();
			if(res.next()){
				quest = mapRow(res);
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception("Error while getting Questions By SectionId");
		}
		finally{
			DBHelper.close(res);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
		return quest;
	}
	
	public int getMaxBarometerQuestionPosition() throws Exception{
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement(
					" SELECT MAX(position) AS pos FROM inq_question WHERE type='score' "
			);
			res = stat.executeQuery();
			if(res.next()){
				return res.getInt("pos");
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception("Error while getting Questions By SectionId");
		}
		finally{
			DBHelper.close(res);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
		return 0;
	}

}