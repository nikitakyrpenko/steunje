package com.negeso.module.inquiry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.module.inquiry.domain.Section;

public class SectionDao {
	private static Logger logger = Logger.getLogger( SectionDao.class );
	
	private static String sectionsByQuestionnaireIdSql = 
		" SELECT * FROM inq_section WHERE questionnaire_id=? ORDER BY position ";
	private static String insertSectionSql = 
		" INSERT INTO inq_section " +
		" (id, name, position, questionnaire_id) " +
		" VALUES (?,?,?,?)";
	
	public List getSectionsByQuestionnaireId(int questionnaireId) throws Exception{
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		List list = new ArrayList();
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement(sectionsByQuestionnaireIdSql);
			stat.setInt(1, questionnaireId);
			res = stat.executeQuery();
			while(res.next()){
				list.add(mapRow(res));
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Exception("Error while getting Sections list");
		}
		finally{
			DBHelper.close(res);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
		return list;
	}
	
	private Section mapRow(ResultSet res) throws Exception{
		Section sect = new Section();
		sect.setId(res.getInt("id"));
		sect.setName(res.getString("name"));
		sect.setPosition(res.getInt("position"));
		if(res.getObject("questionnaire_id")==null){
			sect.setQuestionnaireId(null);
		}
		else{
			sect.setQuestionnaireId(res.getLong("questionnaire_id"));
		}
		return sect;
	}
	
	public void copySections(Connection conn, int prevQuestionnaireId, int newQuestionnaireId) throws Exception{
		PreparedStatement stat = conn.prepareStatement(sectionsByQuestionnaireIdSql);
		PreparedStatement stat2 = conn.prepareStatement(insertSectionSql);
		stat.setInt(1, prevQuestionnaireId);
		ResultSet res = null;
		Long id;
		QuestionDao qDao = new QuestionDao();
		res = stat.executeQuery();
		while(res.next()){
			id = DBHelper.getNextInsertId(conn, "inq_section_id_seq");
			stat2.setInt(1, id.intValue());
			stat2.setString(2, res.getString("name"));
			stat2.setInt(3, res.getInt("position"));
			stat2.setInt(4, newQuestionnaireId);
			stat2.executeUpdate();
			qDao.copyQuestions(conn, res.getInt("id"), id.intValue(), newQuestionnaireId);
		}
		stat2.close();
		stat.close();
	}
}
