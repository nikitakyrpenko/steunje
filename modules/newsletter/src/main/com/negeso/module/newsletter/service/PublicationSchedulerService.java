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

import static java.lang.String.format;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.negeso.framework.dao.Callback;
import com.negeso.framework.dao.SessionTemplate;
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
public class PublicationSchedulerService {
	
	private class SenderTask implements Runnable {
		
		private Long publicationId;
		private String publicationTitle;
		private boolean isActive = true;
		
		public SenderTask(Long publicationId, String publicationTitle) {
			this.publicationId = publicationId;
			this.publicationTitle = publicationTitle;
		}
		
		public void run() {
			if (!isActive) {
				return;
			}
			logger.debug("+");
			if (logger.isDebugEnabled()) {
				logger.debug(format("publication '%s' is sending out", publicationTitle));
			}
			sessionTemplate.execute(new Callback() {
				public void process() {
					mailingService.send(publicationId, null);
				}
			});
			logger.debug("-");
		}

		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}
	
	private static final Logger logger = Logger.getLogger(PublicationSchedulerService.class);

	private SessionTemplate sessionTemplate;
	
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(
			Configuration.SCHEDULED_THREAD_POOL_SIZE);
	private Map<Long, SenderTask> publicationIdToTasksMap = new HashMap<Long, SenderTask>();
	
	private MailingService mailingService;
	private NewPublicationService publicationService;
	
	public void startup() {
		logger.debug("+");

		if (logger.isInfoEnabled()) {
			logger.info(format("Starting mail scheduler, time: %s",
					new Date(System.currentTimeMillis())));
		}

		sessionTemplate.execute(new Callback() {
			public void process() {
				List<Publication> scheduledPublications = publicationService.listPublicationByScheduledDate();
				startPublications(scheduledPublications);
			}
		});
		
		logger.debug("-");
	}

	public void shutdown() {
		logger.debug("+");
		
		logger.info("Shutdown of the publication service");
		SCHEDULED_EXECUTOR_SERVICE.shutdown();
		publicationIdToTasksMap.clear();
		logger.debug("-");
	}
	
	public void startPublications(List<Publication> scheduledPublications) {
		logger.debug("+");
		
		for (Publication p : scheduledPublications) {
			try {
				schedulePublication(p, true);
			} catch (Exception e) {
				logger.error("Could not schedule publication '"
						+ p.getTitle() + "'", e);
			}
		}
		
		logger.debug("-");
	}
	
	public synchronized void  schedulePublication(final Publication publication, boolean scheduled) {
		logger.debug("+");
		unschedulePublication(publication);
		if (scheduled) {
			long relativeDelay = publication.getPublishDate().getTime() - System.currentTimeMillis(); 
			SenderTask st = new SenderTask(publication.getId(), publication.getTitle());
			SCHEDULED_EXECUTOR_SERVICE.schedule(st, relativeDelay, TimeUnit.MILLISECONDS);
			publicationIdToTasksMap.put(publication.getId(), st);
			if (logger.isDebugEnabled()) {
				logger.debug(format("Publication '%s' was scheduled, scheduled time: %s",
						publication.getTitle(), publication.getPublishDate()));
			}
		}
		logger.debug("-");
	}

	private void unschedulePublication(final Publication publication) {
		SenderTask st = publicationIdToTasksMap.get(publication.getId());
		if (st != null) {
			st.setActive(false);
		}
	}

	public void setMailingService(MailingService mailingService) {
		this.mailingService = mailingService;
	}

	public void setPublicationService(NewPublicationService publicationService) {
		this.publicationService = publicationService;
	}

	public void setSessionTemplate(SessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;
	}
	
}
