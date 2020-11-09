/*
* @(#)$Id: EventComponentService.java,v 1.2, 2007-02-05 14:46:15Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;

/**
 *
 * TODO
 * 
 * @version                $Revision: 3$
 * @author                 sbondar
 * 
 */
public class EventComponentService {

	private static Logger logger = Logger.getLogger(EventComponentService.class);
	
	public static HashMap getUserInfo(Session hiberSession, int userId) {
		logger.debug("+-");
		return (HashMap) hiberSession.createQuery(
				"FROM UserInfo ui" +
				" WHERE ui.siteId = " + Env.getSiteId() +
				" and ui.userId = " +  userId
		).list().get(0); 
	}
	
	public static String getVisitorEmail(Session hiberSession, int userId) {
		try {
		 return (String) hiberSession.createQuery(
				 "select ui.email from UserInfo ui where ui.userId = " + userId
					).list().get(0);
		} catch (Exception e) {
			// TODO localize error message als			
			logger.error("Exception: Can't get visitor's email -" + e);
			throw new CriticalException();
		}
	}
	
	public static HashMap getEventDetailsMap(Session hiberSession, Long languageId, Long eventId) {
		return (HashMap) hiberSession.createQuery( 
				"from EventDetails ed where ed.langId = " + languageId +  
				" and ed.eventId = " + eventId
		).list().get(0);

	}

}
