/*
 * @(#)Id: NewsletterCategoryDao.java, 21.02.2008 17:41:08, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.newsletter.bo.SubscriptionCategory;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface SubscriptionCategoryDao extends GenericDao<SubscriptionCategory, Long> {

	SubscriptionCategory findByTitle(String title);
	List<SubscriptionCategory> listAll();
	int countAll();
	
}
