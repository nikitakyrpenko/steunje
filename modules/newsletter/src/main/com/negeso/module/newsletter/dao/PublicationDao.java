/*
 * @(#)Id: NewsletterPublicationDao.java, 21.02.2008 18:22:04, Dmitry Fedotov
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
import com.negeso.module.newsletter.bo.Publication;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface PublicationDao extends GenericDao<Publication, Long> {

	List<Publication> listBySubscriptionCategory(Long subscriptionCategoryId);
	Publication findByTitle(String title, Long langId);
	List<Publication> listPublication(Long stateId);
	List<Publication> listSheduled();
}
