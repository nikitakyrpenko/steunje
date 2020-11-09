/*
 * @(#)Id: GroupDao.java, 25.02.2008 13:31:26, Dmitry Fedotov
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
import com.negeso.module.newsletter.bo.SubscriberGroup;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface SubscriberGroupDao extends GenericDao<SubscriberGroup, Long> {

	public List<SubscriberGroup> listOrderedGroup();
	public SubscriberGroup findByTitle(String title, Long langId);
	public Integer countNextOrderNumber();
	public int countSubscriberGroup();
	
}
