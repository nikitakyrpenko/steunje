/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.menu.bo.Menu;
import com.negeso.framework.menu.service.MenuService;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.site_map.*;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class MenuPagesHandler implements PagesHandler{
	
	private static final String FRONTPAGE = "frontpage";
	
	Logger logger = Logger.getLogger(MenuPagesHandler.class);

	@Override
	public PageDescriptor getPages(Long langId, Object... args) {
		logger.debug("+");
		PageDescriptor root = new PageDescriptor(null, "Menu", false, false, null, null, null,null);
		Connection conn = null; 
		try{
			conn = DBHelper.getConnection();
			List<Menu> menu = MenuService.getSpringInstance().getMain(langId, Env.getSiteId());
			addPagesToDescriptor(root, langId, menu);						
			root.getContent().add(0, getFrontPageDescriptor(langId));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBHelper.close(conn);
		}
		logger.debug("-");
		return root;
	}
	
	private PageDescriptor getFrontPageDescriptor(Long langId){
		logger.debug("+");
		PageH front = null;
		PageDescriptor frontPage = null;
		try {
			front = PageService.getInstance().findByCategory(FRONTPAGE, Language.findById(langId).getCode());			
			frontPage = new PageDescriptor(
					front.getId(),
					front.getTitle() + " - " + front.getCategory(),
					true,
					false,
					front.getId(),
					null,
					front.getFilename(),
					null);
		} catch (Exception e) {
			logger.error("cannot find frontpage!");
		}
		logger.debug("-");
		return frontPage;
	}
	
	private void addPagesToDescriptor(PageDescriptor page, Long langId, List<Menu> menu){
		logger.debug("+");
		List<PageDescriptor> pages = new ArrayList<PageDescriptor>();			
		for (Menu item : menu) {
			if (item.getPageId() != null){						
				PageH p = PageService.getInstance().findById(item.getPageId());
				PageDescriptor pageDescriptor = new PageDescriptor(
						item.getId(), 
						item.getTitle(),
						false,
						!p.isDateOk(),
						p.getId(),
						null,
						p.getFilename(),
						null);
				addPagesToDescriptor(pageDescriptor, langId, item.getMenuItems());
				pages.add(pageDescriptor);		
							
			}
		}	
		if (menu.size() == 0){			
			page.setLeaf(true);
		}
		page.setContent(pages);
		logger.debug("-");
	}

	@Override
	public String getPage(Long langId) {
		return null;
	}

}

