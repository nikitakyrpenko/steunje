/*
 * @(#)Id: BannerTypeService.java, 18.12.2007 10:08:13, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.dao.BannerTypeDao;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
@Transactional
public class BannerTypeService{
	
	Logger logger = Logger.getLogger(BannerTypeService.class);
	
	BannerTypeDao bannerTypeDao;

	public BannerType findById(Long typeId){
		logger.debug("+ -");
		return bannerTypeDao.findById(typeId);
	}
	
	public BannerType findByBannerId(Long bannerId){
		logger.debug("+ -");
		return bannerTypeDao.findByBannerId(bannerId);
	}
	
	public void delete(Long id){
		logger.debug("+");
		BannerType bannertype = bannerTypeDao.read(id);
		bannerTypeDao.delete(bannertype);
		logger.debug("-");
	}
	
	public void update(BannerType bannerType){
		logger.debug("+");
		bannerTypeDao.update(bannerType);
		logger.debug("-");
	}
	
	public BannerType findByTitle(String title){
		logger.debug("+ -");
		return bannerTypeDao.findByTitle(title);
	}

	public void create(BannerType type){
		logger.debug("+");
		bannerTypeDao.create(type);
		logger.debug("-");
	}	
	
	public List<BannerType> findAllTypes(){
		logger.debug("+ -");
		return bannerTypeDao.listAllTypes();
	}
	
	public void evictType(BannerType bannerType) {
		logger.debug("+");
		bannerTypeDao.evict(bannerType);
		logger.debug("-");
	}
	
	public void setBannerTypeDao(BannerTypeDao bannerTypeDao){
		this.bannerTypeDao = bannerTypeDao;
	}

	public BannerTypeDao getBannerTypeDao() {
		return bannerTypeDao;
	}
}
