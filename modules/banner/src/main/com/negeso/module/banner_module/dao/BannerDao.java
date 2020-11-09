/*
 * @(#)Id: BannerDao.java, 02.01.2008 14:48:45, Dmitry Fedotov
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
import com.negeso.module.banner_module.bo.Banner;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface BannerDao extends GenericDao<Banner, Long>{

	List<Banner> listAllBanners();
	List<Banner> listBannersByParentId(Long banId);
	List<Banner> listBannersByTypeId(Long typeId);
}
