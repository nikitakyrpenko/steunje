/*
 * @(#)$Id: RuntimeModule.java,v 1.1, 2007-01-31 12:40:54Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 2$
 *
 */
public class RuntimeModule extends AbstractModule {

    private static Logger logger = Logger.getLogger(RuntimeModule.class);
	
    public static final String EXTENSION_POINT = "core"; //$NON-NLS-1$

    public static final String MAPPING_URL_TAG = "mapping-url"; //$NON-NLS-1$
    public static final String PAGE_COMPONENTS_TAG = "page-components"; //$NON-NLS-1$
    public static final String PAGE_COMPONENT_TAG = "page-component"; //$NON-NLS-1$
	
	private static RuntimeModule instance;

	private Map<String, List<Pattern>> mappingUrlPatterns = new HashMap<String, List<Pattern>>();  
	private Map<String, String> componentToClassMap = new HashMap<String, String>();  
	
	private RuntimeModule() {
		startup();
	}
	
	public synchronized static RuntimeModule getModule() {
		if (instance == null) {
			instance = new RuntimeModule(); 
		}
		return instance;
	}
	
	/**
	 * Returns page components.
	 * @return the list of page components.
	 */
	public Map<String, String> getPageComponents() {
		return Collections.unmodifiableMap(componentToClassMap);
	}
	
	/**
	 * Verifies the mapping url for passed URI and returns a module
	 * identifier if matched.
	 * @param uri the URI from request.
	 * @return the module identifier or null if passed URI cannot
	 * be passed here.
	 */
	public String obtainModuleId(String uri) {
		logger.debug("+uri=" + uri);
		for (String moduleId : mappingUrlPatterns.keySet()) {
			List<Pattern> patterns = mappingUrlPatterns.get(moduleId);
			for (Pattern pattern : patterns) {
				if (pattern.matcher(uri).matches()) {
					logger.debug("- matched");
					return moduleId;
				}
			}
		}
		logger.debug("- not matched");
		return null;
	}

	@Override
	protected void handleConfigurationElement(String moduleId, ConfigurationElement element) {
		if (MAPPING_URL_TAG.equals(element.getName())) {
			List<Pattern> patterns = mappingUrlPatterns.get(moduleId);
			if (patterns == null) {
				patterns = new ArrayList<Pattern>();
				mappingUrlPatterns.put(moduleId, patterns);
			}
			patterns.add(Pattern.compile(element.getValue()));
		} else if (PAGE_COMPONENTS_TAG.equals(element.getName())) {
			List<ConfigurationElement> children = element.getChildren();
			for (ConfigurationElement child : children) {
				if (PAGE_COMPONENT_TAG.equals(child.getName())) {
					componentToClassMap.put(child.getAttribute("name"), child.getAttribute("class"));
				}
			}
		}
	}

	@Override
	protected String getExtensionPoint() {
		return EXTENSION_POINT;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	
}
