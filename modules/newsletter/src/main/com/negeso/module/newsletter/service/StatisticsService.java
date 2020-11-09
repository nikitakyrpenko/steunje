/*
 * @(#)Id: StatisticsService.java, 27.02.2008 18:24:37, Dmitry Fedotov
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
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.PageNavigator;
import com.negeso.module.newsletter.bo.Mailing;
import com.negeso.module.newsletter.bo.MailingState;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.SubscriptionCategory;
import com.negeso.module.newsletter.dao.MailingStateDao;
import com.negeso.module.newsletter.dao.PublicationDao;
import com.negeso.module.newsletter.dao.StatisticsDao;
import com.negeso.module.newsletter.dao.SubscriptionCategoryDao;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class StatisticsService {
	
	private static final Logger logger = Logger.getLogger(StatisticsService.class);

	//private static final int RECORDS_PER_PAGE = Configuration.getPagingPortionSize();;
	//private static final int PAGE_NUMBER = Configuration.getPagingItemsPerPage();
	
	private StatisticsDao statisticsDao;
	private PublicationDao publicationDao;
	private SubscriptionCategoryDao subscriptionCategoryDao;
	private MailingStateDao mailingStateDao;
	
	public MailingState getMailingState(String name){
		MailingState state = mailingStateDao.findByName(name);
		if (state == null){
			logger.error("wrong mail status: " + state);
		}
		return state;
	}

	public void setStatisticsDao(StatisticsDao statisticsDao) {
		this.statisticsDao = statisticsDao;
	}

	public void setPublicationDao(PublicationDao publicationDao) {
		this.publicationDao = publicationDao;
	}
	
	public int getPublicationTotalSubscribers(Long publicationId, Long stateId){
		return statisticsDao.countSubscribersByPublicationId(publicationId, stateId);
	}

	public int getCategoryTotalSubscribers(Long categoryId, Long stateId){
		int totalCount = 0;
		SubscriptionCategory category = subscriptionCategoryDao.read(categoryId);
		
		if (category == null || category.getPublications() == null ||
				category.getPublications().size() == 0)
			return 0;
		
		for (Publication p : category.getPublications()){
			totalCount += getPublicationTotalSubscribers(p.getId(), stateId);
		}
		return totalCount;
	}
	
	public List<Mailing> listSubscriberStatus(Long publicationId, Long stateId, int currentPid){
		logger.debug("+");
		final int RECORDS_PER_PAGE = Configuration.getPagingPortionSize();
		int count = getPublicationTotalSubscribers(publicationId, stateId);
		
		if (currentPid > 0 && currentPid * RECORDS_PER_PAGE > count){
			currentPid = (count - 1) / RECORDS_PER_PAGE;
		}
		
		List<Mailing> list = statisticsDao.listSubscribersInRange(publicationId, 
				stateId, RECORDS_PER_PAGE, currentPid * RECORDS_PER_PAGE);
		logger.debug("-");
		return list;
	}
	
	public List<Mailing> listSummerySubscriberStatusByCategory(Long categoryId, 
			Long stateId, int currentPaginationId){
		logger.debug("+ -");
		List<Mailing> summaryCategoryList = new ArrayList<Mailing>();
		
		SubscriptionCategory category = 
			subscriptionCategoryDao.read(categoryId);
		
		if (category == null || category.getPublications() == null ||
				category.getPublications().size() == 0){
			logger.debug("caegory is empty");
			return null;
		}
		
		for (Publication p : category.getPublications())
			summaryCategoryList.addAll(listSubscriberStatus(p.getId(), 
					stateId, currentPaginationId));
		
		logger.debug("-");
		return summaryCategoryList;
	}
	
	public PageNavigator getPublicationPageNavigator(Long publicationId, 
			Long stateId, int currentPid){
		return getPageNavigator(getPublicationTotalSubscribers(
				publicationId, stateId), currentPid);
	}
	
	public PageNavigator getCategoryPageNavigator(Long publicationId, 
			Long stateId, int currentPid){
		return getPageNavigator(getCategoryTotalSubscribers(
				publicationId, stateId), currentPid);
	}
	
	public PageNavigator getPageNavigator(int count, int currentPid){
		PageNavigator pageNavigator = new PageNavigator();
		pageNavigator.setPageNumber(Configuration.getPagingItemsPerPage());
		pageNavigator.setRecordsPerPage(Configuration.getPagingPortionSize());
		pageNavigator.setCount(count);
		pageNavigator.setCurrentPid(currentPid);
		return pageNavigator;
	}
	
	public List<Publication> listSheduled(){
		return publicationDao.listSheduled();
	}

	public void setMailingStateDao(MailingStateDao mailingStateDao) {
		this.mailingStateDao = mailingStateDao;
	}

	public void setSubscriptionCategoryDao(SubscriptionCategoryDao dao) {
		this.subscriptionCategoryDao = dao;
	}

}
