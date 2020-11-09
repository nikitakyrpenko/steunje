/*
 * @(#)$DeletePhotoAlbumCommand.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.photoalbum.command;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.FolderDomain;
import com.negeso.module.media_catalog.domain.Resource;
import com.negeso.module.photoalbum.domain.PhotoAlbum;

/**
 * @author Sergiy Oliynyk
 *
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DeletePhotoAlbumCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(
        DeletePhotoAlbumCommand.class);
    
    //  Input parameters
    public static final String INPUT_ALBUM_ID = "id"; // null if root folder
    public static final String INPUT_IDS = "ids";
    public static final String INPUT_ERROR_TEXT = "errorMessage";
    
    @ActiveModuleRequired
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
            Long[] ids = request.getLongs(INPUT_IDS);
            List<String> images = new ArrayList<String>();            
            for (int i = 0; i < ids.length; i++) {
                PhotoAlbum album = PhotoAlbum.getById(conn, ids[i]);
                checkPermission(conn, user, album, request);
                if (album.getMcFolderId() != null){
                	//delete folder without check dependencies                	
                	Folder folder = Repository.get().getFolder(album.getMcFolderId());
                	File directory = folder.getFile();
                	album.delete(conn);
                    FileUtils.deleteDirectory(directory);                	
                	FolderDomain.deleteFolders(conn, folder.getCatalogPath());                	
                }else{
                	//delete image with check dependencies
                	String filePath = getPathFile(conn, album);
                	String thumbnailPath = PhotoAlbum.getBySrcThumbnail(conn, album.getThumbnailId());
                	album.delete(conn);
                	images.add(filePath);
                	images.add(thumbnailPath);                	
                }
            }
            Long albumId = request.getLong(INPUT_ALBUM_ID);
            if (albumId != null) {
               	PhotoAlbum album = PhotoAlbum.getById(conn, albumId);
               	album.setThumbnailId(getFirstThumbnailId(conn, albumId));
               	album.update(conn);
            }            
            conn.commit();
            if(!images.isEmpty())
            	deleteImage(user, images, request);
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
		
        GetPhotoAlbumCommand command = new GetPhotoAlbumCommand();
        command.setRequestContext(request);        
        logger.debug("-");
        return command.execute();
    }

	private String getPathFile(Connection conn, PhotoAlbum album) {
		PhotoAlbum albumParent = PhotoAlbum.getById(conn, album.getParentId());
		Long folderId = albumParent.getMcFolderId();
		Folder folder = Repository.get().getFolder(folderId);
		String filePath=folder.getCatalogPath()+album.getName();
		return filePath;
	}

    private static final String firstThumbnailId =
        "select thumbnail_id from photo_album where name=" +
        " (select min(name) from photo_album where parent_id=?" +
        " and mc_folder_id is null)";

    private Long getFirstThumbnailId(Connection conn, Long albumId) {
        logger.debug("+");
        PreparedStatement stmt = null;
        Long thumbnailId = null;
        try {
            stmt = conn.prepareStatement(firstThumbnailId);
            stmt.setObject(1, albumId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                thumbnailId = DBHelper.makeLong(rs.getLong(1));
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return thumbnailId;
    }
    
    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLongs(INPUT_IDS) == null)
            throw new CriticalException("Parameter missing");
    }
    
    private static void  deleteImage(User user, List<String> catalogPathImages,  RequestContext request){
    	logger.debug("+");
    	for(String imagePath:catalogPathImages){
    		if(imagePath != null)
    			deleteFileFromMedia(user, imagePath, request);
    	}	    
	    logger.debug("-");
	}

    private static void deleteFileFromMedia(User user, String catalogPathFile, RequestContext request){
    	logger.debug("+");
    	Resource resource = null;
		boolean success = false;
    	try{
    		resource = Repository.get().getResource(catalogPathFile);
			success = resource.delete(user);			
			if (!success){
				request.setParameter(INPUT_ERROR_TEXT, "Unable to remove file"+ catalogPathFile);
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
    
	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM; 
	}
	
    private static void checkPermission(Connection conn, User user, PhotoAlbum item, RequestContext request)
	    throws AccessDeniedException, CriticalException
	{        
    	logger.debug("+");
        PhotoAlbum album = item.getMcFolderId() != null ? item :
            PhotoAlbum.getById(conn, item.getParentId());
        Long folderId = album.getMcFolderId();
        Folder folder = Repository.get().getFolder(folderId);        
        if (folder != null && !folder.canManage(user))
            throw new AccessDeniedException("Forbidden");
        request.setParameter(INPUT_ERROR_TEXT, 
    			"You can't removed this directory, because another groups use it also. " +
    			"<br/>On all questions, please contact the Administrator!");
        logger.debug("-");	    
	}
}
