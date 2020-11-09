/*
 * @(#)Id: CommandTestCase.java, 19.11.2007 14:16:43, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.cactus.ServletTestCase;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.menu.command.CreateMenuItemDialogCommand;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class CommandTestCase extends ServletTestCase {
    public AbstractCommand command = null;	
	public RequestContext requestContext;
	
	public CommandTestCase() {
		super();
	}
	
	public void setUp() throws Exception{
		super.setUp();
		requestContext = new RequestContextWrapper(request, response, config);
	}

	/**
		This method validates <code>document</code> with xml file 
		<code>sampleFilename</code>. At first time you need to create 
		your sample : 
		<code>XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/get-extra-pages-command.xml"); </code>
	*/
	public void validateXML(Node node,String sampleFilename)throws TransformerException,IOException,SAXException {
//		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/"+sampleFilename);
		Document controledDocument = TestUtil.getExpectedDocument(
				this.getClass(), 
				sampleFilename);
		node = TestUtil.removeCactusRequestParams(
				node);
		Diff diff = null;
		diff = new Diff  (TestUtil.parseToString(
				controledDocument),
				TestUtil.parseToString(node));	
		DetailedDiff dDiff = new DetailedDiff(diff);
		List allDifferences = dDiff.getAllDifferences();
		for (Object difference : allDifferences) {
			fail(difference.toString());
		}
		assertTrue(diff.identical());
	}
	public void testAccessDenied(AbstractCommand command){
		requestContext.getSession().setUserId(3);		
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		assertEquals(AbstractCommand.RESULT_ACCESS_DENIED, responseContext.getResultName());	

	}
}
