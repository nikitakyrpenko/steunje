/*
 * @(#)$Id: ChooseFormCommandCactusTest.java,v 1.2, 2006-11-20 12:42:46Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.form_manager.command;

import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 3$
 *
 */
public class ChooseFormCommandCactusTest extends CommandTestCase {

	private Command command;
	
	public ChooseFormCommandCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ChooseFormCommand();
		command.setRequestContext(requestContext);
	}
	
	public void testCanEdit() {
		ResponseContext response = command.execute();
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
		Document document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		assertNotNull(document);
	}

}
