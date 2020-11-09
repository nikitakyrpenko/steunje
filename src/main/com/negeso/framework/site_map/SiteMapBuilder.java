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
package com.negeso.framework.site_map;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SiteMapBuilder {
	
	private Map<String, PagesHandler> handlers = new HashMap<String, PagesHandler>();
	
	private static SiteMapBuilder instance;
	
	public static synchronized SiteMapBuilder getInstance() {
		if (instance == null) {
			instance = new SiteMapBuilder();
		}
		return instance; 
	}

	/**
	 * @return the handlers
	 */
	public Map<String, PagesHandler> getHandlers() {
		return handlers;
	}

	/**
	 * @param handlers the handlers to set
	 */
	public void setHandlers(Map<String, PagesHandler> handlers) {
		this.handlers = handlers;
	}
}

