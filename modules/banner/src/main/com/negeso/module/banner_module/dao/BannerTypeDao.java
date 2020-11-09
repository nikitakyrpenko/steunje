/*
 * @(#)Id: BannerTypeDao.java, 02.01.2008 16:09:30, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.banner_module.bo.BannerType;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface BannerTypeDao extends GenericDao<BannerType, Long>{
	
	BannerType findById(Long typeId);
	BannerType findByTitle(String title);
	BannerType findByBannerId(Long bannerId);
	List<BannerType> listAllTypes();
}
