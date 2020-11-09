/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.negeso.module.imp.extension.ImportModule;
import com.negeso.module.imp.log.EventLogger;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.ImportSubscribersForm;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ImporterThread implements Runnable {
	
	private static final Logger logger = Logger.getLogger(ImporterThread.class);
	
	private ImportModule importModule;
	private MultipartFile importFile;
	private ImportSubscribersForm form;
	private EventLogger eventLogger;
	private String uploadMethod; 
	private List<String> groupIds;

	
	public ImporterThread(ImportModule importModule, MultipartFile importFile,
			ImportSubscribersForm form, EventLogger eventLogger,
			String uploadMethod, List<String> groupIds) {
		super();
		this.importModule = importModule;
		this.importFile = importFile;
		this.form = form;
		this.eventLogger = eventLogger;
		this.uploadMethod = uploadMethod;
		this.groupIds = groupIds;
	}


	@Override
	public void run() {
		InputStream is = null;
		try {
			is = importFile.getInputStream();
			importModule.setRunning(true);
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put(Configuration.DELIMETER, form.getDelimiter().trim());
			attributes.put(Configuration.SUBSCRIPTION_LANG, form.getLangId());
			attributes.put(Configuration.IMPORT_TYPE, uploadMethod);
			attributes.put(Configuration.IMPORT_GROUPS, groupIds);
			importModule.getImportConfiguration(form.getImporterId()).setAttributes(attributes);
			importModule.doImport(is, form.getImporterId(), eventLogger);
		} catch (Exception e) {
			logger.error("Import exception - " + e);
			eventLogger.getEvents().clear();
			eventLogger.addEvent("Error", null, e.getMessage(), "Error", null, null);
			importModule.setEventLogger(eventLogger);
		}
		finally {
			importModule.setRunning(false);
			IOUtils.closeQuietly(is);
		}
	}

}

