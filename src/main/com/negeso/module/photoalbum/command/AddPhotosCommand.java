/*
 * @(#)$AddPhotosCommand.java$
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
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;


import com.negeso.framework.Env;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.photoalbum.domain.PhotoAlbum;
import com.negeso.module.photoalbum.domain.Image;

/**
 * @author Sergiy Oliynyk
 *
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AddPhotosCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(AddPhotosCommand.class);

    //  Input parameters
    public static final String INPUT_ALBUM_ID = "id";
    public static final String INPUT_NAME = "name";
    public static final String INPUT_WIDTH = "width";
    public static final String INPUT_HEIGHT = "height";
    public static final String INPUT_THUMBNAIL_NAME = "thumbnailName";
    public static final String INPUT_THUMBNAIL_WIDTH = "thumbnailWidth";
    public static final String INPUT_THUMBNAIL_HEIGHT = "thumbnailHeight";
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
            Long albumId = request.getLong(INPUT_ALBUM_ID);
            PhotoAlbum album = PhotoAlbum.getById(conn, albumId);
            checkPermission(user, album);
            Long thumbnailId = createPhotos(conn, request);
            if (album.getThumbnailId() == null) {
                album.setThumbnailId(thumbnailId);
                album.update(conn);
            }
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

    private Long createPhotos(Connection conn, RequestContext request)
        throws CriticalException
    {
        logger.debug("+");
        Long albumId = request.getLong(INPUT_ALBUM_ID);
        String[] names = getParameters(request, INPUT_NAME);
        int[] width = getInts(request, INPUT_WIDTH);
        int[] height = getInts(request, INPUT_HEIGHT);
        String[] thumbnailNames = getParameters(request, 
            INPUT_THUMBNAIL_NAME);
        int[] thumbnailWidth = getInts(request, INPUT_THUMBNAIL_WIDTH);
        int[] thumbnailHeight = getInts(request, INPUT_THUMBNAIL_HEIGHT);
        PhotoAlbum photo = new PhotoAlbum();
        Image image = new Image();
        Long firstThumbnailId = null;
        for (int i = 0; i < names.length; i++) {
            image.setSrc(names[i]);
            image.setWidth(width[i]);
            image.setHeight(height[i]);
            Long imageId = image.insert(conn);
            image.setSrc(thumbnailNames[i]);
            image.setWidth(thumbnailWidth[i]);
            image.setHeight(thumbnailHeight[i]);
            Long thumbnailId = image.insert(conn);
            if (firstThumbnailId == null)
                firstThumbnailId = thumbnailId;
            photo.setName(names[i].substring(names[i].lastIndexOf('/')+1));
            photo.setImageId(imageId);
            photo.setThumbnailId(thumbnailId);
            photo.setLangId(new Long(1));
            photo.setParentId(albumId);
            photo.setArticleId(createArticle(conn, request));
            photo.insert(conn);
        }
        logger.debug("-");
        return firstThumbnailId;
    }

    private Long createArticle(Connection conn, RequestContext request) 
       throws CriticalException
    {
        logger.debug("+");
        Article article = new Article();
        Language lang = request.getSession().getLanguage();
        article.setLangId(lang.getId());
        article.setText(Env.getProperty("photo-album.newPhoto.title", "Photo"));
        Long articleId = article.insert(conn);
        logger.debug("-");
        return articleId;
    }

    private String[] getParameters(RequestContext request, String name) {
        StringTokenizer st = new StringTokenizer(request.getParameter(name), 
            ";");
        String[] values = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            values[i++] = st.nextToken();
        }
        return values;
    }

    private int[] getInts(RequestContext request, String name) {
        StringTokenizer st = new StringTokenizer(request.getParameter(name), ";");
        int[] values = new int[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            values[i++] = Integer.parseInt(st.nextToken());
        }
        return values;
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        logger.debug("+");
        if (request.getLong(INPUT_ALBUM_ID) == null ||
            request.getParameter(INPUT_NAME) == null ||
            request.getParameter(INPUT_WIDTH) == null ||
            request.getParameter(INPUT_HEIGHT) == null ||
            request.getParameter(INPUT_THUMBNAIL_NAME) == null ||
            request.getParameter(INPUT_THUMBNAIL_WIDTH) == null ||
            request.getParameter(INPUT_THUMBNAIL_HEIGHT) == null)
            throw new CriticalException("Parameter missing");
        logger.debug("-");
    }
    
    private void checkPermission(User user, PhotoAlbum album)
        throws CriticalException, AccessDeniedException
    {
        logger.debug("+");
        Long folderId = album.getMcFolderId();
        Folder folder = Repository.get().getFolder(folderId); 
        if (folder != null && !folder.canEdit(user)) 
            throw new AccessDeniedException("Forbidden");
        logger.debug("-");
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
