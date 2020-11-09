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

import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.module.newsletter.bo.MailTemplate;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.service.MailTemplateService;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
@ContextConfiguration(locations={"/test-newsletter-applicationContext.xml"})
public class MailTemplateListControllerIntegrationTest extends
		BaseControllerIntegrationTest {
	@Autowired
	private MailTemplateListController mailTemplateListController;
	@Autowired
	private MailTemplateService mailTemplateService;
	
	@Test
	public void testList() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		//request.addParameter("id", "0");
		
		ModelAndView mv = mailTemplateListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		List<MailTemplate> templates= mailTemplateService.findAllMailTemplates(1L);
		assertNotNull(templates);
		assertEquals(templates.size(),
				((List<MailTemplate>)mv.getModel().get("templates")).size());
		assertEquals("nl_mailtemplates", mv.getViewName());
	}
	
	@Test
	public void testDelete() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("id", "1");
		request.addParameter("action", "delete");
		ModelAndView mv = mailTemplateListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		MailTemplate template= mailTemplateService.getMailTemplate(1L);
		assertNull(template);
		assertEquals("nl_mailtemplates", mv.getViewName());
	}
	
	

}

