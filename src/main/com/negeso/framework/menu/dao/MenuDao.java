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
package com.negeso.framework.menu.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.framework.menu.bo.Menu;

/**
 * 
 * @TODO
 * 
 * @author		E.Dzhentemirova
 * @version		$Revision: $
 *
 */

public interface MenuDao extends GenericDao<Menu, Long>{

	public List<Menu> listMenuByLink(String link);
	public List<Menu> listMainMenu(Long langId, Long siteId);
	public List<Menu> listAllMenuForLanguage(Long langId, Long siteId);
	public List<Menu> listByPageId(Long pageId);	
	public int countItems(Long langId, Long siteId);
	
}
