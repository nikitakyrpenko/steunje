/*
* @(#)$Id: SubscriptionHelper.java,v 1.1, 2007-02-01 14:42:35Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.lite_event.domain.Event;
import com.negeso.module.lite_event.domain.Subscription;

/**
 *
 * TODO
 * 
 * @version                $Revision: 2$
 * @author                 sbondar
 * 
 */
public class SubscriptionHelper {

	private Logger logger = Logger.getLogger(SubscriptionHelper.class);
	
	private final String VISITOR_MAIL_SUBJECT = "you were subscribed";
	private final String ADMIN_MAIL_SUBJECT = "user registered";

	private final String ADMIN_BODY_TEMPLATE = "/template/admin_email_notification.xsl";
	private final String VISITOR_BODY_TEMPLATE = "/template/visitor_email_notification.xsl";

	private static SubscriptionHelper instance;
	
	private Session hiberSession;
	private SessionData session; 
	private Long amount;
	private Event event;
	
	
	private SubscriptionHelper() {
		// do nothing
	}

	/**
	 * Returns an instance of singleton.
	 * @return the SubscriptionHelper instance.
	 */
	public static synchronized SubscriptionHelper getInstance() {
		if (instance == null) {
			instance = new SubscriptionHelper();
		}
		return instance;
	}
	
	public void doSubscribe(Session hiberSession, SessionData session, Long amount, Event event) throws Exception {
		logger.debug("+-");
		this.hiberSession = hiberSession;
		this.session = session;
		this.amount = amount;
		this.event = event;
		notifyAdmin();
		notifyVisitor();
		saveSubscriptionToDB();
			
	}

	private void notifyAdmin() {
		 logger.debug("+");
		 try {
			 MailSender mailSender = new MailSender();
			 mailSender.setMailto(Env.getProperty("lite_event.adminEmail"));
			 mailSender.setSubject(ADMIN_MAIL_SUBJECT);
			 mailSender.setEmailBodyTemplate(ADMIN_BODY_TEMPLATE);
			 
			 mailSender.setBodyAttributes(
					 prepareAttributeForAdminNotification(session, amount, event)
			 );
			 mailSender.sendMail();
		 } catch (Exception e) {
			logger.error("Exception - " + e);
			throw new CriticalException("Error while sending mail to admin");
		 }
	     logger.debug("-");    
	}

	private void notifyVisitor() {
		logger.debug("+");
		try {
			MailSender mailSender = new MailSender();
				mailSender.setMailto(EventComponentService.getVisitorEmail( hiberSession, session.getUserId() ));

			mailSender.setSubject(VISITOR_MAIL_SUBJECT);
			mailSender.setEmailBodyTemplate(VISITOR_BODY_TEMPLATE);
			mailSender.setBodyAttributes(
					prepareAttributeForVisitorNotification()
			);
			mailSender.sendMail();
		
		} catch (CriticalException e) {
			logger.info("Subscription Visitor notification is failed. Email not found" +
					"UserInfo is not mapped: Exception-" + e);

		} catch (Exception e) {
			logger.error("Exception-" + e);
			throw new CriticalException("Error while sending mail to visitor");
		}
		logger.debug("-");    
	}
	
	private Map prepareAttributeForVisitorNotification() {
		logger.debug("+");
		Map<String, String> attributes = new HashMap<String, String>();
		HashMap userInfoMap = EventComponentService.getUserInfo( hiberSession, session.getUserId() );
		attributes.put("lang", session.getLanguageCode());
		attributes.put("eventName", event.getFields().get(0).getValue());
		attributes.put("eventInfo", event.getFields().get(1).getValue());
		attributes.put("userName", 
		        userInfoMap.get("firstName").toString() + " " + 
		        userInfoMap.get("middleName").toString() + " " + 
		        userInfoMap.get("lastName").toString());
		attributes.put("disclaimer", "{Disclaimer}");
		logger.debug("-");
		return attributes;
	} 
	
	private Map prepareAttributeForAdminNotification(SessionData session, Long amount, Event event) {
		logger.debug("+");
		Map<String, String> attributes = new HashMap<String, String>();
		try {
			HashMap userInfoMap = EventComponentService.getUserInfo( hiberSession, session.getUserId() );
			attributes.put("userName", 
					userInfoMap.get("firstName").toString() + " " + 
					userInfoMap.get("middleName").toString() + " " + 
					userInfoMap.get("lastName").toString());
			attributes.put("eventName", event.getFields().get(0).getValue());
			attributes.put("companyName", userInfoMap.get("companyName").toString());
			attributes.put("peopleAmount", amount.toString());
			attributes.put("disclaimer", "{Disclaimer}");
			attributes.put("lang", session.getLanguageCode());			
		} catch (Exception e) {
			logger.info("UserInfo is not mapped: Exception-" + e);
			 if (attributes.isEmpty()) {
				 attributes.put("userName", session.getUser().getName());
				 attributes.put("eventName", event.getDefaultTitle());
				 attributes.put("peopleAmount", amount.toString());
				 attributes.put("lang", session.getLanguageCode());							 
			 }
		}
		logger.debug("-");
		return attributes;
	}
	
	
	private void saveSubscriptionToDB() throws CriticalException {
		logger.debug("+");
		try {
			Subscription subscription = new Subscription();
			subscription.setDate(new Date());
			subscription.setEventId(event.getId());
			subscription.setPeopleAmount(amount);
			subscription.setUserId(new Long(session.getUserId()));
			hiberSession.save(subscription);
			hiberSession.flush();
		} catch (Exception e) {
			logger.error("Exception-" + e);
			throw new CriticalException("Error while saving subscription");
		}
		logger.debug("-");
	}
	

}
