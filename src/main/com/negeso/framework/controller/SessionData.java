/*
 * @(#)$Id: SessionData.java,v 1.14, 2007-03-01 07:48:18Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import java.io.Serializable;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;


/**
 * Provides access to http session data and adds a few helper methods.
 * 
 * @author      Stanislav Demchenko
 * @version     $Revision: 15$
 */

@SuppressWarnings("serial")
public class SessionData implements Serializable {
    
    
    public static final String USER_ATTR_NAME = "user_object";
    
    
    public static final String LANGUAGE_ATTR_NAME = "language_object";
    
    
    public static final String INTERFACE_LANGUAGE_ATTR_NAME = "interface-language";
    
    
    private static final int NONE_USER_ID = 0;
    
    
    private static Logger logger = Logger.getLogger(SessionData.class);
    
    
    /**
     * We have to store http request object locally, because this is the only
     *  way to create or destroy a session on demand
     */
    private HttpServletRequest request = null;
    
    
    /**
     * Creates a SessionData object. A session does not start automatically,
     * when you create SessionData this way. This method can be useful when
     * you need to check whether a session is started beforehand.
     * 
     * @param request HttpServletRequest to manage HttpSession later
     * @return SessionData (always a new instance); never null
     */
    public static SessionData getSessionData(HttpServletRequest request)
    {
        logger.debug("+");
        SessionData sessionData = new SessionData();
        sessionData.request = request;
        logger.debug("-");
        return sessionData;
    }
    
    
    /**
     * If the user is authenticated, returns user id.
     * Otherwise, <code>null</code> is returned
     * 
     * @return user id (as in table <code>users</code>)
     * @throws IllegalStateException if this method is called on an invalidated
     *         session
     */
    public int getUserId() throws IllegalStateException
    {
        logger.debug("+");
        if ( this.getUser() == null ){
            return NONE_USER_ID; 
        }
        logger.debug("-");
        return this.getUser().getId().intValue();
    }
    
    
    /**
     * Stores user id in session. This method should be used for uathenticated
     * users only. Anonymous users have zero user id.
     * 
     * @throws IllegalStateException if this method is called on an invalidated
     *         session
     */
    public void setUserId(int userId) throws IllegalStateException
    {
        logger.debug("+");
        User user = null;
        try {
            user = User.findById(new Long(userId));
        }
        catch (ObjectNotFoundException e){
            throw new IllegalStateException(e.getMessage());
        }
        catch (CriticalException e){
            throw new IllegalStateException(e.getMessage());
        }
        this.setAttribute(USER_ATTR_NAME, user);
        logger.debug("-");
    }
    
    /**
     * Stores user id in session. This method should be used for uathenticated
     * users only. Anonymous users have zero user id.
     * Included connection for transaction support.      
     * 
     * @param con
     * @param userId
     * @throws IllegalStateException
     */

	public void setUserId(Connection con, int userId) throws IllegalStateException
	    {
	    	logger.debug("+");
	    	User user = null;
	    	try {
	    		user = User.findById(con, new Long(userId));
	    	}
	    	catch (CriticalException e){
	    		throw new IllegalStateException(e.getMessage());
	    	}
	    	this.setAttribute(USER_ATTR_NAME, user);
	    	logger.debug("-");
	    }
    
    
    /**
     * stores Superuser (simple admin user w/ wirtual property Superuser=true
     * in sessionData)
     * @param superuser
     */
    public void setSuperuser(User superuser) {
    	logger.debug("+-");
    	if( superuser!=null ){
    		superuser.setSuperUser(true);
    	}
    	this.setAttribute(USER_ATTR_NAME, superuser);
    }
    
    
    /**
     * Returns user's language code (as in table <code>language</code>). This
     * code should correspond to the language of the latest page read by
     * the user (or to the page being served), no matter editor or visitor.
     * 
     * @return      The language code <code>String</code>, if languge 
     *              is not set - default language code returned ("en" 
     *              for example)
     * 
     * @throws IllegalStateException if this method is called on an invalidated
     *         session
     */
    public String getLanguageCode() throws IllegalStateException
    {
        logger.debug("+");
        Language language = getLanguage();
        if (language == null){
            logger.warn("- language is not resolved");
            return null;
        } else {
            logger.debug("- language is found");
            return language.getCode();
        }
    }
    

