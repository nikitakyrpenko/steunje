/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.imp.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.imp.extension.ImportDescription;
import com.negeso.module.imp.extension.ImportModule;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */

public class ImportController extends MultiActionController {

    private static Logger logger = Logger.getLogger(ImportModule.class);
	
	public ModelAndView getImporters(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("+");
		
		RequestUtil.getHistoryStack(request).push( new Link("lnk.importers", 
				"/admin/importers.html", true));
		
		ImportModule importModule = ImportModule.getModule();
		List<ImportDescription> descriptions = importModule.getImportDescriptions();
		logger.debug("-");
		return new ModelAndView("importers", "descriptions", descriptions);		
	}
	
	public ModelAndView getImporter(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("+");
		
		String importerId = request.getParameter("importerId");
		RequestUtil.getHistoryStack(request).push(
				new Link("imp.Importer", 
				"/admin/importer.html?importerId=" + importerId, true));
		
		logger.debug("-");
		return new ModelAndView("importer", "importerId", importerId);		
	}
	
}