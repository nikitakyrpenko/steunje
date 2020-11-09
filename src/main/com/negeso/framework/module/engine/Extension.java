/*
 * @(#)$Id: Extension.java,v 1.2, 2007-01-16 11:15:43Z, Svetlana Bondar$
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
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 
 * An extension declared in module.
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 3$
 *
 */
public class Extension {

	private String epid;

	private List<ConfigurationElement> configurationElements =
		new ArrayList<ConfigurationElement>();
	
	public void setExtensionPointIdentifier(String epid) {
		this.epid = epid;
	}

	public String getExtensionPointIdentifier() {
		return epid;
	}

	public void addConfigurationElement(ConfigurationElement ce) {
		// TODO error handling
		configurationElements.add(ce);
	}
	
	public ConfigurationElement[] getConfigurationElements() {
		return (ConfigurationElement[]) configurationElements.toArray(
				new ConfigurationElement[configurationElements.size()]);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
}
