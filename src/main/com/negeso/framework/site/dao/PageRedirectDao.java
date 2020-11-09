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
package com.negeso.framework.site.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.framework.site.PageRedirect;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public interface PageRedirectDao extends GenericDao<PageRedirect, Long>{
	public List<PageRedirect> listByLangId(Long langId,Long siteId);
	public List<PageRedirect> listByLangAndCountry(Long langId, String countryCode, Long siteId);
	public List<PageRedirect> listAll(Long siteId);
	
}

