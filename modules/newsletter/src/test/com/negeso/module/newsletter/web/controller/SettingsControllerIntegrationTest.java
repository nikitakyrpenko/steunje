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

import java.util.List;



import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;

import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.service.MailTemplateService;

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
public class SettingsControllerIntegrationTest extends
		BaseControllerIntegrationTest{
	@Autowired
	private SubscriberService subscriberService;
	@Autowired
	private SettingsController settingsController;
	@Autowired
	private MailTemplateService mailTemplateService;
	
	@Test
	public void testShowParameters() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "showParameters");
		
		ModelAndView mv = settingsController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		assertEquals("nl_settings", mv.getViewName());
		assertNotNull(mv.getModel().get("types"));
		assertNotNull(mv.getModel().get("language"));
		assertNotNull(mv.getModel().get("confirmationTemplate"));
		assertNotNull(mv.getModel().get("languages"));
	}
	
	@Test
	public void testSaveParameters() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("attr1", "1");
		request.addParameter("rte_text", "test template");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "saveParameters");
		
		ModelAndView mv = settingsController.handleRequest(
				request, 
				new MockHttpServletResponse());
		assertEquals("redirect:/admin/nl_settings?lang_id=1", mv.getViewName());
		List<SubscriberAttributeType> types = 
			subscriberService.listSubscriberAttributesTypes();
		for (SubscriberAttributeType t : types){
			if (t.getId()==1){
				assertTrue(t.isVisible());
			}else{
				assertFalse(t.isVisible());
			}
		}
		assertEquals("test template", mailTemplateService.findConfirmationText(1L).getText());
		
		
	}
	
	

}

