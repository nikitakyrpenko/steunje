/*
 * @(#)$Id: LoginCommandCactusTest.java,v 1.2, 2006-11-02 11:48:23Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.command;

import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.cactus.Cookie;
import org.apache.cactus.WebRequest;
import org.apache.cactus.WebResponse;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.SharedValidator;
import com.negeso.TableRowCounter;
import com.negeso.framework.Env;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.page.command.GetEditablePageCommand;

/**
 * 
 * @TODO
 * 
 * @author Alex Serbin
 * @version $Revision: 3$
 * 
 */
public class LoginCommandCactusTest extends CommandTestCase {

	public static final String COOKIE_NAME = "interface_language";

	private Command command;

	public LoginCommandCactusTest() {
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new GetEditablePageCommand();
		command.setRequestContext(requestContext);
	}

	public void testSiteSuspended() throws XpathException {
		SharedValidator.validateSiteStatusWarning(command, requestContext,
				"suspended");
	}

	public void testHistoryStack() {
		SharedValidator.validateHistoryStack(command, requestContext);
	}

	public void testResultSuccess() {
		// authorized as admin user_id = 1
		ResponseContext responseContext = command.execute();
		assertEquals(AbstractCommand.RESULT_SUCCESS, responseContext
				.getResultName());
		// this xml validates by GetPageCommandCactusTest
		assertNotNull(responseContext.getResultMap().get(
				AbstractCommand.OUTPUT_XML));
	}

	// this method also serves as testAccessDenied()
	private Document prepareXMLforCheckingInterfaceLanguage(
			HttpServletRequest request, HttpServletResponse response,
			ServletConfig config) throws TransformerException, SQLException, XpathException {
		// command result should be access denied
		requestContext.getSessionData().setAttribute(
				SessionData.USER_ATTR_NAME, null);
		ResponseContext responseContext = command.execute();
		assertEquals(LoginCommand.RESULT_DENIED, responseContext
				.getResultName());
		Document document = (Document) responseContext.getResultMap().get(
				AbstractCommand.OUTPUT_XML);
		assertNotNull(document);
		
		commonValidateLoginXML(document);
		return document;
	}

	public void beginInterfaceLanguageThroughRequestParameter(
			WebRequest webRequest) {
		webRequest.addParameter(LoginCommand.INPUT_LANGUAGE, "fr");
	}
	
	// test expected to get InterfaceLanguage through HttpRequest parameter
	public void testInterfaceLanguageThroughRequestParameter()
			throws TransformerException, SQLException, XpathException {
		Document xml = prepareXMLforCheckingInterfaceLanguage(request,response, config);
		// make shure that language is french
		XMLAssert.assertXpathEvaluatesTo("fr",
				"/*[local-name()='loginform']/@interface-language", xml);
	}

	public void endInterfaceLanguageThroughRequestParameter(WebResponse response) {
		// Asserts the returned HTTP response
		// make sure that cookie was added tov response
		Cookie cookie = response.getCookie(COOKIE_NAME);
		assertEquals("fr", cookie.getValue());
	}

	public void testInterfaceLanguageThroughDefaultValue()
			throws TransformerException, SQLException, XpathException {
		Document xml = prepareXMLforCheckingInterfaceLanguage(request,
				response, config);
		// make shure that language is default interface language
		
		XMLAssert.assertXpathEvaluatesTo(Env.getDefaultInterfaceLanguageCode(),
				"/*[local-name()='loginform']/@interface-language", xml);
	}

	public void beginLogout(WebRequest request) {
		request.addParameter(LogoutCommand.INPUT_LOGOUT, "1");
	}

	/*
	 * // can't set cookie in begin method !!!!!!!! // TODO find out why adding
	 * cookies failed public void
	 * beginInterfaceLanguageThroughRequestCookies(WebRequest webRequest) { //
	 * TODO set cookie in begin method webRequest.addCookie("fdgdsfgds",
	 * "sadfasdfasdf"); //Cookie c = new Cookie(); /*c.setName("name");
	 * c.setValue("value"); webRequest.addCookie(c);
	 * webRequest.addCookie("sdfsaf", "sdfsadf", "sdfasdf"); }
	 * 
	 * public void testInterfaceLanguageThroughRequestCookies() throws
	 * TransformerException, SQLException {
	 * 
	 * assertNotNull(request.getCookies()); Document xml =
	 * prepareXMLforCheckingInterfaceLanguage(request, response, config); //make
	 * shure that language is italian XMLAssert.assertXpathEvaluatesTo("it",
	 * "/*[local-name()='loginform']/@interface-language", xml); }
	 */

