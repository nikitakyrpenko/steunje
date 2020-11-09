/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.custom_consts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.core.service.ModuleService;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class CustomConstsEntryPointController extends AbstractController {

	private static Logger logger = Logger.getLogger(CustomConstsEntryPointController.class);
	
	private ModuleService moduleService;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+-");
		RequestUtil.getHistoryStack(request).push( new Link("CONSTS.ENTRY_POINT", 
				"/admin/consts", true));
		return new ModelAndView("entry_point", "modules", moduleService.getAllModules());
	}

	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

}

