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
package com.negeso.module.core.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.negeso.module.core.domain.Reference;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class HibernateReferenceDao extends HibernateDaoSupport implements IReferenceDao {

	@SuppressWarnings("unchecked")
	public List<Reference> getAll(Long type) {
		return getHibernateTemplate().find("FROM Reference WHERE referenceType = " + type);
	}

}

