/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.form_manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.negeso.framework.cache.Cachable;
import com.negeso.framework.cache.CacheFacade;
import com.negeso.module.form_manager.domain.FormField;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FormFieldsCache implements Cachable{

	private static FormFieldsCache instance;
	
	private Map<Long, Map<String, FormField>> formFieldsMap = new HashMap<Long, Map<String,FormField>>();
	
	public static synchronized FormFieldsCache getInstance() {
		if (instance == null) {
				instance = new FormFieldsCache();
				List<Cachable> cachableList = CacheFacade.getInstance().getCachableList();  
				synchronized (cachableList) {
					cachableList.add(instance);
				}	
		}
		return instance;
	}
	
	public Map<String, FormField> getFormFields(Long formId){
		return formFieldsMap.get(formId);
	}
	
	public void setFormFields(Long formId, Map<String, FormField> fields){
		formFieldsMap.put(formId, fields);
	}
	
	public void resetCache() {
		if (instance != null) 
		synchronized (instance) {
			instance = null;
		}
	}

}

