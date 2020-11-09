/*
 * @(#)Id: SubscriberService.java, 25.02.2008 17:52:18, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.imp.extension.ImportObject;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.bo.SubscriberAttributeValue;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.dao.SubscriberAttributeTypeDao;
import com.negeso.module.newsletter.dao.SubscriberDao;
import com.negeso.module.newsletter.dao.SubscriberGroupDao;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
@Transactional
public class SubscriberService {

	private static final Logger logger = Logger.getLogger(SubscriberService.class);
	
	//public static final int RECORDS_PER_PAGE = Configuration.getPagingPortionSize();
	//public static final int PAGE_NUMBER = Configuration.getPagingItemsPerPage();
	
	private SubscriberDao subscriberDao;
	private SubscriberAttributeTypeDao subscriberAttributeTypeDao;
	private SubscriberGroupDao subscriberGroupDao;

	public SubscriberGroupDao getSubscriberGroupDao() {
		return subscriberGroupDao;
	}

	public void setSubscriberGroupDao(SubscriberGroupDao subscriberGroupDao) {
		this.subscriberGroupDao = subscriberGroupDao;
	}

	public void setSubscriberDao(SubscriberDao subscriberDao) {
		this.subscriberDao = subscriberDao;
	}
	
	public int countSubscribers(){
		return subscriberDao.countSubscribers();
	}	
	
	public void updateSubscriberList(List<Subscriber> subscribers){
		subscriberDao.updateAll(subscribers);
	}
	
	public void deleteSubscriberList(List<Subscriber> subscribers){
		subscriberDao.deleteAll(subscribers);
	}
	
	public boolean isAllowByLimit(){
		return countSubscribers() < Configuration.getMaxSubscribersCount();
	}
	
	public Subscriber findById(Long id){
		return subscriberDao.read(id);
	}
	
	public void saveSubscriberAttributes(List<SubscriberAttributeType> types){
		logger.debug("+");
		subscriberAttributeTypeDao.updateAll(types);
		logger.debug("-");
	}
	
	public SubscriberAttributeType findTypeById(Long id){
		return subscriberAttributeTypeDao.read(id);
	}
	
	private String getPrepearedQuery(String query){
		if (query != null && query.length() > 0){
			return "%" + query.trim().toLowerCase() + "%";
		}
		return null;
	}

	public List<Subscriber> listSubscribers(Long groupId, String query){
		String prepearedQuery = getPrepearedQuery(query);
        if (groupId != null){
            if (query != null && query.length() > 0 && groupId != 0L ){
                return subscriberDao.listSubscribersByGroupIdAndQuery(groupId, prepearedQuery);
            } else
            if (groupId == 0L ){
                return subscriberDao.listSubscribersWithNoGroup();
            } else {
                return subscriberDao.listSubscribersByGroupId(groupId);
            }
        }else
        if (query != null && query.length() > 0){
            return subscriberDao.listSubscribersByQuery(prepearedQuery);
        }

		return subscriberDao.listActivatedSubscribers();
	}
	
	public List<Subscriber> listSubscriptionRequests(){
		return subscriberDao.listSubscriptionRequests();
	}
	
	public List<Subscriber> listAllSubscribers(){
		return subscriberDao.readAll();
	}
	
	public void save(Subscriber subscriber){
		subscriberDao.createOrUpdate(subscriber);
	}
	
	public void delete(Subscriber subscriber){
		subscriberDao.delete(subscriber);
	}
	
	public List<SubscriberAttributeType> listSubscriberAttributesTypes(){
		return subscriberAttributeTypeDao.readAll();
	}
	
	public List<SubscriberAttributeType> listRequiredSubscriberAttributesTypes(){
		return subscriberAttributeTypeDao.listRequiredSubscriberAttributesTypes();
	}

	public SubscriberAttributeTypeDao getSubscriberAttributeTypeDao() {
		return subscriberAttributeTypeDao;
	}

	public void setSubscriberAttributeTypeDao( SubscriberAttributeTypeDao subscriberAttributeTypeDao) {
		this.subscriberAttributeTypeDao = subscriberAttributeTypeDao;
	}
	
	public boolean isExists(List<Subscriber> subscribers, String mail){
		return findByEmail(subscribers, mail) != null;
	}
	
	public boolean isExists(String mail){
		Subscriber s = findByEmail(mail);
		return s != null;
	}
	
	public Subscriber findByEmail(String email){
		List<Subscriber> subscribers = subscriberDao.listByEMail(email.toLowerCase());
		if (!subscribers.isEmpty()) {
			subscribers.get(0);
		}
		return null;
	}
	
	public Subscriber findByEmail(List<Subscriber> subscriber, String email){
		 for (Subscriber s : subscriber)
			 if (s.getEmail().trim().toLowerCase().
					 equals(email.trim().toLowerCase()))
				 return s;
		 return null;
	}
	
	public void updateImportedSubscriber(Subscriber s, List<SubscriberAttributeType> types, ImportObject io){
		for (String attr : io.getRow().keySet()){
			String value = (String)io.getRow().get(attr);
			if (!s.setAttribute(attr, value)){
				addSubscriberAttribute(s, types, attr, value);
			}
		}
	}
	
	public void addSubscriberAttribute(Subscriber s, List<SubscriberAttributeType> types, String key, String value){
		SubscriberAttributeType type = findTypeByKey(key, types);
		SubscriberAttributeValue attrValue = new SubscriberAttributeValue();
		attrValue.setSubscriber(s);
		attrValue.setSubscriberAttributeType(type);
		attrValue.setValue(value);
		s.getAttributes().add(attrValue);
	}
	
	public SubscriberAttributeType findTypeByKey(String key, List<SubscriberAttributeType> types){
		for (SubscriberAttributeType type : types){
			if (type.getKey().equals(key))
				return type;
		}
		return null;
	}
	
	public Subscriber collectSubscriberFromImportObject(ImportObject io, 
			List<SubscriberAttributeType> attrs){

		Subscriber s = new Subscriber();
		for (String key : io.getRow().keySet()){
			s.addAttribute(key, (String)io.getRow().get(key));
		}
		return s;
	}
	
	public void updateGroups(List<Subscriber> subscribers, List<String> group_ids){
		
		List<SubscriberGroup> groups = new ArrayList<SubscriberGroup>();
		for (String gId : group_ids){
			groups.add(subscriberGroupDao.read(Long.valueOf(gId)));
		}
		
		for (Subscriber s : subscribers){
			for (SubscriberGroup g : groups){
				if (!s.getSubscriberGroups().contains(g)){
					s.getSubscriberGroups().add(g);
				}
			}
		}
	}
	
	public void unSubscribe(Long subscriberId){
		logger.debug("+");
		Subscriber subscriber = subscriberDao.read(subscriberId);
		subscriber.getSubscriberGroups().clear();
		logger.debug("-");
	}

    public void unSubscribe(Long subscriberId, Collection<SubscriberGroup> groups){
        logger.debug("+");
        Subscriber subscriber = subscriberDao.read(subscriberId);

        subscriber.getSubscriberGroups().removeAll(groups);
        logger.debug("-");
    }

    public void unSubscribe(Long subscriberId, SubscriberGroup group){
        logger.debug("+");
        Subscriber subscriber = subscriberDao.read(subscriberId);

        subscriber.getSubscriberGroups().remove(group);
        logger.debug("-");
    }



    public SubscriberAttributeType getSubscriberAttributeByTitle(String attributeTitle){
		return subscriberAttributeTypeDao.findAttributeByTitle(attributeTitle);
	}
    
    public List<Subscriber> listSubscriber(SubscriberSearchInfo searchInfo,
			int currentPid, int recordsPerPage) {
		logger.debug("+");
		List<Subscriber> list = subscriberDao.getSubscribers(searchInfo,
				currentPid, recordsPerPage);
		logger.debug("-");
		return list;
	}
    
    public int countSubscribers (SubscriberSearchInfo searchInfo){
    	logger.debug("+-");
    	return subscriberDao.getSubscribersAmount(searchInfo);
    }
    
    public List<SubscriberAttributeType> listAttributeTypes() {
    	return subscriberAttributeTypeDao.readByCriteriaSorted(Order.asc("orderNumber"), Restrictions.eq("visible", true));
    }

}
