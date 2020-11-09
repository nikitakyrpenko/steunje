/*
 * @(#)RedirectView.java  Created on 07.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
 
package com.negeso.framework.view;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.WebFrontController;


/**
 * Sends redirection header to the browser. Destination (parameter
 * 'destination') is usually defined in the sitemap.
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */
public class RedirectView extends AbstractHttpView {


    private static Logger logger = Logger.getLogger(RedirectView.class);
    
    
    public static final String PARAMETER_DESTINATION = "destination";

    public void process(RequestContext request, ResponseContext response) {
        logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        HttpServletRequest httpRequest =
            (HttpServletRequest) request.getIOParameters()
                .get(WebFrontController.HTTP_SERVLET_REQUEST);
        String destination = (String) response.get(PARAMETER_DESTINATION);
        /*
         * The next check attempts to not go above the root of the webapp's
         * context. This can help, if the application is in some context
         * (not in the root).
         * It is not an ideal solution. Destination like '../../' may fail.
         * But simpler destinations ('../index.html', 'login.htm' etc)
         * will work.
         */ 
        if(httpRequest.getRequestURI().indexOf('/', 1) == -1){
            destination = httpRequest.getContextPath() + "/" + destination;
        }
        logger.warn("Resource " + httpRequest.getRequestURI()
             + " is redirected to " + destination);
        try {
            httpResponse.sendRedirect(destination);
        } catch (IOException e) {
            logger.error("- IOException", e);
        }
        logger.debug("-");
    }

}
