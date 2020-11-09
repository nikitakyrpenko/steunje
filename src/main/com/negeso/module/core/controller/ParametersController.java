/*
 * @(#)$Id: ParametersController.java,v 1.1, 2007-04-12 08:56:56Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ModuleService;
import com.negeso.module.core.service.ParameterService;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class ParametersController extends MultiActionController {

	private static Logger logger = Logger.getLogger(ParametersController.class);
	
	private ParameterService parameterService;
	private ModuleService moduleService;
	
	public ModelAndView getParameters(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("+-");
		final Long moduleId = NegesoRequestUtils.getModuleId(request, null);
		if ( moduleId != null) {
			return getModuleWithParameters(moduleId, request);			
		} else {
			return getUnlinkedParameters(request);
		}
	}

	private ModelAndView getUnlinkedParameters(HttpServletRequest request) {
		RequestUtil.getHistoryStack(request).push( new Link("unlinked parameters", 
				"/admin/parameters", false));
		return new ModelAndView("parameters", "module", parameterService
				.getUnlinkedModule());
	}

	private ModelAndView getModuleWithParameters(final Long moduleId, HttpServletRequest request) {
		RequestUtil.getHistoryStack(request).push( new Link("module's parameters", 
				"/admin/parameters?moduleId=" + moduleId, false));
		return new ModelAndView("parameters", "module",
				moduleService.getModuleById(moduleId));
	}
	
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}
	
	public ModelAndView deleteParameter(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("+-");
		final Long parameterId = NegesoRequestUtils.getId(request, null);
		if (parameterId != null) {
				parameterService.deleteParameter(parameterId);
		} else throw new CriticalException("parameter id should not be null");
		return getParameters(request, response);
	}
	
	private void bindParameters(HttpServletRequest request, ConfigurationParameter parameter) {
		try {
			super.bind(request, parameter);
		}catch (Throwable t) {
			//What is this exception about? All is correct
		}
		parameter.setVisible(request.getParameter("visible") != null);
		parameter.setReadonly(request.getParameter("readonly") != null);
		parameter.setGlobal(request.getParameter("global") != null);
		parameter.setRequired(request.getParameter("required") != null);
		parameter.setResetCache(request.getParameter("resetCache") != null);
		if (request.getParameter("newModuleId").equals("unlinked")) {
			parameter.setModuleId(null);
		}
	}
	
	public ModelAndView saveParameter(HttpServletRequest request,
 	 		HttpServletResponse response) throws Exception {
		logger.debug("+-");
		ConfigurationParameter parameter = new ConfigurationParameter();
		bindParameters(request, parameter);
		if (parameter.getId() == null) {
			parameterService.save(parameter);
		} else {
			parameterService.update(parameter, true);
		}
		return getParameters(request, response);
	}
	
	public ModelAndView updateParameter(HttpServletRequest request,
 			HttpServletResponse response){
		logger.debug("+-");
		final Long parameterId = NegesoRequestUtils.getId(request, null);
		if (parameterId != null) {
			ConfigurationParameter parameter = parameterService.load(parameterId);
			RequestUtil.getHistoryStack(request).push( new Link("update parameter " + parameter.getName(), 
					"/admin/parameters?act=updateParameter&id=" + parameterId, false));
			return prepareModelAndView(parameter);
		} else {
			throw new CriticalException("Parameter id should not be null");
		}
	}
	
	public ModelAndView addParameter(HttpServletRequest request,
 			HttpServletResponse response) {
		logger.debug("+-");
		final Long moduleId = NegesoRequestUtils.getModuleId(request, null);
		if (moduleId == null) {
			RequestUtil.getHistoryStack(request).push( new Link("add unlinked parameter", 
					"/admin/parameters", false));
		} else {
			RequestUtil.getHistoryStack(request).push( new Link("add parameter for module " + moduleId, 
					"/admin/parameters?moduleId=" + moduleId, false));
		}
		ConfigurationParameter parameter = new ConfigurationParameter();
		parameter.setModuleId(moduleId);
		return prepareModelAndView(parameter);
 	}
	
	private ModelAndView prepareModelAndView(ConfigurationParameter parameter) {
		ModelAndView modelAndView = new ModelAndView("parameters_details");
		modelAndView.addObject("parameter", parameter);
		modelAndView.addObject("modules", moduleService.getAllModules());
		return modelAndView;
	}

	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}
	
}

