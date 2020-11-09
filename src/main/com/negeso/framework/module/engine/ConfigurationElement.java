/*
 * @(#)$Id: ConfigurationElement.java,v 1.4, 2007-01-31 11:19:56Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 
 * Configuration element that presents a parsed version
 * of an extension element.
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 5$
 *
 */
public class ConfigurationElement {

	private String name;
	private String value;
	private Map<String, String> attributes = new HashMap<String, String>();
	private List<ConfigurationElement> children = new LinkedList<ConfigurationElement>();

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getAttribute(String name) {
		return attributes.get(name);
	}
	
	public String[] getAttributeNames() {
		String[] atts = new String[attributes.size()];
		return (String[]) attributes.keySet().toArray(atts);
	}

	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	public void addChild(ConfigurationElement ce) {
		children.add(ce);
	}
	
	public List<ConfigurationElement> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	public Object createExecutableExtension(String classAttributeName) throws ModuleException {
		String className = getAttribute(classAttributeName);
		Class classInstance = null;
		try {
			classInstance = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ModuleException("Class '" + className + "' not found");
		}
		Object result = null;
		try {
			result = classInstance.newInstance();
		} catch (Exception e) {
			throw new ModuleException("Class '" + "' not instantiated");
		}
		return result;
	}

}
