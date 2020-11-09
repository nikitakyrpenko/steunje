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
package com.negeso.module.newsletter.service;

import org.apache.log4j.Logger;

import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.Publication;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class NewsletterLimitationService {
	
	private static final Logger logger = Logger.getLogger(NewsletterLimitationService.class);
	
	private NewPublicationService publicationService;
	
	public NewPublicationService getPublicationService() {
		return publicationService;
	}

	public void setPublicationService(NewPublicationService publicationService) {
		this.publicationService = publicationService;
	}
	
	public boolean canSend(Long publicationId) {
		logger.debug("+");
		
	    if (!Configuration.isSendingOn()) {
	        logger.info("-DEMOVERSION: Sending is OFF");
	        return false;
	    }

	    Publication publication = publicationService.getPublicationById(publicationId);
	    
	    Long currentSentValue = publication.getSubscribersNumber() + 
	    	publicationService.countSentMailByCurrentMonth(publication.getId());
	    
	    if (currentSentValue > Configuration.getMaxLettersCount()){
	    	logger.error("limit of sent mails is reached: " + currentSentValue + ">" + Configuration.getMaxLettersCount());
	    	return false;
	    }
	    
	    logger.debug("-");
	    return true;
	}
}
