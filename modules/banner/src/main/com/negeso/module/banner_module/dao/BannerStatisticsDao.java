/*
 * @(#)Id: BannerStatisticsDao.java, 23.01.2008 16:33:19, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.dao;

import java.sql.Timestamp;
import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.banner_module.bo.BannerStatistics;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface BannerStatisticsDao extends GenericDao<BannerStatistics, Long>{
	
	int countTotalClicksCount(Long bannerId);
	int countTotalViewsCount(Long bannerId);
	BannerStatistics findByDate(Long bannerId, Timestamp date);
	List<BannerStatistics> listByDates(Long bannerId, Timestamp startDate, Timestamp finishDate);
	
}
