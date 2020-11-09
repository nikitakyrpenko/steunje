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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.service.NewPublicationService;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
@ContextConfiguration(locations={"/test-newsletter-applicationContext.xml"})
public class PublicationControllerIntegrationTest extends
		BaseControllerIntegrationTest {
	@Autowired
	private PublicationController publicationController;
	@Autowired
	private NewPublicationService publicationService;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
	public void testAddPublication() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		//request.addParameter("id", "0");
		request.addParameter("category_id", "1");
		request.addParameter("title", "test title");
		request.addParameter("mail_template_id", "1");
		request.addParameter("publishDate_", "2009-08-08 00:00:00");
		request.addParameter("save", "");
		request.addParameter("state", "1");
		request.addParameter("attachmentLink", "c:/test.txt;d:/test.txt");
		request.addParameter("feedbackEmail", "netdeveloper@bigmir.net");
		request.addParameter("group_1", "1");
		ModelAndView mv = publicationController.handleRequest(
				request, 
				new MockHttpServletResponse());
		Publication pub= publicationService.getPublicationDao().findByTitle("test title", 1L);
		assertNotNull(pub);
		assertEquals(2, pub.getAttachments().size());
		assertEquals(1, pub.getPublicationState().getId().longValue());
		assertEquals(1, pub.getSubscriberGroups().size());
		assertEquals(new Timestamp(sdf.parse("2009-08-08 00:00:00").getTime()), pub.getPublishDate());
		String viewName = "redirect:/admin/nl_editpublication?id=" + pub.getId().toString()+ "&lang_id=1";
		assertEquals(viewName, mv.getViewName());
	}
	
	@Test
	public void testAddPublicationWithClose() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		//request.addParameter("id", "0");
		request.addParameter("category_id", "1");
		request.addParameter("title", "test title");
		request.addParameter("mail_template_id", "1");
		request.addParameter("publishDate_", "2009-08-08 00:00:00");
		request.addParameter("save", "save_and_close");
		request.addParameter("state", "1");
		request.addParameter("attachmentLink", "c:/test.txt;d:/test.txt");
		request.addParameter("feedbackEmail", "netdeveloper@bigmir.net");
		request.addParameter("group_1", "1");
		ModelAndView mv = publicationController.handleRequest(
				request, 
				new MockHttpServletResponse());
		Publication pub= publicationService.getPublicationDao().findByTitle("test title", 1L);
		assertNotNull(pub);
		assertEquals(2, pub.getAttachments().size());
		assertEquals(1, pub.getPublicationState().getId().longValue());
		assertEquals(1, pub.getSubscriberGroups().size());
		assertEquals(new Timestamp(sdf.parse("2009-08-08 00:00:00").getTime()), pub.getPublishDate());
		String viewName = "redirect:/admin/nl_publicationlist?categoryId=" + pub.getSubscriptionCategory().getId();
		assertEquals(viewName, mv.getViewName());
	}

}

