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
package com.negeso.module.form_manager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.negeso.module.form_manager.domain.FormField;
import com.negeso.module.form_manager.domain.Forms;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FormAnalyzer {
	
	private static final Logger logger = Logger.getLogger(FormAnalyzer.class);
	
	private static List<String> formFields = new ArrayList<String>();
	private static List<String> allowedInputTypes = new ArrayList<String>();
	private static final String BEGIN_ROOT_ELEMENT = "<ROOT>";
	private static final String END_ROOT_ELEMENT = "</ROOT>";
	private int orderNumber = 0;
	private Forms form = null;
		
	static{
		formFields.add("INPUT");
		formFields.add("SELECT");
		formFields.add("TEXTAREA");
		allowedInputTypes.add("checkbox");
		allowedInputTypes.add("file");
		allowedInputTypes.add("hidden");
		allowedInputTypes.add("password");
		allowedInputTypes.add("text");
		allowedInputTypes.add("radio");
	}
	
	public void parseFormFields (String formText) {
		InputStream is = null;
		try {
			String str = BEGIN_ROOT_ELEMENT + formText.replace("&", "&amp;") + END_ROOT_ELEMENT;
			byte[] bytes = str.getBytes("UTF-8");
			is =  new ByteArrayInputStream(bytes);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			Document doc = documentBuilderFactory.newDocumentBuilder().parse(is);
			processNodes(doc.getChildNodes());
		} catch (Exception e) {
			logger.error("Cannot parse form", e);
		}finally{
			IOUtils.closeQuietly(is);
		}		
	}
	
	private void processNodes(NodeList list){
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element)list.item(i);
				String tagName = element.getTagName();
				String elementAttrName = element.getAttribute("name");
				if (formFields.contains(tagName.toUpperCase()) && !"".equals(elementAttrName)){
					if ("INPUT".equals(tagName.toUpperCase())) {
						if (allowedInputTypes.contains(element.getAttribute("type"))){
							addField(elementAttrName);
						}
					}else{
						addField(elementAttrName);
					}
				}
				if (element.getChildNodes().getLength() > 0){
					processNodes(element.getChildNodes());
				}
			}			
		}
	}
	
	private void addField (String fieldName){
		orderNumber++;
		for (FormField field : form.getFields()) {
			if (field.getName().equals(fieldName)){
				field.setOrderNumber(orderNumber);
				return;
			}
		}
		FormField newFormField = new FormField();
		newFormField.setName(fieldName);
		newFormField.setOrderNumber(orderNumber);
		newFormField.setFormId(form.getId());
		form.getFields().add(newFormField);
	}
	
	public void setForm(Forms form) {
		this.form = form;
	}
}