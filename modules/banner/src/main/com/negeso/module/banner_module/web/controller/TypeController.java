/*
 * @(#)Id: TypeController.java, 19.12.2007 19:49:05, Dmitry Fedotov
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

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.service.BannerTypeService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class TypeController extends SimpleFormController {

	private BannerTypeService bannerTypeService;
	
	public void setBannerTypeService(BannerTypeService bannerTypeService){
		this.bannerTypeService = bannerTypeService;
	}
	
	public TypeController(){
		setCommandClass(BannerType.class);
		setCommandName("type");
	}

	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		BannerType type = (BannerType) command;
		if (type.getId() < 0){
			bannerTypeService.create(type);
		}else{
			bannerTypeService.update(type);
		}
		RequestUtil.getHistoryStack(request).goBack();
		return new ModelAndView("bm_typelist", "typelist", bannerTypeService.findAllTypes());
	}
}
