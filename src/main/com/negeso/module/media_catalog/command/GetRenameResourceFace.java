/*
 * @(#)GetRenameResourceFace.java       @version	21.03.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.command;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;


import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Resource;

/**
 * Rename resource from Media Catalog
 *
 * @version 	21.03.2004
 * @author 	Olexiy.Strashko
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GetRenameResourceFace extends AbstractCommand {
	private static Logger logger = Logger.getLogger(GetRenameResourceFace.class);

	public static final String INPUT_VICTIM = "victim";
	public static final String INPUT_CURRENT_DIR = "currentDir";
	public static final String INPUT_PARENT_DIR = "parentDir";
	public static final String INPUT_VIEW_MODE = "viewMode";
	public static final String INPUT_SORT_MODE = "sortMode";
	public static final String INPUT_SHOW_MODE = "showHiddenFiles";

	/* Added to request parameters */
	public static final String INPUT_ERROR_TEXT = "errorMessage";
	public static final String INPUT_USER_TEXT = "userMessage";
	
	public static final String RESULT_USER_ERROR = "user-error";
	
	public static final String OUTPUT_RESOURCE = "resource";
	public static final String OUTPUT_ERROR_TEXT = "errorMessage";
	//public static final String OUTPUT_USER_TEXT = "userMessage";
	public static final String OUTPUT_PARENT_DIR = "parentDir";
	public static final String OUTPUT_CURRENT_DIR = "currentDir";
	public static final String OUTPUT_VIEW_MODE = "viewMode";
	public static final String OUTPUT_SORT_MODE = "sortMode";
	public static final String OUTPUT_SHOW_MODE = "showHiddenFiles";


	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	@ActiveModuleRequired
	public ResponseContext execute() {
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		Map<String, Object> resultMap = response.getResultMap();
		
		try{
			if (!SecurityGuard.isContributor(request.getSession().getUser())) {
				response.setResultName(RESULT_ACCESS_DENIED);
				logger.debug("-");
				return response;
			}
		}
		catch(CriticalException e){
			response.setResultName(RESULT_FAILURE);
			logger.debug("-");
			return response;
		}


		String victim = request.getParameter(INPUT_VICTIM);
		String currentDir = request.getParameter(INPUT_CURRENT_DIR);
		String showMode = request.getParameter(INPUT_SHOW_MODE);
		/* pass through parameters */
		resultMap.put(OUTPUT_VIEW_MODE, request.getParameter(INPUT_VIEW_MODE));
		resultMap.put(OUTPUT_SORT_MODE, request.getParameter(INPUT_SORT_MODE));
		resultMap.put(OUTPUT_PARENT_DIR, request.getParameter(INPUT_PARENT_DIR));
		resultMap.put(OUTPUT_CURRENT_DIR, request.getParameter(INPUT_CURRENT_DIR));
		resultMap.put(OUTPUT_SHOW_MODE, request.getParameter(INPUT_SHOW_MODE));
		
		Resource resource = Repository.get().getResource(
				currentDir + 
				File.separator +
				victim 
		);

		resultMap.put(OUTPUT_RESOURCE, resource);
		response.setResultName(RESULT_SUCCESS);
		
		return response;
	}


	@Override
	public String getModuleName() {
		return ModuleConstants.MEDIA_CATALOGUE_MODULE;
	}
}
