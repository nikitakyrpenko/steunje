/*
 * @(#)Id: SettingsController.java, 16.04.2008 17:04:32, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.service.MailTemplateService;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SettingsController extends MultiActionController {

	private static final Logger logger = Logger.getLogger(SettingsController.class);
	
	private SubscriberService subscriberService;
	private MailTemplateService mailTemplateService;

	public ModelAndView showParameters(HttpServletRequest request, 
			HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		Language language = getLanguageFromRequest(request);
		
		List<SubscriberAttributeType> types = 
			subscriberService.listSubscriberAttributesTypes();
		
		RequestUtil.getHistoryStack(request).push( new Link("NL.SETTINGS", 
				"/admin/nl_settings", true, 0) );
		
		logger.debug("-");
		return new PreparedModelAndView("nl_settings").
			addToModel("types", types).
			addToModel("language", language).
			addToModel("confirmationTemplate", mailTemplateService.
					findConfirmationText(language.getId())).
			addToModel("languages", Language.getItems()).get();
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView saveParameters(HttpServletRequest request, 
			HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		List<SubscriberAttributeType> types = 
			subscriberService.listSubscriberAttributesTypes();
		for (SubscriberAttributeType t : types){
			t.setVisible(false);
		}
		for (Map.Entry<String, String[]> entry : 
			(Set<Map.Entry<String, String[]>>)request.getParameterMap().entrySet())
			if (entry.getKey().startsWith("attr")){
				try{
					Long attrId = new Long(entry.getValue()[0]);
					for (SubscriberAttributeType t : types){
						if (t.getId().equals(attrId)){
							t.setVisible(true);
							break;
						}
					}
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		subscriberService.saveSubscriberAttributes(types);
		
		String confText = request.getParameter("rte_text");
		Language language = getLanguageFromRequest(request);
		
		mailTemplateService.updateConfirmationText(confText.trim(), language.getId());
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_settings?lang_id=" + language.getId());
	}
	
	private Language getLanguageFromRequest(HttpServletRequest request){
		try{
			Long langId = ServletRequestUtils.getLongParameter(request, "lang_id");
			Language language = null;
			if (langId == null){
				language = NegesoRequestUtils.getInterfaceLanguage(request);
			}else{
				language = Language.findById(langId);
			}
			return language;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return Language.getDefaultLanguage();
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}
}
