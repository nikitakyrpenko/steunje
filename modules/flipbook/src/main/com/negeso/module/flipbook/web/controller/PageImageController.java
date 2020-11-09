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
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.flipbook.AbstractFilePagesReader;
import com.negeso.module.flipbook.FilePagesReaderFactory;
import com.negeso.module.flipbook.FlipBookModule;
import com.negeso.module.flipbook.service.FileSenderService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PageImageController extends AbstractController{
	
	private static final String GENERATED_PAGE_IMAGE = "page_%s.png",
								THUMB = "thumb";
	

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String filePath = ServletRequestUtils.getStringParameter(request, FlipBookModule.FILE_PATH);
		int pageNumber = ServletRequestUtils.getIntParameter(request, FlipBookModule.PAGE_NUMBER_PARAM);
		
		String generatedSubPath = filePath.replace(FlipBookModule.PDF, StringUtils.EMPTY);
		String realPath = request.getSession().getServletContext().getRealPath(FlipBookModule.GENERATED_PAGES_FOLDER + generatedSubPath);
		File folder = new File(realPath);
		if (!folder.exists()) {
			FileUtils.forceMkdir(folder);			
		}
		boolean isThumb = ServletRequestUtils.getBooleanParameter(request, THUMB, false);
		File file = new File(folder, String.format(GENERATED_PAGE_IMAGE, isThumb ? THUMB + pageNumber : pageNumber));
		if (!file.exists()) {
			AbstractFilePagesReader reader = FilePagesReaderFactory.getReader(FlipBookModule.MODULE_MEDIA_FOLER + filePath, isThumb ? 0.25f : FlipBookModule.getScaling());
			file = reader.getPage(file, pageNumber);
			reader.finish();
		}
		AbstractHttpView.setHeadersToDisableCaching(response);
		FileSenderService.send(request, response, file, true);
		return null;
	}
	
	

}

