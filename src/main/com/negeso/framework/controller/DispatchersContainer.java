/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class DispatchersContainer {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(DispatchersContainer.class);

	private static DispatchersContainer instance;
	
	private  Map<String, ModuleEngineDispatcherServlet> moduleDispatchers =
	    	new HashMap<String, ModuleEngineDispatcherServlet>();
	
	public static synchronized DispatchersContainer getInstance() {
		if (instance == null) {
			instance = new DispatchersContainer();
		}
		return instance;
	}

	Map<String, ModuleEngineDispatcherServlet> getModuleDispatchers() {
		return moduleDispatchers;
	}

	void destroyModuleDispatchers() {
    	for (String moduleId : getModuleDispatchers().keySet()) {
    		ModuleEngineDispatcherServlet dispatcher = getModuleDispatchers().get(moduleId);
    		dispatcher.destroy();
    	}
	}
	
	public Object getBean(String module, String beanName) {
		return moduleDispatchers.get(module).getWebApplicationContext().getBean(beanName); 	
	}
	
}