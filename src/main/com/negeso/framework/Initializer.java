/*
 * @(#)$StartupFilter.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.menu.MenuPagesHandler;
import com.negeso.framework.page.UnlinkedPagesHandler;
import com.negeso.framework.site.SiteEngine;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.framework.site_map.SiteMapBuilder;
import com.negeso.module.BannerUploadProfile;
import com.negeso.module.dictionary.generators.DictionaryFileBuilder;
import com.negeso.module.photoalbum.domain.AlbumUploadProfile;
import com.negeso.module.wcmsattributes.ImageUploadProfile;

/**
 * Makes immediate and deferred initialization of the web-application.
 * 
 * @author Sergiy Oliynyk
 * @author Stanislav Demchenko
 */
public class Initializer implements Filter {

    /** Status of deferred initialization (to initialize only once) */
    private static boolean lazyInitDone = false;

    private static Logger logger = Logger.getLogger(Initializer.class);
    
    private static Properties redirections = null;

    /** @inheritDoc */
    public void destroy() { /* do nothing */ }

    private void createFolder(String path) {
    	File file = new File(path);
		if (!file.exists()){
	        if ( !file.mkdirs() ){
	            throw new CriticalException(
	                    "Unable to create '" + path + "' folder"
	            );
			}
		}
    }

    private void createPredefinedFolders(FilterConfig filterConfig) {
		ServletContext ctx = filterConfig.getServletContext();
		createFolder(ctx.getRealPath("/customers/site/media/documents"));
		createFolder(ctx.getRealPath("/customers/site/media/photo_album"));
    }
    
    /** @inheritDoc
     *  Setting of Env.setServletContext()
     *  is moved to InitialConfigListener */
    public void init(FilterConfig filterConfig) {
        logger.info("+");
        createPredefinedFolders(filterConfig);
        logger.info("-");
    }

    /** @inheritDoc */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws java.io.IOException, ServletException {
        logger.debug("+");
        String host = req.getServerName();
        SiteUrl siteUrl = SiteUrlCache.getSiteUrlMap().get(host);
        if (siteUrl != null && siteUrl.getLangId() != null){
        	try {
				((HttpServletRequest)req).getSession().setAttribute(SessionData.LANGUAGE_ATTR_NAME, Language.findById(siteUrl.getLangId()));
			} catch (Exception e) {
				logger.error(e);
			}
        } else if(isRedirection((HttpServletRequest)req, (HttpServletResponse)resp)) {
            logger.info("- redirection");
            return;
        }
        String defaultSite = Env.getGlobalProperty("mentrix.default.url");
        Validate.notEmpty(defaultSite, "Set global option mentrix.default.url");
        try {
            Env.setSiteId( SiteEngine.get().resolveUrlToSiteId(host) );           
            Validate.notNull(Env.getSiteId(), "unresolved host: " + host);
            
        	if ( Env.isMultisite() ) {
        		SiteEngine.get().checkExpiredSite(Env.getSiteId());
        	}
        	
        } catch (Throwable e) {
            logger.error("no site for host " + host, e);
            ((HttpServletResponse)resp).sendRedirect(defaultSite);
            logger.debug("- redirected to mentrix.default.url");
            return;
        }
        try {
            MDC.put("site", Env.getSite().getName());
            String hostName = getHostName(req, host); 
            Env.setHostName(hostName);
            if (Env.getCommonHostName() == null)
            	Env.setCommonHostName(hostName);
            Env.setServerName(host);
            lazyInit();
            chain.doFilter(req, resp);
        } finally {
            MDC.remove("site");
        }
        logger.debug("-");
    }

    private String getHostName(ServletRequest req, String host) { 
        return req.getScheme() + "://"
        + host
        + (req.getServerPort() < 1 ? "" : ":" + req.getServerPort()) 
        + "/";    	
    }
    
    /**
     * Executes various tasks right before processing the first HTTP request.
     * Synchronized to ensure that initialization is done only once.
     */
    private synchronized void lazyInit() {
        logger.debug("+");
        if(lazyInitDone) {
            logger.debug("- already inited");
            return;
        }
        lazyInitDone = true;
        AlbumUploadProfile.get().init();
        ImageUploadProfile.get().init();
        BannerUploadProfile.get().init();
        SiteMapBuilder.getInstance().getHandlers().put("menu", new MenuPagesHandler());
        SiteMapBuilder.getInstance().getHandlers().put("unlinked", new UnlinkedPagesHandler());
        try { DictionaryFileBuilder.getInstance().generate(false); }
        catch (CriticalException ex) { logger.error("Exception", ex); }
        logger.debug("-");
    }

    /**
     * Redirects the browser to another URL (if necessary)
     * 
     * @return  true, if redirection occurred; false otherwise 
     */
    private static synchronized boolean isRedirection(
            HttpServletRequest request,
            HttpServletResponse response)
    {
        logger.debug("+");
        if(redirections == null) {
            redirections =  new Properties();
            if(Env.getGlobalProperty("redirections") != null) {
                try {
                    redirections.load(
                        new ByteArrayInputStream(
                            Env.getGlobalProperty("redirections").getBytes()));
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
                StringBuffer redirInfo = new StringBuffer("DNS names - URLs:");
                Enumeration e = redirections.keys();
                while(e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    redirInfo.append("\r\n");
                    redirInfo.append(key);
                    redirInfo.append("=");
                    redirInfo.append(redirections.get(key));
                }
                logger.info(redirInfo);
            }
        }
        if(redirections.get(request.getServerName()) != null) {
            try {
                response.sendRedirect(
                    (String) redirections.get(request.getServerName()));
            } catch (IOException e) {
                logger.error("redirection failed", e);
            }
            logger.debug("- redirection");
            return true;
        }
        logger.debug("- not a redirection");
        return false;
    }
    
}
