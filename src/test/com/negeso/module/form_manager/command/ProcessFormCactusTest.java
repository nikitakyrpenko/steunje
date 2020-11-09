/*
 * @(#)$Id: ProcessFormCactusTest.java,v 1.4, 2006-11-22 08:59:40Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.form_manager.command;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.xml.transform.TransformerException;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 5$
 *
 */
public class ProcessFormCactusTest extends CommandTestCase {
	
	private TestProcessForm processForm;
	
	private class TestProcessForm extends ProcessForm {
		
		String messageBody;
		String contentType;
		Element bodyEl;
		String formName;
		String defaultFrom;
		
		@Override
		protected void sendEmail(String messageBody, String contentType,
				Element bodyEl, String formName, String defaultFrom,
				Multipart mp, InternetAddress[] addr) throws MessagingException {
			this.messageBody = messageBody;
			this.contentType = contentType;
			this.bodyEl = bodyEl;
			this.formName = formName;
			this.defaultFrom = defaultFrom;
		}
	}
	
	public ProcessFormCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		// set mockup oblects
		super.setUp();
		processForm = new TestProcessForm();
		processForm.setRequestContext(requestContext);
	}
	
	public void beginNoFormParameterInRequest(WebRequest request) { 
		request.addParameter("", "", WebRequest.POST_METHOD);
	}
	
	public void testNoFormParameterInRequest() throws TransformerException, XpathException {
		
		ResponseContext responseContext = processForm.execute();
		assertNotNull(responseContext);
		assertEquals(
				responseContext.getResultName(), 
				AbstractCommand.RESULT_SUCCESS);
		Document document = (Document) responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML);
		assertNotNull(document);
		XMLAssert.assertXpathExists(
				"/*[local-name()='page']", 
				document);
		XMLAssert.assertXpathEvaluatesTo(
				"mail_failure", 
				"/*[local-name()='page']/@category", 
				document);
	}

	public void beginNoFormEntryInDatabase(WebRequest request) {
		// pass wrong id in request
		request.addParameter("mailToFlag", "999", WebRequest.POST_METHOD);
	}
	
	public void testNoFormEntryInDatabase() throws TransformerException, XpathException {
		ResponseContext responseContext = processForm.execute();
		assertNotNull(responseContext);
		assertEquals(
				responseContext.getResultName(), 
				AbstractCommand.RESULT_SUCCESS);
		Document document = (Document) responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML);
		assertNotNull(document);
		XMLAssert.assertXpathExists(
				"/*[local-name()='page']", 
				document);
		XMLAssert.assertXpathEvaluatesTo(
				"mail_failure", 
				"/*[local-name()='page']/@category", 
				document);
	}
	
	public void beginSuccessfulProcessForm(WebRequest request) {
		// pass correct id in request. This id should exist in 'forms' table.
		request.addParameter("mailToFlag", "1", WebRequest.POST_METHOD);
	}
	
	public void testSuccessfulProcessForm() throws TransformerException, XpathException {
		ResponseContext responseContext = processForm.execute();
		assertNotNull(responseContext);
		assertEquals(responseContext.getResultName(), AbstractCommand.RESULT_SUCCESS);
		Document document = (Document) responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML);
		assertNotNull(document);
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathEvaluatesTo("mail_success", "/*[local-name()='page']/@category", document);
		
		// check email content
		assertEquals(Env.getProperty("DEFAULT_FROM_EMAIL_VALUE"), "false");
		assertEquals("mailform@negeso.com", processForm.defaultFrom);
		assertEquals("text/html", processForm.contentType);
		assertEquals("New Form", processForm.formName);
		assertNotNull(processForm.bodyEl);
		Document doc = processForm.bodyEl.getOwnerDocument();
		XMLAssert.assertXpathExists("/*[local-name()='page']", doc);
		XMLAssert.assertXpathEvaluatesTo("en", "/*[local-name()='page']/@lang", doc);
		XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='page']/@lang-id", doc);
		XMLAssert.assertXpathEvaluatesTo("en", "/*[local-name()='page']/@interface-language", doc);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='webform']", doc);
		XMLAssert.assertXpathEvaluatesTo("New Form",
				"/*[local-name()='page']/*[local-name()='webform']/@title", doc);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='webform']/@posted", doc);
	}

	public void beginSuccessfulProcessFormWithObligatoryEmailField(WebRequest request) {
		request.addParameter("mailToFlag", "1", WebRequest.POST_METHOD);
		request.addParameter("first_obligatory_email_field", "test@test.mail.com");
	}
	
	public void testSuccessfulProcessFormWithObligatoryEmailField() {
		processForm.execute();
		assertEquals(Env.getProperty("DEFAULT_FROM_EMAIL_VALUE"), "false");
		assertEquals("test@test.mail.com", processForm.defaultFrom);
	}

}

