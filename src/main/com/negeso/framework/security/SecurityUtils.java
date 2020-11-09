/*
 * @(#)$Id: SecurityUtils.java,v 1.0, 2006-04-26 10:43:48Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.security;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;

/**
 * 
 * Security/authorization utils
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 1$
 *
 */
public class SecurityUtils {
    private static Logger logger = Logger.getLogger(SecurityUtils.class);
    
	/**
     * Checks visitor's validity and login/logout commands.
     * If session is not started, starts it.
     * @throws CriticalException
     */
    public static void transparentLogon(RequestContext request)
    {
        logger.debug("+");
        SessionData session = request.getSessionData();
        if(!session.isSessionStarted()){
            session.startSession();
            session.setLanguageCode(getPreferredLangCode(
                request.getParameter(RequestContext.REQUEST_LOCALES) ));
        }
        if(request.getParameter("ulogin") == null){
            logger.info("- anonymous user");
            return;
        }
        String password = request.getParameter("upassword");
        if(password == null){
            logger.info("- wrong password");
            return;
        }
        try{
            User user = User.findByLogin(request.getParameter("ulogin"));
            if(password.equals(user.getPassword())){
                session.setUserId(user.getId().intValue());
                logger.info("- user has logged in successfully");
            }else{
                logger.info("- wrong password");
                user = null;
            }
        }
        catch(ObjectNotFoundException e){
            logger.warn("- wrong login");
        }
    }
    
    /**
     * Checks visitor's validity and login/logout commands.
     * If session is not started, starts it.
     */
    public static void logoutUser(RequestContext request)
    {
        logger.debug("+");
        SessionData session = request.getSessionData();
        if(!session.isSessionStarted()){
            session.startSession();
            session.setLanguageCode(getPreferredLangCode(
                request.getParameter(RequestContext.REQUEST_LOCALES) ));
            logger.info("- anonimus session started");
        }
        else{
            String lang = session.getLanguageCode();
            session.destroySession();
            session.startSession();
            session.setLanguageCode(lang);
            logger.info("- visitor logs out");
        }
    }
	

    
    /**
     * @param locales comma-separated list of locales preferred by visitor
     * @return language code, never null
     */
    static String getPreferredLangCode(String locales) {
        logger.debug("+");
        String langAutoSelection =
            Env.getProperty("language.dependent.frontpage", "false");
        if(langAutoSelection.equals("false")){
            logger.debug("- autoselection is off; return default language");
            return Env.getDefaultLanguageCode();
        }
        if(locales == null || "".equals(locales)){
            logger.debug("- preferred language is not specified");
            return Env.getDefaultLanguageCode();
        }
        String[] langs = locales.split(";");
        for (int i = 0; i < langs.length; i++) {
            String lang = langs[i];
            try{
                Language.findByCode(lang);
                logger.debug("- preferred language is found");
                return lang;
            }catch(Exception e){
                // DEBUG because we do not care what languages user has
                logger.debug("Cannot find language");
            }
        }
        logger.debug("- preferred language is not available");
        return Env.getDefaultLanguageCode();
    }



}

