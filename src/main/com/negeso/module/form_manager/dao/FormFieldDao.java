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
package com.negeso.module.form_manager.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.form_manager.domain.FormField;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public interface FormFieldDao extends GenericDao<FormField, Long> {
	
	public FormField findByNameAndFormId(String name, Long formId);

	public List<FormField> listFormFields(Long formId);
}
