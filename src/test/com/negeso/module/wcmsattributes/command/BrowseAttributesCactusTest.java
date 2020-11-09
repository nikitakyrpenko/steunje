/*
 * @(#)Id: BrowseAttributesCactusTest.java, 24.07.2007 12:43:05, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.wcmsattributes.command;

import javax.xml.transform.TransformerException;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.TestUtil;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.ResponseContext;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BrowseAttributesCactusTest extends CommandTestCase {
	
	private Command command; 
	
	public static final String PAGE_ID = "8";
	public static final String PAGE_ID_WRONG = "-1";

	public static final String SET_ID = "195";
	
	public static final String XML_root = "/*[local-name()='page']/*[local-name()='wcms_attributes']";
	public static final String imageSet_path = "/*[local-name()='image_set']";
	public static final String image_path = "/*[local-name()='image']";
	
	public BrowseAttributesCactusTest(){
		super();
	}
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
		command = new BrowseAttributes();
		command.setRequestContext(requestContext);
		
	}
	
	/*
	 * wrong request parameter
	 */
	public void beginNoParameters(WebRequest request){
		request.addParameter("wrong", "wrong");
	}
	
	public void testNoParameters() throws Exception{
		ResponseContext responseContext = command.execute();
		assertNotNull(responseContext);
		assertEquals(responseContext.getResultName(), AbstractCommand.RESULT_FAILURE);
	}
	
	/*
	 * correct request parameter with wrong value
	 */
	public void beginNotNumberRequestParameter(WebRequest request){
		//set wrong parameter == wcms_attribute_set_id, but value is not number
		request.addParameter(BrowseAttributes.WCMS_ATTRIBUTE_SET_ID, "is not number");
	}
	
	public void testNotNumberRequestParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		assertNotNull(responseContext);
		
		assertEquals(responseContext.getResultName(), AbstractCommand.RESULT_FAILURE);
	}
	
	/*
	 * set_id - incorrect, page_id - number, but not in database
	 */
	public void beginPageIdNotCorrect(WebRequest request){
		request.addParameter(BrowseAttributes.PAGE_ID, PAGE_ID_WRONG);
	}
	
	public void testPageIdNotCorrect(){
		ResponseContext response = command.execute();
		
		assertNotNull(response);
		
		assertEquals(response.getResultName(), AbstractCommand.RESULT_FAILURE);
	}
	
	/*
	 * set_id - incorrect, page_id - number, and exist in database
	 */
	public void beginPageIdCorrect(WebRequest request){
		request.addParameter(BrowseAttributes.PAGE_ID, PAGE_ID);
	}
	
	public void testPageIdCorrect() throws Exception{
		ResponseContext response = command.execute();
		
		assertNotNull(response);
		
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
		
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		
		checkXML((Document)TestUtil.removeCactusRequestParams(document));
		
	}
	
	/*
	 * set_id - correct
	 */
	public void beginWCMSAttributeIdCorrect(WebRequest request){
		request.addParameter(BrowseAttributes.WCMS_ATTRIBUTE_SET_ID, SET_ID);
	}
	
	public void testWCMSAttributeIdCorrect() throws Exception{
		ResponseContext response = command.execute();
		
		assertNotNull(response);
		
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
		
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		
		checkXML(document);
		
	}
	
	private void checkXML(Document doc) throws TransformerException, XpathException {
	
		XMLAssert.assertXpathExists(XML_root, doc);

		XMLAssert.assertXpathEvaluatesTo("1", "count(" + XML_root + ")", doc);

		XMLAssert.assertXpathExists(XML_root + "/@id", doc);
		
		XMLAssert.assertXpathEvaluatesTo(SET_ID, XML_root + "/@id", doc);
		
		XMLAssert.assertXpathEvaluatesTo("2", "count(" + XML_root + imageSet_path + ")", doc);
		
		XMLAssert.assertXpathEvaluatesTo("4", "count(" + XML_root + imageSet_path + "[1]" + image_path + ")", doc);
		
		XMLAssert.assertXpathExists(XML_root + imageSet_path + "[1]/@class", doc);
		XMLAssert.assertXpathEvaluatesTo("max width", XML_root + imageSet_path + "/@class", doc);
		XMLAssert.assertXpathEvaluatesTo("159", XML_root + imageSet_path + "/@id", doc);
		
		XMLAssert.assertXpathExists(XML_root + imageSet_path + "[1]" + image_path + "[1]", doc);
		XMLAssert.assertXpathEvaluatesTo("913", XML_root + imageSet_path + "[1]" + image_path + "[1]/@id", doc);
		XMLAssert.assertXpathEvaluatesTo("1", XML_root + imageSet_path + "[1]" + image_path + "[1]/@order", doc);
		XMLAssert.assertXpathEvaluatesTo("media/photo4_1213x109_2.jpg", XML_root + imageSet_path + "[1]" + image_path + "[1]/@src", doc);
		

		XMLAssert.assertXpathExists(XML_root + imageSet_path + "[1]" + image_path + "[2]", doc);
		XMLAssert.assertXpathEvaluatesTo("764", XML_root + imageSet_path + "[1]" + image_path + "[2]/@id", doc);
		XMLAssert.assertXpathEvaluatesTo("2", XML_root + imageSet_path + "[1]" + image_path + "[2]/@order", doc);
		XMLAssert.assertXpathEvaluatesTo("media/photo3.jpg", XML_root + imageSet_path + "[1]" + image_path + "[2]/@src", doc);

		XMLAssert.assertXpathExists(XML_root + imageSet_path + "[1]" + image_path + "[4]", doc);
		XMLAssert.assertXpathEvaluatesTo("1148", XML_root + imageSet_path + "[1]" + image_path + "[4]/@id", doc);
		XMLAssert.assertXpathEvaluatesTo("5", XML_root + imageSet_path + "[1]" + image_path + "[4]/@order", doc);
		XMLAssert.assertXpathEvaluatesTo("media/photo4_1213x109_1_1121x109.jpg", XML_root + imageSet_path + "[1]" + image_path + "[4]/@src", doc);
		
		
		XMLAssert.assertXpathExists(XML_root + imageSet_path + "[1]" + image_path + "[3]", doc);
		XMLAssert.assertXpathEvaluatesTo("762", XML_root + imageSet_path + "[1]" + image_path + "[3]/@id", doc);
		XMLAssert.assertXpathEvaluatesTo("4", XML_root + imageSet_path + "[1]" + image_path + "[3]/@order", doc);
		XMLAssert.assertXpathEvaluatesTo("media/screensaver.swf", XML_root + imageSet_path + "[1]" + image_path + "[3]/@src", doc);
		
		
		
		
		XMLAssert.assertXpathEvaluatesTo("1", "count(" + XML_root + imageSet_path + "[2]" + image_path + ")", doc);
		
		XMLAssert.assertXpathExists(XML_root + imageSet_path + "[2]/@class", doc);
		XMLAssert.assertXpathEvaluatesTo("fixed size", XML_root + imageSet_path + "[2]/@class", doc);
		XMLAssert.assertXpathEvaluatesTo("195", XML_root + imageSet_path + "[2]/@id", doc);
		
		XMLAssert.assertXpathExists(XML_root + imageSet_path + "[2]" + image_path + "[1]", doc);
		XMLAssert.assertXpathEvaluatesTo("903", XML_root + imageSet_path + "[2]" + image_path + "[1]/@id", doc);
		XMLAssert.assertXpathEvaluatesTo("1", XML_root + imageSet_path + "[2]" + image_path + "[1]/@order", doc);
		XMLAssert.assertXpathEvaluatesTo("media/big_image.jpg", XML_root + imageSet_path + "[2]" + image_path + "[1]/@src", doc);

	
		
	}

}
