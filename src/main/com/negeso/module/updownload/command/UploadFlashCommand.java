/*
 * @(#)UploadDocumentCommand.java       @version	27.04.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;

/**
 * TODO Class description goes here
 *
 * @version 	27.04.2004
 * @author 	OStrashko
 */
public class UploadFlashCommand extends UploadFileCommand {
	private static Logger logger = Logger.getLogger(UploadFlashCommand.class);

	protected String [] getAllowedExtensions(){
		logger.debug("+-");
		return Configuration.flashTypeExtensions;
	}

	protected void buildSuccessResultXml(Element page, RequestContext request) {
		logger.debug("+");
		super.buildSuccessResultXml(page, request);
		
		Repository.get().getXBuilder().getFlashUploadOption(
		        page,
				this.getMode(),
				request
		); 

		logger.debug("-");
	}

	protected void buildErrorResultXml(Element page, RequestContext request) {
		logger.debug("+");
		Element fileOption = Repository.get().getXBuilder().getFileUploadOption(
				page,
				this.getWorkingFolder(),
				this.getMode()
		);

		Xbuilder.setAttr(fileOption, "width", request.getNonblankParameter("requiredWidth"));
		Xbuilder.setAttr(fileOption, "height", request.getNonblankParameter("requiredHeight"));
		
		logger.debug("-");
	}

}
