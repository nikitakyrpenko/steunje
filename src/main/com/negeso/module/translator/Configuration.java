/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.translator;

import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.translator.service.ITranslateService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.TransactionException;

import java.util.Map;


/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class Configuration {
	private static final String STM_TRANSLATE_PROVIDER = "stmTranslateProvider";
	public static final String NEGESO_GOOGLE_API_KEY = "googleTranslateAPIKey";
	public static final String NEGESO_GOOGLE_API_KEY_VALUE = "AIzaSyC0UF9XNyfbh65uZkrp6Q1qhaMq1qRi8O4";
	public static final String STM_CHARS_QUOTA = "stmCharsQuota";
	
	private Map<String, String> configuration ;
	
	private static Configuration instance;
	
	public static synchronized Configuration get(){
		if ( Configuration.instance == null ){
			Configuration.instance = (Configuration) DispatchersContainer.getInstance().getBean("site_translation_module", "stmConfiguration");
		}
	    return Configuration.instance;
	}

	@SuppressWarnings("rawtypes")
	public ITranslateService getTranslateService() {
		String stmTranslateProvider = Env.getNotCachedSiteProperty(STM_TRANSLATE_PROVIDER, null);
		if (StringUtils.isBlank(stmTranslateProvider)) {
			throw new TransactionException(String.format("Parameter %s has not been set in the workshop", STM_TRANSLATE_PROVIDER));
		}
		String className = configuration.get(stmTranslateProvider);
		if (className == null) {
			throw new TransactionException(String.format("Class was not set for the provider %s, check spring configs!", stmTranslateProvider));
		}
		try {
			Class class_ = Class.forName(className);
			return (ITranslateService)class_.newInstance();
		} catch (Exception e) {
			throw new CriticalException(e);
		}
	}

	public Map<String, String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}
}

