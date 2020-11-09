/*
 * @(#)$Id: MoveResourceCommand.java,v 1.7, 2005-06-06 13:04:52Z, Stanislav Demchenko$
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
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.FileResource;
import com.negeso.module.media_catalog.domain.Folder;


/**
 * Move resource super command!
 *
 * @version 	$Revision: 8$
 * @author 		Olexiy.Strashko
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MoveResourceCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(MoveResourceCommand.class);

	public static final String INPUT_NEW_NAME = "newName";
	public static final String INPUT_RESOURCE_NAME = "resourceName";
	public static final String INPUT_CURRENT_DIR = "currentDir";
	public static final String INPUT_DESTINATION_FOLDER = "destFolder";
	public static final String INPUT_IS_COPY_OPTION = "isCopy";

	/* Added to request parameters */
	public static final String INPUT_ERROR_TEXT = "errorMessage";
	public static final String INPUT_USER_TEXT = "userMessage";
	
	@ActiveModuleRequired
	public ResponseContext execute() {
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		
		String currentDir = request.getParameter(INPUT_CURRENT_DIR);
		String resourceName = request.getParameter(INPUT_RESOURCE_NAME);
		String destinationFolder = request.getParameter(INPUT_DESTINATION_FOLDER);

		if ( !SecurityGuard.isContributor(request.getSession().getUser()) )	{
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}


		boolean isCopy = false;
		String operationName = "moved";
		/* check&set is adjust image */ 
		if (request.getParameter(INPUT_IS_COPY_OPTION) != null) {
			isCopy = true;
			operationName = "copied";
		}

		boolean success = false;
		try{
		    FileResource srcFile = Repository.get().getFileResource(
	            currentDir +
				File.separatorChar +  
				resourceName
		    );
		    
		    Folder targetFolder = Repository.get().getFolder( destinationFolder );
		    
		    if ( isCopy ){
			    logger.info("Coping file");
		        success = srcFile.copy(request.getSession().getUser(), targetFolder);
		    }
		    else{
			    logger.info("Moving file");
		        success = srcFile.move(request.getSession().getUser(), targetFolder);
		    }
		}
		catch(RepositoryException e){
			request.setParameter(INPUT_ERROR_TEXT, e.getMessage());
		}
		catch(AccessDeniedException e){
			request.setParameter(INPUT_ERROR_TEXT, e.getMessage());
		}
		catch(CriticalException e){
			request.setParameter(INPUT_ERROR_TEXT, e.getMessage());
		}

		if (success){
			request.setParameter(INPUT_USER_TEXT, "File '" + 
				resourceName + "' successfully " + operationName + " to '" + 
				destinationFolder + "'"
			);
		}

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

