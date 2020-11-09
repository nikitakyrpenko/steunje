/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class BatchArticleComponent extends AbstractPageComponent {
    
    
    private static Logger logger = Logger.getLogger(BatchArticleComponent.class);
    
    private static String BATCH_ARTICLE_QUERY = 
    	" SELECT * FROM article WHERE article.id IN ( " +
    	"		SELECT page_component_params.value::INTEGER " +
    	"		FROM page_component_params " +
    	"		LEFT JOIN page_component ON " +
    	"			page_component.id = page_component_params.element_id " +
    	"		WHERE  page_component.page_id = %s " +
    	"		AND (page_component.class_name='article-component' " +
    	"		OR page_component.class_name='article-component-through')) ";
    
    
    public Element getElement(
            Document document,
            RequestContext request,
            Map parameters)
    {
        logger.debug("+");
        Connection conn= null;
        try {
        	String pageId = (String)parameters.get("pageId");
        	Element contents = (Element)document.getElementsByTagName("contents").item(0);
        	conn = DBHelper.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(String.format(BATCH_ARTICLE_QUERY, pageId));
        	ResultSet rs = stmt.executeQuery();
        	while (rs.next()) {
				Article article = Article.load(rs);
				Element elArticle = Xbuilder.createEl(document, "article", null);
	            Xbuilder.setAttr(elArticle, "id", article.getId());
	            Xbuilder.setAttr(elArticle, "lang", article.getLanguage());
	            Xbuilder.setAttr(elArticle, "class", article.getClass_());
	            Timestamp modified = article.getLastModified();
	            Xbuilder.setAttr(elArticle, "last_modified", modified == null ? null : Env.formatDate(modified));
	            Xbuilder.addEl(elArticle, "head", article.getHead());
	            Xbuilder.addEl(elArticle, "text", article.getText());
	            contents.appendChild(elArticle);
			}
            DBHelper.close(rs);
            DBHelper.close(stmt);
            logger.debug("-");
            return null;
        } catch (Exception e) {
            logger.error("- Exception", e);
            return getStubElement(document, request, parameters);
        } finally {
        	DBHelper.close(conn);
        }
    }
    
        
    public Element getStubElement(
            Document doc,
            RequestContext request,
            Map parameters)
    {
        logger.debug("+ -");
        return Env.createDomElement(doc, "article");
    }
    
}