/*
 * @(#)$Id: NewsLineComponent.java,v 1.1, 2007-01-09 18:53:17Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.newsline.component;

import java.sql.Connection;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;
import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.module.newsline.generators.NewsLineParameters;
import com.negeso.module.newsline.generators.NewsLineXmlBuilder;


/**
 * @author Sergiy Oliynyk
 */
public class NewsLineComponent extends AbstractPageComponent {

    private static Logger logger = Logger.getLogger(NewsLineComponent.class);

    // Input parameters
    public static final String INPUT_LISTS = "lists";
    public static final String INPUT_ITEMS_PER_PAGE = "itemsPerPage";
    public static final String INPUT_PAGE_NUMBER = "page";
    public static final String INPUT_PARAMETERS = "parameters";
    public static final String INPUT_INCLUDE_NULL = "includeNull";
    public static final String INPUT_INCLUDE_NOT_NULL = "includeNotNull";

    public Element getElement(Document document, 
        RequestContext request, Map parameters )
    {
        logger.debug("+");
        User user = request.getSession().getUser();
        Connection conn = null;
        Element element = null;
        try {
            Long userId = user != null ? user.getId() : null;
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            NewsLineParameters param = getNewsLineParameters(request, parameters);
            NewsLineXmlBuilder builder = NewsLineXmlBuilder.getInstance();
            element = builder.getElement(conn, document, userId, param, request.getLangId());
            element.setAttribute(INPUT_PAGE_NUMBER, String.valueOf(param.getPageNumber()));
        }
        catch (Exception ex) {
            logger.error("- Cannot process request", ex);
            element = getStubElement(document);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return element;
    }

    private NewsLineParameters getNewsLineParameters(RequestContext request,
        Map parameters) throws CriticalException {
        logger.debug("+");
        String lists = getStringParameter(parameters, INPUT_LISTS);
        String param = getStringParameter(parameters, INPUT_ITEMS_PER_PAGE);
        if (lists == null || lists.length() == 0 || param == null)
            throw new CriticalException("Obligatory parameter is missing");
        int paging = Integer.parseInt(param);
        param = request.getParameter(INPUT_PAGE_NUMBER);             
        int page = param != null && param.length() > 0 ? Integer.parseInt(param) : 1;
        String parametersStr = getStringParameter(parameters, INPUT_PARAMETERS);
        boolean includeNull = "true".equals(getStringParameter(parameters, INPUT_INCLUDE_NULL));
        boolean includeNotNull = "true".equals(getStringParameter(parameters, INPUT_INCLUDE_NOT_NULL));
        NewsLineParameters newsLineParameters = new NewsLineParameters(lists, 
            paging, page, parametersStr, includeNull, includeNotNull);
        logger.debug("-");
        return newsLineParameters;
    }

    public Element getStubElement(Document doc) {
        logger.debug("+");
        return Env.createDomElement(doc, "list");
    }
}
