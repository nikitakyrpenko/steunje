/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.command.VersionInfoCommand;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ProbeController extends AbstractController{
	
	private ParameterService parameterService;
	
	private static String CMS_VERSION = "CMS_VERSION";

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConfigurationParameter param = parameterService.findParameterByName(CMS_VERSION);
		if (param == null){
			param = new ConfigurationParameter();
			param.setSiteId(1L);
			param.setName(CMS_VERSION);
			param.setModuleId(29L);
			param.setValue("");
			parameterService.save(param, false);
		}
		
		VersionInfoCommand versionInfoCommand = new VersionInfoCommand();
		String version = versionInfoCommand.getVersion();
		
		if (version != null && !version.equals(param.getValue())){
			param.setValue(version);
			parameterService.update(param, false);
		}
		
		String siteName = request.getRequestURL().toString().replace("http://", "").replace("https://", "");
		if (siteName.indexOf(":") > 0){
			siteName = siteName.substring(0, siteName.indexOf(":"));
		}
		if (siteName.indexOf("/") > 0){
			siteName = siteName.substring(0, siteName.indexOf("/"));
		}
		return new PreparedModelAndView("probe").
						addToModel("siteName", siteName.toUpperCase()).
						addToModel("version", version).get();
	}

	public ParameterService getParameterService() {
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}

