/*
 * @(#)$Id: RenameResourceCommand.java,v 1.9, 2005-06-06 13:05:01Z, Stanislav Demchenko$
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
import com.negeso.module.media_catalog.domain.Resource;

/**
 * Rename resource command
 *
 * @version 	$Revision: 10$
 * @author 		Olexiy.Strashko
 */

@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RenameResourceCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(RenameResourceCommand.class);

	public static final String INPUT_NEW_NAME = "newName";
	public static final String INPUT_CURRENT_NAME = "currentName";
	public static final String INPUT_CURRENT_DIR = "currentDir";

	/* Added to request parameters */
	public static final String INPUT_ERROR_TEXT = "errorMessage";
	public static final String INPUT_USER_TEXT = "userMessage";

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	@ActiveModuleRequired
	public ResponseContext execute() {
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();

		if ( !SecurityGuard.isContributor(request.getSession().getUser()) )	{
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}
		
		String currentDir = request.getParameter(INPUT_CURRENT_DIR);
		String newName = request.getParameter(INPUT_NEW_NAME);
		String currentName = request.getParameter(INPUT_CURRENT_NAME);

		Resource resource = null;
		boolean success = false;
		try{
			resource = Repository.get().getResource(
				currentDir + 
				File.separatorChar + 
				currentName
			);
			success = resource.rename(
				request.getSession().getUser(),
				newName + "." + resource.getExtension()
			);
			
			if (!success){
				request.setParameter(INPUT_ERROR_TEXT, "Unable to rename file '" 
					+ currentName + "'"
				);
			}	
		}
		catch(AccessDeniedException e){
			request.setParameter(INPUT_ERROR_TEXT, e.getMessage());
		}
		catch(RepositoryException e){
			request.setParameter(INPUT_ERROR_TEXT, e.getMessage());
		} 
		catch (CriticalException e) {
			logger.error("- error", e);
			request.setParameter(INPUT_ERROR_TEXT, e.getMessage());
			response.setResultName(RESULT_FAILURE);
			return response;
		}
		
		if (success){
			request.setParameter(INPUT_USER_TEXT, "File '" + 
			currentName + "' successfully renamed to '" + 
			newName + "." + resource.getExtension() + "'"
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