	public void beginWrongPassword(WebRequest request) {
		request.addParameter(LoginCommand.INPUT_LOGIN, "admin");
		request.addParameter(LoginCommand.INPUT_PASSWORD, "wrong_password");
	}

	public void testWrongPassword() throws TransformerException, XpathException {
		requestContext.getSessionData().setAttribute(
				SessionData.USER_ATTR_NAME, null);
		assertNull(requestContext.getSession().getUser());
		ResponseContext responseContext = command.execute();
		// make sure that authentication was failed
		assertNull(requestContext.getSession().getUser());
		assertEquals(LoginCommand.RESULT_DENIED, responseContext
				.getResultName());
		Document document = (Document) responseContext.getResultMap().get(
				AbstractCommand.OUTPUT_XML);
		assertNotNull(document);
		// make sure that login node was added
		XMLAssert.assertXpathEvaluatesTo("admin",
				"/*[local-name()='loginform']/*[local-name()='login']",
				document);
	}

	public void beginLoginPasswordAuthentication(WebRequest request) {
		request.addParameter(LoginCommand.INPUT_LOGIN, "admin");
		request.addParameter(LoginCommand.INPUT_PASSWORD,
				"adb1bffdbb00067a9c2d88b1614809fb");
	}

	public void testLoginPasswordAuthentication() {
		requestContext.getSessionData().setAttribute(
				SessionData.USER_ATTR_NAME, null);
		assertNull(requestContext.getSession().getUser());
		ResponseContext responseContext = command.execute();
		// make sure that authentication was successfull
		assertNotNull(requestContext.getSession().getUser());
		assertEquals(AbstractCommand.RESULT_SUCCESS, responseContext
				.getResultName());
		// this xml validates by GetPageCommandCactusTest
		assertNotNull(responseContext.getResultMap().get(
				AbstractCommand.OUTPUT_XML));
	}

	private void commonValidateLoginXML(Document document)
			throws TransformerException, SQLException, XpathException {
		
		XMLAssert.assertXpathExists("/*[local-name()='loginform']", document);
		XMLAssert.assertXpathEvaluatesTo("1",
				"count(/*[local-name()='loginform'])", document);
		final String languagesCount = TableRowCounter
				.getRowCount("interface_language");
		XMLAssert.assertXpathExists(
				"/*[local-name()='loginform']/*[local-name()='languages']",
				document);
		XMLAssert
				.assertXpathEvaluatesTo(
						"1",
						"count(/*[local-name()='loginform']/*[local-name()='languages'])",
						document);
		XMLAssert
				.assertXpathEvaluatesTo(
						languagesCount,
						"count(/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language'])",
						document);
		// exists default language
		XMLAssert
				.assertXpathExists(
						"/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language']/@default",
						document);
		// default = true <=> code = Env.getDefaultLanguageCode()
		XMLAssert
				.assertXpathsEqual(
						"/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language'][@default='true']",
						"/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language'][@code='"
								+ Env.getDefaultLanguageCode() + "']", document);
		// count of default languages = 1
		XMLAssert
				.assertXpathEvaluatesTo(
						"1",
						"count(/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language']/@default)",
						document);
		// all attributes of all languages are present
		for (int i = 1; i <= Integer.valueOf(languagesCount); i++) {
			XMLAssert
					.assertXpathExists(
							"/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language']["
									+ i + "]/@id", document);
			XMLAssert
					.assertXpathExists(
							"/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language']["
									+ i + "]/@code", document);
			XMLAssert
					.assertXpathExists(
							"/*[local-name()='loginform']/*[local-name()='languages']/*[local-name()='language']["
									+ i + "]/@name", document);
		}
	}
}
