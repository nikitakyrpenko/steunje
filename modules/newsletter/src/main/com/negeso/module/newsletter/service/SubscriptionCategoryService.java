/*
 * @(#)Id: NewsletterService.java, 21.02.2008 15:49:41, Dmitry Fedotov
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
import com.negeso.module.newsletter.bo.SubscriptionCategory;
import com.negeso.module.newsletter.dao.SubscriptionCategoryDao;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
@Transactional
public class SubscriptionCategoryService {
	
	private static final Logger logger = Logger.getLogger(SubscriptionCategoryService.class);

	private SubscriptionCategoryDao subscriptionCategoryDao;
	private OrderControlHelper orderHelper;

	public void save(SubscriptionCategory category){
		subscriptionCategoryDao.createOrUpdate(category);
	}
	
	public List<SubscriptionCategory> listSubscriptionCategory(){
		return subscriptionCategoryDao.listAll();
	}
	
	public SubscriptionCategory findBySubscriptionCategoryId(Long id){
		return subscriptionCategoryDao.read(id);
	}
	
	public int getNewOrderNumber(){
		return subscriptionCategoryDao.countAll() + 1;
	}

	public void setSubscriptionCategoryDao(SubscriptionCategoryDao dao) {
		this.subscriptionCategoryDao = dao;
	}
	
	public boolean isUnique(SubscriptionCategory category){
		
		SubscriptionCategory subscriptionCategory = subscriptionCategoryDao.findByTitle(category.getTitle());
		
		if (subscriptionCategory != null && !subscriptionCategory.getId().equals(category.getId()))
			return false;
		
		subscriptionCategoryDao.evict(subscriptionCategory);
		
		return true;
	}
	
	
	
	public void evict(SubscriptionCategory category){
		subscriptionCategoryDao.evict(category);
	}
	
	public void delete(SubscriptionCategory category){
		subscriptionCategoryDao.delete(category);
	}
	
	public boolean isCategoryEmpty(SubscriptionCategory category){
		return category.getPublications().size() == 0;
	}
	
	public void changeDirection(Long id, boolean direction){
		logger.debug("+");
		
		SubscriptionCategory category = subscriptionCategoryDao.read(id);
		
		Connection conn = null;
		try {
			conn = DBHelper.getSessionFactory().getCurrentSession().connection();
			if (direction) {
				orderHelper.moveUp(conn, category.getId(), category.getOrderNumber());
			} else {
				orderHelper.moveDown(conn, category.getId(), category.getOrderNumber());
			}
		} catch (Exception e) {
			logger.error("- cannot change direction:" + e);
			throw new CriticalException("cannot change direction");
		} 
		logger.debug("-");
	}

	public OrderControlHelper getOrderHelper() {
		return orderHelper;
	}

	public void setOrderHelper(OrderControlHelper orderHelper) {
		this.orderHelper = orderHelper;
	}

}
