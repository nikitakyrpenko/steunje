/*
 * @(#)$Id: CustomerParametersController.java,v 1.0, 2007-04-12 08:55:23Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

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
 * @version		$Revision: 1$
 *
 */
public class CustomerParametersController extends AbstractController {

	private static Logger logger = Logger.getLogger(CustomerParametersController.class);
	
	private ParameterService parameterService;
	private ModuleService moduleService;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws ServletRequestBindingException {
		logger.debug("+-");
		if (isForSave(request)) {
			saveParameters(request);
		}
		final Long moduleId = NegesoRequestUtils.getModuleId(request, null);
		if (moduleId != null) {
			RequestUtil.getHistoryStack(request).push( 
					new Link("Parameters",
							"/admin/visitor_parameters?moduleId=" + moduleId,
							false, 0));
		} else {
			throw new CriticalException("Module id should not be null");
		}
		return new ModelAndView("visitor_parameters", "module",
				moduleService.getModuleById(moduleId));
	}

	private void saveParameters(HttpServletRequest request) {
		Iterator iterator = request.getParameterMap().keySet().iterator();
		List<ConfigurationParameter> list = new ArrayList<ConfigurationParameter>();  
		while (iterator.hasNext()) {
			String parameterName = (String) iterator.next();
			if (!parameterName.equals(NegesoRequestUtils.INPUT_MODULE_ID) &&
				!parameterName.equals(NegesoRequestUtils.INPUT_ACTION)) {
				ConfigurationParameter parameter = parameterService.findParameterByName(parameterName);
				Validate.notNull(parameter);
				parameter.setValue(request.getParameter(parameterName));
				list.add(parameter);
			}
		}
		parameterService.update(list);
	}

	private boolean isForSave(HttpServletRequest request) {
		return request.getParameter(NegesoRequestUtils.INPUT_ACTION) != null && 
			   request.getParameter(NegesoRequestUtils.INPUT_ACTION).equals("save"); 
	}

	public ParameterService getParameterService() {
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

}

