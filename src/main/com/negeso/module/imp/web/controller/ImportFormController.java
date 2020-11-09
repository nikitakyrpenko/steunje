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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.imp.extension.ImportException;
import com.negeso.module.imp.extension.ImportModule;
import com.negeso.module.imp.log.EventLogger;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class ImportFormController extends SimpleFormController {

	private static Logger logger = Logger.getLogger(ImportFormController.class);

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		
		logger.debug("+");
		
		// get import file
		
		ImportForm form = (ImportForm) command;
		MultipartFile importFile = form.getImportFile();
		if (importFile.isEmpty()) {
			logger.error("import file is empty");
			Map<String, Object> values = new LinkedHashMap<String, Object>();
			values.put("importerId", form.getImporterId());
			values.put("error", "Empty input file");
			return new ModelAndView("importer", "values", values);		
		}
	
		// init history stack
		RequestUtil.getHistoryStack(request).push(
				new Link(String.format("Importer '%s'", form.getImporterId()), 
				"/admin/importer.html", false));
			
		// do import
			
		User user = SessionData.getSessionData(request).getUser(); 
		EventLogger eventLogger = new EventLogger(String.valueOf(new Date().getTime()),
				user.getName());
		ImportModule importModule = ImportModule.getModule();
		
		InputStream is = null;
		try {
			is = importFile.getInputStream();
			importModule.doImport(is, form.getImporterId(), eventLogger);
		} catch (ImportException e) {
			logger.error("Import exception - " + e);
		}
		finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("unexpected IOException", e);
			}
		}

		// put into the map values for jsp
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("events", eventLogger.getEvents());
		values.put("importerId", form.getImporterId());
		logger.debug("-");
		return new ModelAndView("importer", "values", values);		
	}

}
