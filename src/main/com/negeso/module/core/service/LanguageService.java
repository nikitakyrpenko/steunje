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

import com.negeso.framework.domain.Language;
import com.negeso.module.core.dao.LanguageDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class LanguageService {
	
	private LanguageDao languageDao = null;
	
	public List<Language> list() {
		return languageDao.readAll();
	}
	
	public void update(List<Language> list) {
		languageDao.updateAll(list);
		languageDao.flush();
	}

	public LanguageDao getLanguageDao() {
		return languageDao;
	}

	public void setLanguageDao(LanguageDao languageDao) {
		this.languageDao = languageDao;
	}
}

