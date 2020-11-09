/*
 * @(#)$DeleteCategoryCommand.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.document.command;

import java.io.File;
import java.sql.Connection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.document.domain.Category;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.FolderDomain;

/**
 * @author Sergiy Oliynyk
 *
 */
public class DeleteCategoryCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(DeleteCategoryCommand.class);
    
    // Input parameters
    public static final String INPUT_ID = "target_category_id";
    public static final String INPUT_ERROR_TEXT = "errorMessage";
    
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            Long id = request.getLong(INPUT_ID);
            Category category = Category.getById(conn, id);
            Folder folder = Repository.get().getFolder(category.getMcFolderId());
            File directory = folder.getFile();
        	checkPermissionDirOfChild(folder,user, request);
        	category.delete(conn);        	
            FileUtils.deleteDirectory(directory);                	
        	FolderDomain.deleteFolders(conn, folder.getCatalogPath());            
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        BrowseCategory command = new BrowseCategory();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }
    
    
    private static void checkPermissionDirOfChild(Folder folder, User user, RequestContext request) 
    	throws AccessDeniedException, CriticalException
    {    
    	checkPermissionDirectory(user, folder, request);
    	File directory = folder.getFile();    	
    	File[] files = directory.listFiles();
        for(int i=0; i<files.length; i++) {
        	if(files[i].isDirectory()) {
        		Folder folderChild = Repository.get().getFolder(files[i]);
        		checkPermissionDirOfChild(folderChild, user, request);
            }          
        }        
    }
    
    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_ID) == null)
            throw new CriticalException("Parameter missing");
    }
    
    private static void checkPermissionDirectory(User user, Folder folder, RequestContext request)
        throws AccessDeniedException, CriticalException
    {        
        if ( folder == null ){
            logger.error("mc_folder not found");
            request.setParameter(INPUT_ERROR_TEXT, "Directory not found!");
            throw new AccessDeniedException("Forbidden");
        }         
        if ( !SecurityGuard.canManage(user, folder.getContainerId())){
        	request.setParameter(INPUT_ERROR_TEXT, 
        			"You can't removed this directory, because another groups use it also. " +
        			"<br/>On all questions, please contact the Administrator!");
            throw new AccessDeniedException("Forbidden");
        }
    }

}
