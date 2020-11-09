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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.site.PageRedirect;
import com.negeso.framework.site.dao.PageRedirectDao;

/**
 * 
 * @TODO
 * 
 * @author		Andriy Zagorulko
 * @version		$Revision: $
 *
 */
public class PageRedirectService {
	
	private static PageRedirectService pageRedirectService = null;
	
	private Map<String,List<PageRedirect>> redirectsCash = null;
	
	public static PageRedirectService getSpringInstance(){
		if(pageRedirectService == null){
			pageRedirectService = (PageRedirectService)DispatchersContainer.getInstance().getBean("core", "pageRedirectService");
		}
		return pageRedirectService;
	}
	
	
	private PageRedirectDao pageRedirectDao;
	
	public void createOrUpdate (PageRedirect pageRedirect) {
		pageRedirectDao.createOrUpdate(pageRedirect);
		pageRedirectDao.flush();
		clearCashe();
	}
	
	public void delete (PageRedirect pageRedirect) {
		pageRedirectDao.delete(pageRedirect);
		pageRedirectDao.flush();
		clearCashe();
	}
	
	public PageRedirect find (Long id) {
		return pageRedirectDao.read(id);
	}
	
	
	public List<PageRedirect> listByLangAndCountry(Long langId, String countryCode){
		if(langId == null){
			langId = 0L;
		}
		if(redirectsCash != null && redirectsCash.get(hash(langId,countryCode))!= null){
			return redirectsCash.get(hash(langId,countryCode));
		}
		List<PageRedirect> redirects = pageRedirectDao.listByLangAndCountry(langId,countryCode,Env.getSiteId());
		if(redirectsCash == null){
			redirectsCash = new HashMap<String, List<PageRedirect>>(); 
		}
		redirectsCash.put(hash(langId,countryCode), redirects);
		return redirects;
		
	}
	
	private String hash(Long langId, String countryCode){
		return  langId.toString() + countryCode;
	}
	
	
	public List<PageRedirect> listAll(){
		return pageRedirectDao.listAll(Env.getSiteId());
	}

	public void setPageRedirectDao(PageRedirectDao pageRedirectDao) {
		this.pageRedirectDao = pageRedirectDao;
	}
	
	private void clearCashe(){
		redirectsCash = null;
	}
	
}

