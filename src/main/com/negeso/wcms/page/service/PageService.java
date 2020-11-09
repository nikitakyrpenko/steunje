/*
 * @(#)$Id: PageService.java,v 1.2, 2007-03-22 17:58:58Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.wcms.page.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.menu.IMenuService;
import com.negeso.framework.page.Page;
import com.negeso.wcms.service.AbstractJdbcService;

/**
 * 
 * @TODO
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 3$
 *
 */
public class PageService extends AbstractJdbcService {
    private static Logger logger = Logger.getLogger( PageService.class );
    
    private IMenuService menuService;

	public IMenuService getMenuService() {
		return menuService;
	}

	public void setMenuService(IMenuService menuService) {
		this.menuService = menuService;
	}

	public Page createSpecialPage(String fileName, String title, long langId) {
		logger.debug("+");
		
		
		Page page = new Page();
		page.setFilename(fileName);
		page.setCategory("page");
		page.setLangId(langId);
		page.setVisible(true);
		page.setTitle(title);
		page.insert(this.getConnection());
		//menuService.createSpecialPagesMenuItem(title, fileName, langId);
		
		logger.debug("-");
		return page;
	}

	public boolean isPageExistsWithFilename(String fileName) {
		boolean res = false;
		try{
			PreparedStatement stmt = this.getConnection().prepareStatement(
				"SELECT id FROM page WHERE filename=?"
			);
			stmt.setString(1, fileName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				res = true;
			}
			rs.close();
			stmt.close();
		} catch (Exception e) { 
			logger.error("-error", e);
		}
		return res;
	}

	public Page findPageById(Long pageId) {
		try{
			return Page.findById(this.getConnection(), pageId);
		} catch (ObjectNotFoundException e) {
			logger.error("error", e);
		}
		return null;
	} 
}
