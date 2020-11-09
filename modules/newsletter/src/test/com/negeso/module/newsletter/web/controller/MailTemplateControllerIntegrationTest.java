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
import com.negeso.module.newsletter.bo.MailTemplate;
import com.negeso.module.newsletter.dao.MailTemplateDao;
import com.negeso.module.newsletter.service.MailTemplateService;

/**
 * 
 * @TODO
 * 
 * @author Pochapskiy Olexandr
 * @version $Revision: $
 * 
 */
@ContextConfiguration(locations = { "/test-newsletter-applicationContext.xml" })
public class MailTemplateControllerIntegrationTest extends
		BaseControllerIntegrationTest {
	@Autowired
	private MailTemplateController mailTemplateController;
	@Autowired
	private MailTemplateService mailTemplateService;
	@Autowired
	private MailTemplateDao mailTemplateDao;

	@Test
	public void testAddMailTemplate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("title", "test title");
		request.addParameter("text", "test text");
		ModelAndView mv = mailTemplateController.handleRequest(request,
				new MockHttpServletResponse());
		MailTemplate template = (MailTemplate) mailTemplateDao.findByTitle(
				"test title", 1L);
		assertNotNull(template);
		assertEquals("test text", template.getText());
		assertEquals("redirect:/admin/nl_mailtemplates", mv.getViewName());
	}

	@Test
	public void testEditMailTemplate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("id", "1");
		request.addParameter("lang_id", "1");
		request.addParameter("title", "test title");
		request.addParameter("text", "test text");
		ModelAndView mv = mailTemplateController.handleRequest(request,
				new MockHttpServletResponse());
		MailTemplate template = (MailTemplate) mailTemplateService
				.getMailTemplate(1L);
		assertNotNull(template);
		assertEquals("test title", template.getTitle());
		assertEquals("test text", template.getText());
		assertEquals("redirect:/admin/nl_mailtemplates", mv.getViewName());
	}
}
