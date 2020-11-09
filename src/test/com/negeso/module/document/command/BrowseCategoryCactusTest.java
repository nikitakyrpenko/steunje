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
package com.negeso.module.document.command;

import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.TestUtil;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;

/**
 * 
 * @TODO
 * 
 * @author		Sergey V. Klok
 * @version		$Revision: $
 *
 */
public class BrowseCategoryCactusTest extends CommandTestCase {
	
	private Command command;
	
	public BrowseCategoryCactusTest(){
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new BrowseCategory();
		command.setRequestContext(requestContext);
	}
	
	public void testAccessDenied() {
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, null);
		assertNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		assertEquals(response.getResultName(), AbstractCommand.RESULT_ACCESS_DENIED);
	}
	
	public void testRootCategotyOutputXML() throws Exception{
		User user = new User();
		user.setId(1L);		
		requestContext.setParameter("category_id", "1");
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().getSession().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		Document document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		try {
			validateXML(document, "browse-category-root.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :" +
					e.getMessage() + 
					"XML FILE :" + 
					TestUtil.parseToString(document));
		}		
		assertEquals(response.getResultName(), 
				AbstractCommand.RESULT_SUCCESS);		
	}

	public void testNotRootCategotyOutputXML() throws Exception{
		User user = new User();
		user.setId(1L);		
		requestContext.setParameter("category_id", "2");
		requestContext.getSessionData().
		setAttribute(SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.
				getRequestContext().getSession().
				getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		Document document = (Document) response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		try {
			validateXML(document, "browse-category.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :" + 
					e.getMessage() + 
					"XML FILE :" + 
					TestUtil.parseToString(document));
		}		
		assertEquals(response.getResultName(), 
				AbstractCommand.RESULT_SUCCESS);		
	}
	
	public void testNullCategotyId() throws Exception{
		User user = new User();
		user.setId(1L);		
//		requestContext.setParameter("category_id", "");
		requestContext.getSessionData().setAttribute(
				SessionData.USER_ATTR_NAME, user);
		assertNotNull(command.getRequestContext().
				getSession().getAttribute(SessionData.USER_ATTR_NAME));
		ResponseContext response = command.execute();
		Document document = (Document) response.getResultMap().
								get(AbstractCommand.OUTPUT_XML);
		try {
			validateXML(document, "browse-category-null.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :" + 
					e.getMessage() + 
					"XML FILE :" + 
					TestUtil.parseToString(document));
		}		
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);		
	}		
	
}
