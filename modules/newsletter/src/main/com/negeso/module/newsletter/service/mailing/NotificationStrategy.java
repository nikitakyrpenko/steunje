/*
 * @(#)Id: NotificationStrategy.java, 07.04.2008 13:33:34, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service.mailing;

import com.negeso.module.newsletter.bo.Publication;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface NotificationStrategy {
	
	public String getSubject(Publication publication);
	public String getText(Publication publication);
	public String getMailAddress();
	
}
