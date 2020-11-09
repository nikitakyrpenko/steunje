/*
 * @(#)Id: PublicationStateDao.java, 17.03.2008 19:15:28, Dmitry Fedotov
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
import com.negeso.module.newsletter.bo.PublicationState;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface PublicationStateDao extends GenericDao<PublicationState, Long>{
	
	public PublicationState findPublicationStateByName(String n);	
}
