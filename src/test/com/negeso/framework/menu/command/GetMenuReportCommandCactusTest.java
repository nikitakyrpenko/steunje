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

import java.io.File;
import java.io.IOException;

import com.negeso.CommandTestCase;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.view.AbstractHttpView;

/**
 * 
 * @TODO
 * 
 * @author		Vadim Mishchenko
 * @version		$Revision: $
 *
 */
public class GetMenuReportCommandCactusTest extends CommandTestCase {
	
	public GetMenuReportCommandCactusTest(){
		super();
	}

	@Override
	public void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			fail("Exception occured");
		}
		command = new GetMenuReportCommand();
	}
	
	public void testForAccessDeniedResult(){
		testAccessDenied(command);
	}
	
	public void testForSuccessResult(){
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		File file = (File)responseContext.getResultMap().get(AbstractHttpView.KEY_FILE);
		Long lng = (Long)responseContext.getResultMap().get(AbstractHttpView.HEADER_EXPIRES); 
		String str = (String) responseContext.getResultMap().get(AbstractHttpView.HEADER_MIME_TYPE);

		assertTrue(file.exists());
		assertEquals(new Long(0),lng);
		assertEquals(str,"text/plain; charset=UTF-8");
		assertEquals(AbstractCommand.RESULT_SUCCESS, responseContext.getResultName());
	}

}
