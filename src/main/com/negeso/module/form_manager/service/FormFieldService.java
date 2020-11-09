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
package com.negeso.module.form_manager.service;

import java.util.HashMap;
import java.util.Map;

import com.negeso.module.form_manager.FormFieldsCache;
import com.negeso.module.form_manager.dao.FormFieldDao;
import com.negeso.module.form_manager.dao.FormsDao;
import com.negeso.module.form_manager.domain.FormField;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FormFieldService {	
	
	private FormFieldDao formFieldDao = null;
	private FormsDao formsDao = null;
	
	public FormField getFormField(String name, Long formId){
		FormFieldsCache cache = FormFieldsCache.getInstance();
		Map<String, FormField> fields = cache.getFormFields(formId);
		FormField formField = null;
		if (fields != null){
			formField = fields.get(name);
			if (formField == null){
				formField = formFieldDao.findByNameAndFormId(name, formId);
				fields.put(name, formField);
			}
		}else{
			fields = new HashMap<String, FormField>();
			formField = formFieldDao.findByNameAndFormId(name, formId);
			fields.put(name, formField);
		}
		return formField;
	}

	public FormFieldDao getFormFieldDao() {
		return formFieldDao;
	}

	public void setFormFieldDao(FormFieldDao formFieldDao) {
		this.formFieldDao = formFieldDao;
	}

	public FormsDao getFormsDao() {
		return formsDao;
	}

	public void setFormsDao(FormsDao formsDao) {
		this.formsDao = formsDao;
	}
}

