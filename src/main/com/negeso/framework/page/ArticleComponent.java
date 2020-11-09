/*
 * @(#)$Id: ArticleComponent.java,v 1.3, 2006-04-26 10:45:31Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.generators.Xbuilder;

/**
 * @author Stanislav
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArticleComponent  extends AbstractPageComponent {
    
    
    private static Logger logger = Logger.getLogger(ArticleComponent.class);
    
    
    public Element getElement(
            Document document,
            RequestContext request,
            Map parameters)
    {
        logger.debug("+");
        try {
            Article article = null;
            if(parameters.get("id") != null) {
                Long id = getLongParameter(parameters, "id");
                article = Article.findById(id);
            } else if (parameters.get("class") != null) {
                String klass = getStringParameter(parameters, "class");
                String langCode = request.getSessionData().getLanguageCode();
                article = Article.findByClass(klass, langCode);
            } else {
                logger.warn("- cannot decide which article to show");
                return getStubElement(document, request, parameters);
            }
            Element elArticle = Xbuilder.createEl(document, "article", null);
            Xbuilder.setAttr(elArticle, "id", article.getId());
            Xbuilder.setAttr(elArticle, "lang", article.getLanguage());
            Xbuilder.setAttr(elArticle, "class", article.getClass_());
            Timestamp modified = article.getLastModified();
            Xbuilder.setAttr(elArticle, "last_modified", modified == null ? null : Env.formatDate(modified));
            Xbuilder.addEl(elArticle, "head", article.getHead());
            Xbuilder.addEl(elArticle, "text", article.getText());
            logger.debug("-");
            return elArticle;
        } catch (Exception e) {
            logger.error("- Exception", e);
            return getStubElement(document, request, parameters);
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

