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
package com.negeso.module.contact_book.component;

import org.apache.cactus.WebRequest;
import org.w3c.dom.Element;

import com.negeso.PageComponentTestCase;
import com.negeso.TestUtil;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 * @TODO
 * 
 * @author		Sergey V. Klok
 * @version		$Revision: $
 *
 */
public class ContactFormCactusTest extends PageComponentTestCase {
	
	private ContactForm component;
	
	public ContactFormCactusTest(){
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		component = new ContactForm();
	}
	
	public void beginFillingData(WebRequest request){
		request.addParameter("action", "act");
		request.addParameter("contactId", "1111");
		request.addParameter("groupId", "9999");
		request.addParameter("search_string", "cat");
		request.addParameter("order_by", "asd");
		request.addParameter("fname", "Bob");
		request.addParameter("sname", "Hill");
		request.addParameter("dep", "offise");
		request.addParameter("job", "programmer");
		request.addParameter("email", "ya@ukr.net");
	}
	
	public void testFillingData(){
		ContactForm contactForm = new ContactForm();
		contactForm = component.buildFromRequest(requestContext);
		Element parent = Xbuilder.createTopEl("main");
		Element currentElement = contactForm.buildElement(parent);
		try {
			validateXML(currentElement.getOwnerDocument(), "contact-form.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :"+e.getMessage()+"XML FILE :"+TestUtil.parseToString(currentElement));
		}
	}
	
	public void beginActionShowGroup(WebRequest request){
		request.addParameter("groupId", "1");
	}
	
	public void testActionShowGroup(){
		ContactForm contactForm = new ContactForm();
		contactForm = component.buildFromRequest(requestContext);
		assertEquals("show_group", contactForm.getAction());
	}
	
	public void beginActionShowContact(WebRequest request){
		request.addParameter("contactId", "1");
	}
	
	public void testActionShowContact(){
		ContactForm contactForm = new ContactForm();
		contactForm = component.buildFromRequest(requestContext);
		assertEquals("show_contact", contactForm.getAction());
	}	
}
