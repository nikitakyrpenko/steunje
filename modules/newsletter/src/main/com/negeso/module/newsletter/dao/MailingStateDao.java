/*
 * @(#)Id: MailingStateDao.java, 27.03.2008 18:48:44, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.dao;


import com.negeso.framework.dao.GenericDao;
import com.negeso.module.newsletter.bo.MailingState;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface MailingStateDao extends GenericDao<MailingState, Long>{
	
	public MailingState findByName(String name);
	
}
