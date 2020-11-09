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
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import com.negeso.framework.domain.DBHelper;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.Configuration.EmailState;
import com.negeso.module.newsletter.bo.Mailing;
import com.negeso.module.newsletter.bo.MailingState;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.dao.MailingDao;
//import com.negeso.module.newsletter.dao.MailingStateDao;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class StateService {

	private static final Logger logger = Logger.getLogger(StateService.class);
	
	// TODO remove and also from spring
//	private NewPublicationService publicationService;
	private MailingDao mailingDao;
	// TODO remove and also from spring
//	private MailingStateDao mailingStateDao;
	
/*
	public MailingState findByName(String name){
		return mailingStateDao.findByName(name);
	}
*/

	public void updateMailingState(Publication publication,
			List<Subscriber> subscribers,
			EmailState emailState, String message, Long uniqId) {
		logger.debug("+");
		
		logger.debug(format("email state=%s, message=%s", emailState, message));
		
		Session session = null;
		Transaction tx = null;
		try {
			session = DBHelper.openSession();
			tx = session.beginTransaction();
			
			Publication p = (Publication) session.load(Publication.class, publication.getId());

			Criteria criteria = session.createCriteria(MailingState.class);
			criteria.add(Expression.eq("name", emailState.getName()));
			MailingState mailingState = (MailingState) criteria.uniqueResult();
			
			for (Subscriber s : subscribers) {
				Subscriber subscriber = (Subscriber) session.load(Subscriber.class, s.getId());
				Mailing mailing = new Mailing();
				mailing.setPublication(p);
				mailing.setSubscriber(subscriber);
				mailing.setCreatedDate(new Date());
				mailing.setMessage(message);
				mailing.setMailingState(mailingState);
				mailing.setUniqId(uniqId);
				session.save(mailing);
			}
			
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
	
	@SuppressWarnings("unchecked")
	public void updateMailingState(Long uniqId, String errorMessage, String state){
		logger.debug("+");
		
		if (logger.isDebugEnabled()) {
			logger.debug(format("uniqId=%s, errorMessage=%s, state=%s",
					uniqId, errorMessage, state));
		}
		
		Session session = null;
		Transaction tx = null;
		try {
			session = DBHelper.openSession();
			tx = session.beginTransaction();
			
			Query mailingsQuery = session.createQuery(
					"from Mailing m where m.uniqId = " + uniqId); 
			
			List<Mailing> mailings = (List<Mailing>) mailingsQuery.list(); 

			if (mailings == null || mailings.size() == 0){
				logger.error(" - mail wasn't sent, wrong request");
				return;
			}
			
			mailingsQuery = session.createQuery(
					"from MailingState mS where mS.name = '" + state + "'");
			
			MailingState mailingState = (MailingState) mailingsQuery.uniqueResult();
			
			if (mailings.size() == 1){
				Mailing m = new Mailing();
				m.setMailingState(mailingState);
				m.setPublication(mailings.get(0).getPublication());
				m.setSubscriber(mailings.get(0).getSubscriber());
				m.setMessage(errorMessage);
				m.setUniqId(mailings.get(0).getUniqId());
				m.setCreatedDate(new Date());
				mailingDao.create(m);
			}
			
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
	
	public void updateReadState(Long uniqId, String errorMessage){
		updateMailingState(uniqId, errorMessage, Configuration.EmailState.READ.getName());
	}
	
	public void updateBounceState(Long uniqId, String errorMessage){
		updateMailingState(uniqId, errorMessage, Configuration.EmailState.BOUNCED.getName());
	}

/*
	public void setPublicationService(NewPublicationService publicationService) {
		this.publicationService = publicationService;
	}
*/

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

/*
	public void setMailingStateDao(MailingStateDao mailingStateDao) {
		this.mailingStateDao = mailingStateDao;
	}
*/

}
