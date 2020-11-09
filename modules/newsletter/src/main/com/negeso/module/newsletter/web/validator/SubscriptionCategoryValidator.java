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

import com.negeso.module.newsletter.bo.SubscriptionCategory;
import com.negeso.module.newsletter.service.SubscriptionCategoryService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriptionCategoryValidator implements Validator {

	private static final Logger logger = Logger.getLogger(SubscriptionCategoryValidator.class);
	
	private SubscriptionCategoryService subscriptionCategoryService;
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return clazz.equals(SubscriptionCategory.class);
	}

	public void validate(Object obj, Errors errors) {
		logger.debug("+");
		
		SubscriptionCategory category = (SubscriptionCategory) obj;
		
		if (category.getTitle().length() == 0)
			errors.rejectValue("title", "Title is empty");
		
		if (!subscriptionCategoryService.isUnique(category))
			errors.rejectValue("title", "Title is duplicated");

		logger.debug("-");
	}

	public void setSubscriptionCategoryService(SubscriptionCategoryService service) {
		this.subscriptionCategoryService = service;
	}

}
