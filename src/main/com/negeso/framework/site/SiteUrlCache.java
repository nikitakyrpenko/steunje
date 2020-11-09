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
package com.negeso.framework.site;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.site.service.SiteUrlService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SiteUrlCache {
	
	private static Logger logger = Logger.getLogger(SiteUrlCache.class);
	
	private static Map<Long, SiteUrl> LAN_ID_TO_SITE_URL_CACHE_MAP = null;
	private static  Map<String, SiteUrl> SITE_URL_CACHE_MAP = null;
	private static SiteUrl main = null;
	
	public static synchronized Map<Long, SiteUrl> getLangIdToSiteUrlMap() {
		if (LAN_ID_TO_SITE_URL_CACHE_MAP == null) {
			LAN_ID_TO_SITE_URL_CACHE_MAP = new HashMap<Long, SiteUrl>();
			SiteUrlService siteUrlService = (SiteUrlService)DispatchersContainer.getInstance().getBean("core", "siteUrlService");
			List<SiteUrl> urls = siteUrlService.listBySiteId(Env.getSiteId());
			for (SiteUrl siteUrl : urls) {
				for (Long langId : siteUrl.getLangIds()) {
					LAN_ID_TO_SITE_URL_CACHE_MAP.put(langId, siteUrl);
				}
			}
		}
		return LAN_ID_TO_SITE_URL_CACHE_MAP;
	}
	public static synchronized Map<String, SiteUrl> getSiteUrlMap() {
		if (SITE_URL_CACHE_MAP == null) {
			SITE_URL_CACHE_MAP = new HashMap<String, SiteUrl>();
			Connection conn = null;
			try {
				conn = DBHelper.getConnection();
				List<SiteUrl> urls = SiteUrl.list(conn, Env.getSiteId());
				for (SiteUrl siteUrl : urls) {
					SITE_URL_CACHE_MAP.put(siteUrl.getUrl(), siteUrl);
				}
			} catch (SQLException e) {
				logger.error(e);
			} finally {
				DBHelper.close(conn);
			}
		}
		return SITE_URL_CACHE_MAP;
	}
	
	public static SiteUrl getMain() {
		if (main == null) {
			Connection conn = null;
			try {
				conn = DBHelper.getConnection();
				List<SiteUrl> urls = SiteUrl.list(conn, Env.getSiteId());
				for (SiteUrl siteUrl : urls) {
					if (siteUrl.getMain()) {
						main = siteUrl;
						break;
					}
				}
			} catch (SQLException e) {
				logger.error(e);
			} finally {
				DBHelper.close(conn);
			}
		}
		return main;
	}
	
	public static void resetCache() {
		if (LAN_ID_TO_SITE_URL_CACHE_MAP != null) {
			synchronized (LAN_ID_TO_SITE_URL_CACHE_MAP) {
				LAN_ID_TO_SITE_URL_CACHE_MAP = null;
			}
		}
		if (SITE_URL_CACHE_MAP != null) {
			synchronized (SITE_URL_CACHE_MAP) {
				SITE_URL_CACHE_MAP = null;
			}
		}
    }
	
	public static String getMainHostNameForLanguage(Long langId) {
		String hostName = Env.getHostName();
    	SiteUrl siteUrl = SiteUrlCache.getLangIdToSiteUrlMap().get(langId);
    	if (siteUrl != null) {
    		hostName = "http://" + siteUrl.getUrl();
    	}
    	return hostName;
	}
}

