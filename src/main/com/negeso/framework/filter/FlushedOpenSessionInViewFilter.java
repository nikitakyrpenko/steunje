/*
 * @(#)$Id: FlushedOpenSessionInViewFilter.java,v 1.1, 2008-02-03 11:11:22Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.filter;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;

/**
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
 public class FlushedOpenSessionInViewFilter extends OpenSessionInViewFilter {

	static Logger logger = Logger.getLogger(FlushedOpenSessionInViewFilter.class);
	
	@Override
	protected Session getSession(SessionFactory sessionFactory)
			throws DataAccessResourceFailureException {
		logger.debug("getting the session");
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		session.setFlushMode(FlushMode.AUTO);
		return session;
	}
	
	@Override
	protected void closeSession(Session session, SessionFactory sessionFactory) {
		logger.debug("closing the session");
		session.flush();
		SessionFactoryUtils.releaseSession(session, sessionFactory);
	}
}
