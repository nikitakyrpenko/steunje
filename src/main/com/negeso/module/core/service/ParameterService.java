/*
 * @(#)$Id: ParameterService.java,v 1.1, 2007-04-12 08:57:11Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.service;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.negeso.framework.cache.CacheFacade;
import com.negeso.framework.module.Module;
import com.negeso.module.core.dao.IParameterDao;
import com.negeso.module.core.domain.ConfigurationParameter;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class ParameterService {

	private static Logger logger = Logger.getLogger(ParameterService.class);
	
	private IParameterDao dao;
	
	public ConfigurationParameter load(Long parameterId) {
		return dao.load(parameterId);
	}
	
	public IParameterDao getDao() {
		return dao;
	}

	public void setDao(IParameterDao dao) {
		this.dao = dao;
	}

	public Module getUnlinkedModule() {
		Module module = new Module();
		module.setName("unlinked");
		module.setConfigurationParameters(dao.getUnlinkedParameters());
		return module;
	}

	public void deleteParameter(Long parameterId) {
		dao.deleteParameter(parameterId);
		resetCache();
	}

	private void resetCache() {
		logger.debug("+-");
			try {
				CacheFacade.getInstance().cleanUpCache();
			} catch (Throwable e) {
				logger.error("Can't clean cache - ", e);
			}
	}
	private void resetCacheParametrs() {
		logger.debug("+-");
			try {
				CacheFacade.getInstance().cleanUpParametersCache();
			} catch (Throwable e) {
				logger.error("Can't clean cache - ", e);
			}
	}
	public void save(ConfigurationParameter parameter) {
		logger.error("+-");
		dao.save(parameter);
		resetCache();
	}
	
	public void save(ConfigurationParameter parameter, boolean resetCache) {
		logger.error("+-");
		dao.save(parameter);
		if(resetCache) {
			resetCache();
		}
	}

	public void update(ConfigurationParameter parameter, boolean resetCache) {
		dao.update(parameter);
		dao.flush();
		if(resetCache) {
			resetCache();
		}
	}
	
	public Properties getGlobalParameters() {
		return getPropertiesFromList(dao.getGlobalParameters());
	}
	
	public Properties getSiteParameters(Long siteId) {
		return getPropertiesFromList(dao.getSiteParameters(siteId));
	}
	
	private Properties getPropertiesFromList(List<ConfigurationParameter> parameters) {
		Properties properties = new Properties();
		for (ConfigurationParameter parameter: parameters) {
			if(parameter.getName()==null || parameter.getValue()==null) {
				logger.info("parameter name or value is null: " + ReflectionToStringBuilder.toString(parameter));
			}
            properties.put(parameter.getName()==null?"null":parameter.getName(), parameter.getValue()==null?"null":parameter.getValue());
		}
		return properties;
	}

	public ConfigurationParameter findParameterByName(String parameterName) {
		return dao.findParameterByName(parameterName);
	}

	public void update(List<ConfigurationParameter> list) {
		for (ConfigurationParameter parameter: list) {
			dao.update(parameter);
		}
		resetCacheParametrs();
	}

	public List<ConfigurationParameter> findParametersByModule(Long moduleId){
		return dao.findParametersByModule(moduleId);
	}

	public ConfigurationParameter findParameterByNameAndSiteId(String name, Long siteId, String defaultValue) {
		ConfigurationParameter param = dao.findParameterByNameAndSiteId(name, siteId);
		if (param == null) {
			param = new ConfigurationParameter();
			param.setName(name);
			param.setValue(defaultValue);
			param.setSiteId(siteId);
			param.setReadonly(true);
			param.setReadonly(false);
			param.setResetCache(true);
			param.setRequired(true);
			dao.save(param);
			dao.flush();
		}
		return param;
	}
	
}

