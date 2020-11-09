/*
 * @(#)Id: ModelandViewPreparer.java, 06.03.2008 13:33:18, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class PreparedModelAndView {
	
	private static final String ERRORS = "errors";

	private List<String> errors = new ArrayList<String>();
	
	private ModelAndView modelAndView;
	
	public PreparedModelAndView(String view) {
		modelAndView = new ModelAndView(view);
	}
	
	public ModelAndView get(){
		return modelAndView;
	}
	
	public PreparedModelAndView addToModel(String key, Object object){
		modelAndView.addObject(key, object);
		return this;
	}
	
	public PreparedModelAndView addError(String error){
		errors.add(error);
		modelAndView.addObject(ERRORS, errors);
		return this;
	}
	
}
