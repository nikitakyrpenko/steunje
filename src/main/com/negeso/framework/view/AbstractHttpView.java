/*
 * @(#)AbstractView.java  Created on 20.02.2004
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

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.WebFrontController;


/**
 * Contains constants and helper methods.
 * 
 * @author Stanislav Demchenko
 */

abstract public class AbstractHttpView implements View {
	
	/**
	 * Response mime-type. Include charset where possible to help the browser
	 * to render the file.
	 */
	public static final String HEADER_MIME_TYPE = "Content-Type";
	
    /** The key is used to pass values from Commands to Views */
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    
    /** Expiration date (in milliseconds since January 1, 1970) */
    public static final String HEADER_EXPIRES = "Expires";
    
    /** The key is used to pass java.io.File from Command to View */
    public static final String KEY_FILE = "file";
    
    /** The key is used to pass W3C DOM Document from Command to View */
    public static final String KEY_XML = "xml";
    
    /**
     * The key is used to pass <b>resourceId</b> of an XSL file
     * (to be resolved using ResourceMap).
     */
    public static final String KEY_XSL = "xsl";
    
    /** The key is used to pass values from Commands to Views */
    public static final String KEY_XSL_LOCATION = "xsl-location";
    
    /** The key is used to pass JSP file location (relative to webapp root) */
    public static final String KEY_JSP = "jsp";
    
    public static final String HTML_MIME_TYPE = "text/html; charset=UTF-8";
    
    private static final Logger logger = Logger.getLogger(AbstractHttpView.class);
    
    /** Rethrows an exception as RuntimeException or simply ignores it  */
    public void handleException(Exception e) {
		logger.debug("+");
    	if (isClientAbortException(e) ||
			(e.getCause() != null && isClientAbortException(e.getCause())))
    	{
    		logger.debug("- ClientAbortException");
    		return;
    	}
    	throw new RuntimeException(e);
	}
	
    private static boolean isClientAbortException(Throwable e) {
    	logger.debug("+ -");
    	String str = "ClientAbortException";
    	if (e.getClass().getName().contains(str)) return true;
		if (e.getMessage() != null && e.getMessage().contains(str)) return true;
    	return false;
    }
    
    /** Returns original HttpServletResponse. */
    public static HttpServletResponse getServletResponse(RequestContext request) {
    	logger.debug("+ -");
    	return (HttpServletResponse) request.getIOParameters()
    		.get(WebFrontController.HTTP_SERVLET_RESPONSE);
    }
    
    /** Returns original HttpServletRequest. */
    public static HttpServletRequest getServletRequest(RequestContext request) {
    	logger.debug("+ -");
    	return (HttpServletRequest) request.getIOParameters()
    		.get(WebFrontController.HTTP_SERVLET_REQUEST);
    }
    
    public static void setHeadersToDisableCaching(HttpServletResponse response) {
    	logger.debug("+");
    	response.setDateHeader("Expires", System.currentTimeMillis()-15*60*1000);
    	response.setHeader("Pragma", "no-cache");
    	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    	logger.debug("-");
    }
    
    public static String getInterfaceLanguage(RequestContext req) {
        logger.debug("+ -");
        String lang = (String) req.getSession().getAttribute("interface-language");
		return lang != null ? lang : "en";
    }
    
}