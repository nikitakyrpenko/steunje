/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import com.negeso.framework.dao.impl.GenericDaoHibernateImpl;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.dao.SubscriberDao;
import com.negeso.module.newsletter.service.SubscriberSearchInfo;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SubscriberDaoHibernateImpl extends GenericDaoHibernateImpl<Subscriber, Long> implements SubscriberDao{
	
	private static final Logger logger = Logger.getLogger(SubscriberDaoHibernateImpl.class);
	
	private static final String SQL_FIND_SORTED_SUBSCRIBERS = "select subscriber.id, attribute_value.value, attribute_value_email.value " +
			"from nl_subscriber subscriber " +
			"left outer join nl_subscriber_attribute_value attribute_value " +
			"on subscriber.id=attribute_value.subscriber_id and attribute_value.attribute_type_id=? " +
			"left outer join nl_subscriber_attribute_value attribute_value_email " +
			"on subscriber.id=attribute_value_email.subscriber_id and attribute_value_email.attribute_type_id=2 " +
			"where subscriber.id in (%s) " +
			"group by subscriber.id, attribute_value.value, attribute_value_email.value " +
			"order by attribute_value.value asc, attribute_value_email.value asc limit ? offset ?";
	
	public SubscriberDaoHibernateImpl() {
		super(Subscriber.class);
	}
	
	private Criteria getCriteria(SubscriberSearchInfo searchInfo){
		Criteria c = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Subscriber.class);
		c.add(Restrictions.eq("activated", searchInfo.isActivated()));
		if (searchInfo.getGroupId() != null){
			if (searchInfo.getGroupId() != 0L ){
            	c.createAlias("subscriberGroups", "groups").add(Restrictions.eq("groups.id", searchInfo.getGroupId()));
            } else 
            	c.add(Property.forName("subscriberGroups").isEmpty());
            }	
		if (StringUtils.hasText(searchInfo.getQuery())){
        	c.createAlias("attributes", "attr").add(Restrictions.ilike("attr.value", searchInfo.getQuery(), MatchMode.ANYWHERE));
        }
		return c;
	}
	
	public int getSubscribersAmount (SubscriberSearchInfo searchInfo){
		return getCriteria(searchInfo).
					setProjection(Projections.distinct(Projections.projectionList().
							add(Projections.property("id"), "id"))).
					list().size();
	}

	@SuppressWarnings("unchecked")
	public List<Subscriber> getSubscribers(SubscriberSearchInfo searchInfo,
			int currentPid, int recordsPerPage) {
		List listIds = null;
		if (searchInfo.getAttributeTypeId() != null && searchInfo.getAttributeTypeId() > 0 && 
				StringUtils.hasText(searchInfo.getSortDirection())){
			listIds = getCriteria(searchInfo).
						setProjection(Projections.distinct(Projections.projectionList().
								add(Projections.property("id"), "id"))).
					list();
			if (listIds.size() ==  0){
				return listIds;
			}
			
			listIds = extractIdsOnly(sort(searchInfo, listIds, currentPid, recordsPerPage));
			if (listIds.size() ==  0){
				return listIds;
			}
			
			Map<Long, Subscriber> map = new HashMap<Long, Subscriber>();
			List<Subscriber> subscribers = getHibernateTemplate().getSessionFactory().getCurrentSession().
				createCriteria(Subscriber.class).add(Restrictions.in("id", listIds)).list();
			
			for (Subscriber subscriber : subscribers) {
				map.put(subscriber.getId(), subscriber);
			}
			subscribers.clear();
			for (Object id : listIds) {
				subscribers.add(map.get(id));
			}
			return subscribers;
		}else{
			listIds = getCriteria(searchInfo).
			setProjection(Projections.distinct(Projections.projectionList().
					add(Projections.property("id"), "id"))).
					setFirstResult(currentPid * recordsPerPage).
					setMaxResults(recordsPerPage).
			list();
			if (listIds.size() == 0) {
				return listIds;
			}
			return getHibernateTemplate().getSessionFactory().getCurrentSession().
						createCriteria(Subscriber.class).add(Restrictions.in("id", listIds)).list();
		}
	}
	
	@SuppressWarnings("unchecked")
	private List sort(SubscriberSearchInfo searchInfo, List subscriberIds,
			int currentPid, int recordsPerPage) {
		
		return 
		getHibernateTemplate()
			.getSessionFactory()
			.getCurrentSession()
			.createSQLQuery(String.format(SQL_FIND_SORTED_SUBSCRIBERS, StringUtils.collectionToCommaDelimitedString(subscriberIds)))
			.setLong(0, searchInfo.getAttributeTypeId())
			.setInteger(1, recordsPerPage)
			.setInteger(2, currentPid * recordsPerPage)		
			.list();		
	}
	
	@SuppressWarnings("unchecked")
	private List extractIdsOnly(List list){
		List extractedIds = new ArrayList();
		for (Object object : list) {
			Object [] array = (Object [])object;
			if (array[0] instanceof Integer){
				extractedIds.add(new Long((Integer)array[0]));	
			}
		}
		return extractedIds;
	}

	public int countSubscribers() {
		throw new NotImplementedException("countSubscribers");
	}

	public Subscriber findByEMail(String mail) {
		throw new NotImplementedException("findByEMail");
	}

	public List<Subscriber> listActivatedSubscribers() {
		throw new NotImplementedException("listActivatedSubscribers");
	}

	public List<SubscriberAttributeType> listRequiredSubscriberAttributesTypes() {
		throw new NotImplementedException("listRequiredSubscriberAttributesTypes");
	}

	public List<Subscriber> listSubscribersByGroupId(Long groupId) {
		throw new NotImplementedException("listSubscribersByGroupId");
	}

	public List<Subscriber> listSubscribersByGroupIdAndQuery(Long groupId,
			String query) {
		throw new NotImplementedException("listSubscribersByGroupIdAndQuery");
	}

	public List<Subscriber> listSubscribersByQuery(String query) {
		throw new NotImplementedException("listSubscribersByQuery");
	}

	public List<Subscriber> listSubscribersWithNoGroup() {
		throw new NotImplementedException("listSubscribersWithNoGroup");
	}

	public List<Subscriber> listSubscriptionRequests() {
		throw new NotImplementedException("listSubscriptionRequests");
	}

		@Override
	public List<Subscriber> listByEMail(String eMail) {
		throw new NotImplementedException();
	}

}

