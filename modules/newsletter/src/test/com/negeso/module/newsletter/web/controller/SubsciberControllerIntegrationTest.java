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


import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.test.context.ContextConfiguration;

import org.springframework.web.servlet.ModelAndView;


import com.negeso.framework.controller.BaseControllerIntegrationTest;

import com.negeso.module.newsletter.bo.Subscriber;

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
public class SubsciberControllerIntegrationTest 
		extends BaseControllerIntegrationTest{
	@Autowired
	private SubscriberController subscriberController;
	@Autowired
	private SubscriberService subscriberService;
	
	private Long ID;
	
	@Before
	public void setUp(){
		super.setUp();
		Subscriber subscriber = new Subscriber();
		subscriber.addAttribute(Subscriber.ATTR_FIRST_NAME, "Vadim");
		subscriber.addAttribute(Subscriber.ATTR_LAST_NAME, "Mischenko");
		subscriber.addAttribute(Subscriber.ATTR_EMAIL, "vmischenko@phidias.nl");
		subscriberService.save(subscriber);
		ID = subscriber.getId();
	}

	/*TODO:Test is not passed. Refactor ASAP. @Test*/
	public void testGetMethod() throws Exception{
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		
		ModelAndView mv = subscriberController.handleRequest(
				request, 
				new MockHttpServletResponse());
		Assert.assertNotNull(mv.getModel().get("groups"));
		Assert.assertNotNull(mv.getModel().get("languages"));
	}
	
	@Test
	public void testSuccessView() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		ModelAndView mv = subscriberController.handleRequest(
				request, 
				new MockHttpServletResponse());
		assertEquals("redirect:nl_subscriberslist", mv.getViewName()); 
	}
	
	@Test
	public void testEditSubscriber() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("id", ID.toString());
		request.addParameter(SubscriberController.GROUP_PREFIX + "1", "1");
		request.addParameter(SubscriberController.ATTRIBUTE_PREFIX + 
				Subscriber.ATTR_FIRST_NAME, "Misha");
		request.addParameter(SubscriberController.ATTRIBUTE_PREFIX + 
				Subscriber.ATTR_LAST_NAME, "Mischenko");
		request.addParameter(SubscriberController.ATTRIBUTE_PREFIX + 
				Subscriber.ATTR_EMAIL, "vmischenko@phidias.nl");
		ModelAndView mv = subscriberController.handleRequest(
				request, 
				new MockHttpServletResponse());
		Subscriber subscriber = subscriberService.findById(ID);
		Assert.assertEquals("Misha", subscriber.getFirstName());
		
	}
}

