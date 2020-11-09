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

import java.io.Writer;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.flipbook.AbstractFilePagesReader;
import com.negeso.module.flipbook.FilePagesReaderFactory;
import com.negeso.module.flipbook.FlipBookModule;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PagesXmlController extends AbstractController{

	private static final Logger logger = Logger.getLogger(PagesXmlController.class);
	
	private static final String ROOT_XML_TAG_START = "<content width=\"%s\" height=\"%s\" bgcolor=\"%s\" " +
			"loadercolor=\"%s\" panelcolor=\"%s\" buttoncolor=\"%s\" textcolor=\"%s\" fullscreen=\"true\" tellafriendmode=\"manual\">";
	private static final String PAGE_ELEMENT = FlipBookModule.eol + "	<page src=\"/flipbook?" + FlipBookModule.FILE_PATH + "=%s&amp;" + FlipBookModule.PAGE_NUMBER_PARAM + "=%s\"/>";
	private static final String ROOT_XML_TAG_END = FlipBookModule.eol + "</content>";

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(request.getHeader("referer")), "UTF-8");
		String filePath = null;
		for (NameValuePair nameValuePair : params) {
			if (FlipBookModule.FILE_PATH.equals(nameValuePair.getName())) {
				filePath = nameValuePair.getValue();
			}
		}
		if (StringUtils.isBlank(filePath)) {
			AbstractHttpView.setHeadersToDisableCaching(response);
			logger.error("Session param " + FlipBookModule.FILE_PATH + " is empty!");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		AbstractFilePagesReader reader = FilePagesReaderFactory.getReader(FlipBookModule.MODULE_MEDIA_FOLER + filePath);
		StringBuffer sb = new StringBuffer();
		
		sb.append(String.format(ROOT_XML_TAG_START, reader.getWidth(),
				reader.getHeight(), FlipBookModule.getBackgroundColor(),
				FlipBookModule.getLoaderColor(),
				FlipBookModule.getPanelColor(),
				FlipBookModule.getButtonColor(),
				FlipBookModule.getTextColor()));
		for (int i = 1; i <= reader.getPagesCount(); i++) {
			sb.append(String.format(PAGE_ELEMENT, filePath, i));
		}
		reader.finish();
		sb.append(ROOT_XML_TAG_END);
		response.setContentType("text/xml; charset=UTF-8");
		AbstractHttpView.setHeadersToDisableCaching(response);
		Writer writer = response.getWriter();
		writer.write(sb.toString());
		IOUtils.closeQuietly(writer);
		return null;
	}

}

