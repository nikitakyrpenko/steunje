/*
 * @(#)Id: StatisticsFilter.java, 21.11.2007 15:23:26, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.statistics;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.Timer;
import com.negeso.module.statistics.domain.Counter;
import com.negeso.module.statistics.domain.Statistics;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class StatisticsFilter implements Filter{
	
	private static Logger logger = Logger.getLogger(StatisticsFilter.class);

	public void destroy() {/* nothing */}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.debug("+");
		if (!isForbiddenIp( request.getRemoteAddr()))
			addPageHit((HttpServletRequest)request);
		else
			logger.debug("Request from forbidden IP: " + request.getRemoteAddr());
		chain.doFilter(request, response);
		logger.debug("-");
	}

	public void init(FilterConfig config) throws ServletException {/* nothing */}
	
	private synchronized void addPageHit(HttpServletRequest httpRequest){
		Timer timer = null;
		if (logger.isInfoEnabled()) {
			timer = new Timer();
			timer.start();
		}
		if ("false".equals(Env.getProperty("statistics.enabled", "false")) ) {
			logger.debug("- site statistics is disabled. return");
			return;
		}
		User user = (User) httpRequest.getSession().getAttribute(SessionData.USER_ATTR_NAME);
		String pageUrl = httpRequest.getRequestURL().toString().trim().replaceAll("^.*/", "");
		String lang = SessionData.getLangCode(httpRequest);
		PageH page = getPage(pageUrl.replace("/", ""), lang, user);
		if (page == null){
			logger.debug("- page is unknown");
			return;
		}
		if (!this.hasCounter(page.getId())) {
			return;
		}
		Connection con = null;
		try {
			Statistics statistics = new Statistics();
			statistics.setSiteId(Env.getSiteId());
		    if (user != null) {
		    	statistics.setUserId(user.getId());
		    	statistics.setUserLogin(user.getLogin());
		    }
		    statistics.setUser_ip(httpRequest.getRemoteAddr());
		    statistics.setHitDate(new Date(System.currentTimeMillis()));
		    statistics.setHitTime(new Time(System.currentTimeMillis()));
		    statistics.setPageName(page.getFilename());
		    statistics.setReferer(httpRequest.getHeader("Referer")); // referer
		    statistics.setEvent(Statistics.EVENT_PAGE_VISIT);
		    con = DBHelper.getConnection();
		    if (logger.isInfoEnabled()) {
		    	logger.info("------------------ PageName="+statistics.getPageName()+"; HitDate="+statistics.getHitDate());
		    }
		    statistics.insert(con);
        } catch (Exception e) {
        	logger.error("-error", e);
		}
        finally {
            DBHelper.close(con);
        }
		if (logger.isInfoEnabled()) {
			logger.info("AddHit:" + timer.toString());
		}
	}
	
	private PageH getPage(String filename, String lang,User user){
        logger.debug("+");
        PageH page = null;
        String pageName = filename;
        if("".equals(pageName) || "/".equals(pageName) || pageName == null){
            try {
                page = PageService.getInstance().findByCategory("frontpage", lang);
                logger.debug("- returning page of category 'frontpage'");
                return page;
            } catch (Exception e) {
                logger.debug("no page frontpage for language " + lang);
                pageName = "index.html";
            }
        }
        String specialCategory;
        try {
            page = PageService.getInstance().findByFileName(pageName);
            if(SecurityGuard.canView(user, page.getContainerId())) {
                logger.debug("- page is found by name");
                return page;
            }
            specialCategory = "access_denied";
        } catch (Exception e) {
            logger.debug("Page "+ pageName +" is not found or hidden dates", e);
            return null;
        }
        try {
            logger.debug("- returning page of category " + specialCategory);
            return PageService.getInstance().findByCategory(specialCategory, lang);
        } catch (Exception e) {
            logger.debug("- No special page of category " + specialCategory, e);
            return null;
        }
    }

	private boolean hasCounter(Long pageId) {
		if (logger.isInfoEnabled()) {
			logger.info("------------------ "+Env.getProperty("statistics.logOnlyCounterPages", "false"));
		}
		if ("true".equals(Env.getProperty("statistics.logOnlyCounterPages", "true")) ) {
			Connection con = null;
			try {
				con = DBHelper.getConnection();
				if (Counter.isCounterActiveOnPage(con, pageId)) {
	            	return true;
	            }
				else { 
					return false;
				}
	        } catch (Exception e) {
	        	logger.debug("-error", e);
	        	return false;
			}
	        finally {
	            DBHelper.close(con);
	        }
		}
		return true;
	}
	
	private boolean isForbiddenIp(String ip){
		String forbiddenListIp = Env.getProperty("statistics.forbiddenListIp", "");
		if (ip != null && forbiddenListIp.contains(ip))
			return true;
		return false;
	}
	
	
	
	
}
