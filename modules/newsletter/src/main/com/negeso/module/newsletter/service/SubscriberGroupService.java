/*
 * @(#)Id: GroupService.java, 25.02.2008 12:15:47, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.OrderControlHelper;
import com.negeso.module.newsletter.bo.SubscriberGroup;
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
public class SubscriberGroupService {
	
	private static final Logger logger = Logger.getLogger(SubscriberGroupService.class);

	private SubscriberGroupDao subscriberGroupDao;
	private OrderControlHelper orderHelper;

	public void setSubscriberGroupDao(SubscriberGroupDao subscriberGroupDao) {
		this.subscriberGroupDao = subscriberGroupDao;
	}
	public OrderControlHelper getOrderHelper() {
		return orderHelper;
	}
	public void setOrderHelper(OrderControlHelper orderHelper) {
		this.orderHelper = orderHelper;
	}
	
	public int getGroupCount(){
		return subscriberGroupDao.countSubscriberGroup();
	}
	
	public SubscriberGroup findById(Long id){
		return subscriberGroupDao.read(id);
	}
	
	public Long getNextOrderNumber(){
		return Long.valueOf(subscriberGroupDao.countNextOrderNumber());
	}
	
	public List<SubscriberGroup> listAllSubscriberGroups(Long langId) {
		logger.debug("+");
		List<SubscriberGroup> subscriberGroups = subscriberGroupDao.readAll();
		for (SubscriberGroup subscriberGroup : subscriberGroups) {
			subscriberGroup.setLang_id(langId);
		}
        logger.debug("-");
        return subscriberGroups;
	}
	
	public List<SubscriberGroup> listOrderedGroups() {
		return subscriberGroupDao.listOrderedGroup();
	}
	
	public void save(SubscriberGroup group){
		subscriberGroupDao.createOrUpdate(group);
	}
	
	public void delete(Long groupId){
		logger.debug("+");
		SubscriberGroup group = subscriberGroupDao.read(groupId);
		subscriberGroupDao.delete(group);
		logger.debug("-");
	}
	
	public boolean isUnique(SubscriberGroup group){
		
		SubscriberGroup subscriberGroup = 
			subscriberGroupDao.findByTitle(group.getTitle().trim(), group.getLang_id());
		
		if (subscriberGroup != null && !subscriberGroup.getId().equals(group.getId()))
			return false;
		
		subscriberGroupDao.evict(subscriberGroup);
		
		return true;
	}
	
	public void changeDirection(Long id, boolean direction){
		logger.debug("+");
		
		SubscriberGroup group = subscriberGroupDao.read(id);
		
		Connection conn = null;
		try {
			conn = DBHelper.getSessionFactory().getCurrentSession().connection();
			if (direction) {
				orderHelper.moveUp(conn, group.getId(),
						group.getOrderNumber());
			} else {
				orderHelper.moveDown(conn, group.getId(),
						group.getOrderNumber());
			}
			
		} catch (Exception e) {
			logger.error("- cannot change direction:" + e);
			throw new CriticalException("cannot change direction");
		}
		logger.debug("-");
	}
	
	/**
	 * @param string
	 * @return
	 */
	public SubscriberGroup getGroupByName(String groupName, Long langId) {
		return subscriberGroupDao.findByTitle(groupName, langId);
	}
	
}
