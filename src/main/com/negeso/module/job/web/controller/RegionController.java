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
package com.negeso.module.job.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.job.domain.Region;
import com.negeso.module.job.service.RegionService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class RegionController extends MultiActionController{
	
	private String listRegionsView = null;
	
	private RegionService regionService = null; 
	
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RequestUtil.getHistoryStack(request).push( new Link("Job module", 
				"/admin/j_module", false, -1));
		RequestUtil.getHistoryStack(request).push( new Link("JOB_REGIONS", 
				"/admin/job_regions.html", true));
		
		return new PreparedModelAndView(listRegionsView).addToModel("regions", regionService.list()).get();
	}
	
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, 0L);
		Region region = null;
		if (id > 0) {
			region = regionService.findById(id);
		}
		if (region == null) {
			region = new Region();
		}
		NegesoRequestUtils.bind(region, request);
		regionService.createOrUpdate(region);
		return null;
	}
	
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Region region = null;
		Long id = NegesoRequestUtils.getId(request, 0L);
		if (id > 0) {
			region = regionService.findById(id);
		}
		if (region != null && region.getVacancies().isEmpty()) {
			regionService.delete(region);
		}
		return null;
	}

	public RegionService getRegionService() {
		return regionService;
	}

	public void setRegionService(RegionService regionService) {
		this.regionService = regionService;
	}

	public String getListRegionsView() {
		return listRegionsView;
	}

	public void setListRegionsView(String listRegionsView) {
		this.listRegionsView = listRegionsView;
	}
	
}

