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

import org.hibernate.criterion.Restrictions;

import com.negeso.framework.site.PageAlias;
import com.negeso.framework.site.dao.PageAliasDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PageAliasService {
	
	private PageAliasDao pageAliasDao;
	
	public void createOrUpdate (PageAlias pageAlias) {
		pageAliasDao.createOrUpdate(pageAlias);
		pageAliasDao.flush();
	}
	
	public void delete (PageAlias pageAlias) {
		pageAliasDao.delete(pageAlias);
		pageAliasDao.flush();
	}
	
	public PageAlias find (Long id) {
		return pageAliasDao.read(id);
	}
	
	public PageAlias findByFilename (String fileName) {
		List<PageAlias> pageList = pageAliasDao.readByCriteria(Restrictions.ilike("fileName", fileName));
		if (pageList.size() > 0) {
			return pageList.get(0);
		}
		return null;
	}
	
	public List<PageAlias> readAll(Long siteId) {
		return pageAliasDao.listSortedById(siteId);
	}
	
	public List<PageAlias> readAll(Long siteId, Long pageId) {
		return pageAliasDao.listByPageIdSorted(siteId, pageId);
	}

	public PageAliasDao getPageAliasDao() {
		return pageAliasDao;
	}

	public void setPageAliasDao(PageAliasDao pageAliasDao) {
		this.pageAliasDao = pageAliasDao;
	}

	public List<PageAlias> readByLangId(Long siteId, Long langId) {
		return pageAliasDao.readByCriteria(Restrictions.eq("siteId", siteId), Restrictions.eq("langId", langId));
	}
}

