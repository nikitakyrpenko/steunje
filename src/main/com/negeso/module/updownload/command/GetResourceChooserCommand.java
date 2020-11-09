/*
 * @(#)GetResourceChooserCommand.java       @version	04.02.2004
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
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;

/**
 * Get all available resources from media catalog and
 * build xml document model. Used to choose resource from 
 * catalog in linking resource process. 
 *
 * @version 	1.0
 * @author 		Olexiy.Strashko
 */
public class GetResourceChooserCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(GetResourceChooserCommand.class);
	
	/**   */
	public static final String INPUT_TYPE_FILTER = "typeFilter";

	/**  */
	public static final String RESULT_SUCCESS = "success";
	
	/**  */ 
	public static final String RESULT_FAILURE = "failure";
	
	/** Object of class File */
	public static final String OUTPUT_XML = "xml";

	/**
	 * 
	 */
	public GetResourceChooserCommand() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {

		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ) {
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}
		
		Element resourceSet = Repository.get().getAvailableFilesElement(
		    request.getSession().getUser(),    
			request.getParameter(INPUT_TYPE_FILTER)
		);
		
		try{
			Document doc = DocumentHelper.createDocument(resourceSet);
			DOMWriter writer = new DOMWriter();
			org.w3c.dom.Document w3cDoc = writer.write(doc);
			
			response.setResultName(RESULT_SUCCESS);
			response.getResultMap().put(OUTPUT_XML, w3cDoc);
		} catch ( Exception e ) {
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}

		return response;
	}
}
