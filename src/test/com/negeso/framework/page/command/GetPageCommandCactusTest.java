/*
 * @(#)$Id: GetPageCommandCactusTest.java,v 1.6, 2006-11-13 12:11:26Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page.command;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.SharedValidator;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 7$
 *
 */
public class GetPageCommandCactusTest extends CommandTestCase {

    private Command command; 
	
    public GetPageCommandCactusTest(){
    	super();
    }
    
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new GetPageCommand();
		command.setRequestContext(requestContext);
	}

	public void testUnderConstruction() throws XpathException {
		SharedValidator.validateSiteStatusWarning(command, requestContext, "construction");
	}
	
	public void beginLogout(WebRequest theRequest){
	    theRequest.addParameter("logout", "1");
	}
	
	public void testLogout() {
		User user = requestContext.getSession().getUser();
		assertNotNull(user);
		command.execute();
		user = requestContext.getSession().getUser();
		assertNull(user);
	}
	
	public void testWithoutLogout() {
		User userBefore = requestContext.getSession().getUser();;
		command.execute();
		User userAfter = requestContext.getSession().getUser();
		assertNotNull(userAfter);
		assertEquals(userBefore, userAfter);
	}
	
	public void beginCertainPage(WebRequest request) throws TransformerException {
		request.addParameter("filename", "simple_page_en.html");
	}
	
	public void testCertainPage() throws TransformerException, XpathException {
		ResponseContext response = command.execute();
		assertEquals(AbstractCommand.RESULT_SUCCESS, response.getResultName());
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		validateCommonXML(document, requestContext) ;
		Map<String, String> compareParams = new HashMap<String, String>();
		//TODO fetch record from database
		compareParams.put("id", "21");
		compareParams.put("category", "page");
		compareParams.put("filename", "simple_page_en.html");
		compareParams.put("title", "Simple page");
		compareParams.put("class", "simple");
		validateSpecialXML(document, compareParams);
	}
	
	public void beginPagePageNotFound(WebRequest request) throws TransformerException {
		request.addParameter("filename", "not_existing_page");
	}
	
	public void testPagePageNotFound() throws TransformerException, XpathException {
		ResponseContext response = command.execute();
		assertEquals(AbstractCommand.RESULT_SUCCESS, response.getResultName());
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		validateCommonXML(document, requestContext) ;
		Map<String, String> compareParams = new HashMap<String, String>();
		compareParams.put("id", "3");
		compareParams.put("category", "not_found");
		compareParams.put("filename", "error_en.html");
		compareParams.put("title", "Resource not found");
		compareParams.put("class", null);
		validateSpecialXML(document, compareParams);
    }
	
	public void testFrontPage() throws TransformerException, XpathException {
		ResponseContext response = command.execute();
		assertEquals(AbstractCommand.RESULT_SUCCESS, response.getResultName());
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		
		validateCommonXML( (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML), requestContext) ;
		Map<String, String> compareParams = new HashMap<String, String>();
		compareParams.put("id", "1");
		compareParams.put("category", "frontpage");
		compareParams.put("filename", "index_en.html");
		compareParams.put("title", "Welcome");
		compareParams.put("class", "frontpage");
		validateSpecialXML(document, compareParams);
	}

// special validation of result xml
	private void validateSpecialXML(Document document, Map<String, String> compareParams) throws TransformerException, XpathException {
		XMLAssert.assertXpathEvaluatesTo(compareParams.get("id"), "/*[local-name()='page']/@id", document);
   	    if (compareParams.get("class") != null) { 
   	    	    XMLAssert.assertXpathEvaluatesTo(compareParams.get("class"), "/*[local-name()='page']/@class", document);
   	    } else XMLAssert.assertXpathNotExists("/*[local-name()='page']/@class", document);
        XMLAssert.assertXpathEvaluatesTo(compareParams.get("filename"), "/*[local-name()='page']/*[local-name()='filename']", document);
        XMLAssert.assertXpathEvaluatesTo(compareParams.get("category"), "/*[local-name()='page']/@category", document);
        XMLAssert.assertXpathEvaluatesTo(compareParams.get("title"), "/*[local-name()='page']/*[local-name()='title']", document);
	}
	
//	 the most common validation of result xml	
	private void validateCommonXML(Document document, RequestContext reqContext) throws TransformerException, XpathException {
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='page'])", document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/@browser", document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/@id", document);
        XMLAssert.assertXpathEvaluatesTo("administrator", "/*[local-name()='page']/@role-id-max", document);
        XMLAssert.assertXpathEvaluatesTo("administrator", "/*[local-name()='page']/@role-id", document);
        XMLAssert.assertXpathEvaluatesTo("en", "/*[local-name()='page']/@lang", document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/@last_modified", document);
        //XMLAssert.assertXpathExists("/*[local-name()='page']/@class", document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/@category", document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/@isMentrix", document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='filename']", document);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='page']/*[local-name()='filename'])", document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='title']", document);        
        XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='page']/*[local-name()='title'])", document);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='page']/*[local-name()='request'])", document);
        XMLAssert.assertXpathEvaluatesTo(Integer.toString(reqContext.getParameterMap().size()) ,
        		                         "count(/*[local-name()='page']/*[local-name()='request']/*[local-name()='parameter'])"
        		                        ,document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='request']/*[local-name()='parameter']/@name" ,document);
        XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='request']/*[local-name()='parameter']/*[local-name()='value']" ,document);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='page']/*[local-name()='contents'])", document);
	}

}
