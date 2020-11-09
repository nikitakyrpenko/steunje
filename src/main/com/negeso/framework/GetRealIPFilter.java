/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class GetRealIPFilter implements Filter {
	
	public static final String X_FORWARDED_FOR = "x-forwarded-for";
	public static final String X_REAL_IP = "x-real-ip";

	public GetRealIPFilter() {
		// System.out.println("[GetRealIPFilter.GetRealIPFilter]");
	}

	public void destroy() {
		// System.out.println("[GetRealIPFilter.destroy]");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		// System.out.println("[GetRealIPFilter.doFilter]");
		if (request instanceof HttpServletRequest) {

			HttpServletRequest httpRequest = (HttpServletRequest) request;

			final String realIp = httpRequest.getHeader(X_REAL_IP);
			final String realHost = httpRequest.getHeader(X_FORWARDED_FOR);

			// System.out.println("[GetRealIPFilter.doFilter], realIp='"+realIp+"'");
			// System.out.println("[GetRealIPFilter.doFilter], realHost='"+realHost+"'");

			if (realIp != null) {
				if (realHost != null) {
					filterChain.doFilter(new HttpServletRequestWrapper(
							httpRequest) {
						public String getRemoteAddr() {
							return realIp;
						}

						public String getRemoteHost() {
							return realHost;
						}
					}, response);
					return;
				} else {
					filterChain.doFilter(new HttpServletRequestWrapper(
							httpRequest) {
						public String getRemoteAddr() {
							return realIp;
						}
					}, response);
					return;
				}
			} else if (realHost != null) {
				filterChain.doFilter(
						new HttpServletRequestWrapper(httpRequest) {
							public String getRemoteHost() {
								return realHost;
							}
						}, response);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
}
