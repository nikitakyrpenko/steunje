/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.Env;
import com.negeso.module.newsletter.service.StateService;

/**
 * 
 * @TODO
 * 
 * @author		Dmitry Fedotov
 * @version		$Revision: $
 *
 */
public class ReadNotificationController extends MultiActionController {

    private static final Logger logger = Logger.getLogger(ReadNotificationController.class);

    private static final String EMPTY_IMAGE_PATH = "/images/empty.gif";

    private StateService stateService;

    public ModelAndView notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");

		Long mailingId = ServletRequestUtils.getLongParameter(request, "mailing_id");
		
		if (mailingId != null){
			logger.debug("updating read status");
			stateService.updateReadState(mailingId, "");
		}else{
			logger.debug("some wrong request");
			return null;
		}
		FileInputStream fis = null;
		OutputStream stream = null;
		try{
			File file = new File(Env.getRealPath(EMPTY_IMAGE_PATH));
			fis = new FileInputStream(file);
			byte[] a1 = new byte[fis.available()];
			fis.read(a1);
			stream = response.getOutputStream();
			stream.write(a1);
		}catch(Exception e){
			logger.error("error getting image: " + e.getMessage());
		}finally{
			fis.close();
			stream.close();
		}
		
		logger.debug("-");
		return null;
	}

	public void setStateService(StateService stateService) {
		this.stateService = stateService;
	}

}
