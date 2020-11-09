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
package com.negeso.framework.site.service;

import java.util.List;

import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.framework.site.dao.SiteUrlDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SiteUrlService {
	
	private SiteUrlDao siteUrlDao;
	
	public void delete (SiteUrl siteUrl) {
		siteUrlDao.delete(siteUrl);
		siteUrlDao.flush();
		SiteUrlCache.resetCache();
	}
	
	public void createOrUpdate (SiteUrl siteUrl) {
		siteUrlDao.createOrUpdate(siteUrl);
		siteUrlDao.flush();
		SiteUrlCache.resetCache();
	}
	
	public List<SiteUrl> listBySiteId(Long siteId) {
		return siteUrlDao.listBySiteId(siteId);
	}
	
	public List<SiteUrl> listBySiteIdLangId(Long siteId) {
		return siteUrlDao.listBySiteIdLangId(siteId);
	}

	public SiteUrlDao getSiteUrlDao() {
		return siteUrlDao;
	}

	public void setSiteUrlDao(SiteUrlDao siteUrlDao) {
		this.siteUrlDao = siteUrlDao;
	}
	
	public void update(List<SiteUrl> list) {
		siteUrlDao.updateAll(list);
		siteUrlDao.flush();
	}
	
	public SiteUrl findById (Long id) {
		return siteUrlDao.read(id);
	}
}

