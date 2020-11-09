/*
 * @(#)Id: NewsletterSecurityInterceptor.java, 31.03.2008 15:32:29, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.negeso.framework.security.SecurityInterceptor;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class NewsletterSecurityInterceptor extends SecurityInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		if (isReadNotification(request)){
			return true;
		}else{
			return super.preHandle(request, response, handler);
		}
		
	}
	
	private boolean isReadNotification(HttpServletRequest request){
		return request.getParameter(Configuration.READ_NOTIFICATION_PARAM) != null;
	}
	
}
