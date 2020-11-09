/*
 * @(#)Id: LimitationNotificationStrategy.java, 07.04.2008 13:35:05, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service.mailing;

import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.Publication;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class LimitationNotificationStrategy implements NotificationStrategy {
	
	public LimitationNotificationStrategy() {}

	public String getMailAddress() {
		return Configuration.getAdminEMail();
	}

	public String getSubject(Publication publication) {
		return this.buildSubject(Configuration.getErrorLetterSubj());
	}

	public String getText(Publication publication) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html>");
		buffer.append("<body>");

        buffer.append("<h5 style=\"color:blue;\">")
                .append(Configuration.getErrorLetterBody())
                .append(" ")
                .append(Configuration.getMaxSubscribersCount())
                .append(" is the maximum. ")
                .append("</h5>");
		
		buffer.append("</body>");
		buffer.append("</html>");
		return buffer.toString();
	}
	
	private String buildSubject(String initialSubject) {
		return String.format("(%s) %s", Configuration.getNotificationHostName(), initialSubject);
	}
}
