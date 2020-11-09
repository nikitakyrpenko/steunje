/*
 * @(#)$UpdateDescriptionCommand.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.photoalbum.command;

import java.sql.Connection;

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
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.photoalbum.domain.PhotoAlbum;

/**
 * @author Sergiy Oliynyk
 *
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UpdateDescriptionCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(
        UpdateDescriptionCommand.class);
    
    //  Input parameters
    public static final String INPUT_ID = "id";
    public static final String INPUT_ALBUM_ID = "albumId";
    public static final String INPUT_NAME = "name";
    public static final String INPUT_BACK = "back";

    // Result of command
    private static final String RESULT_ALBUM = "ALBUM";
    private static final String RESULT_ALBUM_DESCRIPTION = "ALBUM_DESCRIPTION";
    private static final String RESULT_PHOTO = "PHOTO";
    
    private String resultName;
    
    @ActiveModuleRequired
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        PhotoAlbum album = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            Long id = request.getLong(INPUT_ID);
            album = PhotoAlbum.getById(conn, id);
            checkPermission(conn, user, album);
            album.setName(request.getParameter(INPUT_NAME));
            album.update(conn);
            conn.commit();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return performNextCommand(request, album);
    }
    
    private ResponseContext performNextCommand(RequestContext request,
        PhotoAlbum album)
    {
        logger.debug("+");
        Long albumId = request.getLong(INPUT_ALBUM_ID);
        boolean back = "true".equals(request.getParameter(INPUT_BACK));
        AbstractCommand command = null;
        if (back || album == null) {
            if (albumId != null)
            request.setParameter(INPUT_ID, albumId.toString());
            command = new GetPhotoAlbumCommand();
            resultName = RESULT_ALBUM;
        }
        else if (album.getMcFolderId() != null) {
            command = new GetAlbumDescriptionCommand();
            resultName = RESULT_ALBUM_DESCRIPTION;
        }
        else {
            command = new GetPhotoCommand();
            resultName = RESULT_PHOTO;
        }
        command.setRequestContext(request);
        ResponseContext response = command.execute();
        if (RESULT_SUCCESS.equals(response.getResultName()))
            response.setResultName(resultName);
        logger.debug("-");
        return response;
    }

    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_ID) == null ||
            request.getParameter(INPUT_NAME) == null)
            throw new CriticalException("Parameter missing");
    }
    
    private void checkPermission(Connection conn, User user, PhotoAlbum item)
       throws CriticalException, AccessDeniedException
    {
        logger.debug("+");
        PhotoAlbum album = item.getMcFolderId() != null ? item :
            PhotoAlbum.getById(conn, item.getParentId());
        Long folderId = album.getMcFolderId();
        Folder folder = Repository.get().getFolder(folderId); 
        if (folder != null && !SecurityGuard.canContribute(user, 
            folder.getContainerId()))
            throw new AccessDeniedException("Forbidden");
        logger.debug("-");
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
