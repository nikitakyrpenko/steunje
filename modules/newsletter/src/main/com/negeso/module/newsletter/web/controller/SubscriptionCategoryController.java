/*
 * @(#)Id: NewsletterCategoryController.java, 21.02.2008 18:36:50, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
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
public class SubscriptionCategoryController extends SimpleFormController {

	private static final Logger logger = Logger.getLogger(SubscriptionCategoryController.class);
	
	private SubscriptionCategoryService subscriptionCategoryService;
	
	public SubscriptionCategoryController(){
		setCommandClass(SubscriptionCategory.class);
		setCommandName("subscriptionCategory");
	}
	
	public void setSubscriptionCategoryService( SubscriptionCategoryService service) {
		this.subscriptionCategoryService = service;
	}

	public Object formBackingObject(HttpServletRequest request) throws Exception {
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(request, "categoryId");
		
		SubscriptionCategory category = null;
		
		if (id == null){
			category = new SubscriptionCategory();
			RequestUtil.getHistoryStack(request).push( new Link( "New category", 
					"/admin/nl_editcategory", false) );
		}else{
			category = subscriptionCategoryService.findBySubscriptionCategoryId(id);
			RequestUtil.getHistoryStack(request).push( new Link( category.getTitle(), 
					"/admin/nl_editcategory?categoryId=" + id, false) );
		}
		
		logger.debug("-");
		return category;
	}
	
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
			Object command, BindException errors) throws Exception {
		logger.debug("+");
		
		SubscriptionCategory category = (SubscriptionCategory) command;
		
		category.setOrderNumber(new Long(subscriptionCategoryService.getNewOrderNumber()));
		subscriptionCategoryService.save(category);
		
		RequestUtil.getHistoryStack(request).goBack();
		
		RequestUtil.getHistoryStack(request).push( new Link( "Subscription categories", 
				"/admin/nl_categorylist", false) );
		
		logger.debug("-");
		return new ModelAndView("nl_categorylist", "categorylist", subscriptionCategoryService.listSubscriptionCategory());
	}

}
