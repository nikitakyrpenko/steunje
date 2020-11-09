package com.negeso.framework.page.command;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.User;
import com.negeso.framework.page.PageH;
import com.negeso.framework.security.SecurityGuard;

public class UpdateArticleInfoCommand extends AbstractCommand {
	
	private static Logger logger = Logger.getLogger(UpdateArticleInfoCommand.class);

	@SuppressWarnings("unchecked")
	public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_FAILURE);
        Map responseMap = response.getResultMap();
        try {
        	int id = getRequestContext().getIntValue("id");
			String head = getRequestContext().getParameter("head");
			User user = getRequestContext().getSession().getUser();
            PageH page = UpdateArticleTextCommand.getPage(id);
            if(page != null){  // new security method
                if(!SecurityGuard.canContribute(user, page.getContainerId())){
                    throw new Exception("You are not authorized for this operation");
                }
            } else { 
                if (!SecurityGuard.isContributor(user)) {  // old security method
                    throw new Exception("You are not authorized for this operation");
                }
            }
            Article article = Article.findById(new Long(id));
            article.setHead(head);
            article.setLastModified(new Timestamp(System.currentTimeMillis()));
            article.update();
            response.setResultName(RESULT_SUCCESS);
        } catch(Exception e){
        	logger.error("", e);
            responseMap.put(
        		OUTPUT_ERROR, "Cannot update article. " + e.getMessage());
        }
        logger.debug("-");
        return response;
	}
	
}