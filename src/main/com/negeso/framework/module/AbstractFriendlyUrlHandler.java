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
package com.negeso.framework.module;

import java.util.Map;

import com.negeso.framework.controller.CommandMapping;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public abstract class AbstractFriendlyUrlHandler {
	
	public abstract Map<String, CommandMapping> getUrlMatchers();
	
	protected static final String HTML_VIEW = "html-view";
	protected static final String XML_VIEW = "xml-view";
	protected static final String SITE_XSL = "SITE_XSL";
	protected static final String LOGIN_XSL = "LOGIN_XSL";
	public static final String ADMIN = "/admin/";
}

