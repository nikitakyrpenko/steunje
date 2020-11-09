/*
 * @(#)$Id: StatisticsXmlBuilder.java,v 1.0, 2006-06-01 13:46:04Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 *
 * @version     $Revision: 1$
 * @author      Stanislav Demchenko
 */
public class StatisticsXmlBuilder {
    
    private static Logger logger =
        Logger.getLogger(StatisticsXmlBuilder.class);
    
    public void buildXmlStatistics(Element model, int quizId) {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            addXmlQuiz(conn, model, quizId);
            logger.debug("-");
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read a row", e);
        } finally {
            DBHelper.close(conn);
        }
    }
    
    private void addXmlQuiz(Connection conn, Element model, int quizId)
    throws SQLException {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Element elQuiz =
                XHelper.addXmlQuestionnaire(
                    model, Inquiry.getInstance().loadQuestionnaire(quizId));
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                    " SELECT COUNT(*) AS cnt FROM inq_respondent " +
                    " WHERE questionnaire_id = " + quizId);
            rs.next();
            Xbuilder.setAttrForce(
                    elQuiz, "total_respondents", rs.getString("cnt"));
            addXmlQuestions(conn, elQuiz, quizId);
            logger.debug("-");
        } finally {
            DBHelper.close(rs);
            DBHelper.close(stmt);
        }
    }
    
    private void addXmlQuestions(Connection conn, Element elQuiz, int quizId)
    throws SQLException {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                    " SELECT inq_question.*, " +
                    " ( SELECT COUNT(*) FROM inq_answer_question " +
                    "   WHERE length(answer) > 0 " +
                    "   AND question_id = inq_question.id) AS anscnt, " +
                    " ( SELECT COUNT(*) FROM inq_answer_question " +
                    "   WHERE length(alternative_answer) > 0 " +
                    "   AND question_id = inq_question.id) AS altcnt " +
                    " FROM inq_question " +
                    " WHERE questionnaire_id = " + quizId +
                    " ORDER BY position ");
            while(rs.next()) {
                Element elQ = Xbuilder.addEl(elQuiz, "question", "");
                Xbuilder.setAttrForce(elQ, "title", rs.getString("title"));
                Xbuilder.setAttrForce(
                        elQ, "alternative", rs.getString("alternative"));
                Xbuilder.setAttrForce(elQ, "type", rs.getString("type"));
                Xbuilder.setAttrForce(elQ, "anscnt", rs.getString("anscnt"));
                Xbuilder.setAttrForce(elQ, "altcnt", rs.getString("altcnt"));
                addXmlOptions(conn, elQ, rs.getInt("id"));
            }
            logger.debug("-");
        } finally {
            DBHelper.close(rs);
            DBHelper.close(stmt);
        }
    }
    
    private void addXmlOptions(Connection conn, Element elQ, int qid)
    throws SQLException {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                    " SELECT title, " +
                    " (SELECT COUNT(*) FROM inq_answer_option " +
                    "  WHERE checked AND option_id = inq_option.id) AS cnt " +
                    " FROM inq_option WHERE question_id = " + qid +
                    " ORDER BY position ");
            while(rs.next()) {
                Element elOpt = Xbuilder.addEl(elQ, "option", "");
                Xbuilder.setAttrForce(elOpt, "title", rs.getString("title"));
                Xbuilder.setAttrForce(elOpt, "count", "" + rs.getInt("cnt"));
            }
            logger.debug("-");
        } finally {
            DBHelper.close(rs);
            DBHelper.close(stmt);
        }
    }
    
}

