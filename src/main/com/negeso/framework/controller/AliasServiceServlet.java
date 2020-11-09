/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
@SuppressWarnings("serial")
public class AliasServiceServlet extends HttpServlet implements ContainerServlet{
	
	private static Logger logger = Logger.getLogger(AliasServiceServlet.class);
	
	private Wrapper wrapper = null;

	private Host host = null;
	
	private Context context = null;

	@Override
	public Wrapper getWrapper() {
		return null;
	}

	@Override
	public void setWrapper(Wrapper wrapper) {
		this.wrapper = wrapper;
        if (wrapper == null) {
            host = null;
        } else {
        	context = (Context) wrapper.getParent();
            host = (Host) context.getParent();
        }
		
	}
	
	public void init() throws ServletException {
		
        // Ensure that our ContainerServlet properties have been set
        if ((wrapper == null) || (context == null)) {
        	logger.error("AliasServiceServlet cannot be initialized");
        	throw new UnavailableException("AliasServiceServlet cannot be initialized");        	
        }

        // Verify that we were not accessed using the invoker servlet
        String servletName = getServletConfig().getServletName();
        if (servletName == null)
            servletName = "";
        if (servletName.startsWith("org.apache.catalina.INVOKER.")) {
        	logger.error("Cannot invoke AliasServiceServlet through invoker");
        	throw new UnavailableException("Cannot invoke AliasServiceServlet through invoker");
        }
        initAliases();
    }
	
	private void initAliases() {
		Connection conn = null;
		Long siteId = 1L; //Env.getSiteId()
		try {
			conn = DBHelper.getConnection();
			List<SiteUrl> urls = SiteUrl.list(conn, siteId);
			boolean isMainHostInDb = false;
			SiteUrl mainHost = null;
			for (SiteUrl siteUrl : urls) {
				if (host.getName().equals(siteUrl.getUrl())) {
					isMainHostInDb = true;
					mainHost = siteUrl;
				} else {
					host.addAlias(siteUrl.getUrl());
				}
			}
			if (mainHost != null && !mainHost.getMain()) {
				mainHost.setMain(true);
				mainHost.update(conn);
			}
			if (!isMainHostInDb) {
				mainHost = new SiteUrl(null, host.getName(), siteId, true, null, false, true);
				mainHost.save(conn);
			}
		} catch (SQLException e) {
			logger.error("SQLException - ", e);
		} finally {
			DBHelper.close(conn);
		}
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
	
		
		// Verify that we were not accessed using the invoker servlet
		if (request.getAttribute("org.apache.catalina.INVOKED") != null)
			throw new UnavailableException("Cannot invoke AliasServiceServlet through invoker");
		
		// Identify the request parameters that we need
		String command = request.getParameter("command");
		String inputAlias = request.getParameter("url");
		Long langId = getLong(request, "langId");
		boolean isSingleLanguage = Boolean.valueOf(request.getParameter("singleLanguage"));
		boolean showLangSelector = Boolean.valueOf(request.getParameter("showLangSelector"));
		

		// Prepare our output writer to generate the response message
		response.setContentType("text/plain; ");
		PrintWriter writer = response.getWriter();
		try {
			if (command == null) {
				// No command
			} else if (command.equals("add")) {
				add(inputAlias, langId, isSingleLanguage, showLangSelector);
			} else if (command.equals("delete")) {
				remove(inputAlias);
			} else if (command.equals("update")) {
				Long siteUrlId = new Long (request.getParameter("id"));
				update(inputAlias, siteUrlId, langId, isSingleLanguage, showLangSelector);
			} 
		} catch (AliasException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		writer.flush();
		writer.close();

	}
	
	private void add (String inputAlias, Long langId, boolean isSingleLanguage, boolean showLangSelector) throws AliasException {
		Connection conn = null;
		try {
			
			for (String alias : host.findAliases()) {
				if (alias.equals(inputAlias)) {
					logger.error("Alias: " + inputAlias + " already exists for host: " + host.getName());
					throw new AliasException("Alias: " + inputAlias + " already exists for host: " + host.getName());
				}
			}
			
			conn = DBHelper.getConnection();
			SiteUrl siteUrl =  SiteUrl.findByUrl(conn, inputAlias);
			if (siteUrl != null ){
				logger.error("Alias: " + inputAlias + " already exists for host: " + host.getName());
				throw new AliasException("Alias: " + inputAlias + " already exists for host: " + host.getName());
			}
			siteUrl = new SiteUrl(null, inputAlias, Env.getSiteId());
			siteUrl.setLangId(langId);
			siteUrl.setShowLangSelector(showLangSelector);
			siteUrl.setSingleLanguage(isSingleLanguage);
			siteUrl.save(conn);
			
			host.addAlias(inputAlias);
			SiteUrlCache.resetCache();
		} catch (SQLException e) {
			logger.error("SQLException - ", e);
		} finally {
			DBHelper.close(conn);
		}
	}
	
	private void remove (String inputAlias) throws AliasException {
		Connection conn = null;
		try {			
			conn = DBHelper.getConnection();
			SiteUrl siteUrl =  SiteUrl.findByUrl(conn, inputAlias);
			if (siteUrl != null ){
				siteUrl.delete(conn);
			}
			host.removeAlias(inputAlias);
			SiteUrlCache.resetCache();
		} catch (SQLException e) {
			logger.error("SQLException - ", e);
		} finally {
			DBHelper.close(conn);
		}
	}
	
	private void update (String inputAlias, Long id, Long langId, boolean isSingleLanguage, boolean showLangSelector) throws AliasException {
		Connection conn = null;
		try {
			int counter = 0;
			for (String alias : host.findAliases()) {
				if (alias.equals(inputAlias)) {
					counter++;
				}
			}
			if (counter > 1) {
				logger.error("Alias: " + inputAlias + " already exists in Tomcat for host: " + host.getName());
				throw new AliasException("Alias: " + inputAlias + " already in Tomcat exists for host: " + host.getName());
			}
			
			conn = DBHelper.getConnection();
			
			SiteUrl uniqueUrl =  SiteUrl.findByUrl(conn, inputAlias);
			SiteUrl siteUrl = SiteUrl.findById(conn, id);
			if (uniqueUrl != null && !siteUrl.getId().equals(uniqueUrl.getId())){
				logger.error("Alias: " + inputAlias + " already exists in DB for host: " + host.getName());
				throw new AliasException("Alias: " + inputAlias + " already in DB exists for host: " + host.getName());
			}
						
			if (!inputAlias.equals(siteUrl.getUrl())) {
				String oldAlias = siteUrl.getUrl();				
				host.removeAlias(oldAlias);
				host.addAlias(inputAlias);
			}			
			siteUrl.setUrl(inputAlias);
			siteUrl.setLangId(langId);
			siteUrl.setShowLangSelector(showLangSelector);
			siteUrl.setSingleLanguage(isSingleLanguage);
			siteUrl.update(conn);
			SiteUrlCache.resetCache();
		} catch (SQLException e) {
			logger.error("SQLException - ", e);
		} finally {
			DBHelper.close(conn);
		}
	}
	
	 public Long getLong(HttpServletRequest request, String parameter) {
	        logger.debug("+");
	        String strval = request.getParameter(parameter);
	        if(strval == null || strval.equals("")){
	            logger.debug("- parameter is not set: " + parameter);
	            return null;
	        }
	        Long val = null;
	        try {
	            val = Long.valueOf(strval);
	        } catch (Exception e) {
	            logger.error("Exception: " + parameter, e);
	        }
	        logger.debug("-");
	        return val;
	    }

}

