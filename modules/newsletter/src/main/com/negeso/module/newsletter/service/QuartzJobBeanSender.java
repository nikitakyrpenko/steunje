package com.negeso.module.newsletter.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.negeso.framework.Env;
import com.negeso.framework.dao.SessionTemplate;
import com.negeso.framework.domain.Language;
import com.negeso.module.newsletter.service.mailing.CustomSendStrategy;
import com.negeso.module.newsletter.service.mailing.SendWrapper;
import com.negeso.module.newsletter.service.synchronization.IdMutexProvider;

public class QuartzJobBeanSender extends QuartzJobBean {
	
	private StateService stateService;
	private NewPublicationService publicationService;
	private MailingService mailingService;
	private SessionTemplate sessionTemplate;
	private IdMutexProvider mutexProvider;
	
	
	private static Logger logger = Logger.getLogger(QuartzJobBeanSender.class);
	
	private static boolean locked = false;

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		if (isNotLocked()) {
			setLocked(true);
			try {
				Language language = Language.findByCode(Env.getDefaultLanguageCode());
				List<Long> mailings = mailingService.getMailings(null);
				if (!mailings.isEmpty()) {
					CustomSendStrategy sendStrategy = new CustomSendStrategy(stateService, publicationService);
					SendWrapper send = new  SendWrapper(mailings, sendStrategy, publicationService, mailingService, sessionTemplate, language.getId(), mutexProvider);
					send.doJob();
				}
			} catch (Throwable e) {
				logger.error("Error: ", e);
			} finally {
				setLocked(false);
			}
		}
		
	}

	private boolean isNotLocked() {
		return !QuartzJobBeanSender.locked;
	}

	public void setLocked(boolean locked) {
		QuartzJobBeanSender.locked = locked;
	}


	public void setStateService(StateService stateService) {
		this.stateService = stateService;
	}


	public void setPublicationService(NewPublicationService publicationService) {
		this.publicationService = publicationService;
	}


	public void setMailingService(MailingService mailingService) {
		this.mailingService = mailingService;
	}


	public void setSessionTemplate(SessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;
	}


	public void setMutexProvider(IdMutexProvider mutexProvider) {
		this.mutexProvider = mutexProvider;
	}
}
