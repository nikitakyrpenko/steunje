/*
 * @(#)Id: BannerSecurityIntercaptor.java, 04.01.2008 18:17:11, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerSecurityInterceptor extends SecurityInterceptor {
	
	@Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {
        Boolean isAdmin = isAdminLogon(request);
        if( isAdmin || request.getRequestURL().append("?"+request.getQueryString()).toString().contains("bm_redirect.html?action=redirect&id=")){
            return true;							      
        }else{
            response.sendRedirect("/admin/");
            return false;
        }
    }
}
