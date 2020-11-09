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

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.negeso.CommandTestCase;
import com.negeso.framework.controller.ResponseContext;

/**
 * 
 * @TODO
 * 
 * @author		Vadim Mishchenko
 * @version		$Revision: $
 *
 */
public class GetMenuCommandCactusTest extends CommandTestCase{
	
	public GetMenuCommandCactusTest(){
		super();
	}
	
	@Override
	public void setUp(){		
		try {
			super.setUp();
		} catch (Exception e) {
			fail("Exception occured");
		}
		command = new GetMenuCommand();
	}
	
	public void testForAccessDeniedResult(){
		testAccessDenied(command);
	}
	
	/*public void testForSuccessResult (){
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		Document document = (Document)responseContext.getResultMap().get(CreateMenuItemDialogCommand.OUTPUT_XML);
		try {
			@TODO: 	No file get-menu-command.xml
			validateXML(document, "get-menu-command.xml");
		} catch (Exception e) {
			fail("Exception Ocurred While Validation xml");
		}
	}*/
	
}
