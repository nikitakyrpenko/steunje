/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.command;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.domain.UserCookie;
import com.negeso.framework.page.command.GetPageCommand;
import com.negeso.module.search.BuildIndexExecutor;


/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
public class LogoutCommand extends AbstractCommand{
	private static Logger logger = Logger.getLogger(LogoutCommand.class);
	
	public static final String INPUT_LOGOUT = "logout";
	
	public static final String INTERFACE_LANGUAGE = "interface-language";
	
	public static final String INPUT_LANGUAGE = "interfaceLanguage";
	
	public static final String INPUT_LOGIN = "login";
	
	@Override
	public ResponseContext execute() {
		logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        SessionData session = request.getSessionData();
        
        String interfaceLanguage = request.getParameter(INPUT_LANGUAGE);
        String login = request.getParameter(INPUT_LOGIN);
        
        HttpServletRequest httpRequest =
            (HttpServletRequest)request.getIOParameters()
                .get(WebFrontController.HTTP_SERVLET_REQUEST);
        
        HttpServletResponse httpResponse = 
            (HttpServletResponse)request.getIOParameters()
                .get(WebFrontController.HTTP_SERVLET_RESPONSE);
        
        Cookie[] cookies = httpRequest.getCookies();
        //If user wants to log out, destroy session
        Cookie saveCookie = getSaveCookie(cookies);
        
		
        String lang = session.getLanguageCode();
        session.destroySession();
        session.startSession();
        session.setLanguageCode(lang);
        session.setAttribute(INTERFACE_LANGUAGE, interfaceLanguage);
        if (saveCookie != null){
        	String loginUniqNumber = getUniqNumber(saveCookie.getValue());
            UserCookie userCookie = null;
        	try {
    			userCookie = UserCookie.findById(Long.valueOf(loginUniqNumber));
    			userCookie.delete();    			
    		} catch (ObjectNotFoundException e) {
    			logger.warn("- (cookie for user " + login + "exists," +
    					"but no user in system)");
    		}
    		//delete cookie with current user
    		saveCookie.setMaxAge(0);
    		httpResponse.addCookie(saveCookie);
    		if (userCookie != null)
    			session.setAttribute(LoginCommand.SAVED_LOGIN, userCookie.getLogin());
        }
        User user = getRequestContext().getSession().getUser();
        GetPageCommand.buildPage(user, request, response, true);
        BuildIndexExecutor.build();
        
        logger.debug("- (logging out)");
        return response; 
	}
	
	/**
	 * @param cookies
	 * @return
	 */
	private Cookie getSaveCookie(Cookie[] cookies) {
		Cookie cookie = null;
		if (cookies != null && cookies.length > 0)
		for (int i = 0 ; i < cookies.length; i++){
			if (cookies[i] != null && cookies[i].getName().equals(LoginCommand.USER_COOKIE) && cookies[i].getValue() != null)
				cookie = cookies[i]; 
		}
		return cookie;
	}
	
	/**
	 * @param value
	 * @return
	 */
	private String getUniqNumber(String value) {
		return value.substring(value.indexOf("_") + 1, value.length());
	}

}

