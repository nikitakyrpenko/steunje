/*
 * @(#)Id: StatisticsDao.java, 27.02.2008 18:40:33, Dmitry Fedotov
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
import com.negeso.module.newsletter.bo.Mailing;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface StatisticsDao extends GenericDao<Mailing, Long> {

	public List<Mailing> listSubscribersInRange(Long publicationId, Long statusId, int limit, int offset);
	public int countSubscribersByPublicationId(Long publicationId, Long statusId);
	
}
