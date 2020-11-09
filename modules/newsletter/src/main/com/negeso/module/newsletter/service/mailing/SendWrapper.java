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

import static java.lang.String.format;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.MailSendException;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Callback;
import com.negeso.framework.dao.SessionTemplate;
import com.negeso.framework.domain.Language;
import com.negeso.framework.i18n.DatabaseResourceBundleMessageSource;
import com.negeso.framework.mailer.MailClient;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.Configuration.EmailState;
import com.negeso.module.newsletter.bo.Attachment;
import com.negeso.module.newsletter.bo.Mailing;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.service.MailingService;
import com.negeso.module.newsletter.service.NewPublicationService;
import com.negeso.module.newsletter.service.synchronization.IdMutexProvider;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class SendWrapper implements Runnable {

	private static final Logger logger = Logger.getLogger(SendWrapper.class);

	public static final String LINK_TO_PAGE = "NL.LINK_TO_PAGE_ETC";
	
	private List<Long> mailingIds;
	private Long langId;
	private SendStrategy sendStrategy;
	
	private NewPublicationService publicationService;
	private MailingService mailingService;
	
	private SessionTemplate sessionTemplate;
	private IdMutexProvider mutexProvider;

	private MailClient mailClient = new MailClient();

	public SendWrapper(List<Long> mailingIds,
			SendStrategy sendStrategy,
			NewPublicationService publicationService,
			MailingService mailingService,
			SessionTemplate sessionTemplate, Long langId,
			IdMutexProvider mutexProvider) {
		
		this.mailingIds = mailingIds;
		this.sendStrategy = sendStrategy;
		this.publicationService = publicationService;
		this.mailingService = mailingService;
		this.sessionTemplate = sessionTemplate;
		this.langId = langId;
		this.mutexProvider = mutexProvider;
	}

	public void run() {
		logger.debug("+");
		doJob();
		logger.debug("-");
	}
	
	public void doJob() {
		sessionTemplate.execute(new Callback() {
			
			public void process() {
				
				try {
					logger.error("send mail thread start");
					Set<Long> publicationsForUpdate = doSequentially();
					for (Long publicationId : publicationsForUpdate) {
						if (mailingService.getMailings(publicationId).isEmpty()) {
							sendStrategy.definePublicationStatus(publicationService.getPublicationById(publicationId), Configuration.PUBLICATION_STATUS_SENT);
						}
					}
					
					logger.error("send mail thread successful");
				} catch (Exception e) {
					logger.error("send mail thread failed, unexpected problem");
					// TODO implement global logic to store fatal
					// errors, yet it is put into the log
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	private Set<Long> doSequentially() throws Exception {
		logger.error("+");

		logger.error(format("sending publication for %s subscribers", mailingIds.size()));
		
		Map<Long, DataSource[]> pubToAttachMap = new HashMap<Long, DataSource[]>();
		Set<Long> publicationsIdsForUpdate = new HashSet<Long>();
		for (final Long mailingId : mailingIds) {
			IdMutexProvider.Mutex mutex = mutexProvider.getMutex(mailingId.toString());
			synchronized (mutex) {
				Mailing mailing = mailingService.findById(mailingId);
				if (mailing != null 
						&& mailing.getMailingState().getName().equals(EmailState.NOT_SENT.getName()) 
						&& mailing.getRetryCount() < 3) {
					Publication publication = mailing.getPublication();
					Subscriber subscriber = mailing.getSubscriber();
					DataSource[] attachments = pubToAttachMap.get(publication.getId());
					if (attachments == null) {
						attachments = getAttachments(publication);
						pubToAttachMap.put(publication.getId(), attachments);
					}
					if (sendStrategy instanceof CustomSendStrategy) {
						publicationsIdsForUpdate.add(publication.getId());
						Long langId = subscriber.getSubscriptionLangId();
						subcriberPublucation(publication, langId, subscriber.isHtml(), 
								replaceAttributes(publication.getText(langId), subscriber), 
								attachments, subscriber, mailing);
					}else{
						subcriberPublucation(publication, langId ,
								true, publication.getText(langId), attachments, subscriber, mailing);
						subcriberPublucation(publication, langId ,
								false, publication.getPlainText(langId), attachments, subscriber, mailing);
					};
				}
			}
        }		
		logger.error("-");
		return publicationsIdsForUpdate;
	}
	
	private void subcriberPublucation(Publication publication, Long langId, boolean isHtml,
			String replacedText, DataSource[] attachments, Subscriber subscriber, Mailing mailing) throws Exception{
		String subject = sendStrategy.getSubject(publication, langId, isHtml);
		String[] to = {null, subscriber.getEmail()};
		String feedbackEmail = StringUtils.isNotBlank(publication.getFeedbackEmail()) ? publication.getFeedbackEmail() : Configuration.getEmailReturnPath();
		String[] from = MailClient.buildAddressUseingEncoding(
				StringUtils.isNotBlank(publication.getFeedbackName()) ? publication.getFeedbackName() : publication.getTitle(langId),
				feedbackEmail);

		Long uniqId = Math.abs(new Random().nextLong());
		String body;
		if (isHtml) {
			body = prepareHtmlBody(publication, replacedText.trim(), uniqId);
		} else {
			body = preparePlainTextBody(publication, replacedText.trim());
		}
		
		String contentType = (isHtml) ? Configuration.HTML_CONTENT_TYPE
				: Configuration.PLAIN_CONTENT_TYPE;
		Map<String, String> headers = getHeaders(publication, uniqId);

		if (logger.isDebugEnabled()) {
			logger.debug(format("Email info: subject=%s, email=%s, body=%s, from=%s,  attachments=%s, header=%s]",
					subject, subscriber.getEmail(), body, from, attachments, headers));
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> e : headers.entrySet()) {
				sb.append(e.getKey()).append("=").append(e.getValue()).append(", ");
			}
			logger.debug("header (details)=" + sb);
			logger.debug("return path=" + feedbackEmail);
		}

		List<Subscriber> subscribers = new LinkedList<Subscriber>();
		subscribers.add(subscriber);
		try {
			mailClient.sendBouncedMessage(to, from,
					feedbackEmail, subject,
					body, attachments, MailClient.DEFAULT_ENCODING,
					contentType, headers);
			
			logger.error("send the mail successfull");
			mailingService.updateMailing(mailing.getId(), EmailState.SENT, null, uniqId);
		} catch (UnsupportedEncodingException e) {
			logger.error("no failed FROM addresses: fromPersonal - " + from[0] + "; email - "+ from[1] );	
			mailingService.updateMailing(mailing.getId(), EmailState.NOT_SENT, e.getMessage(), uniqId);
		} catch (SendFailedException sfe) {
			logger.warn("send the mail failed, probably wrong email");
			logger.debug(sfe.getMessage(), sfe);
			Address[] failedAddresses = sfe.getInvalidAddresses();
			if (failedAddresses != null && failedAddresses.length == 0) {
				// TODO implement global logic to store
				// fatal errors, yet it is put into the log
				logger.error("no failed addresses, the general mail server connection failure occurs", sfe);
			} else {
				logger.error(subscriber.getEmail());
				mailingService.updateMailing(mailing.getId(), EmailState.NOT_SENT, Configuration.INVALID_EMAIL_STATE_MESSAGE, uniqId);
			}
		} catch (AddressException e) {
			mailingService.updateMailing(mailing.getId(), EmailState.NOT_SENT, Configuration.INVALID_EMAIL_STATE_MESSAGE, uniqId);
		} catch(MessagingException e){
			logger.error("-error", e);
			mailingService.updateMailing(mailing.getId(), EmailState.NOT_SENT, e.getMessage(), uniqId);
		} catch(MailSendException e){
			logger.error("-error", e);
			mailingService.updateMailing(mailing.getId(), EmailState.NOT_SENT, e.getMessage(), uniqId);
		}
	}
	
	private void fake()  throws MessagingException, UnsupportedEncodingException {
		throw new UnsupportedEncodingException();
	}

	private String replaceAttributes(String text, Subscriber subscriber){
		return publicationService.getUpdatedForSubscriberText(text, subscriber.getAttributes());
	}

	private DataSource[] getAttachments(Publication publication) {
		logger.debug("+");

		List<FileDataSource> fdsList = new ArrayList<FileDataSource>();
		List<Attachment> attachments = publication.getAttachments();
		for (Attachment attachment : attachments) {
			File file = Repository.get().getResource(attachment.getLink()).getFile();
			FileDataSource fds = new FileDataSource(file);
			fdsList.add(fds);
		}

		logger.debug("-");
		return fdsList.toArray(new DataSource[fdsList.size()]);
	}

	private String prepareHtmlBody(Publication publication, String text, long uniqId) throws Exception {
		StringBuilder sb = null;
		Language lang = Language.findById(publication.getLangId());

		StringBuilder pageLinkBuilder = new StringBuilder();
		if (Configuration.isSpecialPagesEnabled()){
			DatabaseResourceBundleMessageSource s = new DatabaseResourceBundleMessageSource();
			s.setBasename("dict_newsletter.xsl");
			
			try {
				pageLinkBuilder.append("<br/>");
                pageLinkBuilder.append("<a href=\"")
                        .append(publicationService.getPublicationPageLink(publication, lang.getId()))
                        .append("\"> ")
                        .append(s.getMessage(LINK_TO_PAGE, null, new Locale(lang.getCode())))
                        .append("</a> ");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		if (text != null) {
            sb = new StringBuilder(text.length() + 200);
            sb
            .append("<html><head><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=utf-8\"></head><body>")
            .append(text)

            // notification link
            .append("<br/><a href=\"")
            .append(Configuration.getNotificationHostName())
            .append("nl_read_notify.gif?read_notification=true&mailing_id=")
            .append(uniqId).append("\" style=\"display:none;\" >&nbsp;</a>")
            .append(pageLinkBuilder)
            .append("</body></html>");
		}
		return sb.toString().trim();
	}

	private String preparePlainTextBody(Publication publication, String text) throws Exception {
		StringBuilder sb = null;
		Language lang = Language.findById(publication.getLangId());

		StringBuilder pageLinkBuilder = new StringBuilder();
		if (Configuration.isSpecialPagesEnabled()){
			DatabaseResourceBundleMessageSource s = new DatabaseResourceBundleMessageSource();
			s.setBasename("dict_newsletter.xsl");
			
			try {
				pageLinkBuilder.append("\n" + publicationService.getPublicationPageLink(
						publication, lang.getId()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		if (text != null) {
			sb = new StringBuilder(text.length());
			sb.append(StringUtil.stripTagsEx(text));
			sb.append(pageLinkBuilder);
		}
		return sb.toString().trim();
	}
	
	private Map<String, String> getHeaders(Publication publication, Long uniqId) {
		// TODO implement it
		logger.debug("+");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Mailer", Env.WCMS_X_MAILER);
		headers.put("X-Mailing-Id", String.valueOf(uniqId));
		//	        headers.put("List-Subscribe", publicationDao.getSubscribeLink(publication);
		//	        headers.put("List-Unsubscribe", publicationDao. getUnsubscribeLink(publication);
		logger.debug("-");
		return headers;
	}

}
