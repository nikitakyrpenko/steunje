/*
 * @(#)$Id: ListComponentCactusTest.java,v 1.3, 2006-11-13 15:19:19Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.list.component;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.PageComponentTestCase;
import com.negeso.framework.page.PageComponent;
import com.negeso.framework.security.SecurityGuard;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 4$
 *
 */

// SORRY FOR HARDCODING
public class ListComponentCactusTest extends PageComponentTestCase {

	public static final int SERVER_NAME_STARTED_POSITION = 7;
	
	public static final long ADMIN_USER_ID = 1;
	
	public static final long NULL_CONTAINER = 0;
	
	public static final String TEST_LIST_ID = "19";
	
	public static final String TEST_LIST_NAME = "About us > News";
	
	public static final String TEST_LIST_TYPE = "newslist";
	
	public static final String TEST_LIST_PATH = "51";
	
	// error if try to parse
	public static final String WRONG_LIST_PATH_VALUE = "1,5,8";
	
	public static final String WRONG_LIST_ID = "99999";
	
	public static final String NESTED_LEVEL = "1";
	public static final String ROOT_LEVEL = "0";

	
	private PageComponent component;

	public ListComponentCactusTest() {
		super();
	}

	public void setUp() throws Exception {
		super.setUp();
		component = new ListComponent();
	}
	
	public void testIsNotSpecified() throws TransformerException, XpathException {
		Element element = getElement(component);

		Document document = element.getOwnerDocument();
		document.appendChild(element);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']", document);
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list'])", document);
		XMLAssert.assertXpathEvaluatesTo("listId is not specified", "/*[local-name()='list']/@error", document);
	}
	
	private void validateExceptionOccuredXML(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathExists("/*[local-name()='list']", document);
		
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list'])", document);
		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@error", document);
		
        XMLAssert.assertXpathNotExists("/*[local-name()='list']/@level", document);
        		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@canManage", document);
		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@canContribute", document);
		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@id" , document);
		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@name" , document);
		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@type" , document);
	}
	
	public void testWronglistPathValue() throws TransformerException, XpathException {
		getParameters().clear();
		getParameters().put(ListComponent.INPUT_LIST_ID, TEST_LIST_ID);
		getParameters().put(ListComponent.INPUT_LIST_PATH, WRONG_LIST_PATH_VALUE);
		
		Element element = getElement(component);
		
		Document document = element.getOwnerDocument();
		document.appendChild(element);
		
		validateExceptionOccuredXML(document);
	}
	
	public void testListNotFound() throws TransformerException, XpathException {
		getParameters().clear();
		getParameters().put(ListComponent.INPUT_LIST_ID, WRONG_LIST_ID);
		
		Element element = getElement(component);
		
		Document document = element.getOwnerDocument();
		document.appendChild(element);
		
		validateExceptionOccuredXML(document);
	}
	
