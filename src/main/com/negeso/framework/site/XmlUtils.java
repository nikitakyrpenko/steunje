/*
 * @(#)$Id: XmlUtils.java,v 1.6, 2005-10-26 13:57:20Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.site;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.domain.CriticalException;

/**
 * 
 *
 * Xml utils 
 * 
 * @version		$Revision: 7$
 * @author		Olexiy V. Strashko
 *
 */
public class XmlUtils {
    private static Logger logger = Logger.getLogger(XmlUtils.class);

    private static final TransformerFactory transformerFactory =
        TransformerFactory.newInstance();
    
    public static Transformer newTransformer()
    {
        logger.debug("+ -");
        try {
            synchronized(transformerFactory){
                return transformerFactory.newTransformer();
            }
        } catch (TransformerConfigurationException e) {
            logger.error("- Cannot create XSL Transformer", e);
            throw new CriticalException("Cannot create XSL Transformer");
        }
    }
   
    /**
     * Serializes Document to string
     * 
     * @param doc
     * @return
     */
    public static String documentToString(org.w3c.dom.Document doc){
        
    	Transformer transformer = XmlUtils.newTransformer();
        StringWriter writer = new StringWriter();
        try{
	        transformer.transform(
	        	new DOMSource(doc), new StreamResult(writer)
	        );
        }
		catch (TransformerException e) {
    		logger.error("-ERROR:", e);
    		throw new CriticalException(e);
		}
		return writer.toString();
    }

    /**
     * Serializes String to Document
     * 
     * @param doc
     * @return
     */    
    public static String getStringFromDocument(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	}



}
