/*
 * @(#)$Id: $
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

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.negeso.framework.module.Module;
import com.negeso.framework.module.domain.ModuleItem;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class HibernateModuleDao extends HibernateDaoSupport implements IModuleDao {

	private static Logger logger = Logger.getLogger(HibernateModuleDao.class);
	
	public Module getModuleById(Long moduleId) {
		Module module = (Module) getHibernateTemplate().load(Module.class, moduleId);
		return module;
	}

	@SuppressWarnings("unchecked")
	public List getAllModules() {
		logger.debug("+-");
		return getHibernateTemplate().find("from Module");
	}

	@SuppressWarnings("unchecked")
	public Long getModuleByName(String moduleName) {
		String query = "from Module module WHERE module.name = :moduleName";
		List<Module> resultList = (List<Module>) getHibernateTemplate()
				.findByNamedParam(query, "moduleName", moduleName);
		if (resultList.size() > 0) {
			return resultList.get(0).getId(); 
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Module> readAll() {
		Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Module.class);
		criteria.addOrder(Order.asc("orderNumber"));
		return (List<Module>)criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getHelpLink(String moduleLink){
		String query = "from Module module WHERE module.url = '" + moduleLink + "'";		
		List<Module> resultList = (List<Module>) getHibernateTemplate().find(query);
		if (resultList.size() > 0) {
			return resultList.get(0).getHelpUrl(); 
		} else {
			query = "from ModuleItem moduleItem WHERE moduleItem.url like '%" + moduleLink + "%'";
			List<ModuleItem> resultItemList = (List<ModuleItem>) getHibernateTemplate().find(query);
			if (resultItemList.size() > 0) {
				return resultItemList.get(0).getHelpUrl();
			}
		}	
		return null;
	}

}

