/*
 * @(#)$Id: RespondentDao.java,v 1.3, 2006-06-01 13:46:55Z, Anatoliy Pererva$
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
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.Xquery;
import com.negeso.module.inquiry.DuplicateRespondentException;
import com.negeso.module.inquiry.Inquiry;
import com.negeso.module.inquiry.XHelper;
import com.negeso.module.inquiry.domain.Question;
import com.negeso.module.inquiry.domain.Questionnaire;
import com.negeso.module.inquiry.domain.Respondent;

/**
 * 
 *
 * @version     $Revision: 4$
 * @author      Stanislav Demchenko
 */
public class RespondentDao {
    
    private static Logger logger = Logger.getLogger(RespondentDao.class);
	
	private String insertSql = 
		" INSERT INTO inq_respondent " +
		" (id,questionnaire_id,address,submit_time, user_id) " +
		" VALUES (?,?,?,?,?) ";

    public void storeRespondent(Element model) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            if(isDuplicateSubmission(model)) {
                logger.error("- duplicate submission!!!");
                throw new DuplicateRespondentException();
            }
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(insertSql);
            int respId = DBHelper
                    .getNextInsertId(conn, "inq_respondent_id_seq").intValue();
            Element elQuiz = Xquery.elem(model, ".//negeso:questionnaire");
            pstmt.setInt(1, respId);
            pstmt.setInt(2, Integer.parseInt(elQuiz.getAttribute("id")));
            pstmt.setString(3, elQuiz.getAttribute("address"));
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            Node uid = Xquery.node(model, ".//negeso:inquiry_user/@id");
            if (uid == null) {
                pstmt.setObject(5, null);
            } else {
                pstmt.setInt(5, Integer.parseInt(uid.getNodeValue()));
            }
            pstmt.execute();
            storeAnswers(conn, model, respId);
            conn.commit();
            logger.debug("-");
        } catch (SQLException e) {
            DBHelper.rollback(conn);
            logger.error("Exception", e);
            throw new RuntimeException("cannot store a row", e);
        } catch (CriticalException e) {
            DBHelper.rollback(conn);
            logger.error("Exception", e);
            throw new RuntimeException("cannot store a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(pstmt);
        }
    }
    
    private boolean isDuplicateSubmission(Element model) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            Element elQuiz = Xquery.elem(model, ".//negeso:questionnaire");
            pstmt = conn.prepareStatement(
                " SELECT submit_time FROM inq_respondent " +
                " WHERE address = ? AND questionnaire_id = ? " +
                " ORDER BY submit_time DESC");
            pstmt.setString(1, elQuiz.getAttribute("address"));
            pstmt.setInt(2, Integer.parseInt(elQuiz.getAttribute("id")));
            rs = pstmt.executeQuery();
            if(!rs.next()) {
                logger.debug("- IP is unique");
                return false;
            }
            long last = rs.getTimestamp("submit_time").getTime();
            long now = System.currentTimeMillis();
            logger.debug("-");
            logger.error("(now-last)*1000" + (now-last)*1000);
            return (now-last)/1000 < Env.getIntProperty("inquiry.timeout", 40); 
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read rows", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(pstmt);
            DBHelper.close(rs);
        }
    }

    private void storeAnswers(Connection conn, Element respondent, int respId)
            throws SQLException {
        logger.debug("+");
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                " INSERT INTO inq_answer_question " +
                " (respondent_id, question_id, answer, alternative_answer, remark) " +
                " VALUES (?,?,?,?,?) ");
            List<Element> list = Xquery.nodes(respondent, ".//negeso:question");
            for (Element elQ : list) {
                pstmt.setInt(1, respId);
                pstmt.setInt(2, Integer.parseInt(elQ.getAttribute("id")) );
                pstmt.setString(3, elQ.getAttribute("answer"));
                pstmt.setString(4, elQ.getAttribute("alternative_answer"));
                pstmt.setString(5, elQ.getAttribute("remark"));
                pstmt.executeUpdate();
            }
            pstmt = conn.prepareStatement(
                " INSERT INTO inq_answer_option " +
                " (respondent_id, option_id, checked) " +
                " VALUES (?,?,?) ");
            list = Xquery.nodes(respondent, ".//negeso:option");
            for (Element elO : list) {
                pstmt.setInt(1, respId);
                pstmt.setInt(2, Integer.parseInt(elO.getAttribute("id")) );
                pstmt.setBoolean(3, "true".equals(elO.getAttribute("checked")));
                pstmt.executeUpdate();
            }
            logger.debug("-");
        } finally {
            DBHelper.close(pstmt);
        }
    }
    
    private Respondent mapRow(ResultSet rs)
    {
        logger.debug("+");
        try {
            Respondent r = new Respondent();
            r.setId(rs.getInt("id"));
            r.setQuestionnaireId(rs.getInt("questionnaire_id"));
            r.setUserId(rs.getInt("user_id"));
            r.setEmail(rs.getString("email"));
            r.setIpAddress(rs.getString("address"));
            r.setSubmitTime(rs.getTimestamp("submit_time"));
            logger.debug("-");
            return r;
        } catch (SQLException e) {
            logger.error("- Exception", e);
            throw new RuntimeException("Cannot read a row", e);
        }
    }

    public List<Respondent> getRespondentsByQuestionnaireId(int id) {
        logger.debug("+");
        List<Respondent> result = new LinkedList<Respondent>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT inq_respondent.*, " +
                " (SELECT email FROM inq_user WHERE id = user_id) AS email " +
                " FROM inq_respondent WHERE questionnaire_id = " + id +
                " ORDER BY id ");
            while(rs.next()) result.add(mapRow(rs));
            logger.debug("-");
            return result;
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read rows", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stmt);
            DBHelper.close(rs);
        }
    }

    public void modelRespondentWithAnswers(Element parent, int respondentId) {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            rs = stmt.executeQuery(
                 " SELECT inq_respondent.*, " +
                 " (SELECT email FROM inq_user WHERE id = user_id) AS email " +
                 " FROM inq_respondent " +
                 " WHERE inq_respondent.id = " + respondentId);
            rs.next();
            Respondent resp = mapRow(rs);
            Inquiry inquiry = Inquiry.getInstance();
            Questionnaire quiz = inquiry.loadQuestionnaire(resp.getQuestionnaireId());
            Element elQuiz = XHelper.addXmlQuestionnaire(parent, quiz);
            Element elResp = XHelper.addXmlRespondent(elQuiz, resp);
            rs = stmt.executeQuery(
                " SELECT inq_question.*, answer, alternative_answer, remark " +
                " FROM inq_question, inq_answer_question " +
                " WHERE inq_answer_question.respondent_id = " + respondentId +
                " AND inq_question.id = inq_answer_question.question_id " +
                " ORDER BY inq_question.position ");
            while(rs.next()) {
                Element elQ = Xbuilder.addEl(elResp, "question", "");
                Xbuilder.setAttrForce(
                        elQ, "title", rs.getString("title"));
                Xbuilder.setAttrForce(elQ, "answer", rs.getString("answer"));
                Xbuilder.setAttrForce(
                        elQ, "alternative", rs.getString("alternative"));
                Xbuilder.setAttrForce(elQ, "alternative_answer",
                        rs.getString("alternative_answer"));
                elQ.setAttribute("remark", rs.getString("remark"));
                int qId = rs.getInt("id");
                rs2 = stmt2.executeQuery(
                    " SELECT title, checked " +
                    " FROM inq_option, inq_answer_option " +
                    " WHERE inq_option.question_id = " + qId +
                    " AND inq_answer_option.respondent_id = " + respondentId +
                    " AND inq_answer_option.option_id = inq_option.id " +
                    " ORDER BY inq_option.position ");
                while(rs2.next()) {
                    Element elO = Xbuilder.addEl(elQ, "option", "");
                    Xbuilder.setAttrForce(elO, "title", rs2.getString("title"));
                    Xbuilder.setAttrForce(
                            elO, "checked", "" + rs2.getBoolean("checked"));
                }
            }
            logger.debug("-");
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stmt);
            DBHelper.close(stmt2);
            DBHelper.close(rs);
            DBHelper.close(rs2);
        }
    }

    public Respondent loadRespondent(int id) {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT inq_respondent.*, " +
                " (SELECT email FROM inq_user WHERE id = user_id) AS email " +
                " FROM inq_respondent WHERE id = " + id);
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
            DBHelper.close(conn);
            DBHelper.close(stmt);
            DBHelper.close(rs);
        }
    }

    public void deleteRespondent(int id) {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM inq_respondent WHERE id =" + id);
            logger.debug("-");
        } catch (SQLException e) {
            throw new RuntimeException("cannot delete a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stmt);
        }
    }
	
	public void storeBarometerRespondent(int questionnaireId, RequestContext request) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(insertSql);
			int respId = DBHelper.getNextInsertId(conn, "inq_respondent_id_seq").intValue();
			stat.setInt(1, respId);
            stat.setInt(2, questionnaireId);
			stat.setString(3, request.getRemoteAddr());
			stat.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			stat.setObject(5, null);
            stat.execute();
			storeBarometerAnswers(respId, questionnaireId, request, conn);
            conn.commit();
			conn.setAutoCommit(true);
        } catch (Exception e) {
            DBHelper.rollback(conn);
            logger.error("Exception", e);
            throw new RuntimeException("cannot store a row", e);
        } finally {
            DBHelper.close(conn);
            DBHelper.close(stat);
        }
	}
	
	public void storeBarometerAnswers(int respId, int questId, RequestContext request, Connection conn) throws Exception{
		List qList = new QuestionDao().getQuestionsByQuestionnaireId(questId);
		Question quest = null;
		PreparedStatement stat = conn.prepareStatement(
				" INSERT INTO inq_answer_question " +
                " (respondent_id, question_id, answer, alternative_answer, remark, intvalue) " +
                " VALUES (?,?,?,?,?, ?) ");
		for(int i=0; i<qList.size(); i++){
			quest = (Question) qList.get(i);
			stat.setInt(1, respId);
			stat.setInt(2, quest.getId());
			stat.setString(3, "");
			stat.setString(4, "");
			stat.setString(5, (request.getParameter("com"+quest.getId())==null?"": request.getParameter("com"+quest.getId())));
			stat.setInt(6, new Long(request.getParameter("sel"+quest.getId())).intValue());
			stat.executeUpdate();
		}
		stat.close();
	}

}

