/*
 * @(#)$Id: AbstractModule.java,v 1.0, 2007-01-31 11:13:35Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine;

import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
public abstract class AbstractModule {

	public void startup() {
		for (Module module : ModuleEngine.getInstance().getModules()) {
			try {
				Extension extension = module.getExtension(getExtensionPoint());
				if (extension != null) {
					for (ConfigurationElement ce : extension.getConfigurationElements()) {
						handleConfigurationElement(module.getId(), ce);
					}
				}
			} catch (Exception e) {
				getLogger().error("Error in module '" + module.getName() + "'", e);
			}
		}
	}

	protected abstract void handleConfigurationElement(String moduleId, ConfigurationElement element) throws ModuleException;
	
	protected abstract String getExtensionPoint();

	protected abstract Logger getLogger();
}
