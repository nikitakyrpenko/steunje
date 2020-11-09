/*
 * @(#)Id: HibernateBannerDao.java, 12.01.2008 11:16:32, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.dao;

import java.util.Date;
import java.util.List;

import com.negeso.module.banner_module.bo.Banner;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface HibernateBannerDao {

	List<Banner> findParametrizedBannerList(Long categoryId, String query);
	Long countTotalViewsCount(Long bannerId);
	Long countTotalClicksCount(Long bannerId);

	Long countTotalViewsByDates(Long bannerId, Date d1, Date d2);
	Long countTotalClicksByDates(Long bannerId, Date d1, Date d2);
	
}
