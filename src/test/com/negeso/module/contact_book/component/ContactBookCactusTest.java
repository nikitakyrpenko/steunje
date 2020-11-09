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
import org.w3c.dom.Node;

import com.negeso.PageComponentTestCase;
import com.negeso.TestUtil;
import com.negeso.framework.page.PageComponent;
/**
 * 
 * @TODO
 * 
 * @author		Sergey V. Klok
 * @version		$Revision: $
 *
 */
public class ContactBookCactusTest extends PageComponentTestCase {
	
	private PageComponent component;
	
	public ContactBookCactusTest(){
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		component = new ContactBook();
	}
	
	public void beginOrderBy(WebRequest request){
		request.addParameter("action", "show_all");
		request.addParameter("order", "down");
		request.addParameter("order-by", "first_name, second_name");
	}
	
	public void testOrderBy(){
		Element element = getElement(component);
		assertEquals("down", element.getFirstChild().getAttributes().getNamedItem("order").getNodeValue());
		assertEquals("first_name", element.getFirstChild().getAttributes().getNamedItem("order-by").getNodeValue());
	}
	
	public void beginOrder(WebRequest request){
		request.addParameter("action", "show_all");
		request.addParameter("order", "down");
	}
	
	public void testOrder(){
		Element element = getElement(component);
		assertEquals("down", element.getFirstChild().getAttributes().getNamedItem("order").getNodeValue());
	}	
	
	public void beginXMLMainPage(WebRequest request){
		request.addParameter("action", "hello");
	}
	
	public void testXMLMainPage(){
		Element element = getElement(component);
		try {
			validateXML(element, "contact-book-main-page.xml");
		} catch (Exception ex) {
			fail("Exception while validating xml :"+ex.getMessage()+"XML FILE :"+TestUtil.parseToString(element));
		}		
	}
	
	public void beginXMLShowContact(WebRequest request){
		request.addParameter("action", "show_contact");
		request.addParameter("contactId", "1");
	}
	
	public void testXMLShowContact(){
		Element element = getElement(component);
		try {
			validateXML(element, "contact-book-show-contact.xml");
		} catch (Exception ex) {
			fail("Exception while validating xml :"+ex.getMessage()+"XML FILE :"+TestUtil.parseToString(element));
		}		
	}
	
	public void beginXMLAdvancedSearch(WebRequest request){
		request.addParameter("action", "advanced_search");
		request.addParameter("fname", "Car");
		request.addParameter("sname", "Car");
		request.addParameter("email", "car@car.com");
		request.addParameter("dep", "Cars");
		request.addParameter("job", "Fast delivery");
	}
	
	public void testXMLAdvancedSearch(){
		Element element = getElement(component);
		try {
			validateXML(element, "contact-book-advanced-search.xml");
		} catch (Exception ex) {
			fail("Exception while validating xml :"+ex.getMessage()+"XML FILE :"+TestUtil.parseToString(element));
		}		
	}
	
	public void beginXMLShowAll(WebRequest request){
		request.addParameter("action", "show_all");
	}
	
	public void testXMLShowAll(){
		Element element = getElement(component);
		try {
			validateXML(element, "contact-book-show-all.xml");
		} catch (Exception ex) {
			fail("Exception while validating xml :"+ex.getMessage()+"XML FILE :"+TestUtil.parseToString(element));
		}		
	}
	
	public void beginXMLShowGroup(WebRequest request){
		request.addParameter("action", "show_group");
		request.addParameter("groupId", "1");
	}
	
	public void testXMLShowGroup(){
		Element element = getElement(component);
		try {
			validateXML(element, "contact-book-show-group.xml");
		} catch (Exception ex) {
			fail("Exception while validating xml :"+ex.getMessage()+"XML FILE :"+TestUtil.parseToString(element));
		}		
	}
	
	public void beginXMLSearch(WebRequest request){
		request.addParameter("action", "search");
		request.addParameter("search_string", "Cat");
	}
	
	public void testXMLSearch(){
		Element element = getElement(component);
		try {
			validateXML(element, "contact-book-search.xml");
		} catch (Exception ex) {
			fail("Exception while validating xml :"+ex.getMessage()+"XML FILE :"+TestUtil.parseToString(element));
		}		
	}		
}
