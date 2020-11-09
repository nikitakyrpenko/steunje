/*
 * @(#)Id: GetCategoriesCommandCactusTest.java, 31.07.2007 14:02:44, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.module;

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class GetCategoriesCommandCactusTest extends CommandTestCase{
	
	//test module = news_module with id=1 and name=news_module (in DB)
	private static final String MODULE_ID = "1";
	private static final String MODULE_NAME = "news_module";
	
	private static final String NUMBER_OF_PAGES_WITH_SUCH_MODULE = "6";
	
	private GetCategoriesCommand command;
	
	public GetCategoriesCommandCactusTest(){
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new GetCategoriesCommand();
		command.setRequestContext(requestContext);
	}
	
	/*
	 *	 We don't specify any parameters, waiting for failure response
	 */
	public void testNoRequestParameters() throws Exception {
		ResponseContext responseContext = command.execute();
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	/*
	 *	INPUT_MODULE_NAME - null, will set moduleId by INPUT_MODULE_ID
	 */
	public void beginSetModuleById(WebRequest request) throws Exception {
		request.addParameter(GetCategoriesCommand.INPUT_MODULE_ID, MODULE_ID);
	}
	
	public void testSetModuleById() throws Exception {
		ResponseContext responseContext = command.execute();
		Document document = (Document) responseContext.getResultMap().get(GetCategoriesCommand.OUTPUT_DOCUMENT);
		validateXML(document);
		assertEquals(MODULE_NAME, responseContext.getResultName());
	}
	
	private void validateXML(Document document) throws Exception {
		XMLAssert.assertXpathExists("/*[local-name()='categories']",document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/@module",document);
		XMLAssert.assertXpathEvaluatesTo(MODULE_NAME,"/*[local-name()='categories']/@module",document);
		XMLAssert.assertXpathEvaluatesTo(NUMBER_OF_PAGES_WITH_SUCH_MODULE, "count(/*[local-name()='categories']/*[local-name()='category'])", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][1]", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][1]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][1]/@name", document);
		
		XMLAssert.assertXpathEvaluatesTo("19", "/*[local-name()='categories']/*[local-name()='category'][1]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("About us > News", "/*[local-name()='categories']/*[local-name()='category'][1]/@name", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][2]", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][2]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][2]/@name", document);
		
		XMLAssert.assertXpathEvaluatesTo("2", "/*[local-name()='categories']/*[local-name()='category'][2]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("Home_en", "/*[local-name()='categories']/*[local-name()='category'][2]/@name", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][3]", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][3]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][3]/@name", document);
		
		XMLAssert.assertXpathEvaluatesTo("47", "/*[local-name()='categories']/*[local-name()='category'][3]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("Home_nl", "/*[local-name()='categories']/*[local-name()='category'][3]/@name", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][4]", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][4]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][4]/@name", document);
		
		XMLAssert.assertXpathEvaluatesTo("20", "/*[local-name()='categories']/*[local-name()='category'][4]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("Newsline", "/*[local-name()='categories']/*[local-name()='category'][4]/@name", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][5]", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][5]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][5]/@name", document);
		
		XMLAssert.assertXpathEvaluatesTo("50", "/*[local-name()='categories']/*[local-name()='category'][5]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("Newsline nl", "/*[local-name()='categories']/*[local-name()='category'][5]/@name", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][6]", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][6]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='categories']/*[local-name()='category'][6]/@name", document);
		
		XMLAssert.assertXpathEvaluatesTo("49", "/*[local-name()='categories']/*[local-name()='category'][6]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("Over ons > Nieuws", "/*[local-name()='categories']/*[local-name()='category'][6]/@name", document);
	}
	
	
	/*
	 *	INPUT_MODULE_ID - null, we will set module by its name
	 */
	public void beginSetModuleByName(WebRequest request) throws Exception {
		request.addParameter(GetCategoriesCommand.INPUT_MODULE_NAME, MODULE_NAME);
	}
	
	public void testSetModuleByName() throws Exception {
		ResponseContext responseContext = command.execute();
		Document document = (Document) responseContext.getResultMap().get(GetCategoriesCommand.OUTPUT_DOCUMENT);
		validateXML(document);
		assertEquals(MODULE_NAME, responseContext.getResultName());
	}
}
