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
package com.negeso.module.custom_consts.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.negeso.module.custom_consts.domain.CustomConst;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class HibernateCustomConstsDao extends HibernateDaoSupport implements ICustomConstsDao {

	private static Logger logger = Logger.getLogger(HibernateCustomConstsDao.class);
	
	public List getCommonConsts() {
		logger.debug("+-");
		return getHibernateTemplate().find("FROM CustomConst const WHERE const.moduleId is NULL");
	}

	public List getConstsByModuleId(Long moduleId) {
		logger.debug("+");
		String queryString = "FROM CustomConst const WHERE const.moduleId = "+moduleId;
		logger.debug("-");
		return getHibernateTemplate().find(queryString);
	}

	public void deleteConst(Long constId) {
		CustomConst customConst = (CustomConst)getHibernateTemplate().get(CustomConst.class, constId);
		if (customConst != null) {
			getHibernateTemplate().delete(customConst);
		}	
	}

	public void saveConst(CustomConst customConst) {
		getHibernateTemplate().saveOrUpdate(customConst);
		getHibernateTemplate().flush();
	}

	public CustomConst loadConst(Long id) {
		return (CustomConst) getHibernateTemplate().load(CustomConst.class, id);
	}

	public CustomConst loadConst(String key) {
		List list = getHibernateTemplate().find("FROM CustomConst const WHERE const.key= '"+key+"'");		
		if(list.isEmpty())
			return null;			
		else 
			return (CustomConst)list.get(0);			
	}

	@SuppressWarnings("unchecked")
	public List getCommonTranslations(String langCode) {
		String query = " SELECT const.key, translation.translation " +  
		   " FROM CustomTranslation translation " + 
		   " LEFT JOIN translation.customConst const " + 
		   " WHERE const.moduleId IS NULL " + 
		   " AND translation.language.code = '"+langCode+"'";
	return getHibernateTemplate().find(query);	
	}

	public List getModuleConstsTranslations(Long moduleId, String langCode) {
		String query = " SELECT const.key, translation.translation " +  
		" FROM CustomTranslation translation " + 
		" LEFT JOIN translation.customConst const " + 
		" WHERE const.moduleId = " + String.valueOf(moduleId) + "" + 
		" AND translation.language.code = '"+ langCode + "'";
	 
		return getHibernateTemplate().find(query);		
	}

}

