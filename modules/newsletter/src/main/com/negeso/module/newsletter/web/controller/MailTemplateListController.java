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
package com.negeso.module.newsletter.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.newsletter.I18nUtil;
import com.negeso.module.newsletter.bo.MailTemplate;
import com.negeso.module.newsletter.service.MailTemplateService;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class MailTemplateListController extends MultiActionController {

	private static final Logger logger = Logger.getLogger(MailTemplateListController.class);
	
	private static final String HISTORY_STACK_LINK_TITLE_KEY = "NL.BROWSE_MAIL_TEMPLATES"; // $NON-NLS-1$
	private static final String HISTORY_STACK_URL_PATH = "/admin/nl_mailtemplates"; // $NON-NLS-1$
	
	private static final String VIEW_NAME = "nl_mailtemplates"; // $NON-NLS-1$
	private static final String MODEL_NAME = "templates"; // $NON-NLS-1$
	
	private MailTemplateService mailTemplateService;
	
	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");

		Language language = NegesoRequestUtils.getInterfaceLanguage(request); 
		if (logger.isDebugEnabled()) {
			logger.debug("language=" + ReflectionToStringBuilder.toString(language));
		}
		List<MailTemplate> list = mailTemplateService.findAllMailTemplates(language.getId());
		I18nUtil.setCurrentLanguageForList(list, language.getId());

		Link link = new Link(HISTORY_STACK_LINK_TITLE_KEY, HISTORY_STACK_URL_PATH, true, -1);
		RequestUtil.getHistoryStack(request).push(link);
		
		logger.debug("-");
		return new ModelAndView(VIEW_NAME, MODEL_NAME, list);
	}

	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		Long id = ServletRequestUtils.getRequiredLongParameter(request, "id");
		if (logger.isDebugEnabled()) {
			logger.debug("mail template id=" + id);
		}
		String status = mailTemplateService.deleteMailTemplate(id);
		mailTemplateService.getMailTemplateDao().flush();
		
		logger.debug("-");
		return list(request, response).addObject("status", status);
	}

}
