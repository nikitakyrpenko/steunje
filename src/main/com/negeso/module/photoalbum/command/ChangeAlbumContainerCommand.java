/*
 * @(#)$ChangeAlbumContainerCommand.java$
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
public class ChangeAlbumContainerCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(
        ChangeAlbumContainerCommand.class);
    
    //  Input parameters
    public static final String INPUT_ID = "id";
    public static final String INPUT_CONTAINER_ID = "containerId";
    
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
            Long id = request.getLong(INPUT_ID);
            Long containerId = request.getLong(INPUT_CONTAINER_ID);
            checkPermission(user, containerId);
            PhotoAlbum album = PhotoAlbum.getById(conn, id);
            Long folderId = album.getMcFolderId();
            if (folderId != null) {
                Folder folder = Repository.get().getFolder(folderId);
                if (folder != null) {
                    checkPermission(user, folder.getContainerId());
                    folder.setContainerId(containerId);
                    conn.commit();
                    
                }
            }
              
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

    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLongs(INPUT_ID) == null)
            throw new CriticalException("Parameter missing");
    }
    
    private void checkPermission(User user, Long containerId)
        throws AccessDeniedException
    {
        logger.debug("+");
        if (!SecurityGuard.canManage(user, containerId))
            throw new AccessDeniedException("Forbidden");
        logger.debug("-");
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
