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
package com.negeso.module.flipbook.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.framework.view.GetFileView;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FileSenderService {
	

	public static void send(HttpServletRequest request, HttpServletResponse response, File file, boolean isCached) {
		
		Map<String, Object> currentIO = new HashMap<String, Object>();
		currentIO.put(WebFrontController.HTTP_SERVLET_RESPONSE, response);
		RequestContext requestContext = new RequestContext(null, null, currentIO);
		ResponseContext responseContext = new ResponseContext();
		responseContext.getResultMap().put(AbstractHttpView.KEY_FILE, file);
		String mimeType = Env.getMimeType(file.getName().toLowerCase());
		responseContext.getResultMap().put( AbstractHttpView.HEADER_MIME_TYPE, mimeType);
		responseContext.getResultMap().put( AbstractHttpView.HEADER_EXPIRES, 0L);
		new GetFileView().process(requestContext, responseContext);
	}
}

