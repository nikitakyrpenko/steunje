/*
 * @(#)$CreateAlbumCommand.java$
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


import com.negeso.framework.Env;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.Command;
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
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.FileKeeper;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.photoalbum.domain.PhotoAlbum;

/**
 * @author Sergiy Oliynyk
 * Realises creation of photo album without security records.
 * If it should be used then mappings.xml must be changed
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateAlbumCommand extends AbstractCommand {

    private static Logger logger = 
        Logger.getLogger(CreateAlbumCommand.class);
    
    //  Input parameters
    public static final String INPUT_ID = "id";
    
    @ActiveModuleRequired
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            Long parentId = request.getLong(INPUT_ID);
            Folder folder = getFolder(conn, parentId);
            if (folder != null) {
                checkPermission(user, folder.getContainerId());
                Language lang = request.getSession().getLanguage();
                createAlbum(conn, parentId, lang, folder.getId());
            }
            if (parentId != null) {
                request.setParameter(GetPhotoAlbumCommand.INPUT_ID,
                    parentId.toString());
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
        Command command = new GetPhotoAlbumCommand();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }
    
    private Folder getFolder(Connection conn, Long folderId)
        throws CriticalException
    {
        logger.debug("+");
        Folder folder = null;
        if (folderId != null) {
            PhotoAlbum album = PhotoAlbum.getById(conn, folderId);
            folder = Repository.get().getFolder(album.getMcFolderId());
        }
        else
            folder = Repository.get().getFolder("media/photo_album/");
        logger.debug("-");
        return folder;
    }

    private void createAlbum(Connection conn, Long parentId, Language lang, 
        Long folderId) throws CriticalException
    {
        logger.debug("+");
        PhotoAlbum album = new PhotoAlbum();
        String newAlbumName = getNewAlbumName(conn, lang.getCode(), folderId);
        album.setName(newAlbumName);
        album.setLangId(lang.getId());
        album.setParentId(parentId);
        album.setArticleId(createArticle(conn, lang.getId(), newAlbumName));
        album.setMcFolderId(createFolder(newAlbumName, folderId));
        album.insert(conn);
        logger.debug("-");
    }

    private Long createArticle(Connection conn, Long langId, String text) 
        throws CriticalException
    {
        logger.debug("+");
        Article article = new Article();
        article.setLangId(langId);
        article.setText(text);
        Long articleId = article.insert(conn);
        logger.debug("-");
        return articleId;
    }

    private Long createFolder(String name, Long parentFolderId)
        throws CriticalException
    {
        logger.debug("+");
        Folder parentFolder = Repository.get().getFolder(parentFolderId);
        Folder folder = Repository.get().createFolder(parentFolder, name, 
            parentFolder.getContainerId()); 
        logger.info("Created folder " + folder.getCatalogPath());
        logger.debug("-");
        return folder.getId();
    }

    private String getNewAlbumName(Connection conn, String langCode, 
        Long parentFolderId) throws CriticalException
    {
        logger.debug("+");
        String name = Env.getProperty("photo-album.newAlbum.title", "Album");

        //String name = DictionaryUtil.findEntry(conn, "NEW_ALBUM", langCode);
        Folder folder = Repository.get().getFolder(parentFolderId);
        String path = folder.getCatalogPath() + FileKeeper.prepareFileName(name);
        if (Repository.get().getFolder(path + '/') != null) {
            int counter = 0;
            while (Repository.get().getFolder(path + "_" + (++counter) + 
                "/").exists()) {}
            name += " " + (counter);
        }
        logger.debug("-");
        return name;
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException {}

    private void checkPermission(User user, Long containerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canContribute(user, containerId))
            throw new AccessDeniedException("Forbidden");
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.PHOTO_ALBUM;
	}
}
