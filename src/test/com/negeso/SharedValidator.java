/*
 * @(#)$Id: SharedValidator.java,v 1.0, 2007-01-10 13:29:33Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.navigation.HistoryStack;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.site.Site;

/** Class for duplicated validations
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class SharedValidator {

	private static final String HISTORY_STACK_KEY   = "historyStack";
	
	private static void validateSiteStatusWarningXML(Document document) throws XpathException {
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='page'])", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='site_status_warning']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='site_status_warning']/@creation_date", document);		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='site_status_warning']/@deletion_date", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='site_status_warning']/@suspension_date", document);		
	}
	
	public static void validateSiteStatusWarning(
			Command command, 
			RequestContext requestContext, 
			String status) throws XpathException {
    	Site site = Env.getSite();
    	String oldstatus = site.getStatus();
    	site.setStatus(status);
    	try {
			command.setRequestContext(requestContext);
			ResponseContext response = command.execute();
			Assert.assertEquals(AbstractCommand.RESULT_SUCCESS, response.getResultName());
			validateSiteStatusWarningXML((Document)response.getResultMap().get(AbstractCommand.OUTPUT_XML));
    	} finally {
    		site.setStatus(oldstatus);
    	}
	}
	
	private static Link findLinkByTitleInHistoryStack(String linkTitle, HistoryStack historyStack) {
		for (int i = 0; i < historyStack.getSize(); i++) {
			Link link = historyStack.getLinks().elementAt(i);
			if (linkTitle.equals(link.getTitle())) {
				return link; 
			}
		}
		return null;
	}
	
	public static void validateHistoryStack(Command command, RequestContext requestContext) {
		requestContext.getSession().setAttribute(HISTORY_STACK_KEY, null);
		command.setRequestContext(requestContext);
		requestContext.getSessionData().setAttribute(HISTORY_STACK_KEY, null);
		command.execute();
		final HistoryStack historyStack = (HistoryStack)requestContext.getSessionData().getAttribute(HISTORY_STACK_KEY);
		//make sure historyStack exists
		Assert.assertNotNull(historyStack);
		final Link modulesLink = findLinkByTitleInHistoryStack("Modules", historyStack);
		//make sure historyStack modules Link was added
		Assert.assertNotNull(modulesLink); 
		//make sure modeles link URL = /admin/?command=get-modules-command
		Assert.assertEquals(modulesLink.getUrl(), "/admin/?command=get-modules-command");
	}
	
}

