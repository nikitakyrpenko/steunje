package com.negeso.module.inquiry.command;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.SecurityGuard;

public class InquiryExportController extends AbstractCommand{

	private static Logger logger = Logger.getLogger(InquiryExportController.class);
	
	private Element model = Xbuilder.createTopEl("model");
	
	private RequestContext req;
    
	public ResponseContext execute() {
		logger.debug("+");
        ResponseContext response = new ResponseContext();
        req = getRequestContext();
        if(!SecurityGuard.isContributor(req.getSession().getUser())) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- not a contributor");
            return response;
        }
		try{
			export(response, req.getParameter("id"));
			return response;
		}
		catch(Exception e){
			logger.error("Error while exporting quistionnaire");
			logger.error(e.getMessage(), e);
			Xbuilder.setAttr(model, "response", "error");
		}
		return response;
	}
	
	private void export(ResponseContext response, String id) throws Exception{
		String export = " ";
		java.sql.Connection conn = null;
		PreparedStatement stat = null;
		PreparedStatement stat1 = null;
		PreparedStatement stat2 = null;
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement("SELECT title FROM inq_question WHERE questionnaire_id=? ORDER BY position");
			stat.setInt(1, new Integer(id));
            ResultSet res = stat.executeQuery();
			while(res.next()){
				export+=";"+res.getString("title");
			}
			export+="\n";
			DBHelper.close(stat);
			stat = conn.prepareStatement("SELECT inq_respondent.id, user_id, submit_time, email  FROM inq_respondent LEFT JOIN inq_user ON inq_user.id=inq_respondent.user_id WHERE inq_respondent.questionnaire_id=? ");
			stat.setInt(1, new Integer(id));
			res = stat.executeQuery();
			int respId;
			boolean first;
			stat1 = conn.prepareStatement("SELECT inq_question.id, inq_question.type, inq_question.alternative, answer, alternative_answer, remark " +
					" FROM inq_question, inq_answer_question " +
					" WHERE inq_answer_question.respondent_id = ? " +
					" AND inq_question.id = inq_answer_question.question_id " +
            		" ORDER BY inq_question.position");
			stat2 = conn.prepareStatement("SELECT title " +
                    " FROM inq_option, inq_answer_option " +
                    " WHERE inq_option.question_id = ? " +
                    " AND inq_answer_option.respondent_id = ? " +
                    " AND inq_answer_option.option_id = inq_option.id " +
                    " AND checked=true " +
                    " ORDER BY inq_option.position");
			while(res.next()){
				respId = res.getInt("id");
				if(res.getObject("email")==null){
					export+="Respondent # "+respId;
				}
				else{
					export+=res.getString("email");
				}
				stat1.setInt(1, respId);
                ResultSet res1 = stat1.executeQuery();
				while(res1.next()){
					export+=";";
					first=true;
					if(res1.getObject("answer")!=null && !res1.getString("answer").equals("")){
						export += res1.getString("answer");
						first = false;
					}
					if(res1.getObject("alternative_answer")!=null && !res1.getString("alternative_answer").equals("")){
						if(!first){
							export += ", ";
						}
						export += res1.getString("alternative_answer");
						first = false;
					}
					if(first){
						stat2.setInt(1, res1.getInt("id"));
						stat2.setInt(2,respId);
                        ResultSet res2 = stat2.executeQuery();
						while (res2.next()){
							if(!first){
								export += ", ";
							}
							export += res2.getString("title");
							first = false;
						}
						DBHelper.close(res2);
					}
				}
				DBHelper.close(res1);
				export+="\n";
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new Error("Error while export");
		}
		finally{
			DBHelper.close(stat2);
			DBHelper.close(stat1);
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
        byte[] bytes = export.getBytes("iso-8859-1");
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		response.getResultMap().put("mime-type", "text/csv; charset=iso-8859-1");
		response.getResultMap().put("stream-length", bytes.length);
        response.getResultMap().put("stream", in);
        response.setResultName(RESULT_SUCCESS);
	}

}
