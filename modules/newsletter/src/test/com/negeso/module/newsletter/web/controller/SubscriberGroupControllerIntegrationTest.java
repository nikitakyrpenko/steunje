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

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.Env;
import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.Subscriber;
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
public class SubscriberGroupControllerIntegrationTest 
		extends BaseControllerIntegrationTest{
	@Autowired
	private SubscriberGroupController subscriberGroupController;
	@Autowired
	private SubscriberGroupService subscriberGroupService;
	@Autowired
	private ParameterService parameterService;
	
	private int groupCount;
	
	
	@Test
	public void testShowForm() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		ModelAndView mv;
	
		mv = subscriberGroupController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		Assert.assertEquals(mv.getViewName(), "nl_editgroup");
		Assert.assertNotNull(mv.getModel().get("languages"));
		Assert.assertNotNull(mv.getModel().get("group"));
		Assert.assertEquals(((SubscriberGroup)mv.getModel().get("group")).
				getId().longValue(), 
				0
				);
	}
	
	@Test
	public void testUpdateGroup() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("gid", "1");
		request.addParameter("title", "test Title");
		request.addParameter("description", "test Description");
		ModelAndView mv;
	
		mv = subscriberGroupController.handleRequest(
				request, 
				new MockHttpServletResponse());
		SubscriberGroup subscriberGroup = subscriberGroupService.findById(1L);
		Assert.assertEquals("test Title", subscriberGroup.getTitle());
		Assert.assertEquals(mv.getViewName(), "nl_grouplist");
		Assert.assertNotNull(mv.getModel().get("grouplist"));
	}
	
	@Test
	public void testAddGroup() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("title", "test Title");
		request.addParameter("description", "test Description");
		ModelAndView mv;
		mv = subscriberGroupController.handleRequest(
				request, 
				new MockHttpServletResponse());
		int groupCountAfter = subscriberGroupService.getGroupCount();
		SubscriberGroup group = (SubscriberGroup)getSessionFactory().
		getCurrentSession().createQuery("select g from SubscriberGroup as g " +
				"left join g.customFields as fields " +
				"where fields.title = 'test Title' and " +
				"fields.description = 'test Description'").list().get(0);
		Assert.assertNotNull(group);
		Assert.assertEquals(mv.getViewName(), "nl_grouplist");
		Assert.assertNotNull(mv.getModel().get("grouplist"));
	}
	
	
	/* TODO: Fix as soon as possible @Test*/
	public void testExceedingMaxGroupCount() throws Exception{
		groupCount = subscriberGroupService.getGroupCount();
		ConfigurationParameter param = parameterService.findParameterByName(
				"newsletter.max.group.number");
		param.setValue("3");
		parameterService.save(param);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		ModelAndView mv;
		mv = subscriberGroupController.handleRequest(
				request, 
				new MockHttpServletResponse());
		List<String> errors = (List<String>)mv.getModel().get("errors");
		
		Assert.assertEquals(errors.get(0), "max number of groups");
		Assert.assertEquals(mv.getViewName(), "nl_grouplist");
		Assert.assertNotNull(mv.getModel().get("grouplist"));
	}

}

