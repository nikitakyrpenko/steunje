/*
 * @(#)Id: SubscriberGroupValidator.java, 06.03.2008 16:01:14, Dmitry Fedotov
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

import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.service.SubscriberGroupService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriberGroupValidator implements Validator{

	private static final Logger logger = Logger.getLogger(SubscriberGroupValidator.class);
	
	private SubscriberGroupService subscriberGroupService;
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return clazz.equals(SubscriberGroup.class);
	}

	public void validate(Object obj, Errors errors) {
		logger.debug("+");
		
		SubscriberGroup group = (SubscriberGroup)obj;
		if (group.getTitle() == null || group.getTitle().trim().equals(""))
			errors.rejectValue("title", "Title is empty");
		
		if (!subscriberGroupService.isUnique(group))
			errors.rejectValue("title", "Title is duplicated");
		
		logger.debug("-");
	}

	public void setSubscriberGroupService(SubscriberGroupService service) {
		this.subscriberGroupService = service;
	}

}
