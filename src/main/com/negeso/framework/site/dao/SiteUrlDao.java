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
import com.negeso.framework.site.SiteUrl;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public interface SiteUrlDao extends GenericDao<SiteUrl, Long> {
	public List<SiteUrl> listBySiteId(Long siteId);
	public List<SiteUrl> listBySiteIdLangId(Long siteId);
}

