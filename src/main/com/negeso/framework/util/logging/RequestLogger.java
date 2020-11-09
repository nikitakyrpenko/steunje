/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.util.logging;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.commons.fileupload.FileItem;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;

/**
 *
 * @TODO
 *
 * @author		Halyasovskyy Andriy
 * @version		$Revision: $
 *
 */
public class RequestLogger {
	private static Logger logger = Logger.getLogger(RequestLogger.class);

	private static String getSeparatorLine() {
    	return "***************************************";
    }

	private static Map _parameters;

    /** Records every admin's steps and also some visitor's
     *
     * @param request
     * @param parameters
     * @param resultName
     *
     * @return nothing
     *  */
    public static void putRequestToLog(HttpServletRequest request, Map parameters, String resultName) {
    	_parameters = parameters;
    	if (isValidRequest(request)) {
    		String formatedParams = "";

		    for (Object key: parameters.keySet()) {
            	formatedParams += key + " = " + getParameter(request, _parameters, key) + "\n";
            }

            try {
            	SessionData session = SessionData.getSessionData(request);
            	String userName = "";
            	String userLogin = "";

                User user = session.getUser();
                if (user != null) {
                	userName = user.getName();
                	userLogin = " (" + user.getLogin() + ")";
				} else {
					userName = "No user in session";
				}
            	logger.info("\nURI: " + request.getRequestURI() +
            				"\nUser: "  + userName + userLogin +
            				"\nIP: " + request.getRemoteAddr() +
	    					"\nHost: " + request.getRemoteHost() +
	    					"\nMethod: "  + request.getMethod() +
	    					"\nProtocol: "  + request.getProtocol() +
	    					"\nContentType: "  + request.getContentType() +
	    					"\nCharacterEncoding: "  + request.getCharacterEncoding() +
	    					"\nLocale: "  + request.getLocale() +
	    					"\nQuerry:\n" + getSeparatorLine() + "\n" +
	    									formatedParams +
	    									getSeparatorLine() +
	    					"\nResponse: " + resultName
	    					);
            } catch (Exception e){
            	logger.error(e);
            }
		}
    }

    /** Checks if request is made by admin or by visitor,
     *  but in specific places (web forms, newletter subscription, etc)
     *
     * @param request
     *
     * @return true is request is admin's one or visitor's
     * (for some scenario's)
     *  */
    private static boolean isValidRequest(HttpServletRequest request) {
    	return  (	request.getRequestURI().contains("/admin/") ||
    				request.getRequestURI().contains("/su/") ||
    				request.getRequestURI().contains("/send_email") ||
    				isValidParameter(request, "action", "subscribe") ||
    				isValidParameter(request, "action", "unsubscribe")
    			) &&
    			(	!request.getRequestURI().contains("/rtecom/") &&
    				!request.getRequestURI().contains("/help/")
    			)
    	;
    }

    /** Checks if there is specified parameter with specified value
     *  in request
     *
     * @param request
     * @param paramName
     * @param paramValue
     *
     * @return true is request contains specified parameter and
     * it has specified value
     *  */
    private static boolean isValidParameter(HttpServletRequest request, String paramName, String paramValue) {
        for (Object key: _parameters.keySet()) {
        	if (key.toString().equals(paramName) && getParameter(request, _parameters, key).equals(paramValue)) {
        		return true;
			}
        }
    	return false;
    }

    /** Checks if request is POST one
     *
     * @param request
     *
     * @return true is request is POST one, false - otherwise
     *  */
    private static boolean isPostRequest(HttpServletRequest request) {
    	return "POST".equals(request.getMethod());
    }

    /** Returns parameter value for Multipart POST request
     *  or for simple POST one
     *
     * @param request
     * @param map
     * @param key
     *
     * @return Parameter value
     *  */
    private static String getParameter(HttpServletRequest request, Map map, Object key) {
    	if (isPostRequest(request)) {
    		return getPostParameter(map, key);
    	} else {
    		return request.getParameter(key.toString());
    	}
    }

    /** Returns parameter value for Multipart POST request
     *
     * @param map
     * @param key
     *
     * @return Parameter value
     *  */
    private static String getPostParameter(Map map, Object key) {
        String result = "";
        Object[] arr = (Object[]) map.get(key);
        if (arr instanceof FileItem[] ){
            result = ((FileItem)arr[0]).getName();
        } else {
            result += arr[0];
        }
        return result;
    }
}
