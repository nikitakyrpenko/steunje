/*
 * @(#)Id: ListDirectoryCommandCactusTest.java, 04.09.2007 17:17:59, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.media_catalog.command;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;

import junit.framework.TestCase;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class ListDirectoryCommandCactusTest extends CommandTestCase{
	
	private AbstractCommand command;
	
	public ListDirectoryCommandCactusTest(){
		super();
	}
	
	public void setUp() throws Exception {
		super.setUp();
		command = new ListDirectoryCommand();
		command.setRequestContext(requestContext);
	}

	//TODO add test for anonymous
	//result should be ACCESS DENIED
		
	/*
	 * test access denied
	 */
	public void testSuccesfull() throws Exception {
		SessionData session = SessionData.getSessionData(request);
		requestContext.setSessionData(session);
		ResponseContext responseContext = command.execute();
		assertEquals(ListDirectoryCommand.RESULT_SUCCESS, responseContext.getResultName());
	}

	/*
	 * test with no parameters
	 */
	public void testNoParameters() throws Exception {
		ResponseContext responseContext = command.execute();
		
		assertNotNull(responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_CURRENT_DIR));
		
		assertEquals("media", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_CURRENT_DIR));
		assertNotNull(responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_RESOURCE));
		assertNotNull(responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_PARENT_DIR));
		assertEquals("", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_ERROR_TEXT));
		assertEquals("", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_USER_TEXT));

		assertEquals(ListDirectoryCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	

	
	/*
	 * test if currentdir parameter is not null
	 */
	public void beginCurrentDirNotNull(WebRequest request) throws Exception{
		request.addParameter(ListDirectoryCommand.INPUT_CURRENT_DIR, "not null");
	}
	public void testCurrentDirNotNull() throws Exception {
		ResponseContext responseContext = command.execute();
		
		assertEquals("not null", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_CURRENT_DIR));
		assertNotNull(responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_RESOURCE));
		assertNotNull(responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_PARENT_DIR));
		assertEquals("", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_ERROR_TEXT));
		assertEquals("", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_USER_TEXT));
		
		assertEquals(ListDirectoryCommand.RESULT_SUCCESS, responseContext.getResultName());
	}

	

	/*
	 * test if parentdir parameter is not null
	 */
	public void beginParentDirNotNull(WebRequest request) throws Exception{
		request.addParameter(ListDirectoryCommand.INPUT_PARENT_DIR, "not null");
	}
	public void testParentDirNotNull() throws Exception {
		ResponseContext responseContext = command.execute();
		
		assertEquals("media", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_CURRENT_DIR));
		assertNotNull(responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_RESOURCE));
		assertEquals("not null", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_PARENT_DIR));
		assertEquals("", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_ERROR_TEXT));
		assertEquals("", responseContext.getResultMap().get(ListDirectoryCommand.OUTPUT_USER_TEXT));
		
		assertEquals(ListDirectoryCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
}
