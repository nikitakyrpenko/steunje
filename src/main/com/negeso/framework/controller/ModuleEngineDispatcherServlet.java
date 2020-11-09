/*
 * @(#)$Id: ModuleEngineDispatcherServlet.java,v 1.1, 2006-04-27 13:18:24Z, Stanislav Demchenko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
/**
 * 
 * The dispatcher servlet for module.
 * <p>
 * Dispatcher servlets are instantiated by WebFrontController rather than by
 * servlet container. To provide correct behaviour of its methods, we delegate
 * to WebFrontController.
 * </p>
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 2$
 *
 */
public class ModuleEngineDispatcherServlet extends DispatcherServlet {

	private static final long serialVersionUID = -2166568932487357581L;

	private WebFrontController controller;
	private String contextConfigLocation;
	private String moduleName;
	
	public ModuleEngineDispatcherServlet(WebFrontController controller, String contextConfigLocation) {
		this.controller = controller;
		this.contextConfigLocation = contextConfigLocation;
	}

	public ModuleEngineDispatcherServlet(WebFrontController controller, String contextConfigLocation, String moduleName) {
		this.controller = controller;
		this.contextConfigLocation = contextConfigLocation;
		this.moduleName = moduleName;
	}

	/**
	 * Initializes WebApplicationContext using ModuleConfig class
	 * placed on a root module package if this one exist. Else
	 * uses spring-xml configuration
	 */
	@Override
	protected WebApplicationContext initWebApplicationContext() throws BeansException {
		try {
			String configClass = "com.negeso.module.%s.ModuleConfig";
			Class.forName(String.format(configClass, moduleName));
			super.setContextClass(AnnotationConfigWebApplicationContext.class);
			super.setContextConfigLocation("com.negeso.module.webshop.ModuleConfig");
		} catch (ClassNotFoundException e) {
			super.setContextConfigLocation(contextConfigLocation);
		}

		return super.initWebApplicationContext();
	}
	
	@Override
	public ServletConfig getServletConfig() {
		return controller.getServletConfig();
	}
	
	@Override
	public String getServletInfo() {
		return controller.getServletInfo();
	}
	
	/** overriden to increase visibility of the method  */
	@Override
	public long getLastModified(HttpServletRequest arg0) {
		return super.getLastModified(arg0);
	}
	
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(arg0, arg1);
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}