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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.flipbook.FlipBookModule;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class LanguageController extends AbstractController{
	
	private static final String [] constants = 
		{"langFrontCover", "Cover",
		"langBackCover", "Back Cover",
		"langNextPage", "Next Page",
		"langPrevPage", "Previous Page",
		"langActualSize", "Actual Size",
		"langFitToPage", "Fit To Page",
		"langZoomIn", "Zoom.In",
		"langZoomOut", "Zoom.Out",
		"langFullScreen", "Full Screen",
		"langExitFullScreen", "Exit Full Screen",
		"langCreatingPages", "creating pages",
		"langLoadingSound", "loading sound",
		"langButZoomIn", "zoom in",
		"langButZoomOut", "zoom out",
		"langButGoFullScreen", "full screen",
		"langButExitFullScreen", "exit full screen",
		"langButFitToPage", "fit to page",
		"langButActualSize", "actual size",
		"langButHighQuality", "high quality",
		"langButLowQuality", "low quality",
		"langButPrint", "print",
		"langButSoundOn", "sound on",
		"langButSoundOff", "sound off",
		"langButSendToFriend", "tell a friend",
		"langButBackground", "background",
		"langButBack", "go back",
		"langButSelectColor", "select color",
		"langButFrontCover", "cover",
		"langButBackCover", "back cover",
		"langButNextPage", "next page",
		"langButPrevPage", "previous page",
		"langButGoToPage", "go to page",
		"langButClose", "close",
		"langButBG1", "park",
		"langButBG2", "beach",
		"langButBG3", "office",
		"langExpTextBG", "select background image and sound",
		"langButCancel", "cancel",
		"langButTryAgain", "try again",
		"langExpTextPrint", "please select page to print",
		"langPrintPage", "page",
		"langButSend", "send",
		"langStf1", "your e-mail address:",
		"langStf2", "your friend e-mail address:",
		"langStf3", "your message (optional):",
		"langStf4", "Dear friend",
		"langStf5", "",
		"langSendSubSend", "sending...",
		"langSendCantSend", "unable to send",
		"langSendSend", "has been send!",
		"loadedLang", "1"};
	
	private static final String langPattern = "&%s=%s" + FlipBookModule.eol,
								constPattern = "%s.%s",
								CONSTANT_NOT_EXIST_IDENTIFIER = "!!!";

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		StringBuffer sb = new StringBuffer();
		String langCode = SessionData.getLanguageCode(request);
		for (int i = 0; i < constants.length; i += 2) {
			String translation = getTranslation(langCode, constants[i]);
			if (translation.startsWith(CONSTANT_NOT_EXIST_IDENTIFIER)) {
				translation = constants[i + 1];
			}
			sb.append(String.format(langPattern, constants[i], translation));
		}
		response.setContentType("text/plain; charset=UTF-8");
		AbstractHttpView.setHeadersToDisableCaching(response);
		Writer writer = response.getWriter();
		writer.write(sb.toString());
		IOUtils.closeQuietly(writer);
		return null;
	}
	
	private String getTranslation(String langCode, String key) {
		return DatabaseResourceBundle.getTranslation(FlipBookModule.NAME, langCode, String.format(constPattern, FlipBookModule.NAME, key));
	}

}

