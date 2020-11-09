/*
 * @(#)Id: CategoryDao.java, 04.01.2008 9:37:02, Dmitry Fedotov
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
import com.negeso.module.banner_module.bo.BannerCategory;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface BannerCategoryDao extends GenericDao<BannerCategory, Long>{
	
	List<BannerCategory> listCategoriesByParentId(Long catId);
	List<BannerCategory> listTopCategories();
	BannerCategory findCategoryByTitleParentId(String title, Long pId);
	BannerCategory findRootCategoryByTitle(String title);
	Integer countCategory(Long parentId);
	Integer countTopCategory();
	Integer countBanners(Long catId);
}
