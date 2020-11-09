/*
 * @(#)$Id: CreateSecuredAlbumCommand.java,v 1.5, 2006-05-04 08:51:28Z, Dmitry Dzifuta$
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
import org.w3c.dom.Element;

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
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.FileKeeper;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.photoalbum.domain.PhotoAlbum;
import com.negeso.module.user.domain.Container;
import com.negeso.module.user.domain.Group;

/**
 * @author Sergiy Oliynyk
 * This class realises a creation of photo album combined with creation of
 * group, user and container in the security module.
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateSecuredAlbumCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(CreateSecuredAlbumCommand.class);

    public static final String INPUT_ID = "id";
    public static final String INPUT_ALBUM_NAME = "name";
    public static final String INPUT_USER_NAME = "user";
    public static final String INPUT_LOGIN = "login";
    public static final String INPUT_PASSWORD = "password";

    public static final String RESULT_CREATE = "create";

    // Output parameter
    public static final String OUTPUT_DOCUMENT = "xml";
    @ActiveModuleRequired
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            if (request.getParameter(INPUT_ID) == null) {
                logger.debug("- Return document");
                return getDocument(request);
            }
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            createSecureAlbum(conn, request);
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

    private ResponseContext getDocument(RequestContext request)
        throws Exception
    {
        logger.debug("+");
        Document doc = Env.createDom();
        Element element = Env.createDomElement(doc, "photo_album"); 
        doc.appendChild(element);
        element.setAttribute("album", request.getParameter(INPUT_ALBUM_NAME));
        element.setAttribute("user", request.getParameter(INPUT_USER_NAME));
        element.setAttribute("login", request.getParameter(INPUT_LOGIN));
        ResponseContext response = new ResponseContext();
        Map resultMap = response.getResultMap();
        resultMap.put(OUTPUT_DOCUMENT, doc);
        response.setResultName(RESULT_CREATE);
        logger.debug("-");
        return response;
    }

    private void createSecureAlbum(Connection conn, 
        RequestContext request) throws Exception
    {
        logger.debug("+");
        User user = request.getSession().getUser();
        Long parentId = request.getLong(INPUT_ID);
        String albumName = request.getParameter(INPUT_ALBUM_NAME);
        String userName = request.getParameter(INPUT_USER_NAME);
        String login = request.getParameter(INPUT_LOGIN);
        String password = request.getParameter(INPUT_PASSWORD);
        String groupName = albumName + "_visitors";
        Folder folder = getFolder(conn, parentId);
        if (folder != null) {
            checkPermission(user, folder.getContainerId());
            Language lang = request.getSession().getLanguage();
            createAlbum(conn, albumName, parentId, lang.getId(), 
                folder.getId());
            createSecurityRecords(userName, login, password, albumName,
                groupName);
        }
        if (parentId != null) {
            request.setParameter(GetPhotoAlbumCommand.INPUT_ID,
                parentId.toString());
        }
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

    private void createAlbum(Connection conn, String name, Long parentId, 
        Long langId, Long folderId) throws CriticalException
    {
        logger.debug("+");
        PhotoAlbum album = new PhotoAlbum();
        album.setName(name);
        album.setLangId(langId);
        album.setParentId(parentId);
        album.setArticleId(createArticle(conn, langId, name));
        album.setMcFolderId(createFolder(name, folderId));
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

    private Long createFolder(String albumName, Long parentFolderId)
        throws CriticalException
    {
        logger.debug("+");
        String name = getNewFolderName(albumName, parentFolderId);
        Folder parentFolder = Repository.get().getFolder(parentFolderId);
        Folder folder = Repository.get().createFolder(parentFolder, name, 
            parentFolder.getContainerId()); 
        logger.info("Created folder " + folder.getCatalogPath());
        logger.debug("-");
        return folder.getId();
    }

    private String getNewFolderName(String albumName, Long parentFolderId)
        throws CriticalException
    {
        logger.debug("+");
        String name = new String(albumName);
        Folder folder = Repository.get().getFolder(parentFolderId);
        String path = folder.getCatalogPath() + FileKeeper.prepareFileName(name);
        if (Repository.get().getFolder(path + '/') != null) {
            int counter = 0;
            while (Repository.get().getFolder(path + "_" + (++counter) + 
                "/").exists()) {}
            if (counter > 0)
                name += " " + (counter);
        }
        logger.debug("-");
        return name;
    }

    private void createSecurityRecords(String userName, String login, 
        String password, String albumName, String groupName)
        throws CriticalException
    {
        logger.debug("+");
        User user = new User();
        user.setName(userName);
        user.setLogin(login);
        user.setPassword(password);
        user.setType("visitor");
        Long[] users = new Long[1];
        users[0] = user.insert();
        Container container = new Container();
        container.setSiteId(1L);
        container.setName(albumName);
        Long[] containers = new Long[1];
        containers[0] = container.insert();
        Group group = new Group();
        group.setSiteId(1L);
        group.setName(groupName);
        group.setRoleId("visitor");
        group.insert();
        group.setVisitors(users);
        group.setContainers(containers);
        group.update();
        logger.debug("-");
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getParameter(INPUT_ALBUM_NAME) == null ||
            request.getParameter(INPUT_USER_NAME) ==  null ||
            request.getParameter(INPUT_LOGIN) == null ||
            request.getParameter(INPUT_PASSWORD) == null)
            throw new CriticalException("Parameter missing");
    }
    
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
