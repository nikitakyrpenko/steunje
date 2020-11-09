/*
 * @(#)RemoveResourceCommand.java       @version	21.03.2004
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
 * Remove resource from MediaCatalog
 *
 * @version 	21.03.2004
 * @author 	Olexiy.Strashko
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RemoveResourceCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(RemoveResourceCommand.class);

	public static final String INPUT_VICTIM = "victim";
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
		String victim = request.getParameter(INPUT_VICTIM);
		
		String catalogPath = currentDir + File.separator + victim; 

		Resource resource = null;
		boolean success = false;
		try{
			resource = Repository.get().getResource(catalogPath);
			boolean isFolder = resource.isFolder(); 
			success = resource.delete(request.getSession().getUser());
			if (!success){
				request.setParameter(INPUT_ERROR_TEXT, "Unable to remove file '" 
					+ victim + "'"
				);
			}
			else{
			    if (isFolder){
				    request.setParameter(INPUT_USER_TEXT, "Folder '" + 
					        victim + "' is successfully removed"
					);
			    }
			    else{
				    request.setParameter(INPUT_USER_TEXT, "File '" + 
					        victim + "' is successfully removed"
					);
			    }
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
