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
package com.negeso.module.newsletter.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriptionCategory;
import com.negeso.module.newsletter.dao.SubscriptionCategoryDao;
import com.negeso.module.newsletter.service.SubscriptionCategoryService;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
@ContextConfiguration(locations={"/test-newsletter-applicationContext.xml"})
public class SubscriptionCategoryControllerIntegrationTest extends
		BaseControllerIntegrationTest{
	@Autowired
	private SubscriptionCategoryController subscriptionCategoryController;
	@Autowired
	private SubscriptionCategoryService subscriptionCategoryService;
	@Autowired
	private SubscriptionCategoryDao subscriptionCategoryDao;
	
	@Test
	public void testAddSubscriptionCategory() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("title", "test category title");
		ModelAndView mv = subscriptionCategoryController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		SubscriptionCategory subscriptionCategory = subscriptionCategoryDao.
													findByTitle("test category title");
		assertNotNull(subscriptionCategory);
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals("nl_categorylist", mv.getViewName());
	}
	
	@Test
	public void testEditSubscriptionCategory() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("categoryId", "1");
		request.addParameter("title", "test updated category title");
		ModelAndView mv = subscriptionCategoryController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		SubscriptionCategory subscriptionCategory = subscriptionCategoryService.
													findBySubscriptionCategoryId(1L);
		assertNotNull("test updated category title", subscriptionCategory.getTitle());
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals("nl_categorylist", mv.getViewName());
	}
	
	

}

