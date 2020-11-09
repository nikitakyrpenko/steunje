/*
 * @(#)GetPageCommand.java  Created on 01.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page.command;


import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.negeso.framework.view.HtmlView;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.Guid;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.LoginCommand;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PageBuilder;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.site.PageAlias;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.framework.site.service.PageAliasService;
import com.negeso.framework.util.NegesoRequestUtils;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */
public class GetPageCommand extends AbstractCommand {
	private static final Logger logger = Logger.getLogger(GetPageCommand.class);

    public static final String INPUT_PASSWORD = "upassword";
	public static final String INPUT_LOGIN = "ulogin";

    /** Filename to find the page by */
    public static final String INPUT_FILENAME = "filename";
    public static final String DOT_HTML = ".html";
    public static final String LANG_CODE_DOT_HTML = "_%s" + DOT_HTML;
    private static final String[] INDEX_PAGE_PATH_IDENTIFIERS = {StringUtils.EMPTY, "/", DOT_HTML};
    
    /** Visitor who requested the page */
    private User user = null;
    
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_FAILURE);
        
        updateSession(request);
        if (request.getParameter("behavior") != null && request.getParameter("behavior").equals("AUTHORIZED")){
	        HttpServletResponse res = HtmlView.getServletResponse(request);
	        String pagePath = Env.getFrontPageName(Env.getPreferredLangCode(request));
	        res.addHeader("Location", "/" + pagePath);
	        res.setStatus(302);
        }
        if (!Env.getSite().isLive() ) {
            response.getResultMap().put(OUTPUT_XML, biuldSiteStatusWarning());
            response.setResultName(RESULT_SUCCESS);
            logger.debug("site is not live");
            return response;
        }
        
        buildPage(this.user, request, response, true);
        
        User user = request.getSessionData().getUser();
        if ( user != null && user.isSingleUser() ) {
        	logger.debug("updateLastActionDate for User = " + user.toString());        	
        	user.updateLastActionDate();
        }
        logger.debug("-");
        return response;
    }

	public static void buildPage(User user, RequestContext request,
			ResponseContext response, boolean forVisitors) {
		PageH page = decideOnPageToShow(user, request, forVisitors);
        if(page != null){
            Document pageDoc = new PageBuilder(user).buildPage(
            	page, request, forVisitors
            );
            response.setResultName(RESULT_SUCCESS);
            response.getResultMap().put(OUTPUT_XML, pageDoc);
        }else{
        	response.setResultName(RESULT_FAILURE);
            logger.error(
                "Cannot get appropriate Page object: " + 
                request.getParameter(INPUT_FILENAME) +
                " Result:" + response.getResultName() 
            );
        }
	}
    
    /** Returns a Document containing site status warning (or null, if) */
    static Document biuldSiteStatusWarning() {
        logger.debug("+");
        Element elPage = Xbuilder.createTopEl("page");
        Element elWarning = Xbuilder.addEl(elPage, "site_status_warning", null);
        Xbuilder.setAttr(elWarning, "status", Env.getSite().getStatus());
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Date creatDate = Env.getSite().getCreatedDate();
        Xbuilder.setAttr(elWarning,"creation_date", f.format(creatDate));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,
                Env.getGlobalIntProperty("mentrix.construction.period", 10));
        Xbuilder.setAttr(elWarning, "suspension_date", f.format(cal.getTime()));
        cal.add(Calendar.DATE,
                Env.getGlobalIntProperty("mentrix.suspended.period", 10));
        Xbuilder.setAttr(elWarning, "deletion_date", f.format(cal.getTime()));
        logger.debug("-");
        return elPage.getOwnerDocument();
    }
    
    private static PageH decideOnPageToShow(
            User user,
            RequestContext request,
            boolean checkDates)
    {
        logger.debug("+");
        SessionData session = request.getSessionData();
        String lang = session.getLanguageCode();
        String langCode = request.getParameter(NegesoRequestUtils.LANG_CODE);
        String filename = request.getParameter(GetPageCommand.INPUT_FILENAME);		
        if(filename == null || ArrayUtils.contains(INDEX_PAGE_PATH_IDENTIFIERS, filename)){
        	filename = Env.getFrontPageName(StringUtils.isNotBlank(langCode) ? langCode : lang);
        	/*try {
                Page page = Page.findByCategory("frontpage", lang);
                saveLanguageAndPageInSession(session, page);
                logger.debug("- returning page of category 'frontpage'");
                return page;
            } catch (Exception e) {
                logger.warn("no page frontpage for language " + lang);
                filename = "index.html";
            }*/
        }
        String specialCategory;
        try {
        	filename = URLDecoder.decode(filename, "UTF-8");
        	PageH page = null;
        	boolean canonical = false;
        	try {
        		if (StringUtils.isNotBlank(langCode)){        			
        			String searchFilename = filename.contains(String.format(LANG_CODE_DOT_HTML, langCode)) ? filename : filename.replace(".", "_" + langCode + ".").toLowerCase();
        			page = PageService.getInstance().findByFileName(searchFilename);
        			canonical = true;
        		} else {
        			page = PageService.getInstance().findByFileName(filename);        		
        		}
			} catch (Exception e) {
				logger.error(e);
			}
			if (page == null) {
				canonical = true;
				page = PageService.getInstance().findByFileName(filename.replace(DOT_HTML, String.format(LANG_CODE_DOT_HTML, session.getLanguageCode())));
			}
			if (page == null) {
				page = PageService.getInstance().findByFileName(filename.replace(DOT_HTML, String.format(LANG_CODE_DOT_HTML, Env.getDefaultLanguageCode())));
			}
            if (page == null) {
            	page = findInAliases(filename, request);
            }
            if (page == null) {
            	page = findInAliases(filename.replace(DOT_HTML, String.format(LANG_CODE_DOT_HTML, session.getLanguageCode())), request);
            }
            if (page == null) {
            	page = findInAliases(filename.replace(DOT_HTML, String.format(LANG_CODE_DOT_HTML, Env.getDefaultLanguageCode())), request);
            }
            if (checkDates) {
            	checkDates(page);
            }
            if (canonical) {
            	request.setCanonicalAttribute();
            }
            SiteUrl siteUrl = SiteUrlCache.getSiteUrlMap().get(Env.getServerName());
            if (User.isNotAdmin(user)) {
            	if (siteUrl != null && Boolean.valueOf(siteUrl.getSingleLanguage()) && siteUrl.getLangId() != null && !page.getLangId().equals(siteUrl.getLangId())) {
            		SiteUrl siteUrlForLang = SiteUrlCache.getLangIdToSiteUrlMap().get(page.getLangId());
            		if (siteUrlForLang != null) {
            			HttpServletRequest req = request.getHttpRequest();
            			((HttpServletResponse)request.getIOParameters().get(WebFrontController.HTTP_SERVLET_RESPONSE)).sendRedirect("http://" + siteUrlForLang.getUrl() + "/" 
            					+ page.getFilename() + 
            					(StringUtils.isNotBlank(req.getQueryString()) ? "?" + req.getQueryString() : StringUtils.EMPTY));
            			req.setAttribute("noCache", true);
            		}
            		throw new Exception("Page " + page.getFilename() + " has another language then set for domain " + Env.getServerName());
            	}
            }
            if(SecurityGuard.canView(user, page.getContainerId())) {
                saveLanguageAndPageInSession(session, page);
                logger.debug("- page is found by name");
                return page;
            }
            if(page.isVisible()){
            	saveLanguageAndPageInSession(session, page);
            }
            specialCategory = "access_denied";
        } catch (Exception e) {
            logger.warn("Page "+ filename +" is not found or hidden dates", e);
            specialCategory = "not_found";
        }
        try {
            logger.warn("- returning page of category " + specialCategory);
            return PageService.getInstance().findByCategory(specialCategory, lang);
        } catch (Exception e) {
            logger.error("- No special page of category " + specialCategory, e);
            return null;
        }
    }

    
    private static void checkDates(PageH page) throws Exception
    {
        logger.debug("+");
        if(!page.isDateOk()) {
            logger.warn("- dates are not ok");
            throw new Exception("page is hidden by publish/expire dates");
        }
        logger.debug("-");
    }


    private static void saveLanguageAndPageInSession(
        SessionData session,
        PageH page)
    {
        logger.debug("+");
        try {
            session.setPageId(page.getId().intValue());
            session.setLanguageCode(
                    Language.findById(page.getLangId()).getCode());
        } catch (Exception e) {
            logger.error("Cannot change session language", e);
        }
        logger.debug("-");
    }

    
    /**
     * Checks visitor's validity and login/logout commands.
     * If session is not started, starts it.
     */
    private void updateSession(RequestContext request)
    {
        logger.debug("+");
        SessionData session = request.getSessionData();
        if(!session.isSessionStarted()){
            session.startSession();
            session.setLanguageCode(Env.getPreferredLangCode(request));
        }
        int uid = session.getUserId();
        if(uid != 0){
            if(request.getParameter("logout") != null){
                session.getUser().clearLastActionDate();
                String lang = session.getLanguageCode();
                session.destroySession();
                session.startSession();
                session.setLanguageCode(lang);
                logger.debug("- visitor logs out");
                return;
            }
            try{
                user = User.findById(new Long(uid));
                logger.debug("- authenticated");
            }catch(Exception e){
                session.setUserId(0);
                logger.warn("- not authenticated! (maybe deleted)");
            }
        }else{
            if(request.getParameter(INPUT_LOGIN) == null){
                logger.debug("- anonymous user");
                return;
            }
            String password = request.getParameter(INPUT_PASSWORD);
            if(password == null){
                logger.debug("- wrong password");
                return;
            }
            authorizeUser(request);
        }
    }
    
    private static PageH findInAliases (String fileName, RequestContext request) throws ObjectNotFoundException {
    	PageAliasService pageAliasService = (PageAliasService)DispatchersContainer.getInstance().getBean("core", "pageAliasService");
    	PageAlias pageAlias = pageAliasService.findByFilename(fileName);
    	if (pageAlias != null) {
    		if (pageAlias.getEntityTypeEnum() != null) {
    			request.setParameter(pageAlias.getEntityTypeEnum().getParam(), pageAlias.getEntityId().toString());
    		}
    		String params = pageAlias.getLink().replaceAll(".*\\.html\\??", org.apache.commons.lang.StringUtils.EMPTY);
    		for (String param : params.split("&")) {
    			String[] paramToken = param.split("=");
    			if (paramToken.length == 2) {
    				request.setParameter(paramToken[0], paramToken[1]);
    			}
			}
    		return PageService.getInstance().findById(pageAlias.getPageId());
    	}
    	return null;
    }
    
    private void authorizeUser(RequestContext request) {
    	logger.debug("+");
    	String userStatus = User.WRONG;
        try{
        	user = User.findByLogin(request.getParameter(INPUT_LOGIN));
        	if ( user.getPassword().equals(request.getParameter(INPUT_PASSWORD)) ) {
        		
        		userStatus = User.getUserStatus(request.getParameter(INPUT_LOGIN));
        		
        		if ( User.NOT_ACTIVATED != userStatus
        				&& User.NOT_APPROVED != userStatus	
        				&& User.SYSTEM_USER != userStatus ) {
        			
        			if (user.isExpired()) {
        				userStatus = User.EXPIRED;
        			} else {
        				if ( !user.isSingleUser() ) {
        					request.getSessionData().setUserId( user.getId().intValue() );
        					logger.info("- user has logged in successfully");
        					userStatus = User.AUTHORIZED;  
        					
        				} else {
        					userStatus = authorizeSingleUser(request);
        				}
        			}
        		}
        	}
        	
        } catch (ObjectNotFoundException e) {
        	
        	logger.info("User was not found by login in user_list");
        	userStatus = User.getUserStatus(request.getParameter(LoginCommand.INPUT_LOGIN));
        	
        } catch(Exception e){
        	
        	userStatus = User.WRONG; 
            logger.warn("some critical exception occured- wrong login");
            
        }
        if (userStatus != User.AUTHORIZED) {
        	user = null;
        }
        request.setParameter("behavior", userStatus.toString());
        logger.debug("behavior = " + userStatus.toString());
        logger.debug("-");
    }
    
    private String authorizeSingleUser(RequestContext request) {
    	String cookieName = "user_guid";
    	String cookieGuid = null;
    	
        Cookie[] cookies = ((HttpServletRequest)request.getIOParameters()
        		.get(WebFrontController.HTTP_SERVLET_REQUEST)).getCookies();   
        if (cookies != null) {
          for ( Cookie cookie: cookies ) {
            if (cookie.getName().equals(cookieName))  {                     
            	cookieGuid = cookie.getValue();
            	break;
            } 
          }
        }

        logger.debug("cookieGuid=" + cookieGuid);
        logger.debug("user.getGuid()=" + user.getGuid());
        
		if (  user.getLastActionDate() == null || 
				( user.getGuid() != null && cookieGuid != null && user.getGuid().equals(cookieGuid)) ||
				System.currentTimeMillis() - user.getLastActionDate().getTime() > Env.getIntProperty("langhenkel.singleUserTimeout", 30)*60*1000 ) {
			
			logger.debug("user.getLastActionDate()=" + user.getLastActionDate());
			request.getSessionData().setUserId( user.getId().intValue() );
			
			if ( user.getGuid() == null || cookieGuid == null || !user.getGuid().equals(cookieGuid) ) {
				
				user.setGuid((new Guid()).toString());
				user.update();
				
				Cookie cookie = new Cookie(cookieName, user.getGuid());      
				cookie.setMaxAge(Env.COOKIE_MAX_AGE);
				cookie.setPath("/");
				((HttpServletResponse)request.getIOParameters()
						.get(WebFrontController.HTTP_SERVLET_RESPONSE)).addCookie(cookie);
				
			}
			
			
			logger.info("- single user has logged in successfully");
			return User.AUTHORIZED;
		} else {
			logger.info("- single user has been logged in");
			user = null;
			return User.ALREADY_LOGGED_IN;                    		
		} 
    }
}

