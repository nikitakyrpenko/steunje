/*
 * @(#)$UpdateCategoryCommand.java$
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


import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.document.domain.Category;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class UpdateCategoryCommand extends AbstractCommand {

    private static Logger logger = 
        Logger.getLogger(UpdateCategoryCommand.class);

    //  Input parameters
    public static final String INPUT_ID = "category_id";
    public static final String INPUT_PARENT_CATEGORY_ID = "parent_category_id";
    public static final String INPUT_NAME = "name";
    public static final String INPUT_LANG_ID = "langId";
    public static final String INPUT_BACK = "back";

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
       
        Connection conn = null;
        try {
            super.checkPermission(user);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            Long id = request.getLong(INPUT_ID);
            logger.info("id:" + id);
            Category category = null;
            if ( id == null ){
                category = new Category();
            }
            else{
                category = Category.getById(conn, id);
            }
            checkPermission(user, category);
            String name = request.getParameter(INPUT_NAME);
            logger.info("name:" + name);
            Long langId = request.getLong(INPUT_LANG_ID);
            logger.info("langId:" + langId);
            category.setName(name);
            category.setLangId(langId);
            
            if ( category.getId() == null ){
                logger.info("new category");
                
                Category parentCategory = null;
                Long parentId = request.getLong(INPUT_PARENT_CATEGORY_ID);
                parentCategory = Category.getById(conn, parentId);

                Folder folder = parentCategory.getFolder();
                Folder newFolder = Repository.get().createFolder(
                    folder, 
                    category.getName(), 
                    folder.getContainerId()
                );
                
                category.setParentId(parentId);
                category.setMcFolderId(newFolder.getId());
                
                category.insert(conn);
                request.setParameter("category_id", category.getId().toString());
            }
            else{
                category.update(conn);
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
        boolean back = "true".equals(request.getParameter(INPUT_BACK));
        logger.info("back:" + request.getParameter(INPUT_BACK));

        Command command = back ? new BrowseCategory() : new GetCategoryDetails();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_ID) == null ||
            request.getParameter(INPUT_NAME) == null ||
            request.getParameter(INPUT_LANG_ID) == null)
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
