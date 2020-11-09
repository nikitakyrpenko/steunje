/*
 * @(#)Id: NewsletterController.java, 21.02.2008 15:56:18, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.SubscriptionCategory;
import com.negeso.module.newsletter.service.SubscriptionCategoryService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriptionCategoryListController extends MultiActionController{
	
	private static final Logger logger = Logger.getLogger(SubscriptionCategoryListController.class);
	
	private SubscriptionCategoryService subscriptionCategoryService;

	public void setSubscriptionCategoryService(SubscriptionCategoryService newsletterCategoryService) {
		this.subscriptionCategoryService = newsletterCategoryService;
	}

    public ModelAndView subscriptionCategoryList(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		List<SubscriptionCategory> categories = subscriptionCategoryService.listSubscriptionCategory();
		
		RequestUtil.getHistoryStack(request).push( new Link("NL.PUBLICATION_CATEGORIES",
				"/admin/nl_categorylist", true, 0) );
		logger.debug("-");
		return new ModelAndView("nl_categorylist", "categorylist", categories);
	}
	
	public ModelAndView deleteSubscriptionCategory(HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(request, "categoryId");
		SubscriptionCategory category = subscriptionCategoryService.findBySubscriptionCategoryId(id);
		
		if(category == null){
			logger.debug("-");
			return new PreparedModelAndView("nl_categorylist").				
				addToModel("categorylist", subscriptionCategoryService.listSubscriptionCategory()).get();				
		}
		
		if (!subscriptionCategoryService.isCategoryEmpty(category)){
			logger.debug("-");
			return new PreparedModelAndView("nl_categorylist").
				addToModel("categorylist", subscriptionCategoryService.listSubscriptionCategory()).
				addError("category is not empty").get();
		}
		
		subscriptionCategoryService.delete(category);
		
		logger.debug("-");
		return new PreparedModelAndView("nl_categorylist").
			addToModel("categorylist", subscriptionCategoryService.listSubscriptionCategory()).get();
	}
	
	public ModelAndView changeDirection(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		boolean direction = ServletRequestUtils.getBooleanParameter(request, "direction", false);
		
		subscriptionCategoryService.changeDirection(id, direction);
		
		logger.debug("-");
		return subscriptionCategoryList(request, response);
	}

}
