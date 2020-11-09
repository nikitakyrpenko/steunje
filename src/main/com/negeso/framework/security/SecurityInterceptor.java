/*
* @(#)$Id: SecurityInterceptor.java,v 1.0, 2006-06-20 11:03:48Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.framework.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.negeso.framework.domain.User;

/**
 *
 * TODO
 * 
 * @version                $Revision: 1$
 * @author                 Svetlana Bondar
 * 
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {
	private static Logger logger = Logger.getLogger(SecurityInterceptor.class);
	
    private String redirectView;
            
    @Override
    public boolean preHandle(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Object handler) 
    throws Exception 
    {
        Boolean isAdmin = isAdminLogon(request);
        if( isAdmin ){
            return true;
        }
        else {
            //request.getRequestDispatcher(redirectView).forward(request,response);
            response.sendRedirect("/admin/");
            return false;
        }
    }
    
    public String getRedirectView() {
        return redirectView;
    }
    public void setRedirectView(String redirectView) {
        this.redirectView = redirectView;
    }
    
    private static final String USER_ATTR_NAME = "user_object";
    static public boolean isAdminLogon(HttpServletRequest request){
        Object user = request.getSession().getAttribute( USER_ATTR_NAME );
        if ( ! (user instanceof User) ){
            logger.debug("- admin is not set");
            return false;
        }
        if( !SecurityGuard.isContributor((User)user) ){
            return false;
        }
        logger.debug("- admin is set");
        return true;
    }
 
    

}
