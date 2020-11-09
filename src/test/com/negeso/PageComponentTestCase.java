/*
 * @(#)Id: PageComponentTestCase.java, 19.11.2007 14:25:09, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.page.PageComponent;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class PageComponentTestCase extends CommandTestCase {

	private Document document;
	private Map<String, Object> parameters;
	
	public PageComponentTestCase() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		parameters = new HashMap<String, Object>();
		document = XmlHelper.newDocument();
	}
	public Element getElement(PageComponent pageComponent) {
		return pageComponent.getElement(document, requestContext, parameters);
	}
	
	public String getlangCode() {
		return requestContext.getSession().getLanguageCode();
	}
	
	public Map<String, Object> getParameters(){
		return parameters;
	}

}
