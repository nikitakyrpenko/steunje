/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.cactus.ServletTestCase;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.negeso.framework.controller.DispatchersContainer;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class BaseControllerTestCase extends ServletTestCase{
	
	public DispatchersContainer dc;
	public static final String FAIL = "This part of code must not be reached";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dc = DispatchersContainer.getInstance();
	}
	
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
}

