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
package com.negeso.module.newsletter.service.mailing;

import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.module.newsletter.Configuration.EmailState;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.PublicationState;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.service.NewPublicationService;
import com.negeso.module.newsletter.service.StateService;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class CustomSendStrategy implements SendStrategy {

	private static final Logger logger = Logger.getLogger(CustomSendStrategy.class);
	
	private StateService stateService;
	private NewPublicationService publicationService;
	
	public CustomSendStrategy(StateService stateService, NewPublicationService publicationService) {
		this.stateService = stateService;
		this.publicationService = publicationService;
	}
	
	public void definePublicationStatus(Publication p, String status) {
		logger.debug("+");

		PublicationState sent = publicationService.getPublicationStateByName(status);
		p.setPublicationState(sent);
		publicationService.save(p);
		
		logger.debug("-");
	}
	public String getSubject(Publication p, Long langId, boolean isHtml) {
		return p.getTitle(langId);
	}
	public void update(Publication publication,
			List<Subscriber> subscribers, EmailState emailState,
			String message, Long uniqId) {
		logger.debug("+");
		
		stateService.updateMailingState(publication, subscribers, emailState, message, uniqId);			
		
		logger.debug("-");
	}

	public boolean isSystemSending() {
		return false;
	}

}
