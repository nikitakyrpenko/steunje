/*
 * @(#)Id: SubscriptionCategoryValidator.java, 05.03.2008 14:45:51, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.validator;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.negeso.module.newsletter.bo.MailTemplate;
import com.negeso.module.newsletter.service.MailTemplateService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class MailTemplateValidator implements Validator {

	private static final Logger logger = Logger.getLogger(MailTemplateValidator.class);
	
	private MailTemplateService mailTemplateService;
	
	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return clazz.equals(MailTemplate.class);
	}
	
	public void validate(Object obj, Errors errors) {
		logger.debug("+");

		MailTemplate mailTemplate = (MailTemplate) obj;

		if (mailTemplate.getTitle() == null || mailTemplate.getTitle().trim().equals(""))
			errors.rejectValue("title", "Title is empty");
		
		if (!mailTemplateService.isUnique(mailTemplate))
			errors.rejectValue("title", "Title is duplicated");

		logger.debug("-");
	}

}
