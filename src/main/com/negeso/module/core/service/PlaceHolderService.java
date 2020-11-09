/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Restrictions;

import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.module.core.dao.PlaceHolderDao;
import com.negeso.module.core.domain.PlaceHolder;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PlaceHolderService {
	
	private static Map<Long, Map<String, String>> cachedPlaceHoldersMap = new HashMap<Long, Map<String,String>>();
	
	private PlaceHolderDao placeHolderDao = null;
	
	private static PlaceHolderService instance = null;
	
	public static PlaceHolderService getInstance() {
		if (instance == null) {
			instance = (PlaceHolderService)DispatchersContainer.getInstance().getBean("core", "placeHolderService");
		}
		return instance;
	}
	
	public List<PlaceHolder> listPlaceHolders(Long siteId) {
		return  placeHolderDao.readByCriteria(Restrictions.eq("siteId", siteId));
	}
	
	public static Map<String, String> getPlaceHoldersMap(Long siteId) {
		Map<String, String> map = cachedPlaceHoldersMap.get(siteId);
		if (map == null) {
			List<PlaceHolder> placeHolders = PlaceHolderService.getInstance().listPlaceHolders(siteId);
			map = new HashMap<String, String>();
			for (PlaceHolder placeHolder : placeHolders) {
				map.put(placeHolder.getKey().toUpperCase(), placeHolder.getValue());
			}
			cachedPlaceHoldersMap.put(siteId, map);
		}
		return map;
	}
	
	public PlaceHolder findById(Long id) {
		return placeHolderDao.read(id);
	}
	
	public void delete(PlaceHolder placeHolder) {
		placeHolderDao.delete(placeHolder);
		placeHolderDao.flush();
		PlaceHolderService.resetCache(placeHolder.getSiteId());
	}
	
	public void createOrUpdate(PlaceHolder placeHolder) {
		placeHolderDao.createOrUpdate(placeHolder);
		placeHolderDao.flush();
		PlaceHolderService.resetCache(placeHolder.getSiteId());
	}

	public PlaceHolderDao getPlaceHolderDao() {
		return placeHolderDao;
	}

	public void setPlaceHolderDao(PlaceHolderDao placeHolderDao) {
		this.placeHolderDao = placeHolderDao;
	}
	
	public static void resetCache(Long siteId) {
		cachedPlaceHoldersMap.put(siteId, null);
	}
}

