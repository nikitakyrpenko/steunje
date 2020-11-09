/*
* @(#)$Id: RequestUtil.java,v 1.1, 2007-01-18 16:56:42Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.framework.navigation;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * TODO
 * 
 * @version                $Revision: 2$
 * @author                 sbondar
 * 
 */
public class RequestUtil {
	
	public static HistoryStack getHistoryStack(HttpServletRequest request){
    	if (   request.getSession() != null 
            && request.getSession().getAttribute(HistoryStack.HISTORY_STACK_KEY) != null) {
    		return (HistoryStack) request.getSession().getAttribute(
                            HistoryStack.HISTORY_STACK_KEY
                    );
        }
    	HistoryStack historyStack = new HistoryStack();
    	setHistoryStack(request, historyStack);  	
		return historyStack;
	}
    
	public static void setHistoryStack(HttpServletRequest request, HistoryStack historyStack){
            request.getSession(true).setAttribute(HistoryStack.HISTORY_STACK_KEY, historyStack);
	}
}