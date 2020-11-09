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

import java.util.List;

import com.negeso.module.newsletter.Configuration.EmailState;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.Subscriber;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public interface SendStrategy {
	
	public void update(Publication publication,
			List<Subscriber> subscribers, EmailState emailState, String message, Long uniqId);
	public void definePublicationStatus(Publication p, String status);
	public String getSubject(Publication p, Long langId, boolean isHtml);
	public boolean isSystemSending();
}
