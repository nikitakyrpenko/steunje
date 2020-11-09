/*
 * @(#)$Id: HibernateParameterDao.java,v 1.1, 2007-04-12 08:56:02Z, Alexander Serbin$
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
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.negeso.module.core.ParameterComparator;
import com.negeso.module.core.domain.ConfigurationParameter;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class HibernateParameterDao extends HibernateDaoSupport implements IParameterDao {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(HibernateParameterDao.class); 

	@SuppressWarnings("unchecked")
	public Set getUnlinkedParameters() {
		Set unlinkedParameters = new TreeSet<ConfigurationParameter>(new ParameterComparator());
		unlinkedParameters.addAll(getHibernateTemplate().find(
		"FROM ConfigurationParameter params WHERE params.moduleId is NULL"));
		return unlinkedParameters;
	}

	public void deleteParameter(Long parameterId) {
		ConfigurationParameter parameter = (ConfigurationParameter) getHibernateTemplate()
				.get(ConfigurationParameter.class, parameterId);
		if (parameter != null) {
			getHibernateTemplate().delete(parameter);
		}
	}

	public ConfigurationParameter getParameter(Long parameterId) {
		return (ConfigurationParameter) getHibernateTemplate().get(
				ConfigurationParameter.class, parameterId);
	}

	public ConfigurationParameter load(Long parameterId) {
		return (ConfigurationParameter) getHibernateTemplate().load(
				ConfigurationParameter.class, parameterId);
	}

	public void save(ConfigurationParameter parameter) {
		getHibernateTemplate().save(parameter);
	}

	public void update(ConfigurationParameter parameter) {
		getHibernateTemplate().update(parameter);
	}

	@SuppressWarnings("unchecked")
	public List<ConfigurationParameter> getGlobalParameters() {
		return getHibernateTemplate().find("FROM ConfigurationParameter WHERE siteId IS NULL");
		
	}

	@SuppressWarnings("unchecked")
	public List<ConfigurationParameter> getSiteParameters(Long siteId) {
		String query = "FROM ConfigurationParameter WHERE siteId = :siteId";
        return getHibernateTemplate().findByNamedParam(query, "siteId", siteId);
	}

	public ConfigurationParameter findParameterByName(String parameterName) {
		String query = "FROM ConfigurationParameter WHERE name=:name";
		List resultList = getHibernateTemplate().findByNamedParam(query, "name", parameterName); 
		return (resultList.size() == 0 ? null : (ConfigurationParameter)resultList.get(0));
		
	}

	@Override
	public ConfigurationParameter findParameterByNameAndSiteId(
			String parameterName, Long siteId) {
		String query = "FROM ConfigurationParameter WHERE name=:name and siteId=:siteId";
		List resultList = getHibernateTemplate().findByNamedParam(query, new String[] {"name", "siteId"}, new Object[] {parameterName, siteId}); 
		return (resultList.isEmpty() ? null : (ConfigurationParameter)resultList.get(0));
	}

	@Override
	public void flush() {
		getHibernateTemplate().flush();
	}

	public List<ConfigurationParameter> findParametersByModule(Long moduleId){
		List list = getHibernateTemplate().findByNamedParam("from ConfigurationParameter where moduleId=:moduleId","moduleId", moduleId);

		return (List<ConfigurationParameter>) list;
	}
}

