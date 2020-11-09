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
package com.negeso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLDataException;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * 
 * @TODO
 * 
 * @author		Vadim Mishchenko
 * @version		$Revision: $
 *
 */
public class TestUtil {
//TODO exceptions
	/**
	 * @param node
	 * @return
	 */
	public static String parseToString(Node node) {
		StringWriter stringWriter;
		StringBuffer buffer = null;
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			stringWriter = new StringWriter(128);
			transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
			buffer =  stringWriter.getBuffer();
		} catch (TransformerConfigurationException e) {
			Assert.fail("Unexpected TransformerConfigurationException occurred");
		} catch (TransformerFactoryConfigurationError e) {
			Assert.fail("Unexpected TransformerFactoryConfigurationError occurred");
		} catch (TransformerException e) {
			Assert.fail("Unexpected TransformerException occurred");
		}		
		return buffer.toString();
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static Node parseToDom (String str) {
		InputStream is = null;
		Document doc = null;
		try {
			byte[] bytes = str.getBytes("UTF-8");
			is =  new ByteArrayInputStream(bytes);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			doc = documentBuilderFactory.newDocumentBuilder().parse(is);
		} catch (UnsupportedEncodingException e) {
			Assert.fail("Unexpected UnsupportedEncodingException occurred");
		} catch (SAXException e) {
			Assert.fail("Unexpected SAXException occurred");
		} catch (IOException e) {
			Assert.fail("Unexpected IOException occurred");
		} catch (ParserConfigurationException e) {
			Assert.fail("Unexpected ParserConfigurationException occurred");
		}finally{
			IOUtils.closeQuietly(is);
		}
		return doc;
	}
	
	/**
	 * @param clazz
	 * @param xmlFilename
	 * @return
	 */
	public static Document getExpectedDocument(Class clazz, String xmlFilename) {
		InputStream is  = clazz.getResourceAsStream(xmlFilename);
		Assert.assertNotNull("xml file not found", is);
		Document controledDocument = null;
		InputSource inputSource = null;
		try {
		  inputSource = new InputSource(is);
		  controledDocument = 
				  XMLUnit.buildControlDocument(
						  inputSource);
		  
		} catch (IOException e) {
			Assert.fail("Unexpected IOException occurred");
		} catch (SAXException e) {
			Assert.fail("Unexpected SAXException occurred");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				;
			}
		}
		return controledDocument;
	}
	
	/**
	 * @param node
	 * @return
	 */
	public static Node removeCactusRequestParams(Node node){
		

	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    xpath.setNamespaceContext(new NamespaceContext() {	
			@Override
			public Iterator getPrefixes(String namespaceURI) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public String getPrefix(String namespaceURI) {
				if (namespaceURI.equals("http://negeso.com/2003/Framework"))
	                return "negeso";
	            else
	                return null;
			}
			@Override
			public String getNamespaceURI(String prefix) {
				if (prefix.equals("negeso"))
	                return "http://negeso.com/2003/Framework";
	            else
	                return XMLConstants.NULL_NS_URI;
			}
		});
	    XPathExpression expr;
	    Object result = null;
	    NodeList paramsList = null;
		try {
			expr = xpath.compile("//negeso:parameter");
			result = expr.evaluate(node, XPathConstants.NODESET);
			paramsList = (NodeList) result;
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

		//NodeList paramsList = node.getElementsByTagName("negeso:parameter");
		if (paramsList==null || paramsList.getLength()==0)
			return node;
		for(Node childNode = paramsList.item(0);childNode!=null;){
			Node nextChild = childNode.getNextSibling();
			NamedNodeMap attrs = childNode.getAttributes();
			for (int j = 0; j < attrs.getLength(); j++) {
		        Attr attribute = (Attr) attrs.item(j);
		        if (attribute.getValue().startsWith("Cactus_") ||
		        		attribute.getValue().equals("REQUEST_URI") ||
		        		attribute.getValue().equals("REQUEST_LOCALES"))
		        	childNode.getParentNode().removeChild(childNode);
		    }
			childNode = nextChild;
		}
		return node;
	}
	
}
