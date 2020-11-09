/*
 * @(#)$Id: NavRequestUtil.java,v 1.0, 2006-02-06 09:33:25Z, Dmitry Dzifuta$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.navigation;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * 
 *
 * Request/Session utils
 * 
 * @version		$Revision: 1$
 * @author		Dmitry Dzifuta
 *
 */          
public class NavRequestUtil {
    private static Logger logger = Logger.getLogger(NavRequestUtil.class);	
    public 	final static String EXPIRED_PAGE = "friendlyExpired";
	private static final String HISTORY_STACK_KEY 	= "historyStack";	

	public static HistoryStack getHistoryStack(HttpServletRequest request){
//		HistoryStack hStack = (HistoryStack) request.getSession(true).getAttribute(
//					HISTORY_STACK_KEY
//			);
//		
//		return hStack;		
		if (request.getSession() != null && request.getSession().getAttribute(HISTORY_STACK_KEY) != null) {
			logger.debug(request);
			logger.debug(request.getSession());
			logger.debug(request.getSession().getAttribute(HISTORY_STACK_KEY));
			logger.debug((HistoryStack) request.getSession().getAttribute(HISTORY_STACK_KEY));
				return (HistoryStack) request.getSession().getAttribute(HISTORY_STACK_KEY);
		}		
		return new HistoryStack();	
	}
	
	public static void setHistoryStack(HttpServletRequest request, HistoryStack historyStack){
		request.getSession(true).setAttribute(HISTORY_STACK_KEY, historyStack);
	}
}
