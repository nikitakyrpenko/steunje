/*
 * @(#)Id: RequestContextWrapper.java, 19.11.2007 14:18:32, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class RequestContextWrapper extends RequestContext {

	public RequestContextWrapper(HttpServletRequest request, 
			HttpServletResponse response, ServletConfig config) {
		super(createCommandParameters(request), createSessionData(request),
				createCurrentIO(request, response, config));
		// init with test data
		getSession().setPageId(1);
		getSession().setLanguageCode("en");
		getSession().setUserId(1);
	}

	@SuppressWarnings("unchecked")
	private static Map createCommandParameters(HttpServletRequest request) {
        Map<String, Object> params = request.getParameterMap(); 
        Map<String, Object> modifiableMap = new LinkedHashMap<String, Object>();
        for (String key : params.keySet()) {
            modifiableMap.put(key, params.get(key));
        }
        modifiableMap.put(
            RequestContext.REQUEST_URI,
            new String[] { request.getRequestURI().replaceAll("/+", "/") } );
        modifiableMap.put(
            RequestContext.REQUEST_LOCALES,
            new String[] { getLocales(request) } );
        return modifiableMap;
	}
	
	private static SessionData createSessionData(HttpServletRequest request) {
        return SessionData.getSessionData(request);
	}

	private static Map<String, Object> createCurrentIO(HttpServletRequest request,
			HttpServletResponse response, ServletConfig config) {
	    Map<String, Object> currentIO = new HashMap<String, Object>();
	    currentIO.put(WebFrontController.HTTP_SERVLET_REQUEST, request);
	    currentIO.put(WebFrontController.HTTP_SERVLET_RESPONSE, response);
	    currentIO.put(WebFrontController.SERVLET_CONFIG, config);
	    return currentIO;
	}
	
    @SuppressWarnings("unchecked")
	private static String getLocales(HttpServletRequest request) {
    	StringBuffer locales = new StringBuffer();
    	Enumeration<Locale> e = request.getLocales();
    	while (e.hasMoreElements()) {
    		locales.append(e.nextElement().getLanguage()).append(';');
    	}
    	return locales.toString();
    }

}
