/*
 * @(#)$Id: XMLtoHardDiskWriter.java,v 1.0, 2007-01-10 13:29:34Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class XMLtoHardDiskWriter {
	
	public static void writeXMLtoHardDisk (Node node, String filepath) {
		
		try {
//			Writer streamWriter = new StringWriter();
            Source source = new DOMSource(node);
//            StreamResult result = new StreamResult(streamWriter);
            StreamResult result = new StreamResult(new File(filepath));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
        } catch (Exception e) {
        	System.err.println(e);
        }

	}

}

