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
package com.negeso.framework.menu.service;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.ApplicationContextProvider;
import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.OrderControlHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.menu.IMenuService;
import com.negeso.framework.menu.MenuBuilder;
import com.negeso.framework.menu.bo.Menu;
import com.negeso.framework.menu.dao.MenuDao;

/**
 * 
 * @TODO
 * 
 * @author		E.Dzhentemirova
 * @version		$Revision: $
 *
 */
public class MenuService implements IMenuService {
	
	private static Logger logger = Logger.getLogger(MenuService.class);
	
	private static IMenuService menuService = null;
	
	private MenuDao menuDao;	
	
	private static OrderControlHelper orderControlHelper = new OrderControlHelper("menu", "parent_id");
	
	public static IMenuService getSpringInstance(){
		if(menuService == null){
			menuService = (IMenuService) ApplicationContextProvider.getApplicationContext().getBean("menuService");
		}
		return menuService;
	}
	
	public MenuService(){}
	
	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}

	public MenuDao getMenuDao() {
		return menuDao;
	}
	
	@Transactional
	@Override
	public void saveMenu(Menu menu){
		menuDao.createOrUpdate(menu);
	}
		
	@Override
	public Menu readMenu(Long id){
		return menuDao.read(id);
	}
	
	@Transactional
	@Override
	public void deleteMenu(Long id){
		Menu menu = menuDao.read(id);		
		if (menu != null) {				
			menuDao.delete(menu);	            
	    }
	}
	
	@Transactional
	@Override
	public void deleteMenuByPageId(Long pageId){
		List<Menu> menus = menuDao.listByPageId(pageId);		
		if (menus != null) {				
			menuDao.deleteAll(menus);	            
	    }
	}	
		
	public Menu getMenuByLink(String link) {
		List<Menu> menus = menuDao.listMenuByLink(link);
		return (menus.size()>0) ? menus.get(0) : null;
	}
	
	@Override
	public List<Menu> getMain(Long langId, Long siteId) {
		List<Menu> menuList = menuDao.listAllMenuForLanguage(langId, siteId);
		Map<Long, Menu> map = new HashMap<Long, Menu>();
		List<Menu> menuTree = new ArrayList<Menu>();
		Menu parent = null; 
		for (Menu menu : menuList) {
			menu.setMenuItems(new LinkedList<Menu>());
			map.put(menu.getId(), menu);
			if ((parent = map.get(menu.getParentId())) != null){
				parent.getMenuItems().add(menu);
			} else {
				menuTree.add(menu);
			}
			
		}
		return menuTree;
	}	
	
	
	public 	boolean isMaximumReached(Long langId, Long siteId) {
		return getMaxTopItems() <= menuDao.countItems(langId, siteId);		
	}	
	    
	public static boolean isUnlimitedTopMenu() {
		return getMaxTopItems() == 0;
	}
	
	private static int getMaxTopItems() {
		return Integer.parseInt(Env.getProperty("menu.top.items.limit", "0"));
	}
	
	@Override
	public void moveMenu(Menu curMenu, Boolean up) {
		logger.debug("+");
        Connection connection;
		try {
			connection = DBHelper.getConnection();
			if (up){
	        	orderControlHelper.moveUp(connection, curMenu.getId(), curMenu.getOrder().longValue(), curMenu.getParentId());
	        }else{
	        	orderControlHelper.moveDown(connection, curMenu.getId(), curMenu.getOrder().longValue(), curMenu.getParentId());
	        }			
		} catch (SQLException e) {			
			e.printStackTrace();
		}                
        logger.debug("-");	
	}
	
	@Override
	public Long getNextOrder(Long parentId) {
		logger.debug("+");
        Connection connection;
		try {
			connection = DBHelper.getConnection();
			return orderControlHelper.getNextInsertOrder(connection, parentId);
		} catch (SQLException e) {			
			e.printStackTrace();
		}                
        logger.debug("-");
		return 0L;	
	}
	
	
	@Override
	public Document getMenuXml(Language language, User user) {
        logger.debug("+");
        Element root = Xbuilder.createTopEl("main_menu", null);
    	List<Menu> topMenu = getMain(language.getId(), Env.getSiteId());
    	new MenuBuilder().buildMenuXML(root, user, topMenu, false, language.getCode());
        return root.getOwnerDocument();
    	
    }
	
	@Override
	public void getMenuExplorerXml(Element root, boolean truncate, String langCode, User user) {
        logger.debug("+");
        List<Menu> topMenu = null;               
        try {
        	Long siteId = Env.getSiteId();
        	Long langId = Language.findByCode(langCode).getId();
        	topMenu = getMain(langId, siteId);
        } catch (Exception e) {
             logger.error("- cannot find main menu", e);
             e.printStackTrace();             
        }
        MenuBuilder builder = new MenuBuilder();
        try{        	
			 builder.buildMenuXML(root, user, topMenu, truncate, langCode);			 
        }catch (Exception e) {
        	logger.error(e);        
		}            	
    }

	@Override
	public void flush() {
		//used only to flush cache
	}

	@Override
	public Long determinateSelectedMenuId(String curPage, Long curItemId) {
		Menu selMi = null;
		if (curItemId != null) {
			selMi = menuDao.read(curItemId);
		}
		if (selMi == null && curPage != null) {
			selMi = getMenuByLink(curPage);
		}
		return selMi != null ? selMi.getId() : 0L;
	}
}
