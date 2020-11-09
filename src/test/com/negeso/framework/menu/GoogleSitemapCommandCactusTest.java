/*
 * @(#)Id: GoogleSitemapCommandCactusTest.java, 04.09.2007 15:18:44, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu;

import javax.xml.transform.TransformerException;

import org.apache.cactus.ServletTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class GoogleSitemapCommandCactusTest extends CommandTestCase{
	
	AbstractCommand command = null;
	
	public GoogleSitemapCommandCactusTest(){
		super();
	}
	
	@Override
	public void setUp(){
		command = new GoogleSitemapCommand();
		command.setRequestContext(requestContext);
	}
	
	/**
     * Returns map containing result details. If map has not been set,
     * a new (empty) one is created.
     * 
     * @return results as map; never null
     */
	public void testXXX() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(GoogleSitemapCommand.OUTPUT_XML);
		
		validateXML(doc);

		assertEquals(GoogleSitemapCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	private void validateXML(Document doc) throws TransformerException, XpathException{

		XMLAssert.assertXpathExists("/*[local-name()='urlset']/*[local-name()='url']", doc);
		XMLAssert.assertXpathExists("/*[local-name()='urlset']/*[local-name()='url']/*[local-name()='loc']", doc);
		XMLAssert.assertXpathExists("/*[local-name()='urlset']/*[local-name()='url']/*[local-name()='changefreq']", doc);
		XMLAssert.assertXpathEvaluatesTo(Env.getHostName(), "/*[local-name()='urlset']/*[local-name()='url'][1]/*[local-name()='loc']", doc);
		XMLAssert.assertXpathEvaluatesTo("daily", "/*[local-name()='urlset']/*[local-name()='url'][1]/*[local-name()='changefreq']", doc);
	}
}
