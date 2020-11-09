/*
 * @(#)Id: GetListCommandCactusTest.java, 30.07.2007 11:39:04, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.list.command;

import javax.xml.transform.TransformerException;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.AccessDeniedException;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class GetListCommandCactusTest extends CommandTestCase{
	
	private static final String LIST_ID = "19";
	private static final String LIST_ID_WRONG = "1";
	private static final String LIST_PATH = "19;51";
	
	private GetListCommand command;
	
	public GetListCommandCactusTest(){
		super();		
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new GetListCommand();
		command.setRequestContext(requestContext);
	}
	
	/*
	 * we don't specify parameter INPUT_LIST_ID -> test for failure response
	 */
	public void testResultFailure(){
		ResponseContext responseContext = command.execute();
		assertEquals(GetListCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	/*
	 * we emulate that we don't have permission
	 */
	public void beginAccessDenied(WebRequest request){
		request.addParameter(GetListCommand.INPUT_LIST_ID, LIST_ID);
	}
	
	public void testAccessDenied() throws Exception, Throwable{
		command = new GetListCommand(){
			@Override
			protected void checkPermission(User user, Long listContainerId) throws AccessDeniedException
		    {
		        throw new AccessDeniedException();
		    }
		};
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		assertEquals(GetListCommand.RESULT_ACCESS_DENIED, responseContext.getResultName());
	}
	
	/*
	 * we will set wrong list id
	 */
	public void beginListIdWrong(WebRequest request){
		request.addParameter(GetListCommand.INPUT_LIST_ID, LIST_ID_WRONG);
	}
	
	public void testListIdWrong() throws Exception, Throwable{
		ResponseContext responseContext = command.execute();
		assertEquals(GetListCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	/*
	 * we will set wrong root id
	 */
	public void beginRootListIdWrong(WebRequest request){
		request.addParameter(GetListCommand.INPUT_LIST_ID, LIST_ID);
		request.addParameter(GetListCommand.INPUT_ROOT_LIST_ID, LIST_ID_WRONG);
	}
	
	public void testRootListIdWrong() throws Exception, Throwable{
		ResponseContext responseContext = command.execute();
		assertEquals(GetListCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	/*
	 * we will set wrong INPUT_LIST_PATH
	 */
	public void testInputListPathWrong() throws Exception, Throwable{
		ResponseContext responseContext = command.execute();
		assertEquals(GetListCommand.RESULT_FAILURE, responseContext.getResultName());
	}

	
	/*
	 * check if params are correct
	 */
	public void beginCorrectExec(WebRequest request){
		request.addParameter(GetListCommand.INPUT_LIST_ID, LIST_ID);
		request.addParameter(GetListCommand.INPUT_LIST_PATH, LIST_PATH);
	}
	
	public void testCorrectExec() throws Exception, Throwable{
		ResponseContext responseContext = command.execute();
		assertEquals(GetListCommand.RESULT_SUCCESS, responseContext.getResultName());
		String output_template = (String) responseContext.getResultMap().get(GetListCommand.OUTPUT_TEMPLATE);
		assertEquals("NEWSLIST_LIST_XSL", output_template);
		Document document = (Document) responseContext.getResultMap().get(GetListCommand.OUTPUT_DOCUMENT);
		validateXML(document);
		
	}
	private void validateXML(Document document) throws TransformerException, XpathException{
		XMLAssert.assertXpathExists("/*[local-name()='list']", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/@canContribute", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/@canManage", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/@level", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/@listPath", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/@type", document);

		XMLAssert.assertXpathEvaluatesTo("true","/*[local-name()='list']/@canContribute", document);
		XMLAssert.assertXpathEvaluatesTo("true","/*[local-name()='list']/@canManage", document);
		XMLAssert.assertXpathEvaluatesTo(LIST_ID,"/*[local-name()='list']/@id", document);
		XMLAssert.assertXpathEvaluatesTo("0","/*[local-name()='list']/@level", document);
		XMLAssert.assertXpathEvaluatesTo(LIST_PATH,"/*[local-name()='list']/@listPath", document);
		XMLAssert.assertXpathEvaluatesTo("newslist","/*[local-name()='list']/@type", document);
		
		XMLAssert.assertXpathEvaluatesTo("6","count(/*[local-name()='list']/*[local-name()='listItem'])",document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][1]", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][1]/@containerId", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][1]/@listId", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][1]/@orderNumber", document);

		XMLAssert.assertXpathEvaluatesTo("2","/*[local-name()='list']/*[local-name()='listItem'][1]/@containerId", document);
		XMLAssert.assertXpathEvaluatesTo("19","/*[local-name()='list']/*[local-name()='listItem'][1]/@listId", document);
		XMLAssert.assertXpathEvaluatesTo("0","/*[local-name()='list']/*[local-name()='listItem'][1]/@orderNumber", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][2]", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][2]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][2]/@listId", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][2]/@orderNumber", document);

		XMLAssert.assertXpathEvaluatesTo("18","/*[local-name()='list']/*[local-name()='listItem'][2]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("19","/*[local-name()='list']/*[local-name()='listItem'][2]/@listId", document);
		XMLAssert.assertXpathEvaluatesTo("1","/*[local-name()='list']/*[local-name()='listItem'][2]/@orderNumber", document);

		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][3]", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][3]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][3]/@listId", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][3]/@orderNumber", document);

		XMLAssert.assertXpathEvaluatesTo("48","/*[local-name()='list']/*[local-name()='listItem'][3]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("19","/*[local-name()='list']/*[local-name()='listItem'][3]/@listId", document);
		XMLAssert.assertXpathEvaluatesTo("2","/*[local-name()='list']/*[local-name()='listItem'][3]/@orderNumber", document);

		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][4]", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][4]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][4]/@listId", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][4]/@orderNumber", document);

		XMLAssert.assertXpathEvaluatesTo("49","/*[local-name()='list']/*[local-name()='listItem'][4]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("19","/*[local-name()='list']/*[local-name()='listItem'][4]/@listId", document);
		XMLAssert.assertXpathEvaluatesTo("3","/*[local-name()='list']/*[local-name()='listItem'][4]/@orderNumber", document);

		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]/@listId", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][5]/@orderNumber", document);

		XMLAssert.assertXpathEvaluatesTo("50","/*[local-name()='list']/*[local-name()='listItem'][5]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("19","/*[local-name()='list']/*[local-name()='listItem'][5]/@listId", document);
		XMLAssert.assertXpathEvaluatesTo("4","/*[local-name()='list']/*[local-name()='listItem'][5]/@orderNumber", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][6]", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][6]/@id", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][6]/@listId", document);
		XMLAssert.assertXpathExists("/*[local-name()='list']/*[local-name()='listItem'][6]/@orderNumber", document);

		XMLAssert.assertXpathEvaluatesTo("51","/*[local-name()='list']/*[local-name()='listItem'][6]/@id", document);
		XMLAssert.assertXpathEvaluatesTo("19","/*[local-name()='list']/*[local-name()='listItem'][6]/@listId", document);
		XMLAssert.assertXpathEvaluatesTo("5","/*[local-name()='list']/*[local-name()='listItem'][6]/@orderNumber", document);
	}
}
