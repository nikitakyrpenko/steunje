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
import com.negeso.XMLtoHardDiskWriter;
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
public class ManageContactsCactusTest extends CommandTestCase {
	
	private Command command;
	private User user;
	
	public ManageContactsCactusTest(){
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ManageContacts();
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
	
	public void testShowContact() throws Exception{
		requestContext.setParameter("action", "show_contact");
		requestContext.setParameter("groupId", "2");
		ResponseContext response = command.execute();
		Document document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		testNegesoPaqe(document);
		XMLAssert.assertXpathEvaluatesTo("2", "/*[local-name()='page']/*[local-name()='contact']/@group-id", document);
		XMLAssert.assertXpathEvaluatesTo("Good", "/*[local-name()='page']/*[local-name()='contact']/@group-title", document);
		XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-height", document);
		XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-width", document);		
		assertEquals("edit-contact", response.getResultName());		
	}
	
	public void testShowContactNullGroup() throws Exception{
		requestContext.setParameter("action", "show_contact");
		requestContext.setParameter("contactId", "2");
		ResponseContext response = command.execute();
		Document document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		testNegesoPaqe(document);
		XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='page']/*[local-name()='contact']/@group-id", document);
		XMLAssert.assertXpathEvaluatesTo("Animals", "/*[local-name()='page']/*[local-name()='contact']/@group-title", document);
		XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-height", document);
		XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-width", document);		
		assertEquals("edit-contact", response.getResultName());		
	}	
	
	public void testRemoveContact() throws Exception{
		String query = "INSERT INTO contact (id) VALUES (9999);"+ "INSERT INTO cb_cont_group VALUES (1111,9999,2);";
		new PreparedDatabaseLauncher() {
			@Override
			protected void assertTest() {
			}	
		}.runTest(query);		
		requestContext.setParameter("action", "remove_contact");
		requestContext.setParameter("groupId", "2");
		requestContext.setParameter("contactId", "9999");
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		Document documentAfterRemove = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathEvaluatesTo("false","/*[local-name()='page']/*[local-name()='cb-group']/*[local-name()='contact']/@id=9999", documentAfterRemove);
		assertEquals("contact-list", response.getResultName());		
	}
	
	public void testRemoveContactNotExitstContactId() throws Exception{
		requestContext.setParameter("action", "remove_contact");
		ResponseContext response = command.execute();
		Document documentAfterRemove = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathNotExists("/*[local-name()='page']/*[local-name()='cb-group']", documentAfterRemove);
		assertEquals("contact-list", response.getResultName());		
	}
	
	public void testRemoveContactNotExitstGroupId() throws Exception{
		requestContext.setParameter("action", "remove_contact");
		requestContext.setParameter("contactId", "9999");
		ResponseContext response = command.execute();
		Document documentAfterRemove = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathNotExists("/*[local-name()='page']/*[local-name()='cb-group']", documentAfterRemove);
		assertEquals("contact-list", response.getResultName());		
	}	
	
	public void testAddContact() throws Exception{
		requestContext.setParameter("action", "add_contact");
		requestContext.setParameter("groupId", "2");
		requestContext.setParameter("contactId", "9999");
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		final ResponseContext response = command.execute();
		final Document documentAfterAdd = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		testNegesoPaqe(documentAfterAdd);
		XMLAssert.assertXpathEvaluatesTo("2", "/*[local-name()='page']/*[local-name()='contact']/@group-id", documentAfterAdd);
		XMLAssert.assertXpathEvaluatesTo("Good", "/*[local-name()='page']/*[local-name()='contact']/@group-title", documentAfterAdd);
		XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-height", documentAfterAdd);
		XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-width", documentAfterAdd);		
		assertEquals("edit-contact", response.getResultName());
				
			
		
		
	}
	
	public void beginTestSaveContact(WebRequest request){
		
	}
	public void testSaveContact() throws Exception{
		requestContext.setParameter("action", "save_contact");
		requestContext.setParameter("groupId", "2");
		requestContext.setParameter("contactId", "9999");
		requestContext.setParameter("weblink", "http://my.com");
		requestContext.setParameter("birthday", "10-01-2008");
		requestContext.setParameter("removeImage", "xxx.jpg");
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSessionData().getAttribute(SessionData.USER_ATTR_NAME));
		final ResponseContext response = command.execute();
		final Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		
		PreparedDatabaseLauncher pdl = new PreparedDatabaseLauncher() {
			@Override
			protected void assertTest() throws Exception {
				testNegesoPaqe(document);
				XMLAssert.assertXpathEvaluatesTo("2", "/*[local-name()='page']/*[local-name()='contact']/@group-id", document);
				XMLAssert.assertXpathEvaluatesTo("Good", "/*[local-name()='page']/*[local-name()='contact']/@group-title", document);
				XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-height", document);
				XMLAssert.assertXpathEvaluatesTo("100", "/*[local-name()='page']/*[local-name()='contact']/@image-width", document);
				XMLAssert.assertXpathEvaluatesTo("contact_book", "/*[local-name()='page']/*[local-name()='contact']/@type", document);
				assertEquals("edit-contact", response.getResultName());
				
			}
		};
		String deleteId =  XPathAPI.eval(document, "/*[local-name()='page']/*[local-name()='contact']/@id").toString();
		pdl.runTestAndClean("delete from contact where id=" + deleteId);
	}
	
	public void testSearchContact() throws Exception{
		requestContext.setParameter("action", "search_contacts");
		requestContext.setParameter("groupId", "2");
		requestContext.setParameter("contactId", "9999");		
		requestContext.setParameter("search_string", "Tower");
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSessionData().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathEvaluatesTo("06-09-2005", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@birthday", document);
		XMLAssert.assertXpathEvaluatesTo("Building", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@department", document);
		XMLAssert.assertXpathEvaluatesTo("build@build.com", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@email", document);
		XMLAssert.assertXpathEvaluatesTo("Tower", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@first-name", document);
		XMLAssert.assertXpathEvaluatesTo("2", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@group-id", document);
		XMLAssert.assertXpathEvaluatesTo("Good", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@group-title", document);
		XMLAssert.assertXpathEvaluatesTo("5", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@id", document);
		XMLAssert.assertXpathEvaluatesTo("media/london_99x100_1.jpg", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@image-link", document);
		XMLAssert.assertXpathEvaluatesTo("Support", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@job-title", document);
		XMLAssert.assertXpathEvaluatesTo("111-11-11", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@phone", document);
		XMLAssert.assertXpathEvaluatesTo("Tower", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@second-name", document);
		XMLAssert.assertXpathEvaluatesTo("Tower Tower", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@title", document);
		XMLAssert.assertXpathEvaluatesTo("contact_book", "/*[local-name()='page']/*[local-name()='cb-search-results']/*[local-name()='contact']/@type", document);
		assertEquals("contact-list", response.getResultName());
	}
	
	public void testDefaultAction() throws Exception{
		requestContext.setParameter("action", "fdgdrgsr");
		requestContext.setParameter("groupId", "2");
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSessionData().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		try {
			validateXML(document, "manage-contacts.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :"+e.getMessage()+"XML FILE :"+TestUtil.parseToString(document));
		}			
		assertEquals("contact-list", response.getResultName());		
	}

	private void testNegesoPaqe(Document doc) throws Exception{
		XMLAssert.assertXpathEvaluatesTo("en", "/*[local-name()='page']/@interface-language", doc);
		XMLAssert.assertXpathEvaluatesTo("en", "/*[local-name()='page']/@lang", doc);
		XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='page']/@lang-id", doc);
		XMLAssert.assertXpathEvaluatesTo("2", "/*[local-name()='page']/@total-site-languages", doc);
	}
}
