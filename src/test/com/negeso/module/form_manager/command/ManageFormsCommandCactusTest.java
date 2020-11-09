/*
 * @(#)$Id: ManageFormsCommandCactusTest.java,v 1.1, 2006-10-27 08:11:59Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.form_manager.command;

import java.util.HashMap;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 2$
 *
 */
public class ManageFormsCommandCactusTest extends CommandTestCase {

	private Command command;
	private static final String FORM_LANGUAGE_KEY = "form_language";  
	private static final String FORM_LANGUAGE_VALUE = "en";  
	
	public ManageFormsCommandCactusTest() {
		super();
	}

	public void begin(WebRequest request) {
		request.addParameter(FORM_LANGUAGE_KEY, FORM_LANGUAGE_VALUE);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ManageFormsCommand();
		command.setRequestContext(requestContext);
	}

	@Override
	protected void tearDown() throws Exception {
		assertEquals(request.getParameter(FORM_LANGUAGE_KEY), FORM_LANGUAGE_VALUE);
	}
	
	public void testFailure() {
		RequestContext testRequestContext = new TestRequestContext(request,
				response, config) {
			@Override
			public String getParameter(String name) {
				throw new RuntimeException("Test exception");
			}
		};
		command.setRequestContext(testRequestContext);
		
		try {
			ResponseContext response = command.execute();
			
			assertEquals(response.getResultName(), AbstractCommand.RESULT_FAILURE);
			
		} catch (Throwable t) {
			fail("Unexpected exception: "  + t.getMessage());
		}
	}
	
	public void testAccessDenied() {
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, null);
		assertNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		assertEquals(response.getResultName(), AbstractCommand.RESULT_ACCESS_DENIED);
	}
	
	public void testNoTodo() {
		ResponseContext response = command.execute();
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
		Document document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		assertNotNull(document);
		// TODO validate xml
	}
	
	public void testDelete() {
		// TODO write tests
	}

	public void testEdit() {
		// TODO write tests
	}

	public void testSave() {
		// TODO write tests
	}
	
	public void testAdd() {
		// TODO write tests
	}
	
}
