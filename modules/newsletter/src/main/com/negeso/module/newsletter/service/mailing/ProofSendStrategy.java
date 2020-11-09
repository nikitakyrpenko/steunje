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

import com.negeso.framework.LocalizedMessage;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.Configuration.EmailState;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.service.StateService;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class ProofSendStrategy implements SendStrategy {
	
	private static final Logger logger = Logger.getLogger(ProofSendStrategy.class);

	private StateService stateService;
	
	public ProofSendStrategy(StateService stateService) {
		this.stateService = stateService;
	}
	
	public void definePublicationStatus(Publication p, String status) {
		logger.debug("+-");
	}
	
	public String getSubject(Publication p, Long langId, boolean isHtml) {
		logger.debug("+");

		LocalizedMessage proofingLocalizedMessage = new LocalizedMessage(Configuration.PROOFING_MESSAGE_KEY);

		logger.debug("-");
		if (isHtml){
			return proofingLocalizedMessage.getMessage(langId.intValue()) + " (Html): " + p.getTitle(langId); 
		}else{
			return proofingLocalizedMessage.getMessage(langId.intValue()) + " (plain text): " + p.getTitle(langId);		
		}
	}

	public void update(Publication publication, List<Subscriber> subscribers, EmailState emailState, String message, Long uniqId) {
		logger.debug("+");
		
		stateService.updateMailingState(publication, subscribers, emailState, message, uniqId);			
		
		logger.debug("-");
	}
	
	public boolean isSystemSending() {
		return true;
	}

}
