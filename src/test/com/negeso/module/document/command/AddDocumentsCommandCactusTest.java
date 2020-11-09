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

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.PreparedDatabaseLauncher;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;

/**
 * 
 * @TODO
 * 
 * @author		Sergey V. Klok
 * @version		$Revision: $
 *
 */
public class AddDocumentsCommandCactusTest extends CommandTestCase {
	
	private Command command;
	
	public AddDocumentsCommandCactusTest(){
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new AddDocumentsCommand();
		command.setRequestContext(requestContext);
	}
	
	
	public void beginAddOneDocument(WebRequest request){
		request.addParameter("category_id", "1");
		request.addParameter("link", "d:/xxx.jpg");	
		request.addParameter("image", "d:/xxx.jpg");
	}
	
	public void testAddOneDocument() throws Exception{
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathEvaluatesTo("true","/*[local-name()='page']/*[local-name()='category']/*[local-name()='document']/@name='xxx.jpg'", document);
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
		String query = "DELETE FROM dc_document WHERE name='xxx.jpg'AND category_id=1;";
		new PreparedDatabaseLauncher() {
			@Override
			protected void assertTest() {
			}	
		}.runTest(query);		
	}
	
	public void beginAddDocuments(WebRequest request){
		request.addParameter("category_id", "1");
		request.addParameter("link", "d:/node.xml;d:/xxx.jpg;d:/test.html");
		request.addParameter("image", "d:/xxx.jpg");
	}
	
	public void testAddDocuments()throws Exception{
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathEvaluatesTo("true","/*[local-name()='page']/*[local-name()='category']/*[local-name()='document']/@name='node.xml'", document);
		XMLAssert.assertXpathEvaluatesTo("true","/*[local-name()='page']/*[local-name()='category']/*[local-name()='document']/@name='xxx.jpg'", document);
		XMLAssert.assertXpathEvaluatesTo("true","/*[local-name()='page']/*[local-name()='category']/*[local-name()='document']/@name='test.html'", document);
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
		String query = "DELETE FROM dc_document WHERE name='xxx.jpg'AND category_id=1;"+
						"DELETE FROM dc_document WHERE name='test.html'AND category_id=1;"+
						"DELETE FROM dc_document WHERE name='node.xml'AND category_id=1;";
		new PreparedDatabaseLauncher() {
			@Override
			protected void assertTest() {
			}	
		}.runTest(query);
	}	
	
	public void beginAccessDenied(WebRequest request){
		request.addParameter("category_id", "1");
		request.addParameter("link", "d:/node.xml;d:/xxx.jpeg");
	}
	
	public void testAccessDenied(){
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, null);
		ResponseContext response = command.execute();
		assertEquals(response.getResultName(), AbstractCommand.RESULT_ACCESS_DENIED);		
	}
	
	public void beginThumbnail(WebRequest request){
		request.addParameter("category_id", "1");
		request.addParameter("link", "xxx.jpg");
		request.addParameter("image", "media/thumbnails/cimg0055_140x30.jpg");
	}
	
	public void testThumbnail() throws Exception{
		ResponseContext response = command.execute();
		Document document = (Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML);
		XMLAssert.assertXpathEvaluatesTo("true","/*[local-name()='page']/*[local-name()='category']/*[local-name()='document']/@name='xxx.jpg'", document);
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
		String query = "DELETE FROM dc_document WHERE name='xxx.jpg'AND category_id=1;";
		new PreparedDatabaseLauncher() {
			@Override
			protected void assertTest() {
			}	
		}.runTest(query);		
	}
	
	public void beginNoneDocument(WebRequest request){
		request.addParameter("category_id", "1");
	}
	
	public void testNoneDocument(){
		ResponseContext response = command.execute();
		assertEquals(response.getResultName(), AbstractCommand.RESULT_SUCCESS);
	}
}
