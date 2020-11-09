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
package com.negeso.module.dynamic_item;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.component.ListComponent;
import com.negeso.framework.list.generators.DynamicItemsXmlBuilder;
import com.negeso.framework.list.generators.ListVisitorXmlBuilder;
import com.negeso.framework.list.generators.ListXmlBuilder;
import com.negeso.framework.page.AbstractPageComponent;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DynamicItemComponent extends AbstractPageComponent{
	 private static Logger logger = Logger.getLogger(ListComponent.class);

	    // Input parameters
	    public static final String INPUT_LIST_ID = "listId";
	    public static final String INPUT_LIST_PATH = "listPath";
	    
	    public static final String summeryPartAlign = "dim.summeryPartAlign";
	    public static final String numberOfArticles = "dim.numberOfArticles";
	    public static final String orderBy = "dim.orderBy";

	    public Element getElement(Document document, 
	        RequestContext request, Map parameters )
	    {
	        logger.debug("+");
	        User user = request.getSession().getUser();
	        Connection conn = null;
	        Element element = null;
	        try {
	            Long listId = getLongParameter(parameters, INPUT_LIST_ID, null);
	            if (listId == null)  {
	                logger.error("- id is not specified");
	                Element el = getStubElement(document);
	                el.setAttribute("error", INPUT_LIST_ID + " is not specified");
	                return el;
	            }
	            conn = DBHelper.getConnection();
	            conn.setAutoCommit(false);
	            ListXmlBuilder builder = getListXmlBuilder();
	            String listPath = getStringParameter(parameters, "listPath");
	            element = builder.getElement(conn, document, listId,
	                user != null ? user.getId() : null, listPath);
	            element.setAttribute(summeryPartAlign, Env.getProperty(summeryPartAlign, "right"));
	            element.setAttribute(numberOfArticles, Env.getProperty(numberOfArticles, "5"));
	        }
	        catch (Exception ex) {
	            logger.error("Cannot process request", ex);
	            element = getStubElement(document);
	        }
	        finally {
	            DBHelper.close(conn);
	        }
	        logger.debug("-");
	        return element;
	    }

	    protected ListXmlBuilder getListXmlBuilder() {
	        return DynamicItemsXmlBuilder.getInstance();
	    }

	    protected Element getStubElement(Document doc) { 
	        return Env.createDomElement(doc, "list");
	    }

}