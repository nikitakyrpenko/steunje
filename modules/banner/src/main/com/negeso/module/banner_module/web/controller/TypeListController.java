/*
 * @(#)Id: TypeListController.java, 19.12.2007 19:49:22, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.service.BannerService;
import com.negeso.module.banner_module.service.BannerTypeService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class TypeListController extends MultiActionController{
	
	private static Logger logger = Logger.getLogger(TypeListController.class);
	
	private BannerTypeService bannerTypeService;
	private BannerService bannerService;
	
	public void setBannerTypeService(BannerTypeService bannerTypeService){
		this.bannerTypeService = bannerTypeService;
	}
	
	public void setBannerService(BannerService bannerService){
		this.bannerService = bannerService;
	}
	
	public ModelAndView addType(HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.debug("+");
		RequestUtil.getHistoryStack(request).push( new Link("New type", "/admin/bm_typelist.html?action=addType", false) );
		logger.debug("-");
		return new ModelAndView("bm_edittype", "type", new BannerType());
	}
	
	public ModelAndView editType(HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.debug("+");
		Long tId = ServletRequestUtils.getLongParameter(request, "typeId");
		BannerType bannerType = bannerTypeService.findById(tId);
		RequestUtil.getHistoryStack(request).push( new Link(bannerType.getTitle(), "/admin/bm_typelist.html?action=editType&typeId=" + bannerType.getId(), false) );
		logger.debug("-");
		return new ModelAndView("bm_edittype", "type", bannerType);
	}
	
	public ModelAndView deleteType(HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.debug("+");
		Long typeId = ServletRequestUtils.getLongParameter(request, "typeId");
		BannerType bannerType = bannerTypeService.findById(typeId);
		
		if (bannerService.findBannersByTypeId(bannerType.getId()).size() == 0){
			bannerTypeService.delete(bannerType.getId());
		}else{
			request.setAttribute("error_message", "BM.TYPE_CANNOT_BE_DELETED");
		}
		
		logger.debug("-");
		return new ModelAndView("bm_typelist", "typelist", bannerTypeService.findAllTypes());
	}

	public ModelAndView showTypeList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.debug("+");
		RequestUtil.getHistoryStack(request).push( new Link("Top types", "/admin/bm_typelist.html?action=showTypeList", false, -1) );
		logger.debug("-");
		return new ModelAndView("bm_typelist", "typelist", bannerTypeService.findAllTypes());
	}
	
}
