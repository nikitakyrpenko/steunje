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
package com.negeso.framework.menu;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.menu.bo.Menu;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.module.core.PreparedModelAndView;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class MenuItemController extends  MultiActionController {
	
	private IMenuService menuService;
	private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	
	public ModelAndView editMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		Menu menu = menuService.readMenu(id);		 
		
		if ("save".equals(ServletRequestUtils.getStringParameter(request, "act"))) {
			String title = ServletRequestUtils.getStringParameter(request, "title").trim();			
			menu.setTitle(title);
			Timestamp publishDate = converToTimestamp(ServletRequestUtils.getStringParameter(request, "publishDate"));
			Timestamp expiredDate = converToTimestamp(ServletRequestUtils.getStringParameter(request, "expiredDate"));
			String link = ServletRequestUtils.getStringParameter(request, "link");
			
			if (menu.getPageId() == null) {		
				menu.setPublishDate(publishDate);
				menu.setExpiredDate(expiredDate);
				menu.setLink(link);
			}else{
				PageService pageService = PageService.getInstance();
				PageH page = pageService.findById(menu.getPageId());
				page.setPublishDate(publishDate);
				page.setExpiredDate(expiredDate);
				pageService.save(page);
			}
			menuService.saveMenu(menu);
			menuService.flush();
		}
		return new PreparedModelAndView("menu_item_dialog")
						.addToModel("menuItem", menu)
						.get();
	}
	
	public ModelAndView moveMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		Menu menu = menuService.readMenu(id);
		Boolean up = ServletRequestUtils.getBooleanParameter(request, "up");		
		menuService.moveMenu(menu, up);		
		menuService.saveMenu(menu);
		menuService.flush();
		response.getWriter().print("success");
		return null;
	}

	public void setMenuService(IMenuService menuService) {
		this.menuService = menuService;
	}
	
	public Timestamp converToTimestamp(String str){ 
		if (StringUtils.isNotBlank(str)){
			try {
				return new Timestamp(format.parse(str).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

