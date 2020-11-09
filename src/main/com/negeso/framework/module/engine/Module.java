/*
 * @(#)$Id: Module.java,v 1.2, 2007-01-16 11:15:43Z, Svetlana Bondar$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 
 * Module.
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 3$
 *
 */
public class Module {

	private String id;
	private String name;
	
	private Map<String, Extension> extensions = new HashMap<String, Extension>();
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addExtension(Extension extension) {
		// TODO error handling
		extensions.put(extension.getExtensionPointIdentifier(), extension);
	}
	
	public Extension getExtension(String extensionId) {
		// TODO error handling
		return extensions.get(extensionId);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
	
}
