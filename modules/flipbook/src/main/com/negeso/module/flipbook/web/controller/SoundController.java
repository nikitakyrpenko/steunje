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
package com.negeso.module.flipbook.web.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.module.flipbook.service.FileSenderService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SoundController extends AbstractController{
	
	private static Logger logger = Logger.getLogger( SoundController.class );

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest  request,
			HttpServletResponse response) throws Exception {
		String realPath = request.getSession().getServletContext().getRealPath(request.getRequestURI());
		File file = new File(realPath);
		if (file.exists()) {
			FileSenderService.send(request, response, file, true);
		} else {
			logger.error("File not found: " + realPath);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return null;
	}
}

