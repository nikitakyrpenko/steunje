/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.dao;

import java.sql.Timestamp;
import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.newsletter.bo.Mailing;
import com.negeso.module.newsletter.bo.MailingState;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public interface MailingDao extends GenericDao<Mailing, Long>{

	public MailingState findMailingStateByName(String name);
	public List<Mailing> listMailingsByUniqId(Long uniqId);
	public int countMailByPeriod(Long publicationId, 
			Long publicationState, Timestamp fromDate, Timestamp toDate);
	
}