	public void testSuccessWithTree() throws TransformerException, XpathException {
		getParameters().clear();
		getParameters().put(ListComponent.INPUT_LIST_ID, TEST_LIST_ID);
		getParameters().put(ListComponent.INPUT_LIST_PATH, TEST_LIST_PATH);		
		
		Element element = getElement(component);
		
		XMLAssert.assertNotNull(element);
		Document document = element.getOwnerDocument();
		document.appendChild(element);
		validateRootListXML(document);
		
		// listPath should be absent
        XMLAssert.assertXpathEvaluatesTo(TEST_LIST_PATH, "/*[local-name()='list']/@listPath", document);
		validateRootListItemXML(document);
		// nested list item should be absent
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/*[local-name()='list-item']/*[local-name()='list']", document);
		// selected should be present and be equal 1 
		XMLAssert.assertXpathEvaluatesTo("51", "/*[local-name()='list']/@selected", document);
        validateNestedListXML(document);
	}
	
	private void validateNestedListXML(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']", document);
		
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list'])", document);
		
        XMLAssert.assertXpathEvaluatesTo(NESTED_LEVEL, 
        		                        "/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@level", document);
        		
		XMLAssert.assertXpathEvaluatesTo(Boolean.toString(SecurityGuard.canManage(ADMIN_USER_ID, NULL_CONTAINER)),
				"/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@canManage", document);
		
		XMLAssert.assertXpathEvaluatesTo(Boolean.toString(SecurityGuard.canContribute(ADMIN_USER_ID, NULL_CONTAINER)),
				"/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@canContribute", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@id" , document);
		
        XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@name", document);
				
		XMLAssert.assertXpathEvaluatesTo("About us > News", "/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@name" , document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@type" , document);
		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/*[local-name()='listItem'][5]/*[local-name()='list']/@listPath", document);
	}
	
	public void testSuccessWithoutTree() throws TransformerException, XpathException {
		getParameters().clear();
		getParameters().put(ListComponent.INPUT_LIST_ID, TEST_LIST_ID);
		
		Element element = getElement(component);
		
		XMLAssert.assertNotNull(element);
		Document document = element.getOwnerDocument();
		document.appendChild(element);
        
		validateRootListXML(document);
        XMLAssert.assertXpathEvaluatesTo("", "/*[local-name()='list']/@listPath", document);
		validateRootListItemXML(document);
		// nested list item should be absent
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/*[local-name()='list-item']/*[local-name()='list']", document);
		// selected should be absent
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@selected", document);
	}

	private void validateRootListItemXML(Document document) throws TransformerException, XpathException {
		//sorry for hardcoding test values :)
	
	    XMLAssert.assertXpathEvaluatesTo(Boolean.toString(SecurityGuard.canManage(ADMIN_USER_ID, NULL_CONTAINER)),
				"/*[local-name()='list']/*[local-name()='listItem']/@canManage", document);
		
		XMLAssert.assertXpathEvaluatesTo(Boolean.toString(SecurityGuard.canContribute(ADMIN_USER_ID, NULL_CONTAINER)),
				"/*[local-name()='list']/*[local-name()='listItem']/@canContribute", document);
	
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem']", document);

		//make sure that EXPIRED LIST ITEM is absent and there are 5 items
		XMLAssert.assertXpathEvaluatesTo("5", "count(/*[local-name()='list']/*[local-name()='listItem'])", document);
		
		//test first list_item

		XMLAssert.assertXpathEvaluatesTo("18", "/*[local-name()='list']/*[local-name()='listItem']/@id", document);
		
		XMLAssert.assertXpathEvaluatesTo("19", "/*[local-name()='list']/*[local-name()='listItem']/@listId", document);
			
		XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='list']/*[local-name()='listItem']/@orderNumber", document);
		
		XMLAssert.assertXpathEvaluatesTo("Updates on demo site", "/*[local-name()='list']/*[local-name()='listItem']/@title", document);
		
		XMLAssert.assertXpathEvaluatesTo("","/*[local-name()='list']/*[local-name()='listItem']/@parameters", document);
		
		XMLAssert.assertXpathEvaluatesTo("", "/*[local-name()='list']/*[local-name()='listItem']/@documentLink", document);
		
        XMLAssert.assertXpathNotExists("/*[local-name()='list']/*[local-name()='listItem']/@href", document);
        
		// TODO other attributes set by XMLHelper should be asserted as well
		
        
		validateArticleXML(document);
	}

	private void validateArticleXML(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list']/*[local-name()='listItem'][1]/*[local-name()='teaser'])", document);
		
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list']/*[local-name()='listItem'][1]/*[local-name()='teaser']/*[local-name()='article'])", document);
		
		XMLAssert.assertXpathEvaluatesTo("69", "/*[local-name()='list']/*[local-name()='listItem'][1]/*[local-name()='teaser']/*[local-name()='article']/@id", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][1]/*[local-name()='teaser']/*[local-name()='article']/@class", document);
		
		XMLAssert.assertXpathEvaluatesTo("Enter your text here", "/*[local-name()='list'][1]/*[local-name()='listItem']/*[local-name()='teaser']/*[local-name()='article']/*[local-name()='head']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][1]/*[local-name()='teaser']/*[local-name()='article']/*[local-name()='text']", document);
	} 

	private void validateRootListXML(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathExists("/*[local-name()='list']", document);
		
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list'])", document);
		
		XMLAssert.assertXpathNotExists("/*[local-name()='list']/@error", document);
		
        XMLAssert.assertXpathEvaluatesTo(ROOT_LEVEL, 
        		                        "/*[local-name()='list']/@level", document);
        		
		XMLAssert.assertXpathEvaluatesTo(Boolean.toString(SecurityGuard.canManage(ADMIN_USER_ID, NULL_CONTAINER)),
				"/*[local-name()='list']/@canManage", document);
		
		XMLAssert.assertXpathEvaluatesTo(Boolean.toString(SecurityGuard.canContribute(ADMIN_USER_ID, NULL_CONTAINER)),
				"/*[local-name()='list']/@canContribute", document);
		
		XMLAssert.assertXpathEvaluatesTo(TEST_LIST_ID, "/*[local-name()='list']/@id" , document);
		
		XMLAssert.assertXpathEvaluatesTo(TEST_LIST_NAME, "/*[local-name()='list']/@name" , document);
		
		XMLAssert.assertXpathEvaluatesTo(TEST_LIST_TYPE, "/*[local-name()='list']/@type" , document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/@listPath", document);
	}
	
}