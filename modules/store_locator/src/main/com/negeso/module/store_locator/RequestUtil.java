/*
* @(#)$Id: RequestUtil.java,v 1.0, 2006-06-20 11:03:48Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.store_locator;

import javax.servlet.http.HttpServletRequest;

import com.negeso.framework.navigation.HistoryStack;

/**
 *
 * TODO
 * 
 * @version                $Revision: 1$
 * @author                 Svetlana Bondar
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
    
		return new HistoryStack();	
	}
    
    
	public static void setHistoryStack(HttpServletRequest request, HistoryStack historyStack){
            request.getSession(true).setAttribute(HistoryStack.HISTORY_STACK_KEY, historyStack);
	}


}
