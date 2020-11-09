/*
 * @(#)$Id: GetMediaFileCommand.java,v 1.11, 2005-07-18 12:16:54Z, Alexander G. Shkabarnya$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.command;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.GetFileCommand;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.view.GetFileView;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;






/**
 *
 * Media Catalog get file command. Must be used to discriminate
 * access control in files.
 *  
 * @version		$Revision: 12$
 * @author		Olexiy Strashko
 * 
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GetMediaFileCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(GetMediaFileCommand.class);
    
    /** Private flag attribute, for correct caching */
    private static String OUTPUT_IS_PRIVATE = "is-private";
    
    /** Last modified time for file */
    private static final String OUTPUT_LAST_MODIFIED =
        GetFileView.INPUT_LAST_MODIFIED;
    
    /**
     * Serves user media resources. Applies security for file access.
     *
     * @return if user is not authorized, GetFileCommand.RESULT_FAILURE;
     *         otherwise, executes GetFileCommand
     */
    @ActiveModuleRequired
    public ResponseContext execute()
    {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        response.setResultName(GetFileCommand.RESULT_FAILURE);
        RequestContext request = getRequestContext();
         
        String path = getRequestContext().getParameter(GetFileCommand.INPUT_FILE);
        try {
        	path = new String(getRequestContext().
        			getParameter(GetFileCommand.INPUT_FILE)
        			.getBytes("ISO-8859-1"), 
        			"UTF-8");
        	path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get file		
        String fileName = Repository.get().getRealPath(path);
		if(fileName == null){
            response.setResultName(RESULT_FAILURE);
            logger.debug("- file name is not specified");
            return response;
        }
        
        File file = new File(fileName);
        
        if ( file == null ){
            logger.error("- file is null: " + fileName);
            response.setResultName(RESULT_FAILURE);
            return response;
        }

        if ( !file.exists() || !file.isFile() ){
            logger.warn("- bad file download request: " + fileName);
            response.setResultName(RESULT_FAILURE);
            return response;
        }
            
        // check rights for file download
		Folder folder = Repository.get().getFolder( file.getParentFile() );
        boolean allowed = folder.canView(request.getSession().getUser());
        
        /*======GETIING LOGO FIX===========*/
		if ( !allowed ){
			Repository.refreshRepository();
			folder = Repository.get().getFolder( file.getParentFile() );
			allowed = folder.canView(request.getSession().getUser());
		}
		/*======GETIING LOGO FIX END===========*/
		
		if ( !allowed ){
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.error("- access denied, file: " + fileName);
            return response;
        }
        
        request.setParameter(GetFileCommand.INPUT_IS_MEDIA_FLAG, "true");
        
        GetFileCommand cmdGetFile = new GetFileCommand();
        cmdGetFile.setRequestContext(request);
        response = cmdGetFile.execute();
        
        response.getResultMap()
            .put( OUTPUT_LAST_MODIFIED, new Long(file.lastModified()) );
        response.getResultMap().put(OUTPUT_IS_PRIVATE, "true");
        
        logger.debug("-");
        return response;
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.MEDIA_CATALOGUE_MODULE;
	}
}
