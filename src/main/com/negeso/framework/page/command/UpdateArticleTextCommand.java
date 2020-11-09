package com.negeso.framework.page.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainObject;
import com.negeso.framework.domain.User;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;

public class UpdateArticleTextCommand extends AbstractCommand {
	
	private static Logger logger = Logger.getLogger(UpdateArticleTextCommand.class);

	@SuppressWarnings("unchecked")
	public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_FAILURE);
        Map responseMap = response.getResultMap();
        try {
        	updateArticleText(
        			getRequestContext().getSession().getUser(),
        			getRequestContext().getIntValue("id"),
        			getRequestContext().getParameter("text"));
            response.setResultName(RESULT_SUCCESS);
        } catch(Exception e){
        	logger.error("", e);
            responseMap.put(
        		OUTPUT_ERROR, "Cannot update article. " + e.getMessage());
        }
        logger.debug("-");
        return response;
	}

    /**
     * Updates contents of an article
     * @param user 
     * @param cid 
     * @param id id
     * @param text text of the article
     * @return true (success); if fails, an exception is thrown
     */
    public void updateArticleText(User user, int id, String text) throws Exception {
        logger.debug("+");        
        Connection conn = null;
        try {
            Article article = Article.findById(new Long(id));
            article.setText(text);
            article.setLastModified(new Timestamp(System.currentTimeMillis()));
            article.update();
            conn = DBHelper.getConnection();
        } finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
    }
    
    /**
     * Looks for page which includes the article.
     * This method returns the first page which includes the article as page
     * element.
     * 
     * @param articleId
     * @return Page or null
     */
    static PageH getPage(int articleId) {
        logger.debug("+");
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(
                " SELECT DISTINCT page.id " +
                " FROM page, article, page_component, page_component_params " +
                " WHERE page_component.page_id = page.id " +
                " AND page_component.class_name = 'article-component' " +
                " AND page_component_params.element_id = page_component.id " +
                " AND page_component_params.name = 'id' " +
                " AND page_component_params.value = " + articleId);
            if(rs.next()){
                PageH page =
                	PageService.getInstance().findById( DomainObject.makeLong(rs.getLong("id")));
                logger.debug("-");
                return page;
            }
        } catch (Exception e) {
            logger.error("SQLException", e);
        } finally {
        	DBHelper.close(rs, st, conn);
        }
        logger.debug("- page not found");
        return null;
    }
	
}