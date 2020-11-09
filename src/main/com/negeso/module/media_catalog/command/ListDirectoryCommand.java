/*
 * @(#)$Id: ListDirectoryCommand.java,v 1.12, 2005-11-04 11:04:45Z, Olexiy Strashko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.media_catalog.command;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * List files from current directory
 *
 * @version 	$Revision: 13$
 * @author 		Olexiy.Strashko
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ListDirectoryCommand extends AbstractCommand {

	private static Logger logger = Logger.getLogger(ListDirectoryCommand.class);

	// Input parameters
	public static final String INPUT_CURRENT_DIR = "currentDir";
	public static final String INPUT_PARENT_DIR = "parentDir";
	public static final String INPUT_SHOW_MODE = "showHiddenFiles";
	public static final String INPUT_VIEW_MODE = "viewMode";
	public static final String INPUT_SORT_MODE = "sortMode";
	public static final String INPUT_ERROR_TEXT = "errorMessage";
	public static final String INPUT_USER_TEXT = "userMessage";
	public static final String INPUT_ACTION_MODE = "actionMode";

	
	// Output parameters
	public static final String OUTPUT_RESOURCE = "resource";
	public static final String OUTPUT_PARENT_DIR = INPUT_PARENT_DIR;
	public static final String OUTPUT_CURRENT_DIR = INPUT_CURRENT_DIR;
	public static final String OUTPUT_ERROR_TEXT = INPUT_ERROR_TEXT;
	public static final String OUTPUT_USER_TEXT = INPUT_USER_TEXT;
	public static final String OUTPUT_SHOW_MODE = INPUT_SHOW_MODE;
	public static final String OUTPUT_VIEW_MODE = INPUT_VIEW_MODE;
	public static final String OUTPUT_SORT_MODE = INPUT_SORT_MODE;
	public static final String OUTPUT_ACTION_MODE = INPUT_ACTION_MODE;

	@ActiveModuleRequired
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		Map<String, Object> resultMap = response.getResultMap();
		
		if (!request.getSessionData().isAuthorizedUser()) {
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}
		
		/* pass through parameters */
		resultMap.put(OUTPUT_VIEW_MODE, request.getParameter(INPUT_VIEW_MODE));
		resultMap.put(OUTPUT_SORT_MODE, request.getParameter(INPUT_SORT_MODE));
		resultMap.put(OUTPUT_ACTION_MODE, request.getParameter(INPUT_ACTION_MODE));
		
		/* validation */
		String userMessage = request.getString(INPUT_USER_TEXT, "");
		String errorMessage = request.getString(INPUT_ERROR_TEXT, "");
		String currentDir = request.getNonblankParameter(INPUT_CURRENT_DIR);
		String parentDir = request.getParameter(INPUT_PARENT_DIR);
		String showMode = request.getString(INPUT_SHOW_MODE, "false");
		String rootDir = Repository.get().getRootPath();
		
		/* Locate resource for listing */

		Folder folder = null;

		if ( currentDir == null ){
			Folder rootFolder = null;
			rootFolder = Repository.get().getRootFolder();
			folder = rootFolder;
			currentDir = rootDir;
		}
		else{
			folder = Repository.get().getFolder(currentDir); 
		}
		if ((parentDir == null) || (parentDir.trim().equalsIgnoreCase(""))){
			parentDir = rootDir;
		}

		response.setResultName(RESULT_SUCCESS);
		resultMap.put(OUTPUT_RESOURCE, folder);
		resultMap.put(OUTPUT_PARENT_DIR, parentDir);
		resultMap.put(OUTPUT_CURRENT_DIR, currentDir);
		resultMap.put(OUTPUT_ERROR_TEXT, errorMessage);
		resultMap.put(OUTPUT_USER_TEXT, userMessage);
		resultMap.put(OUTPUT_SHOW_MODE, showMode);
		
		logger.debug("-");
		return response;
	}
	
	
	@Override
	public String getModuleName() {
		return ModuleConstants.MEDIA_CATALOGUE_MODULE;
	}
}
