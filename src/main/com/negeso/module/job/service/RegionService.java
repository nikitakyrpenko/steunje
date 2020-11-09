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
package com.negeso.module.job.service;

import java.util.List;

import org.hibernate.criterion.Order;
import org.w3c.dom.Element;

import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.module.job.Configuration;
import com.negeso.module.job.dao.RegionDao;
import com.negeso.module.job.domain.Region;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class RegionService {
	
	private RegionDao regionDao = null;
	
	private static RegionService instance = null;
	
	public static RegionService getInstance() {
		if (instance == null) {
			instance = (RegionService)DispatchersContainer.getInstance().getBean(ModuleConstants.JOB_MODULE, "regionService");
		}
		return instance;
	}
	
	public List<Region> list(String orderBy, boolean direction) {
		return regionDao.readByCriteriaSorted(direction ? Order.asc(orderBy) : Order.desc(orderBy));
	}
	
	public List<Region> list(String orderBy) {
		return list(orderBy, true);
	}
	
	public List<Region> list() {
		return list(Configuration.TITLE, true);
	}
	
	public Region findById(Long id) {
		return regionDao.read(id);
	}

	public RegionDao getRegionDao() {
		return regionDao;
	}
	
	public void createOrUpdate(Region region) {
		if (region.getOrderNumber() == null) {
			region.setOrderNumber(getOrderNumber());
		}
		regionDao.createOrUpdate(region);
	}
	
	public void delete(Region region) {
		regionDao.delete(region);
	}

	public void setRegionDao(RegionDao regionDao) {
		this.regionDao = regionDao;
	}
	
	public Long getOrderNumber() {
		List<Region> list = list(Configuration.ORDER_NUMBER, false);
		if (list.isEmpty()) {
			return 0L;
		}
		return list.get(0).getOrderNumber() != null ? list.get(0).getOrderNumber() + 1 : 0L;
	}
	
	public Element buildRegionsListXml(Element parent) {
		Element regionsEl = Xbuilder.addEl(parent, "job-regions", null);
		for (Region region : list()) {
			Xbuilder.addBeanJAXB(regionsEl, region);
		}
		return regionsEl;
	}
}

