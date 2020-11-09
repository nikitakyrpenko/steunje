/*
 * @(#)$GetPhotoCommand.java$
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
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;

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
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.photoalbum.domain.PhotoAlbum;
import com.negeso.module.photoalbum.generators.PhotoXmlBuilder;

/**
 * @author Sergiy Oliynyk
 *
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GetPhotoCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(GetPhotoCommand.class);
    
    //  Input parameters
    public static final String INPUT_ID = "id";
    
    // Output paremeters
    public static final String OUTPUT_DOCUMENT = "xml";
    @ActiveModuleRequired
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            Long id = request.getLong(INPUT_ID);
            PhotoAlbum photo = PhotoAlbum.getById(conn, id);
            Folder folder = checkPermission(conn, user, photo);
            PhotoXmlBuilder builder = PhotoXmlBuilder.getInstance();
            Document doc = builder.getDocument(conn, id);
            doc.getDocumentElement().setAttribute("canContribute",
                String.valueOf(folder.canEdit(user)));
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
        }
        catch (AccessDeniedException ex) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied", ex);
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("-", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }
    
    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_ID) == null)
            throw new CriticalException("Parameter missing");
    }
    
    private Folder checkPermission(Connection conn, User user, PhotoAlbum photo)
        throws CriticalException, AccessDeniedException
    {
        logger.debug("+");
        PhotoAlbum album = PhotoAlbum.getById(conn, photo.getParentId());
        Long folderId = album.getMcFolderId();
        Folder folder = Repository.get().getFolder(folderId);
        if (folder != null && !folder.canView(user))
            throw new AccessDeniedException("Forbidden");
        logger.debug("-");
        return folder;
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