    /**
     * Stores language code (defined by table <code>language</code>) in
     * session.
     * Language code reflects language of pages for a web-site which has pages
     * in several languages. It does not relate to the language of the
     * administrative interface, which can be switch in the administrative
     * interface independently.
     * 
     * @throws IllegalStateException if this method is called on an invalidated
     *         session
     */
    public void setLanguageCode(String languageCode)
        throws IllegalStateException
    {
        logger.debug("+");
        Language language = null;
        try {
            language = Language.findByCode( languageCode );
        }
        catch (ObjectNotFoundException e){
            throw new IllegalStateException(e.getMessage());
        }
        catch (CriticalException e){
            throw new IllegalStateException(e.getMessage());
        }
        this.setAttribute(LANGUAGE_ATTR_NAME, language);
        logger.debug("-");
    }
    
    
    /**
     * Returns id of the latest (current) page, viewed by the visitor/editor.
     * Page id is tracked to highlight the latest visited menu item. This can
     * be of use, because not every page has a corresponding menu item, and the
     * last active menu item should be highlighted in such cases.
     * 
     * @throws IllegalStateException if this method is called on an invalidated
     *         session
     */
    public int getPageId() throws IllegalStateException
    {
        logger.debug("+");
        Object id = this.getAttribute("page_id");
        if(id == null){
            logger.debug("- page id not set");
            return 0;
        }else{
            logger.debug("-");
            return ((Long) id).intValue();
        }
    }
    
    
    /**
     * Stores page id in session. Do it only if the page has also a
     * corresponding menu item in the site main menu tree.
     * 
     * @throws IllegalStateException if this method is called on an invalidated
     *         session
     */    
    public void setPageId(int pageId) throws IllegalStateException
    {
        logger.debug("+");
        this.setAttribute("page_id", new Long(pageId));
        logger.debug("-");
    }
    
    
    /**
     * If session exists, activates it. If session exists and active, does
     * nothing. If session does not exist, it is created.
     * <p>
     * To make sure the session is properly maintained, you must call this
     * method before the response is committed. Otherwise, an
     * IllegalStateException may be thrown (depending on implementation).
     */
    public void startSession()
    {
        logger.debug("+");
        this.request.getSession(true);
        logger.debug("-");
    }
    
    
    /**
     * Destroys the session and clears all session data.
     * 
     * @throws IllegalStateException if this method is called on an already
     *         invalidated session.
     */
    public void destroySession() throws IllegalStateException
    {
        logger.debug("+");
        this.request.getSession().invalidate();
        logger.debug("-");
    }
    
    
	/**
	 * Test if session is started. Should always be checked before 
	 * using session.  
	 * 
	 * @return 		<code>true</code> if session has been started
	 * 				<code>false</code> overwise.
	 */
    public boolean isSessionStarted()
    {
        return this.request.getSession(false) != null;
    }
	
    
    public Object getAttribute(String attrName)
    {
        return this.request.getSession().getAttribute(attrName);
    }
    
    
    public void setAttribute(String attrName, Object attrValue)
    {
        this.request.getSession().setAttribute(attrName, attrValue);
    }
	
    public void removeAttribute(String attrName) {
    	this.request.getSession().removeAttribute(attrName);
    }
    

    
    /**
     * Session <code>User<code> getter
     * 
     * @return      The <code>User</code> domain object or 
     *              <code>null</code> if user is not set in session.
     */
    public User getUser()
    {
        logger.debug("+");
        Object user = this.getAttribute( USER_ATTR_NAME );
        if ( ! (user instanceof User) ){
            logger.debug("- user is not set");
            return null;
        }
        logger.debug("- user is set");
        return (User) user;
    }
	
    
    /**
     * Session <code>Language<code> getter
     * 
     * @return      The <code>Language</code> domain object of 
     *              <code>null</code> if languade is not set for 
     *              session
     */
    public Language getLanguage()
    {
        logger.debug("+");
        if ( ! (getAttribute(LANGUAGE_ATTR_NAME) instanceof Language) ){
            setLanguageCode(Env.getPreferredLangCode(request));
        }
        logger.debug("-");
        return (Language) getAttribute(LANGUAGE_ATTR_NAME);
    }
    
	
    /**
     * Get Interface Language from Session. If language not set - 
     * get default from Env.  
     * 
     * @return
     */
    public String getInterfaceLanguageCode()
    {
        String interfaceLanguage = 
            (String) request.getSession().getAttribute("interface-language");         
        if (interfaceLanguage == null){
            interfaceLanguage = Env.getDefaultInterfaceLanguageCode(); 
            request.getSession().setAttribute(
                    INTERFACE_LANGUAGE_ATTR_NAME,
                    interfaceLanguage
            );
        }
        return interfaceLanguage;
    }
    
    /**
     * Purpose - retrieving langCode from HttpServletRequest
     * Use it when you don't have requestContext     * 
     * @return 
     */
    public static String getLanguageCode(HttpServletRequest request) {
    	return SessionData.getSessionData(request).getLanguage().getCode();
    }

    public static String getInterfaceLanguageCode(HttpServletRequest request) {
    	return SessionData.getSessionData(request).getInterfaceLanguageCode();
    }
    
    /**
     * Unique session id getter
     * 
     * @return
     */
    public String getId(){
    	return this.request.getSession().getId();
    }
    
	public boolean isAuthorizedUser() {
		return getUserId() != 0;
	}

	//TODO SB: temporary solution
	public HttpServletRequest getRequest() {
		return request;
	}

    public ServletContext getServletContext() {
    	return request.getSession().getServletContext();
    }
	
	public static long getLangId(HttpServletRequest request) {
		return SessionData.getSessionData(request).getLanguage().getId();
	}
	
	public static String getLangCode(HttpServletRequest request) {
		return SessionData.getSessionData(request).getLanguage().getCode();
	}

}
