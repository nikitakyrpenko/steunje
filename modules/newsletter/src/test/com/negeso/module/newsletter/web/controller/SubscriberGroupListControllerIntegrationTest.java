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

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.Env;
import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.service.SubscriberGroupService;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
@ContextConfiguration(locations={"/test-newsletter-applicationContext.xml"})
public class SubscriberGroupListControllerIntegrationTest 
		extends BaseControllerIntegrationTest {
	@Autowired
	private SubscriberGroupListController subscriberGroupListController;
	@Autowired
	private SubscriberGroupService subscriberGroupService;
	
	
	@Test
	public void testDeleteGroup() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "delete");
		request.addParameter("id", "1");
		
		ModelAndView mv = subscriberGroupListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		SubscriberGroup group = (SubscriberGroup)subscriberGroupService.findById(1L);
		assertNull(group);
		assertNotNull(mv.getModel().get("grouplist"));
		assertEquals("nl_grouplist", mv.getViewName());
	}
	
	@Test
	public void testChangeDirectionWrong() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "changeDirection");
		request.addParameter("id", "1");
		request.addParameter("direction", "true");
		
		SubscriberGroup group = (SubscriberGroup)subscriberGroupService.findById(1L);
		long  oldOrderNumber = group.getOrderNumber().longValue();

		//TODO: Refactor in future
		
		Session session = DBHelper.getSessionFactory().getCurrentSession();
		Connection conn = session.connection();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		ModelAndView mv = null;
		try{
			mv = subscriberGroupListController.handleRequest(
					request, 
					new MockHttpServletResponse());
			group = (SubscriberGroup)session.get(SubscriberGroup.class, 1L);
		}catch (Exception e){
			fail("Error in Controller ocurred");
		}finally{
			conn.rollback();
			conn.setAutoCommit(autoCommit);
		}
	
		assertEquals(oldOrderNumber, group.getOrderNumber().longValue());
		assertNotNull(mv.getModel().get("grouplist"));
		assertEquals("nl_grouplist", mv.getViewName());
		
	}
	
	@Test
	public void testChangeDirectionDown() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "changeDirection");
		request.addParameter("id", "1");
		request.addParameter("direction", "false");
		
		SubscriberGroup group = (SubscriberGroup)subscriberGroupService.findById(1L);
		long  oldOrderNumber = group.getOrderNumber().longValue();

		//TODO: Refactor in future
		
		Session session = DBHelper.getSessionFactory().getCurrentSession();
		Connection conn = session.connection();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		ModelAndView mv = null;
		try{
			mv = subscriberGroupListController.handleRequest(
					request, 
					new MockHttpServletResponse());
			group = (SubscriberGroup)session.get(SubscriberGroup.class, 1L);
		}catch (Exception e){
			fail("Error in Controller ocurred");
		}finally{
			conn.rollback();
			conn.setAutoCommit(autoCommit);
		}
	
		assertEquals(oldOrderNumber + 1, group.getOrderNumber().longValue());
		assertNotNull(mv.getModel().get("grouplist"));
		assertEquals("nl_grouplist", mv.getViewName());
		
	}
	
	@Test
	public void testChangeDirectionUp() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "changeDirection");
		request.addParameter("id", "2");
		request.addParameter("direction", "true");
		
		SubscriberGroup group = (SubscriberGroup)subscriberGroupService.findById(2L);
		long  oldOrderNumber = group.getOrderNumber().longValue();

		//TODO: Refactor in future
		
		Session session = DBHelper.getSessionFactory().getCurrentSession();
		Connection conn = session.connection();
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		ModelAndView mv = null;
		try{
			mv = subscriberGroupListController.handleRequest(
					request, 
					new MockHttpServletResponse());
			group = (SubscriberGroup)session.get(SubscriberGroup.class, 2L);
		}catch (Exception e){
			fail("Error in Controller ocurred");
		}finally{
			conn.rollback();
			conn.setAutoCommit(autoCommit);
		}
		
		assertEquals(oldOrderNumber - 1, group.getOrderNumber().longValue());
		assertNotNull(mv.getModel().get("grouplist"));
		assertEquals("nl_grouplist", mv.getViewName());
		
	}
	
	@Test
	public void testShowGroups() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		
		ModelAndView mv = subscriberGroupListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		assertNotNull(mv.getModel().get("grouplist"));
		assertEquals("nl_grouplist", mv.getViewName());
	}

}

