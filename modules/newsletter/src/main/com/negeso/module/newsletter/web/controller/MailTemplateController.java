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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
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
public class MailTemplateController extends SimpleFormController {

    private static final Logger logger = Logger.getLogger(MailTemplateController.class);

    public static final String HISTORY_STACK_LINK_TITLE_KEY = "NL.NEW_NEWSLETTER_TEMPLATE"; // $NON-NLS-1$

    private MailTemplateService mailTemplateService;
	
	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}
	
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		logger.debug("+");
		Language language = NegesoRequestUtils.getInterfaceLanguage(request); 
		Long id = ServletRequestUtils.getLongParameter(request, "id", -1);
		if (logger.isDebugEnabled()) {
			logger.debug("mail template id=" + id);
		}
        MailTemplate mailTemplate;
        if (id > 0) {
			mailTemplate = mailTemplateService.getMailTemplate(id);
			mailTemplate.setCurrentLangId(language.getId());
			String title = mailTemplate.getTitle();
			Link link = new Link(title, getFormView() + "?id=" + id, false);
			RequestUtil.getHistoryStack(request).push(link);
			logger.debug("-");
		} else {
            mailTemplate = new MailTemplate();
            mailTemplate.setCurrentLangId(language.getId());
            Link link = new Link(HISTORY_STACK_LINK_TITLE_KEY, getFormView(), true);
            RequestUtil.getHistoryStack(request).push(link);
            logger.debug(mailTemplate);
            logger.debug("-");
        }
        return mailTemplate;
    }

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("languages", Language.getItems());
		return model;
	}
	
    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest req, HttpServletResponse resp, Object o, BindException e) throws Exception {
        MailTemplate mailTemplate = (MailTemplate) o;

        if (mailTemplate.canBeCopiedToAllLanguages()) {
            mailTemplateService.copyMailTemplateToAllLanguages(mailTemplate);
            logger.info("mail template was copied to all languages");
        }

        mailTemplateService.saveMailTemplate(mailTemplate);

        return new ModelAndView(getSuccessView());
    }

}
