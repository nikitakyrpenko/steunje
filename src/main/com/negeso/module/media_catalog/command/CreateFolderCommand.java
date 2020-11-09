/*
 * @(#)$Id: CreateFolderCommand.java,v 1.12, 2005-06-06 13:04:17Z, Stanislav Demchenko$
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
import com.negeso.module.media_catalog.FileKeeper;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * Create new folder in current.
 *
 * @version 	$Revision: 13$
 * @author 		Olexiy.Strashko
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateFolderCommand extends AbstractCommand {


	private static Logger logger = Logger.getLogger(CreateFolderCommand.class);

	public static final String INPUT_NEW_FOLDER_NAME = "newFolderName";
	public static final String INPUT_CURRENT_DIR = "currentDir";
	public static final String INPUT_PARENT_DIR = "parentDir";
	
	/* Added to request parameters */
	public static final String INPUT_ERROR_TEXT = "errorMessage";
	public static final String INPUT_USER_TEXT = "userMessage";

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	@ActiveModuleRequired
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) )	{
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}

		String currentDir = request.getParameter(INPUT_CURRENT_DIR);
        String iLanguageCode = 
            (String)request.getSession().getAttribute("interface-language");         

		Folder parentFolder = Repository.get().getFolder(currentDir); 
		// Check security rights for folder creation 
		if ( !parentFolder.canEdit(request.getSession().getUser()) ){
			request.setParameter(
			    INPUT_ERROR_TEXT, 
			    Repository.get().getMessage("OPERATION_ACCESS_DENIED").getMessage(iLanguageCode)
			);
		}
		else{
			String newFolderDir = request.getParameter(INPUT_NEW_FOLDER_NAME);
			if ((newFolderDir == null) || (newFolderDir.trim().equalsIgnoreCase(""))){
				request.setParameter(
				    INPUT_ERROR_TEXT, "Unable to create folder " + "with empty name"
				);
			}
			else{
			    // create NICE folder
			    newFolderDir = FileKeeper.prepareFileName(newFolderDir);
			    
				String realPath = Repository.get().getRealPath(
					currentDir + 
					File.separator +
					newFolderDir 
				);
				File file = new File(realPath);
				if (file.exists()){
					request.setParameter(INPUT_USER_TEXT, "Folder '" + 
						newFolderDir +
						"' allreay exists in '" + currentDir + "'"
					);
				}
				else{
					if ( file.mkdir() ){
						request.setParameter(INPUT_USER_TEXT, "Folder '" + 
							newFolderDir +
							"' successfully created"
						);
					}
					else{
						request.setParameter(INPUT_ERROR_TEXT, "Unable to create '" + 
							newFolderDir +
							"' folder"
						);
					}
				}
			}
		}
		Repository.get().getCache().clearFolderDomainCache();
		
		ListDirectoryCommand listCommand = new ListDirectoryCommand();
		listCommand.setRequestContext(request);
		logger.debug("-");
		return listCommand.execute();	
	}
	

	@Override
	public String getModuleName() {
		return ModuleConstants.MEDIA_CATALOGUE_MODULE;
	}
}
