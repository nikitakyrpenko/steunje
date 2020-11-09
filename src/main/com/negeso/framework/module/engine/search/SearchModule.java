/*
 * @(#)$Id: SearchModule.java,v 1.0, 2007-01-31 11:13:36Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.module.engine.ConfigurationElement;
import com.negeso.framework.module.engine.AbstractModule;
import com.negeso.framework.module.engine.ModuleException;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
public class SearchModule extends AbstractModule {
	
    private static Logger logger = Logger.getLogger(SearchModule.class);
	
	private static final String EXTENSION_POINT = "com.negeso.search"; //$NON-NLS-1$
	
    private static final String ELEMENT_TAG = "element"; //$NON-NLS-1$
	public static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
	
	private static SearchModule instance;

	private List<ISearchable> searchList = new ArrayList<ISearchable>();
	
	private SearchModule() {
		startup();
	}
	
	public synchronized static SearchModule getModule() {
		if (instance == null) {
			instance = new SearchModule();
		}
		return instance;
	}
	
	public void doAction() {
		// TODO implement
		for (ISearchable searchable : searchList) {
			logger.error("########## Iterator = " + searchable.getSearchedIndexes());
		}
	}

	@Override
	protected void handleConfigurationElement(String moduleId,
			ConfigurationElement element) throws ModuleException {
		if (ELEMENT_TAG.equals(element.getName())) {
			Object callback = element.createExecutableExtension(CLASS_ATTRIBUTE);
			if (!(callback instanceof ISearchable)) {
				throw new ModuleException("callback class '"
						+ callback.getClass().getName()
						+ "' is not an ISearchable");
			}
			ISearchable searchable = (ISearchable) callback;
			searchList.add(searchable);
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
