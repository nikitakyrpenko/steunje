/*
 * @(#)Id: CategoryController.java, 14.12.2007 16:30:24, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.banner_module.bo.BannerCategory;
import com.negeso.module.banner_module.service.BannerCategoryService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerCategoryController extends SimpleFormController{
	
	private static Logger logger = Logger.getLogger(SimpleFormController.class);
	
	private BannerCategoryService bannerCategoryService;
	
	public BannerCategoryController(){
		setCommandClass(BannerCategory.class);
		setCommandName("category");
	}
	
	@Override
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		logger.debug("+");
		BannerCategory category = (BannerCategory) command;
		try{
			if (category.getId() < 0){
				bannerCategoryService.create(category);
			}else{
				bannerCategoryService.update(category);
			}
			if (category.getParentId() != null ){
				request.setAttribute("parentId", category.getParentId().toString());
			}
			RequestUtil.getHistoryStack(request).goBack();
		}catch(Exception e){
			request.setAttribute("errors", e.getMessage());
		}
		logger.debug("-");
		return new ModelAndView("bm_categorylist", "categorylist", bannerCategoryService.findCategoriesByParentId(category.getParentId()));
	}

	
	public void setBannerCategoryService(BannerCategoryService bannerCategoryService) {
		this.bannerCategoryService = bannerCategoryService;
	}
	
	

}
