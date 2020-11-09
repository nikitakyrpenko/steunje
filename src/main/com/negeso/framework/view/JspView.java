/*
 * @(#)JspView.java  Created on 06.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;


/**
 * This view is for MVC of Negeso W/CMS.<p>
 * Example usage:
 * <pre>
 *	&lt;matcher uri="/admin/setlog" command-name="simple-command"&gt;
 *		&lt;bind result="success" view="jsp-view"&gt;
 *			&lt;parameter name="jsp" value="/WEB-INF/jsp/test.jsp"/&gt;
 *		&lt;/bind&gt;
 *	&lt;/matcher&gt;
 * </pre>
 * <p/>
 * For method GET, request parameters can be accessed from JSPs as always, e.g.:
 * ${ param.foo }
 * <p>
 * For method POST, JSPs have no access to request parameters if InputStream
 * has already been read.
 * To overcome this limitation, JspView exposes RequestContext as request
 * attribute "requestContext", e.g.:
 * ${ requestContext.string("foo", "defaultBar") }
 *  
 * @author Stanislav Demchenko
 */
public class JspView extends AbstractHttpView {
    
    private static final Logger logger = Logger.getLogger(JspView.class);
    
    public void process(RequestContext request, ResponseContext response) {
        logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        HttpServletRequest httpRequest = getServletRequest(request);
        setHeadersToDisableCaching(httpResponse);
        for (String key : response.getResultMap().keySet())
        	httpRequest.setAttribute(key, response.get(key));
        httpRequest.setAttribute("requestContext", request);
        httpRequest.setAttribute(Config.FMT_LOCALE, getInterfaceLanguage(request));
        try {
        	httpRequest.getRequestDispatcher((String) response.get(KEY_JSP))
        		.forward(httpRequest, httpResponse);
        	logger.info("-");
            return;
        } catch (Exception e) {
        	handleException(e);
        }
    }
    
}