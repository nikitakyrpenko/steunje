/*
 * @(#)$DeleteDocumentEntryCommand.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.document.command;

import java.sql.Connection;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.domain.DocumentEntry;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.Resource;

/**
 * @author Sergiy Oliynyk
 *
 */
public class DeleteDocumentEntryCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(DeleteDocumentEntryCommand.class);

    // Input parameters
    public static final String INPUT_ID = "document_id";

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
            Long documentId = request.getLong(INPUT_ID);
            DocumentEntry document = DocumentEntry.findById(conn, documentId);
            Category category = Category.getById(conn, document.getCategoryId());            
            checkPermission(user, category);
            document.delete(conn);
            conn.commit();
            deleteFileFromMedia(user,document.getDocumentLink());
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

    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_ID) == null) 
            throw new CriticalException("Parameter missing");
    }

    private void checkPermission(User user, Category category)
        throws AccessDeniedException, CriticalException
    {
        Long folderId = category.getMcFolderId();
        Folder folder = Repository.get().getFolder(folderId); 
        if (folder != null && !folder.canManage(user))
            throw new AccessDeniedException("Forbidden");
    }
    
    private static void deleteFileFromMedia(User user, String catalogPathFile){
    	logger.debug("+");
    	Resource resource = null;
		boolean success = false;
    	try{
    		resource = Repository.get().getResource(catalogPathFile);
			success = resource.delete(user);			
			if (!success){
				logger.error("Unable to remove file"+ catalogPathFile);				
			}			
		}catch(AccessDeniedException e){
			logger.error("Cannot process request", e);			
		}
		catch(RepositoryException e){
			logger.error("Cannot process request", e);			
		}
		catch (CriticalException e) {
			logger.error("- error", e);
		}    	
    	logger.debug("-");
	}
}
