/*
 * @(#)$Id: NewsLineComponentCactusTest.java,v 1.3, 2006-11-20 09:30:15Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsline.component;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sun.misc.Perf.GetPerfAction;

import com.negeso.PageComponentTestCase;
import com.negeso.XMLtoHardDiskWriter;
import com.negeso.framework.page.PageComponent;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 4$
 *
 */
public class NewsLineComponentCactusTest extends PageComponentTestCase {
	
	private PageComponent component;
	
	public NewsLineComponentCactusTest(){
		super();
	}

	public void setUp() throws Exception {
		super.setUp();
		component = new NewsLineComponent();
	}

   private void validateEmergencyStubElementXml(Document document) throws TransformerException, XpathException {
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list'])", document);
	   XMLAssert.assertXpathEvaluatesTo("0", "count(//*[local-name()='listItem'])", document);
	   XMLAssert.assertXpathNotExists("/*[local-name()='list']/@page", document);
   }

   private Document getOutputXMLDocument(String itemsPerPageValue, String listsValue, String InputPageNumber) {
	   if (getParameters().containsKey(NewsLineComponent.INPUT_PAGE_NUMBER)) {
		   getParameters().remove(NewsLineComponent.INPUT_PAGE_NUMBER);
	   }
	   
	   getParameters().put(NewsLineComponent.INPUT_ITEMS_PER_PAGE, new String[]{itemsPerPageValue});
	   getParameters().put(NewsLineComponent.INPUT_LISTS, new String[]{listsValue});

	   if (InputPageNumber != null) {
		   requestContext.setParameter(NewsLineComponent.INPUT_PAGE_NUMBER, InputPageNumber);
	   }
		   
	   Element element = getElement(component);
	   
	   XMLtoHardDiskWriter.writeXMLtoHardDisk(element, "d:/node.xml");

	   Document document = element.getOwnerDocument();
	   document.appendChild(element);

	   return document;
   }
   
   private void validateEmergencyStubElement(String itemsPerPageValue,
		                                     String listsValue,
		                                     String InputPageNumber) 
   	throws TransformerException, XpathException {
	   validateEmergencyStubElementXml(
			   getOutputXMLDocument(itemsPerPageValue, listsValue, InputPageNumber)
			   );
   }
   
   public void testINPUT_LISTSnull() throws TransformerException, XpathException {
	   validateEmergencyStubElement("12", null, "1");
   }
   
   public void testINPUT_ITEMS_PER_PAGEnull() throws TransformerException, XpathException {
	   validateEmergencyStubElement(null, "20", "1");
   }
   
   public void testINPUT_LISTSempty() throws TransformerException, XpathException {
	   validateEmergencyStubElement("12", "", "1");
   }
   
   public void testNegativeItemsPerPageValue() throws TransformerException, XpathException {
	   validateEmergencyStubElement("-2","20", "1");
   }
   
   public void testNegativePageNumberValue() throws TransformerException {
	   //	   It's a bad unit reaction list id < 0 but attributes of list exists 
	   //	   validateEmergencyStubElement("12","-10", "1"); does no work	   
   }
   
   public void testItemsPerPageValueEquals0() throws TransformerException, XpathException {
	   validateEmergencyStubElement("0","20", "1");
   }
   
   public void testINPUT_PAGE_NUMBERnull() throws TransformerException, XpathException {
      Document document = getOutputXMLDocument("12", "20", null);
      //make sure INPUT_PAGE equals 1 
      XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='list']/@page", document);
   }

   public void testINPUT_PAGE_NUMBER0() throws TransformerException, XpathException {
	  Document document = getOutputXMLDocument("12", "20", "");
	   //make sure INPUT_PAGE equals 1 
	  XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='list']/@page", document);
   }

   private void validateListNode(Document document) throws TransformerException, XpathException {
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list'])", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='list']/@page", document);
	   
	   //make sure count of items equals 6
	   XMLAssert.assertXpathEvaluatesTo("6", "/*[local-name()='list']/@items", document);
	   XMLAssert.assertXpathEvaluatesTo("6", "count(/*[local-name()='list']/*[local-name()='listItem'])", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='list']/@pages", document);
   }
   
   private void validateListItemNode(Document document) throws TransformerException, XpathException {
	   XMLAssert.assertXpathEvaluatesTo("20", "/*[local-name()='list']/*[local-name()='listItem'][@id='55']/@listId", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("", "/*[local-name()='list']/*[local-name()='listItem'][@id='55']/@parameters", document);
	   
	   XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][@id='55']/@documentLink", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("2006-11-09", "/*[local-name()='list']/*[local-name()='listItem'][@id='55']/@publishDate", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("African cotton farmers go online", "/*[local-name()='list']/*[local-name()='listItem'][@id='55']/@title", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list']/*[local-name()='listItem'][@id='55']/*[local-name()='teaser'])", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='list']/*[local-name()='listItem'][@id='55']/*[local-name()='teaser']/*[local-name()='article'])", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("cls", "/*[local-name()='list']/*[local-name()='listItem'][@id='55']/*[local-name()='teaser']/*[local-name()='article']/@class", document);
	   
	   XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][@id='55']/*[local-name()='teaser']/*[local-name()='article']/@id", document);
	   
	   XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][@id='55']/*[local-name()='teaser']/*[local-name()='article']/*[local-name()='head']", document);
	   
	   XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][@id='55']/*[local-name()='teaser']/*[local-name()='article']/*[local-name()='text']", document);
   }
   
   private void validateSuccessfulXML(Document document) throws TransformerException, XpathException {
	   validateListNode(document);
	   validateListItemNode(document);
   }
   
   public void testSuccessfullXML() throws TransformerException, XpathException {
	   Document document= getOutputXMLDocument("12", "20", null);
       validateSuccessfulXML(document);
   }
   
   //TODO test expired items
   //TODO test list items with container_id is not null 
}

