/*
 * @(#)$Id: CacheFacade.java,v 1.1, 2007-04-12 08:55:40Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.CommandFactory;
import com.negeso.framework.page.ComponentManager;
import com.negeso.framework.security.SecurityCache;
import com.negeso.framework.site.SiteEngine;
import com.negeso.module.dictionary.generators.DictionaryFileBuilder;
import com.negeso.module.job.JobModule;
import com.negeso.module.media_catalog.Repository;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class CacheFacade {
	
	private static Logger logger = Logger.getLogger(CacheFacade.class);
	
	private static CacheFacade instance;
	
	private List<Cachable> cachableList = new ArrayList<Cachable>();
	
	public static synchronized CacheFacade getInstance() {
		if (instance == null) {
			instance = new CacheFacade();
		}
		return instance; 
	}
	
	//TODO als add to cachableList  
	public void cleanupCoreCache() {
		SiteEngine.get().reset();
		ResourceMap.reloadCache();
		SecurityCache.invalidate();
//		CommandFactory.reloadCache();
		ComponentManager.get().cleanCache();
		DictionaryFileBuilder.getInstance().generate(true);
	}
	
	//TODO als add to cachableList
	public void cleanupModulesCache() {
		//ProductModule.get().getCache().clearAll();
		JobModule.get().clearCache();
		Repository.get().clearCache();
	}
	
	//TODO als add to cachableList
	public void cleanUpCache() {
		logger.debug("+-");
		cleanupCoreCache();
		cleanupModulesCache();
		cleanUpParametersCache();
		
		for (Cachable cachable: cachableList) {
			cachable.resetCache();
		}
	}
	
	//TODO als add to cachableList
	public void cleanUpParametersCache() {
		resetGlobalProperties();
		resetSiteProperties();
	}

	//TODO als add to cachableList
	private void resetSiteProperties() {
		Env.getSite().setProperties(null);
	}

	//TODO als add to cachableList
	private void resetGlobalProperties() {
		Env.setProperties(null);
	}

	public List<Cachable> getCachableList() {
		return cachableList;
	}

	
	
}

