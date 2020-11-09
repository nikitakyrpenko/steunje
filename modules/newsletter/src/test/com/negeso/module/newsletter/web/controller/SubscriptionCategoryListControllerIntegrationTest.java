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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.List;

import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.bo.SubscriptionCategory;
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
public class SubscriptionCategoryListControllerIntegrationTest extends
		BaseControllerIntegrationTest {
	@Autowired
	private SubscriptionCategoryListController subscriptionCategoryListController;
	@Autowired
	private SubscriptionCategoryService subscriptionCategoryService;
	
	@Test
	public void subscriptionCategoryList() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "subscriptionCategoryList");
		ModelAndView mv = subscriptionCategoryListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		List<SubscriptionCategory> categories = subscriptionCategoryService.
												listSubscriptionCategory();
		
		
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals(categories.size(), 
				((List<SubscriptionCategory>)mv.getModel().
						get("categorylist")).size());
		assertEquals("nl_categorylist", mv.getViewName());
	}
	
	@Test
	public void testDeleteEmptySubscriptionCategory() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "deleteSubscriptionCategory");
		request.addParameter("categoryId", "1");
		ModelAndView mv = subscriptionCategoryListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		SubscriptionCategory category = subscriptionCategoryService.
										findBySubscriptionCategoryId(1L);
		
		assertNull(category);
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals("nl_categorylist", mv.getViewName());
	}
	
	@Test
	public void testDeleteNotEmptySubscriptionCategory() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "deleteSubscriptionCategory");
		request.addParameter("categoryId", "2");
		ModelAndView mv = subscriptionCategoryListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		SubscriptionCategory category = subscriptionCategoryService.
										findBySubscriptionCategoryId(2L);
		
		assertNotNull(category);
		assertEquals("category is not empty", ((List<String>)mv.getModel().get("errors")).get(0));
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals("nl_categorylist", mv.getViewName());
	}
	
	@Test
	public void testChangeDirectionUp() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "changeDirection");
		request.addParameter("id", "2");
		request.addParameter("direction", "true");
		
		SubscriptionCategory category = (SubscriptionCategory)subscriptionCategoryService.
								findBySubscriptionCategoryId(2L);
		long  oldOrderNumber = category.getOrderNumber().longValue();

		//TODO: Refactor in future
		
		Session session = DBHelper.getSessionFactory().getCurrentSession();
		Connection conn = session.connection();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		ModelAndView mv = null;
		try{
			mv = subscriptionCategoryListController.handleRequest(
					request, 
					new MockHttpServletResponse());
			category = (SubscriptionCategory)session.get(SubscriptionCategory.class, 2L);
		}catch (Exception e){
			fail("Error in Controller ocurred");
		}finally{
			conn.rollback();
			conn.setAutoCommit(autoCommit);
		}
	
		assertEquals(oldOrderNumber - 1, category.getOrderNumber().longValue());
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals("nl_categorylist", mv.getViewName());
	}
	
	@Test
	public void testChangeDirectionDown() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "changeDirection");
		request.addParameter("id", "1");
		request.addParameter("direction", "false");
		
		SubscriptionCategory category = (SubscriptionCategory)subscriptionCategoryService.
								findBySubscriptionCategoryId(1L);
		long  oldOrderNumber = category.getOrderNumber().longValue();

		//TODO: Refactor in future
		
		Session session = DBHelper.getSessionFactory().getCurrentSession();
		Connection conn = session.connection();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		ModelAndView mv = null;
		try{
			mv = subscriptionCategoryListController.handleRequest(
					request, 
					new MockHttpServletResponse());
			category = (SubscriptionCategory)session.get(SubscriptionCategory.class, 1L);
		}catch (Exception e){
			fail("Error in Controller ocurred");
		}finally{
			conn.rollback();
			conn.setAutoCommit(autoCommit);
		}
	
		assertEquals(oldOrderNumber + 1, category.getOrderNumber().longValue());
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals("nl_categorylist", mv.getViewName());
	}
	
	@Test
	public void testChangeDirectionWrong() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "changeDirection");
		request.addParameter("id", "1");
		request.addParameter("direction", "true");
		
		SubscriptionCategory category = (SubscriptionCategory)subscriptionCategoryService.
								findBySubscriptionCategoryId(1L);
		long  oldOrderNumber = category.getOrderNumber().longValue();

		//TODO: Refactor in future
		
		Session session = DBHelper.getSessionFactory().getCurrentSession();
		Connection conn = session.connection();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		ModelAndView mv = null;
		try{
			mv = subscriptionCategoryListController.handleRequest(
					request, 
					new MockHttpServletResponse());
			category = (SubscriptionCategory)session.get(SubscriptionCategory.class, 1L);
		}catch (Exception e){
			fail("Error in Controller ocurred");
		}finally{
			conn.rollback();
			conn.setAutoCommit(autoCommit);
		}
	
		assertEquals(oldOrderNumber, category.getOrderNumber().longValue());
		assertNotNull(mv.getModel().get("categorylist"));
		assertEquals("nl_categorylist", mv.getViewName());
	}

}

