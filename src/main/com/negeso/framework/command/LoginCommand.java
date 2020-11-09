/*
 * @(#)LoginCommand.java  Created on 04.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.command;


import java.sql.Connection;
import java.sql.Timestamp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.domain.UserCookie;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.dictionary.generators.LanguagesXmlBuilder;

/**
 * This command processes authentication of editors and administrators
 * (but not for visitors).
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class LoginCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(LoginCommand.class);
    
    public static final String INPUT_LOGIN = "login";
    
    public static final String INPUT_PASSWORD = "password";
    
    public static final String INPUT_ISSAVE = "issave";
    
    public static final String INPUT_LANGUAGE = "interfaceLanguage";
    
    public static final String RESULT_AUTHENTICATED = "authenticated";
    
    public static final String RESULT_DENIED = "denied";
    
    public static final String INTERFACE_LANGUAGE = "interface-language";

    public static final String CONTENT_FREEZE_FLAG = "content-freeze";
    
    public static final String USER_COOKIE = "user-cookie";
    
    public static final String SAVED_LOGIN = "saved_login";
    
    public static boolean saveMe = false;
    
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_DENIED);
        String login = request.getParameter(INPUT_LOGIN);
        String password = request.getParameter(INPUT_PASSWORD);
        String isSave = request.getParameter(INPUT_ISSAVE);
        String interfaceLanguage = request.getParameter(INPUT_LANGUAGE);

        HttpServletRequest httpRequest =
            (HttpServletRequest)request.getIOParameters()
                .get(WebFrontController.HTTP_SERVLET_REQUEST);
        
        Cookie[] cookies = httpRequest.getCookies();
        HttpServletResponse httpResponse = 
            (HttpServletResponse)request.getIOParameters()
                .get(WebFrontController.HTTP_SERVLET_RESPONSE);
        
        SessionData session = getRequestContext().getSessionData();
        if(!session.isSessionStarted()){
            session.startSession();
            session.setLanguageCode(
                Env.getPreferredLangCode(request));
        }
        String savedLogin = (String)session.getAttribute(LoginCommand.SAVED_LOGIN);
        if (login == null){
        	login = savedLogin;
        }

        if (interfaceLanguage == null) {
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equals("interface_language"))
                        interfaceLanguage = cookies[i].getValue();
                }
            }
        }
        
        if (interfaceLanguage == null)
            interfaceLanguage = Env.getDefaultInterfaceLanguageCode();
        
        session.setAttribute(INTERFACE_LANGUAGE, interfaceLanguage);
        Cookie cookie = new Cookie("interface_language", interfaceLanguage);
        cookie.setMaxAge(Env.COOKIE_MAX_AGE);
        cookie.setPath("/");
        httpResponse.addCookie(cookie);

        if (isSave != null || savedLogin != null ){
        	saveMe = true;
        }
        
      //If user wants to log out, destroy session
        Cookie saveCookie = getSaveCookie(cookies);
        
        Document xml = getLoginXml(login, saveMe);
        xml.getDocumentElement().setAttribute(
            INTERFACE_LANGUAGE, interfaceLanguage);
        
        response.getResultMap().put(OUTPUT_XML, xml);
   
        try{
            if(login != null && password != null){
                // Check if user can be logged in to the admin. zone
                User user = User.findByLogin(login);
                if( password.equals(user.getPassword()) &&
                    SecurityGuard.isContributor(user) )
                {
                	if (Env.isContentFreeze() && !SecurityGuard.isAdministrator(user)){
                		response.setResultName(RESULT_DENIED);
                		xml.getDocumentElement().setAttribute(
                	            CONTENT_FREEZE_FLAG, "true");
                		logger.debug("- content is freezed and user is not administrator so access denied");
                		return response;
                	}
                	if (saveCookie != null){
	                	String loginUniqNumber = getUniqNumber(saveCookie.getValue());
	                	UserCookie userCookie = null;
	            		try {
	        				userCookie = UserCookie.findById(Long.valueOf(loginUniqNumber));
	        				userCookie.delete();
	        			} catch (ObjectNotFoundException e) {
	        				logger.warn("- (cookie for user " + login + "exists, but no user in system)");
	        			}
	        			//delete cookie with current user
	        			saveCookie.setMaxAge(0);
	        			httpResponse.addCookie(saveCookie);
                	}
                	if (isSaveMeOnComputer(isSave)){
                		Cookie c = generateNewUserCookieAndInsert(user.getLogin(), interfaceLanguage);
                		httpResponse.addCookie(c);
                		session.setAttribute(LoginCommand.SAVED_LOGIN, user.getLogin());
                	}else{
                		session.setAttribute(LoginCommand.SAVED_LOGIN, null);
                	}
                    session.setUserId(user.getId().intValue());
                    response.setResultName(RESULT_AUTHENTICATED);
                    logger.debug("- (editor is valid; session is started)");
                    return response;
                } else {
                    response.setResultName(RESULT_DENIED);
                    xml.getDocumentElement().setAttribute(
                            "error", "wrong_login_password");
                    logger.warn("- (editor is not in [user] " +
                        " or not a contributor; session is not started)");
                    return response;
                }
            }
            if(session.getUserId() != 0){
                // Check if user still can access the admin. zone
                User user = User.findById(new Long(session.getUserId()));
                if(SecurityGuard.isContributor(user)) {
                    response.setResultName(RESULT_AUTHENTICATED);
                    logger.debug("- (editor is valid; session is continued)");
                    return response;
                } else {
                    response.setResultName(RESULT_DENIED);
                    logger.warn(
                        "- (editor is not in [user]" +
                        " or does not have permissions to edit pages)");
                    return response;
                }
            } else {
                // User is not logged in, and does not try to log in/out
                response.setResultName(RESULT_DENIED);
                logger.warn("- (not an editor; maybe, the session expired)");
                return response;
            }
        }catch(Exception e){
        	xml.getDocumentElement().setAttribute(
                    "error", "wrong_login_password");
            response.setResultName(RESULT_DENIED);
            logger.error("- (failed to query [user]; user is rejected)", e);
            return response;
        }
    }
    /**
	 * @param httpResponse
	 * @param cookieLogin
	 */
	private Cookie generateNewUserCookieAndInsert(String login, String language) {
		UserCookie newUserCookie = new UserCookie(
				login, 
				0L, 
				language,
				new Timestamp(System.currentTimeMillis()),
				Env.getSiteId());
		newUserCookie.generateNewIdNumber();
		newUserCookie.insert();
		Cookie cookie = new Cookie(USER_COOKIE, newUserCookie.getLogin() + "_" + String.valueOf(newUserCookie.getUniqId()));
		cookie.setMaxAge(Env.COOKIE_MAX_AGE);
        cookie.setPath("/");
        return cookie;
	}
	
	
	private boolean isSaveMeOnComputer(String isSave){
    	return isSave != null && isSave.equals("on");
    }
    
    
    private Document getLoginXml(String login, boolean isSaved){
        logger.debug("+");
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = null;
        doc = XmlHelper.newDocument();
        Element form = doc.createElement("loginform");
        doc.appendChild(form);
        if(login != null) {
            Element loginEl = doc.createElement("login");
            loginEl.appendChild(doc.createTextNode(login));
            if (isSaved)
            	loginEl.setAttribute("isSave", "true");
            form.appendChild(loginEl);
        }
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            form.appendChild(
                LanguagesXmlBuilder.getInstance().getElement(conn, doc));
        } catch (Exception e) {
            logger.error("Cannot get list of interface languages", e);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return doc;
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
