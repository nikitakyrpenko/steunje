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

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activation.DataSource;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.dao.SessionTemplate;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.mailer.MailClient;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.Configuration.EmailState;
import com.negeso.module.newsletter.bo.Mailing;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.dao.MailingDao;
import com.negeso.module.newsletter.dao.MailingStateDao;
import com.negeso.module.newsletter.service.mailing.CustomSendStrategy;
import com.negeso.module.newsletter.service.mailing.LimitationNotificationStrategy;
import com.negeso.module.newsletter.service.mailing.NotificationWrapper;
import com.negeso.module.newsletter.service.mailing.ProofSendStrategy;
import com.negeso.module.newsletter.service.mailing.SendStrategy;
import com.negeso.module.newsletter.service.mailing.SendWrapper;
import com.negeso.module.newsletter.service.synchronization.IdMutexProvider;
import com.negeso.module.newsletter.web.component.SubscriberAttributeParameters;


/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class MailingService {
	
	private static final Logger logger = Logger.getLogger(MailingService.class);

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	
	private MailingDao mailingDao;
	private MailingStateDao mailingStateDao;

	private SubscriberGroupService subscriberGroupService;
	private StateService stateService;
	private NewPublicationService publicationService;
	private NewsletterLimitationService limitationService;
	private SubscriberService subscriberService;
	private MailTemplateService mailTemplateService;
	private IdMutexProvider mutexProvider;
	
	private static final String PROOFING_MEMBERS = "Proofing Members";
	
	private SessionTemplate sessionTemplate;
	
	public void startup() {
		logger.debug("+");
		
		logger.debug("-");
	}
	
	public void shutdown() {
		logger.debug("+");
		
		EXECUTOR.shutdown();

		logger.debug("-");
	}
	
	public void send(Long publicationId, Long langId) {
		logger.debug("+");
	
		Publication publication = publicationService.getPublicationById(publicationId);
		List<SubscriberGroup> groups = publication.getSubscriberGroups();
		List<Long> mailings = createMailingList(publicationId);;
		if (logger.isDebugEnabled()) {
			logger.debug(format("Publication %s has %s assigned groups, %s subscribers",
					publication.getTitle(), groups.size(), mailings.size()));
		}
		
		CustomSendStrategy css = new CustomSendStrategy(stateService, publicationService);
		send(publicationId, mailings, css, langId);
		
		logger.debug("-");
	}
	
	private static final String INSERT_MAILING_SQL = 
			"INSERT INTO nl_mailing (publication_id, subscriber_id, mailing_state_id, created_date, retry_count) " +
			"	SELECT DISTINCT ?, s.id, ?, ?::timestamp, 0 " +
			"	FROM nl_subscriber s " +
			"	INNER JOIN nl_subscriber2group g ON s.id = g.subscriber_id " +
			"	INNER JOIN nl_publication2group p ON g.group_id = p.group_id " +
			"	WHERE s.activated = true AND p.publication_id = ?";

	private List<Long> createMailingList(Long publicationId) {
		Connection conn = null;
		PreparedStatement stmt = null;
		List<Long> mailings = Collections.emptyList();
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(INSERT_MAILING_SQL);
			Timestamp createdDate = new Timestamp(System.currentTimeMillis());
			stmt.setLong(1, publicationId);
			stmt.setLong(2, mailingStateDao.findByName(EmailState.NOT_SENT.getName()).getId());
			stmt.setTimestamp(3, createdDate);
			stmt.setLong(4, publicationId);
			stmt.execute();
			mailings = getMailingsByDate(conn, createdDate);
		} catch (Exception e) {
			logger.error("Error: ", e);
		} finally {
			DBHelper.close(null, stmt, conn);
		}
		return mailings;
	}
	
	private static final String SELECT_MAILING_SQL = "SELECT id FROM nl_mailing WHERE created_date=? ORDER BY id";
	
	private List<Long> getMailingsByDate(Connection conn, Timestamp createdDate) throws SQLException {
		List<Long> mailings = new ArrayList<Long>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(SELECT_MAILING_SQL);
			stmt.setTimestamp(1, createdDate);
			rs = stmt.executeQuery();
			while (rs.next()) {
				mailings.add(rs.getLong(1));				
			}
		} finally {
			DBHelper.close(rs, stmt, null);
		}
		return mailings;
	}

	private static final String INSERT_PROOF_MAILINGS_SQL = 
		"INSERT INTO nl_mailing (publication_id, subscriber_id, mailing_state_id, created_date, retry_count) " +
		"	SELECT DISTINCT ?, s.id, ?, ?::timestamp, 0 " +
		"	FROM nl_subscriber s " +
		"	INNER JOIN nl_subscriber2group g ON s.id = g.subscriber_id " +
		"	WHERE s.activated = true AND g.group_id = ?";
	
	private List<Long> createProofList(Long publicationId, Long proofGroupId) {
		Connection conn = null;
		PreparedStatement stmt = null;
		List<Long> mailings = Collections.emptyList();
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(INSERT_PROOF_MAILINGS_SQL);
			Timestamp createdDate = new Timestamp(System.currentTimeMillis());
			stmt.setLong(1, publicationId);
			stmt.setLong(2, mailingStateDao.findByName(EmailState.NOT_SENT.getName()).getId());
			stmt.setTimestamp(3, createdDate);
			stmt.setLong(4, proofGroupId);
			stmt.execute();
			mailings = getMailingsByDate(conn, createdDate);
		} catch (Exception e) {
			logger.error("Error: ", e);
		} finally {
			DBHelper.close(null, stmt, conn);
		}
		return mailings;
	}
	
	private static final String SELECT_NOT_SENT_MAILING_SQL = 
		"	SELECT nl_mailing.id " +
		"	FROM nl_mailing " +
		" 	LEFT JOIN nl_mailing_state " +
		"		ON nl_mailing.mailing_state_id = nl_mailing_state.id " +
		"	WHERE " +
		"			nl_mailing_state.name='not_sent' " +
		"		%s " +
		"		AND " +
		"			nl_mailing.retry_count < 3 " +
		"		ORDER BY id DESC LIMIT 1000"; 
	
	
	public List<Long> getMailings(Long publicationId) {
		List<Long> mailings = new LinkedList<Long>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(String.format(SELECT_NOT_SENT_MAILING_SQL, publicationId != null ? "AND publication_id=" + publicationId : StringUtils.EMPTY));
			rs = stmt.executeQuery();
			while (rs.next()) {
				mailings.add(rs.getLong(1));				
			}
		} catch (SQLException e) {
			logger.error("Error: ", e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
		return mailings;
	}

	public void proof(Long publicationId, Long langId) {
		logger.debug("+");

		SendStrategy ss = new ProofSendStrategy(stateService);
		SubscriberGroup subscriberGroup = subscriberGroupService.getGroupByName(PROOFING_MEMBERS, langId);
		if(subscriberGroup != null){
			send(publicationId, createProofList(publicationId, subscriberGroup.getId()), ss, langId);
		}else{
			logger.error("Group not found by title 'Proof Members'");	
		}
		
		logger.debug("-");
	}

	private void send(final Long publicationId,
			List<Long> mailingIds, final SendStrategy ss, final Long langId) {
		logger.debug("+");
		if (mailingIds == null || mailingIds.isEmpty()) {
			return;
		}
		limitationService.setPublicationService(publicationService);
		if (!ss.isSystemSending() && !limitationService.canSend(publicationId)){
			EXECUTOR.execute(
					new NotificationWrapper(
							publicationId, 
							new LimitationNotificationStrategy(),
							publicationService));
			logger.error("maximum e-mail number reached");
			return;
		}
		
		EXECUTOR.execute(new SendWrapper(mailingIds, ss,
				publicationService,  this, sessionTemplate, langId, mutexProvider));
		
		logger.debug("-");
	}
	
	public static class NotificationStatus {
		private String message;
		private String email;
		private boolean isOk;
		public NotificationStatus(String message, boolean isOk) {
			this.message = message;
			this.isOk = isOk;
		}
		public String getMessage() {
			return message;
		}
		public boolean isOk() {
			return isOk;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
	}
	
	private Subscriber storeSubscriber(String email,
			List<SubscriberAttributeType> types,
			Map<Long, String> attributeValues, Long langId,
			List<Long> groups, boolean isHtml) {
		
//		List<Subscriber> allSubscribers = subscriberService.listAllSubscribers();
		Subscriber subscriber = subscriberService.findByEmail(email);
//		Subscriber subscriber = subscriberService.findByEmail(allSubscribers, email); 
		
		if (subscriber == null){
			subscriber = new Subscriber();
		}
		
		for (SubscriberAttributeType sat : types) {
			String value = attributeValues.get(sat.getId());
			if (value != null) {
				if (!subscriber.setAttribute(sat.getKey(), value)) {
					subscriberService.addSubscriberAttribute(subscriber, types,
							sat.getKey(), value);
				}
				
			} else {
				logger.error("attribute type '" + sat.getKey() + "didn't found in request");
			}
		}
		
		subscriber.setSubscriptionLangId(langId);

		subscriber.setHtml(isHtml);
		
		if (logger.isInfoEnabled()){
		    logger.info(
		        "Subscriber: " + subscriber.getId() + " email: " + subscriber.getEmail()
		    );
		}

		subscriber.setSubscriptionLangId(langId);
		subscriber.setHtml(isHtml);
		
		// TODO refactor to move in one method
		List<SubscriberGroup> sgroups = new LinkedList<SubscriberGroup>();
		for (Long groupId : groups) {
			SubscriberGroup sg = subscriberGroupService.findById(groupId);
			sgroups.add(sg);
		}
		
		subscriber.setSubscriberGroups(sgroups);

		subscriberService.save(subscriber);
		
		return subscriber;
	}
	
	public void notifyAboutSubscriberUpdate(String email, Subscriber subscriber, String action, String detailsForSubject){
		notifyAboutSubscriberUpdate(email, subscriber.getEmail(), action, detailsForSubject);
	}
	
	public void notifyAboutSubscriberUpdate(String email, String subscriberEmail, String action, String detailsForSubject){
		MailClient mailClient = new MailClient();
		try {
			mailClient.sendBouncedMessage(
					MailClient.buildAddressUseingEncoding(null, Configuration.getNewslettersBccEmail()),
			        MailClient.buildAddressUseingEncoding(null, new Configuration().getDefaultFeedbackEmail()),
			        "",
					"Subscriber " + action + detailsForSubject,
					subscriberEmail+
					" action: "+action,
			        Collections.emptyList().toArray(new DataSource[0]),
					MailClient.DEFAULT_ENCODING,
					"text/html",
					getSubscriptionEmailHeaders(null));
		} catch (CriticalException e) {
			logger.error("-error", e);
		} catch (MessagingException e) {
			logger.error("-error", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("-error", e);
		}
		
	}


	
	public NotificationStatus sendNotification(SubscriberAttributeParameters sap,
			String textType, Long langId, String newsletterLink,
			String siteLink, Element element) {

		// get email and validate it in limitation service 
		
		List<SubscriberAttributeType> types = subscriberService.listSubscriberAttributesTypes();
		SubscriberAttributeType emailType = subscriberService.findTypeByKey(Subscriber.ATTR_EMAIL, types);
		
		String email = sap.getAttributeValues().get(emailType.getId());
		
//		if (!limitationService.isMoreSubscriptionsAllowed(email)) {
//			logger.info("Subscriber " + email + " was rejected due to max limit problem.");
//			return new NotificationStatus("RESULT_CANNOT_SUBSCRIBE", false);
//		}

		boolean isHtml = "html".equalsIgnoreCase(textType);
		
		if (sap.getGroupIds() != null && sap.getGroupIds().size() > 0) {

			Subscriber subscriber = storeSubscriber(email, types,
					sap.getAttributeValues(), langId, sap.getGroupIds(), isHtml);
			
			String confirmationLink = newsletterLink + "?" + "action=confirm&subscriberId=" + subscriber.getId();
			
			String confirmationSubject = "";
			
			String lang = "en";
			
			try{
				lang = Language.findById(langId).getCode();
			}catch(Exception e){
				logger.error("-error", e);
			}
			
			try {
				confirmationSubject = DatabaseResourceBundle.getInstance(
						ModuleConstants.NEWSLETTER_MODULE, lang).getString("NL_MAILCONFIRMATIONSUBJECT");
			}catch(Exception e){
				logger.error("-error", e);
			}
			
			String subscriberName = subscriber.getFirstName() + " " + subscriber.getLastName();
			
	    	Xbuilder.setAttr(element, "confirmationLink", confirmationLink);
	    	Xbuilder.setAttr(element, "siteLink", siteLink);
	    	Xbuilder.setAttr(element, "userName", subscriberName);
	    	Xbuilder.setAttr(element, "userEmail", subscriber.getEmail());

	    	String confirmationText = mailTemplateService.findConfirmationText(langId).getText();
	    	confirmationText = confirmationText.replace("$link",
	    			"<a href=\"" + confirmationLink + "\">" + confirmationLink + "</a>");
	    	
	    	String template = publicationService.getUpdatedForSubscriberText(
	    			confirmationText, 
	    			subscriber.getAttributes()).trim(); 
	    	
	    	try {
				
		    	if (logger.isDebugEnabled()){
		    		logger.info("Template: " + template);
		    		logger.info("Feedback email: " + new Configuration().getDefaultFeedbackEmail());
		    	}
		    	
				if (logger.isDebugEnabled()) {
					logger.debug(format("confirmationLink=%s,siteLink=%s,isHtml=%s,"
							+ "mailSubject=%s,subscriberName=%s,subscriberEmail=%s",
							confirmationLink, siteLink, isHtml, confirmationSubject,
							subscriberName, subscriber.getEmail()));
				}
		    	
				MailClient mailClient = new MailClient();
				mailClient.sendBouncedMessage(
			    		MailClient.buildAddressUseingEncoding(subscriberName, subscriber.getEmail()),
				        MailClient.buildAddressUseingEncoding(null, new Configuration().getDefaultFeedbackEmail()),
				        StringUtils.EMPTY,
		        		confirmationSubject,
		        		template,
                        Collections.emptyList().toArray(new DataSource[0]),
		        		MailClient.DEFAULT_ENCODING,
		        		"text/html",
		        		getSubscriptionEmailHeaders(langId));
				
			    NotificationStatus ns = new NotificationStatus("Subscription email is sent to: " + email, true);
			    ns.setEmail(email);
			    return ns;
			}catch(UnsupportedEncodingException e) {
				logger.error("-error", e);
				return new NotificationStatus("SUBSCRIPTION_NOT_SENT_MAIL_UNREACHABLE", false);
	    	}catch(MessagingException e) {
				logger.error("-error", e);
				return new NotificationStatus("SUBSCRIPTION_NOT_SENT_MAIL_UNREACHABLE", false);
			}
		}
		return new NotificationStatus("NO_GROUP_CHOSEN", false);
	}
	
    public Map getSubscriptionEmailHeaders(Long langId) throws CriticalException {
    	logger.debug("+");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Mailer", Env.WCMS_X_MAILER);
    	logger.debug("-");
		return headers;
	}
	
	public MailingDao getMailingDao() {
		return mailingDao;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	public void setSubscriberGroupService(SubscriberGroupService service) {
		this.subscriberGroupService = service;
	}

	public void setStateService(StateService stateService) {
		this.stateService = stateService;
	}

	public void setPublicationService(NewPublicationService service) {
		this.publicationService = service;
	}

	public void setSessionTemplate(SessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;
	}

	public void setLimitationService(NewsletterLimitationService service) {
		this.limitationService = service;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public void setMailingStateDao(MailingStateDao mailingStateDao) {
		this.mailingStateDao = mailingStateDao;
	}

	public Mailing findById(Long mailingId) {
		return mailingDao.read(mailingId);
	}
	
	
	public void updateMailing(Long mailingId, EmailState emailState, String message, Long uniqId) {
		logger.debug("+");
		
		logger.debug(format("email state=%s, message=%s", emailState, message));
		
		Session session = null;
		Transaction tx = null;
		try {
			session = DBHelper.openSession();
			tx = session.beginTransaction();
			
			Mailing mailing = (Mailing)session.load(Mailing.class, mailingId);
			mailing.setMailingState(mailingStateDao.findByName(emailState.getName()));
			mailing.setMessage(message);
			mailing.setUniqId(uniqId);
			if (emailState != EmailState.SENT) {
				mailing.setRetryCount(mailing.getRetryCount() + 1);
			}
			session.update(mailing);
			
			tx.commit();
			
		} catch (Exception e) {
			if (tx != null) {
				try {
					tx.rollback();
				} catch (HibernateException he) {
					logger.error("rollback error", e);
				}
			}
			logger.error(e);
		} finally {
			DBHelper.close(session);
		}
		
		logger.debug("-");
	}

	public void setMutexProvider(IdMutexProvider mutexProvider) {
		this.mutexProvider = mutexProvider;
	}
	
}
