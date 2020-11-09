/*
 * @(#)Id: ImportController.java, 26.02.2008 17:37:04, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.imp.extension.ImportException;
import com.negeso.module.imp.extension.ImportModule;
import com.negeso.module.imp.log.EventLogger;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.ImportSubscribersForm;
import com.negeso.module.newsletter.service.SubscriberGroupService;


/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class ImportController extends SimpleFormController {
	
	private static final Logger logger = Logger.getLogger(ImportController.class);
	
	private SubscriberGroupService subscriberGroupService;
	
	public void setSubscriberGroupService(SubscriberGroupService subscriberGroupService) {
		this.subscriberGroupService = subscriberGroupService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		
		Language l = NegesoRequestUtils.getInterfaceLanguage(request);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("groups", subscriberGroupService.listAllSubscriberGroups(l.getId()));
		model.put("isImportRunning", ImportModule.getModule().isRunning());
		model.put("events", ImportModule.getModule().getEventLogger() == null ? null : ImportModule.getModule().getEventLogger().getEvents());
		model.put("languages", Language.getItems());
		
		return model;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
		HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		logger.debug("+");
		
		Language l = NegesoRequestUtils.getInterfaceLanguage(request);
		
		ImportSubscribersForm form = (ImportSubscribersForm)command;
		form.setImporterId("importer");
		
		if (form.getLangId() == null)
			form.setLangId(Language.getDefaultLanguage().getId());

		List<String> groupIds = new ArrayList<String>();
		
		for (Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext() ;){
			String param_name = (String)i.next();
			if (param_name.startsWith(PublicationController.GROUP_PREFIX)){
				param_name = param_name.substring(PublicationController.GROUP_PREFIX.length());
				groupIds.add(param_name);
			}
		}

        User user = SessionData.getSessionData(request).getUser();
        EventLogger eventLogger = new EventLogger(String.valueOf(new Date().getTime()), user.getName());
        ImportModule importModule = ImportModule.getModule();

        String uploadMethod = ServletRequestUtils.getStringParameter( request, "upload_method", Configuration.IMPORT_TYPE_UPDATE);

        if (uploadMethod.equals(Configuration.IMPORT_TYPE_SINGLE)){
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put(Configuration.SUBSCRIPTION_LANG, form.getLangId());
            attributes.put(Configuration.IMPORT_GROUPS, groupIds);
            attributes.put(Configuration.IMPORT_TYPE, uploadMethod);
            attributes.put(Configuration.ATTRIBUTE_EMAIL, form.getSingleEmail());

            importModule.getImportConfiguration(form.getImporterId()).setAttributes(attributes);
            importModule.doSingleImport(form.getImporterId(), eventLogger);

            RequestUtil.getHistoryStack(request).push( new Link("Import details",
                    "/admin/nl_import_details", false) );

            return new PreparedModelAndView("nl_import").
	        	addToModel("isImportRunning", importModule.isRunning()).
	        	addToModel("importerId", form.getImporterId()).
	        	addToModel("groups", subscriberGroupService.listAllSubscriberGroups(l.getId())).
	        	addToModel("events", importModule.getEventLogger() == null ? null : importModule.getEventLogger().getEvents())
	        	.get();
        }
        if (importModule.isRunning()) {
        	return new PreparedModelAndView("nl_import").
        	addToModel("isImportRunning", importModule.isRunning()).
        	addToModel("importerId", form.getImporterId()).
        	addToModel("groups", subscriberGroupService.listAllSubscriberGroups(l.getId())).
        	addToModel("events", importModule.getEventLogger() == null ? null : importModule.getEventLogger().getEvents())
        	.get();
        }
        MultipartFile importFile = form.getImportFile();
		if (importFile.isEmpty()) {
			logger.error("import file is empty");
			return new PreparedModelAndView("nl_import").
					addError("import file is empty").
					addToModel("importerId", form.getImporterId()).
					addToModel("isImportRunning", importModule.isRunning()).
					addToModel("events", importModule.getEventLogger() == null ? null : importModule.getEventLogger().getEvents()).
					addToModel("groups", subscriberGroupService.
							listAllSubscriberGroups(l.getId())).get();
		}
		synchronized (importModule) {
			new Thread(new ImporterThread(importModule, importFile, form, eventLogger, uploadMethod, groupIds)).start();
		}
		
		return new PreparedModelAndView("nl_import").
			addToModel("isImportRunning", importModule.isRunning()).
			addToModel("importerId", form.getImporterId()).
			addToModel("groups", subscriberGroupService.listAllSubscriberGroups(l.getId())).
			addToModel("events", importModule.getEventLogger() == null ? null : importModule.getEventLogger().getEvents())
			.get();
	}
}
