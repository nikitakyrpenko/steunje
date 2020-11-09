/*
 * @(#)Id: SubscriberDao.java, 25.02.2008 17:50:35, Dmitry Fedotov
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
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.service.SubscriberSearchInfo;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface SubscriberDao extends GenericDao<Subscriber, Long> {
	
	List<SubscriberAttributeType> listRequiredSubscriberAttributesTypes();
	List<Subscriber> listSubscribersByGroupId(Long groupId);
	List<Subscriber> listSubscribersByGroupIdAndQuery(Long groupId, String query);
	List<Subscriber> listSubscribersByQuery(String query);
    List<Subscriber> listSubscribersWithNoGroup();
    List<Subscriber> listSubscriptionRequests();
	List<Subscriber> listActivatedSubscribers();
	List<Subscriber> listByEMail(String eMail);
	int countSubscribers();
	int getSubscribersAmount(SubscriberSearchInfo searchInfo);
	List<Subscriber> getSubscribers(SubscriberSearchInfo searchInfo,
			int currentPid, int recordsPerPage);
}
