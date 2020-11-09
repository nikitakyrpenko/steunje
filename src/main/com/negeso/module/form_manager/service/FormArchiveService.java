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

import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.module.form_manager.dao.FormArchiveDao;
import com.negeso.module.form_manager.domain.FormArchive;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FormArchiveService {
	
	private static final Logger logger = Logger.getLogger(FormArchiveService.class);
	
	FormArchiveDao formArchiveDao;
	
	public List<FormArchive> listFormArchives(Long formId){
		return formArchiveDao.listArchivesByFormId(formId);
	}

	@SuppressWarnings("unchecked")
	public List<FormArchive> listFormArchives(Long formId, int pageNumber, int recordsPerPage){

		Criteria criteria = formArchiveDao.getCriteria();
		criteria.add(Restrictions.eq("formId", formId));
		criteria.setFirstResult((pageNumber - 1) * recordsPerPage);
		criteria.setMaxResults(recordsPerPage);

		return criteria.list();
	}

	public FormArchiveDao getFormArchiveDao(){
		return formArchiveDao;
	}

	public void setFormArchiveDao(FormArchiveDao formArchiveDao){
		this.formArchiveDao = formArchiveDao;
	}
}

