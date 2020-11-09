/*
 * @(#)$Id: TestPageComponentParameters.java,v 1.0, 2007-01-10 13:29:34Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.XmlHelper;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
public class TestPageComponentParameters {

	private Document document;
	private RequestContext requestContext;
	private Map<String, Object> parameters;

	public TestPageComponentParameters(HttpServletRequest request,
			HttpServletResponse response, ServletConfig config) {
		// instantiate a dom document
		document = XmlHelper.newDocument();
		
		// instantiate a request context
		requestContext = new TestRequestContext(request, response, config);
		
		// instantiate a map
		parameters = new HashMap<String, Object>();
	}
	
	public Document getDocument() {
		return document;
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public RequestContext getRequestContext() {
		return requestContext;
	}
	
	public String getlangCode() {
		return requestContext.getSession().getLanguageCode();
	}
	
}
