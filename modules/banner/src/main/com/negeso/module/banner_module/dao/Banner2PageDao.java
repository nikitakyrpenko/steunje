/*
 * @(#)Id: Banner2PageDao.java, 03.01.2008 12:48:10, Dmitry Fedotov
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
import com.negeso.module.banner_module.bo.Banner2Page;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface Banner2PageDao extends GenericDao<Banner2Page, Long>{
	
	List<Banner2Page> listBannerPages(Long bannerId, String type);
	List<Long> listBanner2PagesIds(Long bannerId, String type);
	List<Banner> listBannersByPageId(Long pageId);
	List<Banner> listBannersByPmCategoryId(Long pageId, Long pmCatId);
	List<Banner> listBannersByPmProductId(Long pageId, Long pmProdId);
	Banner2Page findBanner2PageByProductCategoryId(Long bannerId, Long productCategoryId, String type);
	Banner2Page findBanner2PageByProductId(Long bannerId, Long productId, String type);
	
}
