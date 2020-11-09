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
package com.negeso.module.core.service;

import java.util.List;

import com.negeso.module.core.dao.IReferenceDao;
import com.negeso.module.core.domain.Reference;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class ReferenceService {
	
	private IReferenceDao dao;
	
	public List<Reference> getAllLanguages() {
		return dao.getAll(IReferenceDao.TYPE_LANGUAGE);
	}

	public void setDao(IReferenceDao dao) {
		this.dao = dao;
	}

}
