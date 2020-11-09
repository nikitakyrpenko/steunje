/*
 * @(#)$Id: ListComponent.java,v 1.0, 2005-12-05 10:08:15Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.component;

import java.sql.Connection;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.generators.ListVisitorXmlBuilder;
import com.negeso.framework.list.generators.ListXmlBuilder;
import com.negeso.framework.page.AbstractPageComponent;

/**
 * @author Sergiy Oliynyk
 */
public class ListComponent extends AbstractPageComponent {

    private static Logger logger = Logger.getLogger(ListComponent.class);

    // Input parameters
    public static final String INPUT_LIST_ID = "listId";
    public static final String INPUT_LIST_PATH = "listPath";

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
        return ListVisitorXmlBuilder.getInstance();
    }

    protected Element getStubElement(Document doc) { 
        return Env.createDomElement(doc, "list");
    }
}
