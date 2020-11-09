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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.framework.controller.BaseControllerIntegrationTest;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.PublicationState;
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
public class PublicationListControllerIntegrationTest extends
		BaseControllerIntegrationTest{
	
	@Autowired
	private PublicationListController publicationListController;
	@Autowired
	private NewPublicationService publicationService;
	
	@Test
	public void testShowPublications() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		request.addParameter("categoryId", "1");
		
		
		ModelAndView mv = publicationListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		assertEquals("nl_publicationlist", mv.getViewName());
		assertNotNull(mv.getModel().get("category"));
		assertNotNull(mv.getModel().get("mailtemplates"));
	}
	
	@Test
	public void testDeletePublication() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addParameter("lang_id", "1");
		request.addParameter("id", "4");
		request.addParameter("action", "deletePublication");
		Publication publication = publicationService.getPublicationById(4L);
		long categoryId = publication.getSubscriptionCategory().getId();
		ModelAndView mv = publicationListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		
		publication = publicationService.getPublicationById(4L);
		
		assertNull(publication);
		assertEquals("redirect:/admin/nl_publicationlist?categoryId=" + categoryId,
			mv.getViewName());
		
		
	}
	
	/* TODO: Rest have to be writted ASAP @Test */
	public void testProof() throws Exception{
		fail("Notification must be tested with Mock");
	}
	
	@Test
	public void testUnSchedule() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		request.addParameter("id", "4");
		request.addParameter("action", "unSchedule");
		
		ModelAndView mv = publicationListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		Publication publication = publicationService.getPublicationById(4L);
		
		assertEquals("redirect:/admin/nl_publicationlist" +
				"?action=showSheduled", mv.getViewName());
		
		assertEquals(Configuration.PUBLICATION_STATUS_SUSPENDED, 
				publication.getPublicationState().getName());
		
	}
	
	@Test
	public void testShowScheduled() throws Exception{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addParameter("lang_id", "1");
		request.addParameter("action", "showSheduled");
		ModelAndView mv = publicationListController.handleRequest(
				request, 
				new MockHttpServletResponse());
		assertEquals("nl_sheduled", mv.getViewName());
		assertNotNull(mv.getModel().get("publications"));
	}

}

