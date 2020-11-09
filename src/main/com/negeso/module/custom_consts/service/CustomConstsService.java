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
package com.negeso.module.custom_consts.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.module.core.domain.Reference;
import com.negeso.module.core.service.ModuleService;
import com.negeso.module.custom_consts.dao.ICustomConstsDao;
import com.negeso.module.custom_consts.domain.CustomConst;
import com.negeso.module.custom_consts.domain.CustomTranslation;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class CustomConstsService {

	private ICustomConstsDao customConstsDao;
	private ModuleService moduleService;
	
	public List getConstsByModuleId(Long moduleId) {
		return customConstsDao.getConstsByModuleId(moduleId);
	}

	public List getCommonConsts() {
		return customConstsDao.getCommonConsts();
	}

	public void setCustomConstsDao(ICustomConstsDao dao) {
		this.customConstsDao = dao;
	}

	public CustomConst loadConst(Long id) {
		return customConstsDao.loadConst(id);
	}
	public CustomConst loadConst(String key) {
		return customConstsDao.loadConst(key);
	}
	public void deleteConst(Long constId) {
		customConstsDao.deleteConst(constId);
	}

	public void saveConst(CustomConst customConst) {
		customConstsDao.saveConst(customConst);
		clearModuleConstsCache(customConst.getModuleId());
		
		
	}
	
	private void clearModuleConstsCache(Long moduleId){
		String moduleName;
		if (moduleId == null){
			moduleName = DatabaseResourceBundle.DICT_COMMON_XSL;
		}else{
			moduleName = moduleService.getModuleById(moduleId).getName();
		}
		DatabaseResourceBundle.clearModuleConsts(moduleName);
	}

	public List<Reference> getTranslatedLanguages(CustomConst customConst) {
		List<Reference> translatedLanguages = new ArrayList<Reference>(customConst.getTranslations().size());
		Iterator iterator = customConst.getTranslations().iterator();
		while (iterator.hasNext()) {
			translatedLanguages.add(((CustomTranslation)iterator.next()).getLanguage());
		}
		return translatedLanguages;
	}

	@SuppressWarnings("unchecked")
	public Map<String,String> getTranslations(String moduleName, String language) {
		Long moduleId = null;
		List<Object[]> translationsList = null;
		if (!isCommonConsts(moduleName)) {
			moduleId = moduleService.getModuleByName(moduleName);
			if (moduleId == null) {
				translationsList = Collections.emptyList();
			} else {
				translationsList = (List<Object[]>) customConstsDao
				.getModuleConstsTranslations(moduleId, language);
			}
		} else {
			translationsList = (List<Object[]>) customConstsDao
					.getCommonTranslations(language);
		}
		 
		Map<String, String> translationsLookup = new HashMap<String, String>(
				translationsList.size(), 1);
		for (Object[] translationPair : translationsList) {
			translationsLookup.put(translationPair[0].toString(),
					translationPair[1].toString());
		}
		return translationsLookup; 
	}

	private boolean isCommonConsts(String dictionaryFileName) {
		return dictionaryFileName.equals(DatabaseResourceBundle.DICT_COMMON_XSL);
	}

	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

}
