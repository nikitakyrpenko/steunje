/*
 * @(#)Id: NotificationWrapper.java, 07.04.2008 13:20:05, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service.mailing;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.negeso.framework.mailer.MailClient;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.service.NewPublicationService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class NotificationWrapper implements Runnable {
	
	private static final Logger logger = Logger.getLogger(NotificationWrapper.class);

	private Long publicationId;
	private NotificationStrategy strategy;
	private NewPublicationService publicationService;
	
	public NotificationWrapper(Long publicationId, 
			NotificationStrategy strategy,
			NewPublicationService publService) {
		this.publicationId = publicationId;
		this.strategy = strategy;
		this.publicationService = publService;
	}
	
	public void run() {
		logger.debug("+");
		
		Publication publication = publicationService.getPublicationById(publicationId);

		MailClient mailClient = new MailClient();
		
		try {
			mailClient.sendHTMLMessage(
					strategy.getMailAddress(), 
					getFromEMail(publication), 
					strategy.getSubject(publication), 
					strategy.getText(publication));
		} catch (MessagingException e) {
			logger.error("notification mail wasn't sent: " + e.getMessage(), e);
		}
		logger.debug("-");
	}
	
	private String getFromEMail(Publication publication){
		return (publication.getFeedbackEmail() == null) ? Configuration.getEmailReturnPath() : publication.getFeedbackEmail();
	}
}
