/*
 * @(#)$Id: QuestionnaireDao.java,v 1.3, 2006-06-01 13:46:55Z, Anatoliy Pererva$
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

import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.inquiry.DuplicateQuestionnaireException;
import com.negeso.module.inquiry.domain.Questionnaire;

/**
 * 
 * 
 * @version $Revision: 4$
 * @author Stanislav Demchenko
 */
public class QuestionnaireDao {
    
    private static Logger logger = Logger.getLogger(QuestionnaireDao.class);
	
	private static String latestBarometerQuestionnnaireSql =
		" SELECT * FROM inq_questionnaire " +
		" WHERE barometer=true AND lang_id=? AND publish IS NOT NULL " +
		" ORDER BY publish DESC LIMIT 1";
	private static String versionsSql =
		" SELECT * FROM inq_questionnaire " +
		" WHERE barometer=true AND lang_id=? AND publish IS NOT NULL " +
		" ORDER BY publish DESC";
	private static String newVersionSql = 
		" INSERT INTO inq_questionnaire " +
		" (id, lang_id, title, publish, is_public, is_multipage, rf_height, rf_width, rf_multiline, show_answers, barometer ) " +
		" VALUES (?,?,?,now(), true, true, 0, 0, false, true, true) ";
    private static final String INSERT_STATEMENT = 
        " INSERT INTO inq_questionnaire ( " +
        " id, lang_id,title,publish,expire, is_public, is_multipage, " +
        " intro_id, conclusion_id, " +
        " rf_height, rf_width, rf_multiline, show_answers, barometer " +
        " ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
    public List<Questionnaire> getQuestionnaireList(Language lang)
    {
        logger.debug("+");
        List<Questionnaire> result = new LinkedList<Questionnaire>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT * FROM inq_questionnaire " +
                " WHERE lang_id = " + lang.getId() + " AND barometer!=true ORDER BY id ");
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            logger.error("Exception", e);
        } finally {
            DBHelper.close(rs, stmt, conn);
        }
        logger.debug("-");
        return result;
    }
    
    public void updateQuestionnaire(Questionnaire q)
    {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBHelper.getConnection();
            checkTitleNotPresentYet(conn, q);
            pstmt = conn.prepareStatement(
                " UPDATE inq_questionnaire SET " +
                " lang_id=?, title=?, publish=?, expire=?, is_public=?, is_multipage=?, " +
                " rf_height=?, rf_width=?, rf_multiline=?, show_answers=?, barometer=? " +
                " WHERE id=? ");
            pstmt.setInt(1, q.getLangId());
            pstmt.setString(2, q.getTitle());
            pstmt.setTimestamp(3, q.getPublish());
            pstmt.setTimestamp(4, q.getExpired());
            pstmt.setBoolean(5, q.isPublic());
            pstmt.setBoolean(6, q.isMultipage());
            pstmt.setInt(7, q.getRfHeight());
            pstmt.setInt(8, q.getRfWidth());
            pstmt.setBoolean(9, q.isRfMultiline());
            pstmt.setBoolean(10, q.isShowAnswers());
			pstmt.setBoolean(11, q.isBarometer());
            pstmt.setInt(12, q.getId());
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
    
    private PreparedStatement createInsertStatement(Long id, Connection conn,
    		Questionnaire q) throws SQLException {
    	PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT);
        createIntroAndConclusion(q);
        q.setId(id.intValue());
        stmt.setInt(1, q.getId());
        stmt.setInt(2, q.getLangId());
        if(q.getTitle() == null) q.setTitle(generateUniqueQuizName());
        stmt.setString(3, q.getTitle());
        stmt.setTimestamp(4, q.getPublish());
        stmt.setTimestamp(5, q.getExpired());
        stmt.setBoolean(6, q.isPublic());
        stmt.setBoolean(7, q.isMultipage());
        stmt.setInt(8, q.getIntroId());
        stmt.setInt(9, q.getConclusionId());
        stmt.setInt(10, q.getRfHeight());
        stmt.setInt(11, q.getRfWidth());
        stmt.setBoolean(12, q.isRfMultiline());
        stmt.setBoolean(13, q.isShowAnswers());
		stmt.setBoolean(14, q.isBarometer());
		return stmt;
    }
    
    public Long insertQuestionnaire(Questionnaire q) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBHelper.getConnection();
           	checkTitleNotPresentYet(conn, q);
           	Long id = DBHelper.getNextInsertId(conn,"inq_questionnaire_id_seq");
           	pstmt = createInsertStatement(id, conn, q);
            pstmt.execute();
            q.setId(id.intValue());
            logger.debug("-");
            return id;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("cannot store a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(pstmt);
        }
    }

    private void checkTitleNotPresentYet(Connection cn, Questionnaire quiz)
    throws SQLException {
        logger.debug("+");
        PreparedStatement st = null;
        try {
            st = cn.prepareStatement(
                " SELECT count(*) FROM inq_questionnaire " +
                " WHERE lang_id = ? AND title = ? " + 
                (quiz.getId() == 0 ? "" : " AND id != " + quiz.getId()) );
            st.setInt(1, quiz.getLangId());
            st.setString(2, quiz.getTitle());
            ResultSet rs = st.executeQuery();
            rs.next();
            if (rs.getInt(1) != 0) {
                logger.warn("- throwing DuplicateQuestionnaireException");
                throw new DuplicateQuestionnaireException();
            }
            logger.debug("-");
        } finally {
            DBHelper.close(st);
        }
    }

    private void createIntroAndConclusion(Questionnaire q) {
        Article article = new Article();
        article.setHead("head");
        article.setText("Intro text");
        q.setIntroId(article.insert().intValue());
        article = new Article();
        article.setHead("head");
        article.setText("Conclusion text");
        q.setConclusionId(article.insert().intValue());
    }

    public void deleteQuestionnaire(int id)
    {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM inq_questionnaire WHERE id =" + id);
            logger.debug("-");
        } catch (SQLException e) {
            throw new RuntimeException("cannot delete a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stmt);
        }
    }

    public Questionnaire loadQuestionnaire(int id)
    {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT * FROM inq_questionnaire WHERE id = " + id);
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

    public List<Questionnaire> getActiveQuestionnaires(Language lang) {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT * FROM inq_questionnaire " +
                " WHERE days_left(publish, expire) > 0" +
                " AND lang_id = " + lang.getId() +
                " AND barometer!=true " +
                " ORDER BY id ");
            List<Questionnaire> result = new LinkedList<Questionnaire>();
            while(rs.next()) result.add(mapRow(rs));
            logger.debug("-");
            return result;
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read a row", e);
        } finally {
            DBHelper.close(rs, stmt, conn);
        }
    }

    private Questionnaire mapRow(ResultSet rs) {
        logger.debug("+");
        try {
            Questionnaire q = new Questionnaire();
            q.setId(rs.getInt("id"));
            q.setLangId(rs.getInt("lang_id"));
            q.setTitle(rs.getString("title"));
            q.setPublish(rs.getTimestamp("publish"));
            q.setExpired(rs.getTimestamp("expire"));
            q.setIntroId(rs.getInt("intro_id"));
            q.setConclusionId(rs.getInt("conclusion_id"));
            q.setPublic(rs.getBoolean("is_public"));
            q.setMultipage(rs.getBoolean("is_multipage"));
            q.setRfHeight(rs.getInt("rf_height"));
            q.setRfWidth(rs.getInt("rf_width"));
            q.setRfMultiline(rs.getBoolean("rf_multiline"));
            q.setShowAnswers(rs.getBoolean("show_answers"));
			q.setBarometer(rs.getBoolean("barometer"));
            logger.debug("-");
            return q;
        } catch (SQLException e) {
            logger.error("- Exception", e);
            throw new RuntimeException("Cannot read a row", e);
        }
    }
    
    private String generateUniqueQuizName() {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.prepareStatement(
                    "SELECT count(*) FROM inq_questionnaire WHERE title=? ");
            int count = 1;
            while (true) {
                String name = "New questionnaire " + count++;
                st.setString(1, name);
                ResultSet rs = st.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    logger.debug("-");
                    return name;
                }
                if (count == Integer.MAX_VALUE) {
                    throw new Exception("Cannot generate unique title");
                }
            }
        } catch (Exception e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read a row", e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }
    }
	
	public Questionnaire getLatestBarometerQuestionnaire(int langId) throws Exception{
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement(latestBarometerQuestionnnaireSql);
			stat.setInt(1, langId);
			res = stat.executeQuery();
			if(res.next()){
				return mapRow(res);
			}
			else{
				return null;
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception("Error while getting Latest Barometer Questionnaire ");
		}
		finally{
			DBHelper.close(res);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
	}
	
	public List getVersions(int langId) throws Exception{
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		List list = new ArrayList();
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement(versionsSql);
			stat.setInt(1, langId);
			res = stat.executeQuery();
			while(res.next()){
				list.add(mapRow(res));
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception("Error while getting Versions ");
		}
		finally{
			DBHelper.close(res);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
		return list;
	}
	
	public static void addNewVersion(int langId)throws Exception{
		Connection conn = null;
		PreparedStatement stat = null;
		PreparedStatement stat2 = null;
		ResultSet res = null;
		try{
			conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(latestBarometerQuestionnnaireSql);
			stat.setInt(1, langId);
			res = stat.executeQuery();
			if(res.next()){
				Long id = DBHelper.getNextInsertId(conn, "inq_questionnaire_id_seq");
				stat2 = conn.prepareStatement(newVersionSql);
				stat2.setInt(1, id.intValue());
				stat2.setInt(2, res.getInt("lang_id"));
				stat2.setString(3, res.getString("title"));
				stat2.executeUpdate();
				new SectionDao().copySections(conn, res.getInt("id"), id.intValue());
			}
			conn.commit();
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception("Error while add New Version ");
		}
		finally{
			conn.rollback();
			conn.setAutoCommit(true);
			DBHelper.close(res);
			DBHelper.close(stat2);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
	}

	public void copyQuestionnaire(int questionnaireId, String title) {
		logger.debug("+");
		Connection conn = null;
		PreparedStatement selectStat = null;
		PreparedStatement insertStat = null;
		ResultSet rs = null;
		
		try {
			conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			selectStat = conn.prepareStatement(" SELECT * FROM inq_questionnaire WHERE id = ?");
			selectStat.setInt(1, questionnaireId);
			rs = selectStat.executeQuery();
            if(!rs.next()) {
                logger.warn("- cannot find questionnaire row # " + questionnaireId);
                return;
            } 
			
            Questionnaire questionnaire = mapRow(rs);
            questionnaire.setTitle(title);
			Long id = DBHelper.getNextInsertId(conn, "inq_questionnaire_id_seq");
			insertStat = createInsertStatement(id, conn, questionnaire);
			insertStat.executeUpdate();
			new QuestionDao().copyQuestions(conn, questionnaireId, id.intValue());
			conn.commit();
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Error while copy a questionnaire");
		}
		finally {
			try {
				conn.rollback();
			} catch (Exception e) {}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {}
			DBHelper.close(rs);
			DBHelper.close(selectStat);
			DBHelper.close(insertStat);
			DBHelper.close(conn);
		}
		logger.debug("-");
	}	
	
}