/*
 * @(#)$Id: IParameterDao.java,v 1.1, 2007-04-12 08:56:12Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.dao;

import java.util.List;
import java.util.Set;

import com.negeso.module.core.domain.ConfigurationParameter;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public interface IParameterDao {

	Set getUnlinkedParameters();
	void deleteParameter(Long parameterId);
	ConfigurationParameter getParameter(Long parameterId);
	ConfigurationParameter load(Long parameterId);
	void save(ConfigurationParameter parameter);
	void update(ConfigurationParameter parameter);
	List<ConfigurationParameter> getGlobalParameters();
	List<ConfigurationParameter> getSiteParameters(Long siteId);
	ConfigurationParameter findParameterByName(String parameterName);
	ConfigurationParameter findParameterByNameAndSiteId(String parameterName, Long siteId);
	void flush();
	List<ConfigurationParameter> findParametersByModule(Long moduleId);
}

