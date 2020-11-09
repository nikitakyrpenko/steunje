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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.dao.SubscriberDao;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
@ContextConfiguration(locations={"/test-newsletter-applicationContext.xml"})
public class SubscriberListControllerIntegrationTest extends
		BaseControllerIntegrationTest{
	@Autowired
	private SubscriberListController subscriberListController;
	@Autowired
	private SubscriberDao subscriberDao;
	@Autowired
	private SubscriberService subscriberService;
	
	
	/*TODO @Test*/
	public void testShowActivatedSubscribers() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		
		ModelAndView mv = subscriberListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		List<Subscriber> subscribers = subscriberDao.listActivatedSubscribers();
		assertNotNull(mv.getModel().get("groups"));
		assertNotNull(mv.getModel().get("subscribers"));
		assertEquals(subscribers.size(),((List<Subscriber>)mv.getModel().get("subscribers")).size());
		assertEquals("nl_subscriberslist", mv.getViewName());
	}
	
	/* TODO @Test*/
	public void testShowSubscribersWithNoGroup() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("groupId", "0");
		ModelAndView mv = subscriberListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		List<Subscriber> subscribers = subscriberDao.listSubscribersWithNoGroup();
		assertNotNull(mv.getModel().get("groups"));
		assertNotNull(mv.getModel().get("subscribers"));
		assertEquals(subscribers.size(),((List<Subscriber>)mv.getModel().get("subscribers")).size());
		assertEquals("nl_subscriberslist", mv.getViewName());
	}
	
	/* TODO @Test */
	public void testShowSubscribersByGroupId() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("groupId", "1");
		ModelAndView mv = subscriberListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		List<Subscriber> subscribers = subscriberDao.listSubscribersByGroupId(1L);
		assertNotNull(mv.getModel().get("groups"));
		assertNotNull(mv.getModel().get("subscribers"));
		assertEquals(subscribers.size(),((List<Subscriber>)mv.getModel().get("subscribers")).size());
		assertEquals("nl_subscriberslist", mv.getViewName());
	}
	
	/* TODO @Test */
	public void testShowSubscribersByQuery() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("query", "netdeveloper");
		ModelAndView mv = subscriberListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		List<Subscriber> subscribers = subscriberDao.listSubscribersByQuery("%netdeveloper%");
		assertNotNull(mv.getModel().get("groups"));
		assertNotNull(mv.getModel().get("subscribers"));
		assertEquals(subscribers.size(),((List<Subscriber>)mv.getModel().get("subscribers")).size());
		assertEquals("nl_subscriberslist", mv.getViewName());
	}
	
	/* TODO @Test */
	public void testShowSubscribersByQueryAndGroupId() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("groupId", "3");
		request.addParameter("query", "netdeveloper");
		ModelAndView mv = subscriberListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		List<Subscriber> subscribers = subscriberDao.listSubscribersByGroupIdAndQuery(3L, "%netdeveloper%");
		assertNotNull(mv.getModel().get("groups"));
		assertNotNull(mv.getModel().get("subscribers"));
		assertEquals(subscribers.size(),((List<Subscriber>)mv.getModel().get("subscribers")).size());
		assertEquals("nl_subscriberslist", mv.getViewName());
	}
	
	/* TODO Test is not passed. Refactor ASAP @Test*/
	public void testDeleteSubscriber() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "delete");
		request.addParameter("id", "242");
		ModelAndView mv = subscriberListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		Subscriber subscriber = subscriberService.findById(242L);
		assertNull(subscriber);
		assertNotNull(mv.getModel().get("groups"));
		assertNotNull(mv.getModel().get("subscribers"));
		
		assertEquals("nl_subscriberslist", mv.getViewName());
	}
	
	/* TODO: Test is not writed. Write ASAP @Test */
	public void testDeleteFromStatistics() throws Exception{
		fail("Test is under construction");
	}
	
	@Test
	public void testUnsubscribe() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "unSubscribe");
		request.addParameter("id", "242");
		ModelAndView mv = subscriberListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		Subscriber subscriber = subscriberService.findById(242L);
		assertEquals(0, subscriber.getSubscriberGroups().size());
	}

}

