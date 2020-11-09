/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu.command;

import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;

/**
 * 
 * @TODO
 * 
 * @author		Vadim Mishchenko
 * @version		$Revision: $
 *
 */
public class GetExtraPagesCommandCactusTest extends CommandTestCase{
	
	public GetExtraPagesCommandCactusTest (){
		super();
	}
	
	@Override
	public void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			fail("Exception occured");
		}
		command = new GetExtraPagesCommand();
		
	}
	public void testAccessDeniedResult(){
		testAccessDenied(command);
	}
	
	public void testSuccessresult(){
		prepareCommand();
		ResponseContext responseContext = command.execute();
		assertEquals(AbstractCommand.RESULT_SUCCESS, responseContext.getResultName());		
		Document document = (Document)responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML);
		try {
			validateXML(document, "get-extra-pages-command.xml");
		}catch (Exception e) {
			
			fail("Exception occured while valideting XML");
		}
	}

	private void prepareCommand(){
		command = new GetExtraPagesCommand();
		command.setRequestContext(requestContext);
	}
}
