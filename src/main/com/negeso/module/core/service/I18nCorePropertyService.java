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
package com.negeso.module.core.service;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Language;
import com.negeso.module.core.dao.I18nCorePropertyDao;
import com.negeso.module.core.domain.I18nCoreProperty;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class I18nCorePropertyService {
	
	private I18nCorePropertyDao i18nCorePropertyDao = null;
	
	public I18nCoreProperty findByModuleId(Long moduleId, Long siteId) {
		List<I18nCoreProperty> list = i18nCorePropertyDao.readByCriteria(Restrictions.eq("moduleId", moduleId), Restrictions.eq("siteId", siteId));
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public I18nCoreProperty findByName(String name, Long siteId) {
		List<I18nCoreProperty> list = i18nCorePropertyDao.readByCriteria(Restrictions.eq("name", name), Restrictions.eq("siteId", siteId));
		I18nCoreProperty property = null;
		if (list.size() > 0) {
			property = list.get(0);
		} else {
				property = new I18nCoreProperty();
				property.setName(name);
				property.setSiteId(Env.getSiteId());
				createOrUpdate(property);
		}
		return property;
	}
	
	public void setBooleanValues(String name, Long siteId, long[] selectedLangIds) {
		I18nCoreProperty property = findByName(name, siteId);
		for (Language language : Language.getItems()) {
			property.setCurrentLangId(language.getId());
			property.setValue(Boolean.FALSE.toString());
		}
		for (long langId : selectedLangIds) {
			property.getField(langId).setValue(Boolean.TRUE.toString());
		}
		i18nCorePropertyDao.createOrUpdate(property);
	}
	
	public void createOrUpdate(I18nCoreProperty i18nCoreProperty) {
		i18nCorePropertyDao.createOrUpdate(i18nCoreProperty);
		i18nCorePropertyDao.flush();
	}

	public I18nCorePropertyDao getI18nCorePropertyDao() {
		return i18nCorePropertyDao;
	}

	public void setI18nCorePropertyDao(I18nCorePropertyDao i18nCorePropertyDao) {
		this.i18nCorePropertyDao = i18nCorePropertyDao;
	}
}

