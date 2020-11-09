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
import com.negeso.module.form_manager.domain.FormArchive;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public interface FormArchiveDao extends GenericDao<FormArchive, Long>{
	List<FormArchive> listArchivesByFormId(Long formId);
	int countRecords(Long formId);
}

