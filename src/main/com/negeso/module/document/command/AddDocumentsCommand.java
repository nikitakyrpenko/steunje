/*
 * @(#)$AddDocumentsCommand.java$
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
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.TimeUtil;
import com.negeso.module.document.DocumentModule;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.domain.DocumentEntry;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.FileResource;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.ImageResource;

/**
 * @author Sergiy Oliynyk
 *
 */
public class AddDocumentsCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(AddDocumentsCommand.class);

    //  Input parameters
    public static final String INPUT_CATEGORY_ID = "category_id";
    public static final String INPUT_DOCUMENT_LINK = "link";

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        Long id = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            id = request.getLong(INPUT_CATEGORY_ID);
            Category category = Category.getById(conn, id);
            checkPermission(user, category);
            createDocuments(conn, request);
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
    
    /**
     * 
     * @param conn
     * @param request
     * @throws CriticalException
     */
    private void createDocuments(Connection conn, RequestContext request)
        throws CriticalException
    {
        logger.debug("+");
        Long categoryId = request.getLong(INPUT_CATEGORY_ID);
        String[] links = getParameters(request, INPUT_DOCUMENT_LINK);
        DocumentEntry document = new DocumentEntry();
        document.setCategoryId(categoryId);
        document.setLastModified(new Timestamp(TimeUtil.getMidTime()));
        for ( String link: links) {
            FileResource fileRes = Repository.get().getFileResource(link);
            if ( fileRes == null ){
                logger.error("bad link:" + link);
                continue;
            }

            document.setName(fileRes.getName());
            document.setDocumentLink(link);
        	document.setThumbnailLink(null);
            
            if (fileRes.isImage()) {
            	ImageResource image = Repository.get().getImageResource(fileRes.getFile());
            	try{
	            	ImageResource imageRes = image.resizePropByWidth(
	            		DocumentModule.get().getThumbnailWidth(),
	            		DocumentModule.get().getThumbnailHeight()
	            	);
	            	File th = moveThumbnail(imageRes.getFile());
	            	document.setThumbnailLink(Repository.get().getCatalogPath(th));
            	} catch (Exception e) {
            		logger.error("-error", e);
            	}
            } 
            
            if ( request.getSession().getUser().getName() == null ){
                document.setOwner( request.getSession().getUser().getLogin() );
            }
            else{
                document.setOwner( request.getSession().getUser().getName() );
            }
            document.insert(conn);
        }
        logger.debug("-");
    }

	private static File moveThumbnail(File thumbnail) {
        Folder thumbnails = Repository.get().createFolder(
        	Repository.get().getRootFolder(), "thumbnails"
        );
        File newFile = new File(thumbnails.getFile(), thumbnail.getName());
        try{
        	FileUtils.copyFile(thumbnail, newFile);
        	thumbnail.delete();
        	thumbnail = newFile;
        } catch (Exception e) {
        	logger.error("-error", e);
        }
        return thumbnail; 
	}

    
    private String[] getParameters(RequestContext request, String name) {
        String docsString = request.getParameter(name);
        if ( docsString == null ){
            return new String[0];
        }
        return docsString.split(";");
    }

    protected void checkRequest(RequestContext request) 
        throws CriticalException
    {
        if (request.getLong(INPUT_CATEGORY_ID) == null || 
            request.getParameter(INPUT_DOCUMENT_LINK) == null)
            throw new CriticalException("Parameter missing");
    }

    private void checkPermission(User user, Category category)
        throws AccessDeniedException, CriticalException 
    {
        Long folderId = category.getMcFolderId();
        Folder folder = Repository.get().getFolder(folderId); 
        if (folder != null && !folder.canEdit(user))
            throw new AccessDeniedException("Forbidden");
    }
}
