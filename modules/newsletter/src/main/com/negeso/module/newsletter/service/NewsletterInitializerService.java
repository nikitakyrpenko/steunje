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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class NewsletterInitializerService {

	private static final Logger logger = Logger.getLogger(NewsletterInitializerService.class);
	
	private PublicationSchedulerService publicationSchedulerService;
	private MailBounceService mailBounceService;
	private MailingService mailingService;

	public void startup() {
		logger.debug("+");

        publicationSchedulerService.startup();
        mailBounceService.startup();
        mailingService.startup();

        logger.debug("-");
	}

	public void shutdown() {
		logger.debug("+");
		
		publicationSchedulerService.shutdown();
		mailBounceService.shutdown();
		mailingService.shutdown();

		logger.debug("+");
	}
	
	public void setPublicationSchedulerService( PublicationSchedulerService service) {
		this.publicationSchedulerService = service;
	}

	public void setMailBounceService(MailBounceService mailBounceService) {
		this.mailBounceService = mailBounceService;
	}
	
	public void setMailingService(MailingService mailingService) {
		this.mailingService = mailingService;
	}

}
