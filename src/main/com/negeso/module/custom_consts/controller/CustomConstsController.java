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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.command.Command;
import com.negeso.framework.controller.CommandFactory;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.friendly_url.FriendlyUrlHandler;
import com.negeso.framework.friendly_url.ProcessFriendlyUrlCommand;
import com.negeso.framework.module.Module;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.domain.Reference;
import com.negeso.module.core.service.ModuleService;
import com.negeso.module.core.service.ReferenceService;
import com.negeso.module.custom_consts.domain.CustomConst;
import com.negeso.module.custom_consts.domain.CustomTranslation;
import com.negeso.module.custom_consts.service.CustomConstsService;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class CustomConstsController extends MultiActionController {

	private static Logger logger = Logger.getLogger(CustomConstsController.class);
	
	private static final String INPUT_VISITOR_MODE = "visitorMode";
	
	private CustomConstsService customConstsService;
	private ReferenceService referenceService;
	private ModuleService moduleService;
	
	public ModelAndView getConsts(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("+-");
		final Long moduleId = NegesoRequestUtils.getModuleId(request, null);
		if ( moduleId != null) {
			return getModuleCustomConsts(request, moduleId);			
		} else {
			return getCommonCustomConsts(request);
		}
	}

	private ModelAndView getModuleCustomConsts(HttpServletRequest request, final Long moduleId) {
		Module module = moduleService.getModuleById(moduleId);
		RequestUtil.getHistoryStack(request).push( new Link(module.getTitle(), 
				"/admin/module_consts?moduleId=" + moduleId + (isVisitorMode(request)?"&visitorMode=true":""), false ));
		ModelAndView modelAndView = new ModelAndView("custom_consts");
		modelAndView.addObject("module", module);
		modelAndView.addObject("consts", customConstsService.getConstsByModuleId(moduleId));
		modelAndView.addObject("visitorMode", isVisitorMode(request));		
		return modelAndView;
	}

	private boolean isVisitorMode(HttpServletRequest request) {
		return request.getParameter(INPUT_VISITOR_MODE) != null;
	}
	
	private ModelAndView getCommonCustomConsts(HttpServletRequest request) {
		RequestUtil.getHistoryStack(request).push( new Link("COMMON_CONSTS", 
				"/admin/module_consts", true));
		ModelAndView modelAndView = new ModelAndView("custom_consts");
		modelAndView.addObject("module", null);
		modelAndView.addObject("consts", customConstsService.getCommonConsts());
		modelAndView.addObject("visitorMode", isVisitorMode(request));
		return modelAndView;
	}
	
	public void setCustomConstsService(CustomConstsService customConstsService) {
		this.customConstsService = customConstsService;
	}
	
	public ModelAndView deleteConst(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("+-");
		final Long constId = NegesoRequestUtils.getId(request, null);
		if (constId != null) {
				customConstsService.deleteConst(constId);
		} else throw new CriticalException("const id should not be null");
		return getConsts(request, response);
	}
	
	private void bindNewConst(HttpServletRequest request, CustomConst customConst) {
		customConst.setKey(request.getParameter("key"));
		customConst.setId(NegesoRequestUtils.getId(request, null));
		customConst.setModuleId(NegesoRequestUtils.getModuleId(request, null));
		Set<CustomTranslation> translations = new HashSet<CustomTranslation>();
		for (Reference language: referenceService.getAllLanguages()) {
			CustomTranslation translation = new CustomTranslation();
			translation.setLanguage(language);
			translation.setTranslation(request.getParameter(language.getCode()));
			translation.setCustomConst(customConst);			
			translations.add(translation);
		}
		customConst.setTranslations(translations);
	}

	public boolean isAdded(HttpServletRequest request) {
		return NegesoRequestUtils.getId(request, null) == null;
	}

	public CustomConst saveConst(HttpServletRequest request) {
		CustomConst customConst = null;
		if (isAdded(request)) {
			customConst = new CustomConst();
			bindNewConst(request, customConst);
		} else {
			final Long constId = NegesoRequestUtils.getId(request, null); 
			customConst = customConstsService.loadConst(constId);
			bindUpdatedConst(request, customConst);
		}
		return customConst;
	}
	
	public ModelAndView saveConst(HttpServletRequest request,
 	 		HttpServletResponse response) throws Exception {
		logger.debug("+-");
		CustomConst constant = saveConst(request);
		customConstsService.saveConst(constant);
		if (FriendlyUrlHandler.get(constant.getKey()) != null) {
			CommandFactory.removeMatchersByCommandName(ProcessFriendlyUrlCommand.NAME);
			CommandFactory.putMatchers(new FriendlyUrlHandler());
		}
		return getConsts(request, response);
	}
	
	private void bindUpdatedConst(HttpServletRequest request, CustomConst customConst) {
		customConst.setKey(request.getParameter("key"));
		Set<CustomTranslation> translations = customConst.getTranslations();
		CustomTranslation translation = null;
		for (Iterator iterator = translations.iterator(); iterator.hasNext();) {
			translation = (CustomTranslation)iterator.next();
			translation.setTranslation(request.getParameter(translation.getLanguage().getCode()));
		}
	}

	public ModelAndView updateConst(HttpServletRequest request,
 			HttpServletResponse response){
		logger.debug("+-");
		final Long constId = NegesoRequestUtils.getId(request, null);
		if (constId != null) {
			RequestUtil.getHistoryStack(request).push( new Link("EDIT_CONST", 
					"/admin/module_consts?act=updateConst&id=" + constId + (isVisitorMode(request)?"&visitorMode=true":""), true));
			CustomConst customConst = (CustomConst) customConstsService.loadConst(constId);
			addNewLanguages(customConst);
			return prepareModelAndView(request, customConst);
		} else {
			throw new CriticalException("Custom const id should not be null");
		}
	}

	public ModelAndView updateConstKey(HttpServletRequest request,
 			HttpServletResponse response){
		logger.debug("+-");
		final String key = request.getParameter("key");				
		RequestUtil.getHistoryStack(request).push( new Link("EDIT_CONST", 
				"/admin/module_consts?act=updateConstKey&key=" + key + (isVisitorMode(request)?"&visitorMode=true":""), true));
		CustomConst customConst = (CustomConst) customConstsService.loadConst(key);		
		ArrayList<Language> lang=Language.getItems();
		if (customConst!=null) {			
			addNewLanguages(customConst);
			ModelAndView modelAndView = new ModelAndView("edit_const_key");
			modelAndView.addObject("const", customConst);
			modelAndView.addObject("langs", lang);
			modelAndView.addObject("visitorMode", isVisitorMode(request));
			return modelAndView;
		} else {
			throw new CriticalException("Custom const no exist");
		}
	}

	private void addNewLanguages(CustomConst customConst) {
		List translatedLanguages = customConstsService.getTranslatedLanguages(customConst);
		List allLanguages = referenceService.getAllLanguages();
		Iterator iterator = CollectionUtils.subtract(allLanguages,
				translatedLanguages).iterator();
		while (iterator.hasNext()) {
			CustomTranslation translation = new CustomTranslation();
			translation.setCustomConst(customConst);
			translation.setLanguage((Reference)iterator.next());
			customConst.getTranslations().add(translation);
		}
		customConstsService.saveConst(customConst);
	}
	
	public ModelAndView addConst(HttpServletRequest request,
 			HttpServletResponse response) {
		logger.debug("+-");
		CustomConst customConst = new CustomConst();
		customConst.setTranslations(getEmptyTranslations());
		customConst.setModuleId(NegesoRequestUtils.getModuleId(request, null));
		return prepareModelAndView(request, customConst);
 	}
	
	private ModelAndView prepareModelAndView(HttpServletRequest request, CustomConst customConst) {
		ModelAndView modelAndView = new ModelAndView("edit_const");
		modelAndView.addObject("const", customConst);
		modelAndView.addObject("visitorMode", isVisitorMode(request));
		return modelAndView;
	}

	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}
	
	public Set<CustomTranslation> getEmptyTranslations() {
		Set<CustomTranslation> translations = new TreeSet<CustomTranslation>(
				new Comparator<CustomTranslation>() {
					public int compare(CustomTranslation translation1,
							CustomTranslation translation2) {
						return translation1.getLanguage().getId().compareTo(
								translation2.getLanguage().getId());
					}
				});
		for (Reference language: referenceService.getAllLanguages()) {
			CustomTranslation translation = new CustomTranslation();
			translation.setLanguage(language);
			translations.add(translation);
		}
		return translations;
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	public CustomConstsService getCustomConstsService() {
		return customConstsService;
	}
	
}
