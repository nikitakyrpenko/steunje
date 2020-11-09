/*
 * @(#)$Id: CheckFileExistenceCommand.java,v 1.4, 2005-06-06 13:04:13Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.command;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.FileResource;
import com.negeso.module.media_catalog.domain.Folder;

/**
 *
 * Checks file existance. File is setup by folder and file name.
 * 
 * @version		$Revision: 5$
 * @author		Olexiy Strashko
 * 
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckFileExistenceCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(CheckFileExistenceCommand.class);
	
	public static final String INPUT_FILE_NAME = "file_name";
	public static final String INPUT_FOLDER = "folder";
	
	/* Added to request parameters */
	public static final String INPUT_ERROR_TEXT = "errorMessage";
	public static final String INPUT_USER_TEXT = "userMessage";

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	@ActiveModuleRequired
	public ResponseContext execute()  {
		logger.debug("+");
		ResponseContext response = new ResponseContext();
		RequestContext request = getRequestContext();

		if ( !SecurityGuard.isContributor(request.getSession().getUser()) )	{
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}
		
		Element result = Xbuilder.createTopEl("result");
		String currentFolder = request.getString(INPUT_FOLDER, null);
		String fileName = request.getString(INPUT_FILE_NAME, null);
		if ( logger.isInfoEnabled() ){
		    logger.info("Folder:" + currentFolder + " filename:" + fileName);
		}

		try{
			if ( (currentFolder == null) || (fileName == null) ){
			    throw new RequestParametersException("Invalid parameters");
			}
		
			Folder folder = Repository.get().getFolder(currentFolder);
			if ( !folder.canView(request.getSession().getUser()) ){
			    throw new AccessDeniedException();
			}
			
			FileResource file = Repository.get().getFileResource(
			    currentFolder + "/" + fileName
			);
			
            Xbuilder.setAttr(result, "status", "OK");
			if ( file.exists() ){
			    Xbuilder.setAttr(result, "exists", "true");
			}
			else{
			    Xbuilder.setAttr(result, "exists", "false");
			}
		}
		catch(RequestParametersException e){
		    Xbuilder.setAttr(result, "status", "ERROR");
		    Xbuilder.setAttr(result, "message", e.getMessage());
		} 
		catch (AccessDeniedException e) {
		    Xbuilder.setAttr(result, "status", "ERROR");
		    Xbuilder.setAttr(result, "message", e.getMessage());
        }
		
        response.setResultName(RESULT_SUCCESS);
        response.getResultMap().put(OUTPUT_XML, result.getOwnerDocument());
		
		logger.debug("-");
		return response;	
	}

	@Override
	public String getModuleName() {
		return ModuleConstants.MEDIA_CATALOGUE_MODULE;
	}
    
}
