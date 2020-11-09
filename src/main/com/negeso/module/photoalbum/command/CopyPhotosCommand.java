/*
 * @(#)$CopyPhotosCommand.java$
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.module.engine.ModuleEngine;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.photoalbum.domain.Image;
import com.negeso.module.photoalbum.domain.PhotoAlbum;

/**
 * @author Sergiy Oliynyk
 *
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CopyPhotosCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(
        CopyPhotosCommand.class);
    
    //  Input parameters
    public static final String INPUT_IDS = "ids";
    public static final String INPUT_ALBUM_ID = "id";
    public static final String INPUT_TARGET_ALBUM_ID = "moveToId";
    public static final String INPUT_MOVE = "move";
    
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
            copyPhotos(conn, request);
            conn.commit();
           
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

    private void processImages(String src, Folder targetFolder, boolean isMove)
    throws IOException {
    	String filename = src.substring(src.lastIndexOf('/') + 1);
    	String sourcePathname = Repository.get().getRealPath(src);
    	File sourceFile = new File(sourcePathname);

    	String targetPath = Repository.get().getRealPath(targetFolder.getCatalogPath());
    	File targetFile = new File(targetPath, filename);
  	
    	ModuleEngine.copyFile(sourceFile, targetFile);
    	
    	if (isMove) {
    		sourceFile.delete();
    	}
    }
    
    private String replacePath(String pathname, String path) {
    	String filename = pathname.substring(pathname.lastIndexOf('/') + 1);
    	return path + filename;
    }
    
    private void moveImage(Connection conn, Long imageId, Folder targetFolder)
    throws IOException {
        Image image = Image.findById(conn, imageId);
    	String src = image.getSrc();
    	String targetCatalogPath = targetFolder.getCatalogPath();
        String targetImageSrc = replacePath(src, targetCatalogPath);
        image.setSrc(targetImageSrc);
        image.update(conn);
        processImages(src, targetFolder, true);
    }

    private Long copyImage(Connection conn, Long imageId, Folder targetFolder)
    throws IOException {
        Image image = Image.findById(conn, imageId);
    	String src = image.getSrc();
    	String targetCatalogPath = targetFolder.getCatalogPath();
        String targetImageSrc = replacePath(src, targetCatalogPath);
        image.setSrc(targetImageSrc);
        Long newImageId = image.insert(conn);
        processImages(src, targetFolder, false);
        return newImageId;
    }
    
    private void copyPhotos(Connection conn, RequestContext request)
        throws CriticalException, AccessDeniedException,
        ObjectNotFoundException, IOException
    {
        logger.debug("+");
        User user = request.getSession().getUser();
        Long albumId = request.getLong(INPUT_ALBUM_ID);
        PhotoAlbum album = PhotoAlbum.getById(conn, albumId);
        Long targetAlbumId = request.getLong(INPUT_TARGET_ALBUM_ID);
        PhotoAlbum targetAlbum = PhotoAlbum.getById(conn, targetAlbumId);
        boolean isMove = "true".equalsIgnoreCase(request.getParameter(
            INPUT_MOVE));
        checkPermission(user, album, targetAlbum, isMove);
        Long[] photos = request.getLongs(INPUT_IDS);
		Long thumbnailId = null;
		Long mcFolderId = targetAlbum.getMcFolderId();
    	Folder targetFolder = Repository.get().getFolder(mcFolderId);

        for (int i = 0; i < photos.length; i++) {
            PhotoAlbum photo = PhotoAlbum.getById(conn, photos[i]);
            
            Long parentId = photo.getParentId();
            if (parentId.equals(albumId) &&
            		!photoExists(conn, targetAlbumId, photo.getImageId())) {
				photo.setParentId(targetAlbumId);
            	if (isMove) {
                	moveImage(conn, photo.getImageId(), targetFolder);
                	moveImage(conn, photo.getThumbnailId(), targetFolder);
            		photo.update(conn);
    				if (thumbnailId == null)
    					thumbnailId = photo.getThumbnailId();
            	}
            	else {
                	Long newImageId = copyImage(conn, photo.getImageId(), targetFolder);
                	Long newThumbnailId = copyImage(conn, photo.getThumbnailId(), targetFolder);
                    Article article = Article.findById(conn, photo.getArticleId());
                    Long newArticleId = article.insert(conn);
                	photo.setImageId(newImageId);
                	photo.setThumbnailId(newThumbnailId);
                	photo.setArticleId(newArticleId);
            		photo.insert(conn);
    				if (thumbnailId == null)
    					thumbnailId = newThumbnailId;
            	}
            }
        }
		if (targetAlbum.getThumbnailId() == null) {
            targetAlbum.setThumbnailId(thumbnailId);
            targetAlbum.update(conn);
        }
        logger.debug("-");
    }

    private boolean photoExists(Connection conn, Long albumId, Long imageId)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        boolean result = false;
        try {
            stmt = conn.prepareStatement(
                "select id from photo_album where parent_id = ?" +
                " and image_id = ?");
            stmt.setObject(1, albumId);
            stmt.setObject(2, imageId);
            ResultSet rs = stmt.executeQuery();
            result = rs.next();
            rs.close();
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return result;
    }

    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_ALBUM_ID) == null ||
            request.getLongs(INPUT_IDS) == null || 
            request.getLong(INPUT_TARGET_ALBUM_ID) == null)
            throw new CriticalException("Parameter missing");
    }
    
    private void checkPermission(User user, PhotoAlbum source, PhotoAlbum target, 
        boolean isMove) throws CriticalException, AccessDeniedException
    {
        logger.debug("+");
        Folder sourceFolder = Repository.get().getFolder(source.getMcFolderId());
        Folder targetFolder = Repository.get().getFolder(target.getMcFolderId());
        if (sourceFolder != null && targetFolder != null) {
            if (isMove) {
                if (!sourceFolder.canManage(user) || !targetFolder.canEdit(user))
                    throw new AccessDeniedException("Forbidden");
            }
            else {
                if (!sourceFolder.canView(user) || !targetFolder.canEdit(user)) 
                    throw new AccessDeniedException("Forbidden");    
            }
        }
        logger.debug("-");
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
