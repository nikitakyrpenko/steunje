/*
 * @(#)Id: CategoryService.java, 13.12.2007 19:31:23, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.service;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.OrderControlHelper;
import com.negeso.module.banner_module.bo.BannerCategory;
import com.negeso.module.banner_module.dao.BannerCategoryDao;

/**
 * 
 * @TODO
 * 
 * @version Revision:
 * @author Dmitry Fedotov
 * 
 */
@Transactional
public class BannerCategoryService {

	Logger logger = Logger.getLogger(BannerCategoryService.class);

	private BannerCategoryDao bannerCategoryDao;
	private OrderControlHelper orderHelper;

	public List<BannerCategory> findCategoriesByParentId(Long catId) {
		logger.debug("+");
		if (catId == null) {
			logger.debug("-");
			return bannerCategoryDao.listTopCategories();
		} else {
			logger.debug("-");
			return bannerCategoryDao.listCategoriesByParentId(catId);
		}
	}

	public BannerCategory findCategoryByTitleParentId(String title, Long pId) {
		logger.debug("+ -");
		if (pId == null) {
			return bannerCategoryDao.findRootCategoryByTitle(title);
		} else {
			return bannerCategoryDao.findCategoryByTitleParentId(title, pId);
		}
	}

	public BannerCategory findCategoryById(Long catId) {
		logger.debug("+ -");
		return bannerCategoryDao.read(catId);
	}

	public void deleteCategory(Long catId) {
		logger.debug("+");
		BannerCategory category = bannerCategoryDao.read(catId);
		bannerCategoryDao.delete(category);
		logger.debug("-");
	}

	public void update(BannerCategory category) {
		logger.debug("+");
		bannerCategoryDao.update(category);
		logger.debug("-");
	}

	public void changeDirection(BannerCategory category, boolean direction)
			throws CriticalException {
		logger.debug("+");
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			if (direction) {
				orderHelper.moveUp(conn, category.getId(),
						category.getOrderNumber(), category.getParentId());
			} else {
				orderHelper.moveDown(conn, category.getId(),
						category.getOrderNumber(), category.getParentId());
			}
		} catch (Exception e) {
			logger.error("- cannot change direction:" + e);
			throw new CriticalException("cannot change direction");
		}
		logger.debug("-");
	}

	public void create(BannerCategory category) {
		logger.debug("+");
		try {
			category.setSiteId(Env.getSiteId());
			category.setOrderNumber(getCategoryCount(category.getParentId()));
			bannerCategoryDao.create(category);
		} catch (Exception e) {
			logger.error("- cannot create category, " + e);
			throw new CriticalException("cannot create category");
		}
		logger.debug("-");
	}

	private Long getCategoryCount(Long catId) {
		if (catId == null) {
			return Long.parseLong(bannerCategoryDao.countTopCategory().toString());
		}
		return Long.parseLong(bannerCategoryDao.countCategory(catId).toString());
	}

	public boolean isContainBanners(Long catId) {
		return !(bannerCategoryDao.countBanners(catId) == 0);
	}

	public boolean isContainCategories(Long categoryId) {
		return !(getCategoryCount(categoryId) == 0);
	}

	public void setBannerCategoryDao(BannerCategoryDao bannerCategoryDao) {
		this.bannerCategoryDao = bannerCategoryDao;
	}

	public void setOrderHelper(OrderControlHelper orderHelper) {
		this.orderHelper = orderHelper;
	}
}
