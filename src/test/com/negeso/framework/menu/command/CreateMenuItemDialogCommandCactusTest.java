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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.cactus.ServletTestCase;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.TestUtil;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;

/**
 * 
 * @TODO
 * 
 * @author		Vadim Mishchenko
 * @version		$Revision: $
 *
 */
public class CreateMenuItemDialogCommandCactusTest extends CommandTestCase {

	public CreateMenuItemDialogCommandCactusTest(){
		super();
	}

	@Override
	public void setUp(){		
		try {
			super.setUp();
		} catch (Exception e) {
			fail("Exception occured");
		}
		command = new CreateMenuItemDialogCommand();
	}
	
	public void testSuccess()throws TransformerException,SAXException,IOException{
		prepareRequestContext(1L);		
		ResponseContext responseContext = command.execute();
		Document document = (Document)responseContext.getResultMap().get(CreateMenuItemDialogCommand.OUTPUT_XML);
		
		validateXML(document, "create-menu-item-command.xml");
		assertEquals(CreateMenuItemDialogCommand.RESULT_SUCCESS, responseContext.getResultName());		
	}
	
	public void testForAccessDenied(){
		testAccessDenied(command);
	}
	
	public void testFailure(){
		makeMockForSession();
		ResponseContext responseContext = command.execute();
		assertEquals(CreateMenuItemDialogCommand.RESULT_FAILURE, responseContext.getResultName());		
	}
	
	private void prepareRequestContext(Long userId){
		requestContext.getSession().setLanguageCode("en");
		requestContext.getSession().getUser().setId(userId);		
		command.setRequestContext(requestContext);
	}
	
	private void makeMockForSession(){
		requestContext.setSessionData(new SessionData(){
			@Override
			public User getUser() {
				User user = new User();
				user.setId(1L);
				return user;
			}
			@Override
			public Language getLanguage() {				
				throw new CriticalException();
			}
			@Override
			public boolean isSessionStarted() {
				return true;
			}
			@Override
			public void setAttribute(String attrName, Object attrValue) {
			}
		});	
		command.setRequestContext(requestContext);
	}
}
