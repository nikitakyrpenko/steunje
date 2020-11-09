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
package com.negeso.module.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.Env;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.PlaceHolder;
import com.negeso.module.core.service.PlaceHolderService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PlaceHolderController extends MultiActionController{
	
	private PlaceHolderService placeHolderService = null;
	
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RequestUtil.getHistoryStack(request).push( new Link("CM_PLACEHOLDERS", 
				"/admin/site_placeholder.html", true));
		return new PreparedModelAndView("site_placeholder")
			.addToModel("placeHolders", placeHolderService.listPlaceHolders(Env.getSiteId()))
			.get();
	}
	
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		PlaceHolder placeHolder = new PlaceHolder();
		placeHolder.setSiteId(Env.getSiteId());
		NegesoRequestUtils.bind(placeHolder, request);
		placeHolderService.createOrUpdate(placeHolder);
		return null;
	}
	
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		PlaceHolder placeHolder = placeHolderService.findById(ServletRequestUtils.getLongParameter(request, "id"));
		if (placeHolder != null) {
			placeHolderService.delete(placeHolder);
		}
		return null;
	}

	public PlaceHolderService getPlaceHolderService() {
		return placeHolderService;
	}

	public void setPlaceHolderService(PlaceHolderService placeHolderService) {
		this.placeHolderService = placeHolderService;
	}
}

