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
import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.Mailing;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.service.StatisticsService;
import com.negeso.module.newsletter.service.SubscriberService;

import java.util.List;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
@ContextConfiguration(locations={"/test-newsletter-applicationContext.xml"})
public class StatisticsControllerIntegrationTest extends
		BaseControllerIntegrationTest {
	@Autowired
	private StatisticsController statisticsController;
	@Autowired
	private StatisticsService statisticsService; 
	@Autowired
	private SubscriberService subscriberService;
	
	@Test
	public void testShowStatisticsWithNoStatus() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "showStatistics");
		request.addParameter("id", "2");
		ModelAndView mv = statisticsController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		Assert.assertEquals("nl_statistics", mv.getViewName());
	}
	@Test
	public void testShowStatisticsWithNotSentStatus() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "showStatistics");
		request.addParameter("status", "not_sent");
		request.addParameter("id", "2");
		
		ModelAndView mv = statisticsController.handleRequest(
				request, 
				new MockHttpServletResponse());
		List<Mailing> mailings = statisticsService.listSubscriberStatus(2L, 2L, 0);
		assertEquals("nl_statistics", mv.getViewName());
		assertNotNull(mv.getModel().get("statlists"));
		assertNotNull(mv.getModel().get("pageNavigator"));
		assertEquals(
				mailings.size(), 
				((List<Subscriber>)mv.getModel().get("statlists")).size());

	}
	
	/* TODO @Test */
	public void testShowSubscriptionRequests() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "showSubscriptionRequests");
		ModelAndView mv = statisticsController.handleRequest(
				request, 
				new MockHttpServletResponse());
		List<Subscriber> subscribers = subscriberService.listSubscriptionRequests();
		assertEquals("nl_requests", mv.getViewName());
		assertNotNull(mv.getModel().get("subscribers"));
		assertEquals(
				subscribers.size(), 
				((List<Subscriber>)mv.getModel().get("subscribers")).size());
	}

}

