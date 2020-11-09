/*
 * @(#)Id: HibernateBannerDaoImpl.java, 11.01.2008 18:32:48, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import com.negeso.module.banner_module.bo.Banner;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class HibernateBannerDaoImpl extends HibernateDaoSupport implements HibernateBannerDao{
	
	Logger logger  = Logger.getLogger(HibernateBannerDaoImpl.class);
	
	@SuppressWarnings("unchecked")
	public List<Banner> findParametrizedBannerList(Long categoryId, String query){
		logger.debug("+");
		List<Banner> banners = new ArrayList<Banner>();
		try{
			banners = (List<Banner>)getHibernateTemplate().find(query);
		}catch(Exception e){
			logger.error(e);
		}
		logger.debug("-");
		return banners;
	}
	
	@SuppressWarnings("unchecked")
	public Long countTotalViewsCount(Long bannerId){
		logger.debug("+");
		Long count = ((ArrayList<Long>)executeQuery("" +
				"select sum(bStat.views) " +
				"from BannerStatistics bStat " +
				"where bStat.bannerId = " + bannerId)).get(0);
		if (count == null)
			return 0L;
		logger.debug("-");
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public Long countTotalClicksCount(Long bannerId){
		logger.debug("+");
		Long count = ((ArrayList<Long>)executeQuery("" +
				"select sum(bStat.clicks) " +
				"from BannerStatistics bStat " +
				"where bStat.bannerId = " + bannerId)).get(0);
		if (count == null)
			return 0L;
		logger.debug("-");
		return count;
	}
	
	@SuppressWarnings("unchecked")
	private Object executeQuery(String query){
		logger.debug("+ -");
		return getHibernateTemplate().find(query);
	}

	public Long countTotalClicksByDates(Long bannerId, Date d1, Date d2) {
		logger.debug("+");
		Long count = ((ArrayList<Long>)executeQuery("" +
				"select sum(bStat.views) " +
				"from BannerStatistics bStat " +
				"where bStat.bannerId = " + bannerId + " and bStat.date > " + d1 + " and bStat.date < " + d2)).get(0);
		if (count == null)
			return 0L;
		logger.debug("-");
		return count;
	}

	public Long countTotalViewsByDates(Long bannerId, Date d1, Date d2) {
		return null;
	}
	
	
	
}
