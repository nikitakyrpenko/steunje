/*
 * @(#)Id: PublicationValidator.java, 19.03.2008 14:18:16, Dmitry Fedotov
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

import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.service.NewPublicationService;
import com.negeso.module.newsletter.service.StatisticsService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class PublicationValidator implements Validator {
	
	private static final Logger logger = Logger.getLogger(PublicationValidator.class);
	
	private NewPublicationService publicationService;
//	private StatisticsService statisticsService;

	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return clazz.equals(Publication.class);
	}

	public void validate(Object obj, Errors errors) {
		logger.debug("+");
		
		Publication publication = (Publication)obj;
		if (publication.getTitle() == null || publication.getTitle().trim().equals(""))
			errors.rejectValue("title", "Title is empty");
		
		if (!publicationService.isUnique(publication))
			errors.rejectValue("title", "Title is duplicated");
		
		logger.debug("-");
	}

	public NewPublicationService getPublicationService() {
		return publicationService;
	}

	public void setPublicationService(NewPublicationService publicationService) {
		this.publicationService = publicationService;
	}

/*
	public void setStatisticsService(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}
*/
}
