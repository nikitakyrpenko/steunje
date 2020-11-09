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

import org.apache.commons.io.FileUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.flipbook.FlipBookModule;
import com.negeso.module.flipbook.web.component.FlipBookComponent;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FlipBookManagementController extends AbstractController {
	

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if ("resetCache".equals(request.getParameter("act"))) {
			String realPath = request.getSession().getServletContext().getRealPath(FlipBookModule.GENERATED_PAGES_FOLDER);
			File folder = new File(realPath);
			if (folder.exists()) {
				FileUtils.cleanDirectory(folder);
				FlipBookComponent.resetCache();
			}
		}
		RequestUtil.getHistoryStack(request).push( new Link("FLIP_BOOK_MODULE", 
				"/admin/fbm_settings.html", true, -1));
		
		return new PreparedModelAndView("fbm_settings").get();
	}

}

