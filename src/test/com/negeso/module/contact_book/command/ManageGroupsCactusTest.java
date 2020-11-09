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
package com.negeso.module.contact_book.command;

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.PreparedDatabaseLauncher;
import com.negeso.TestUtil;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * 
 * @TODO
 * 
 * @author		Sergey V. Klok
 * @version		$Revision: $
 *
 */
public class ManageGroupsCactusTest extends CommandTestCase {
	
	private Command command;
	private User user;
	
	public ManageGroupsCactusTest(){
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ManageGroups();
		command.setRequestContext(requestContext);
		user = new User();
		user.setId(1L);
	}
	
	public void testAccessDenied() {
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, null);
		assertNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		assertEquals(response.getResultName(), AbstractCommand.RESULT_ACCESS_DENIED);
	}
	
	public void beginShowGroup(WebRequest request){
		request.addParameter("groupId", "2");
		request.addParameter("action", "show_group");
	}
	
	public void testShowGroup() throws Exception{
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSessionData().getAttribute(SessionData.USER_ATTR_NAME));		
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		testNegesoPaqe(document);
		XMLAssert.assertXpathEvaluatesTo("true", "/*[local-name()='page']/*[local-name()='cb-group']/@title='Good'", document);
		XMLAssert.assertXpathEvaluatesTo("true", "/*[local-name()='page']/*[local-name()='cb-group']/@id=2", document);
		assertEquals("edit-group", response.getResultName());
	}
	
	public void beginRemoveGroup(WebRequest request){
		request.addParameter("groupId", "9999");
		request.addParameter("action", "remove_group");
	}
	
	public void testRemoveGroup() throws Exception{
		String query = "INSERT INTO cb_group VALUES (9999,'Test');";
		new PreparedDatabaseLauncher() {
			@Override
			protected void assertTest() {
			}	
		}.runTest(query);		
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		Document document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathEvaluatesTo("false","/*[local-name()='page']/*[local-name()='cb-groups']/*[local-name()='cb-group']/@id=9999", document);
		assertEquals("group-list", response.getResultName());		
	}
	
	public void beginAddContactGroup(WebRequest request){
		request.addParameter("groupId", "9999");
		request.addParameter("action", "add_group");
	}
	
	public void testAddContactGroup() throws Exception{
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSessionData().getAttribute(SessionData.USER_ATTR_NAME));		
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		testNegesoPaqe(document);
		assertEquals("edit-group", response.getResultName());
	}
	
	public void beginSaveGroup(WebRequest request){
		request.addParameter("groupId", "1000");
		request.addParameter("title", "Test");
		request.addParameter("action", "save_group");
	}
	
	public void testSaveGroup() throws Exception{
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSessionData().getAttribute(SessionData.USER_ATTR_NAME));		
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		testNegesoPaqe(document);
		XMLAssert.assertXpathEvaluatesTo("Test", "/*[local-name()='page']/*[local-name()='cb-group']/@title", document);
		assertEquals("edit-group", response.getResultName());
		String deleteId = XPathAPI.eval(document, "/*[local-name()='page']/*[local-name()='cb-group']/@id").toString();
		requestContext.setParameter("action", "remove_group");
		requestContext.setParameter("groupId", deleteId);
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		response = command.execute();
		document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathEvaluatesTo("false","/*[local-name()='page']/*[local-name()='cb-groups']/*[local-name()='cb-group']/@id=9999", document);
		assertEquals("group-list", response.getResultName());		
	}
	
	public void beginDefaultAction(WebRequest request){
		request.addParameter("action", "asdfasdf");
	}
	
	public void testDefaultAction() throws Exception{
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSessionData().getAttribute(SessionData.USER_ATTR_NAME));		
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		try {
			validateXML(document, "manage-groups.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :"+e.getMessage()+"XML FILE :"+TestUtil.parseToString(document));
		}		
		assertEquals("group-list", response.getResultName());	
	}
	
	private void testNegesoPaqe(Document doc) throws Exception{
		XMLAssert.assertXpathEvaluatesTo("en", "/*[local-name()='page']/@interface-language", doc);
		XMLAssert.assertXpathEvaluatesTo("en", "/*[local-name()='page']/@lang", doc);
		XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='page']/@lang-id", doc);
		XMLAssert.assertXpathEvaluatesTo("2", "/*[local-name()='page']/@total-site-languages", doc);
	}
}
