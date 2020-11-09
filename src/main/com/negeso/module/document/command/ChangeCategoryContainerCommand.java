/*
 * @(#)$ChangeCategoryContainerCommand.java$
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


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.document.domain.Category;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class ChangeCategoryContainerCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(
        ChangeCategoryContainerCommand.class);

    //  Input parameters
    public static final String INPUT_ID = "target_category_id";
    public static final String INPUT_CONTAINER_ID = "container_id";

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
            id = request.getLong(INPUT_ID);
            logger.info("folder_id:" + id);
            Long containerId = request.getLong(INPUT_CONTAINER_ID);
            logger.info("container:" + containerId);
            checkPermission(user, containerId);
            Category category = Category.getById(conn, id);
            Long folderId = category.getMcFolderId();
            if (folderId != null) {
                Folder folder = Repository.get().getFolder(folderId);
                if (folder != null) {
                    checkPermission(user, folder.getContainerId());
                    folder.setContainerId(containerId);
                    conn.commit();
                }
                else{
                    logger.error("Folder is null, id: " + folderId );
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
        
        BrowseCategory command = new BrowseCategory();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }

    protected void checkRequest(RequestContext request)
       throws CriticalException
    {
        if (request.getLong(INPUT_ID) == null ){
            logger.error("parameter missing:" + INPUT_ID);
            throw new CriticalException("Parameter missing");
        }
        if (request.getLong(INPUT_CONTAINER_ID) == null ){
            logger.error("parameter missing:" + INPUT_CONTAINER_ID);
            throw new CriticalException("Parameter missing");
        }
    }
    
    private void checkPermission(User user, Long containerId)
        throws AccessDeniedException
    {
        logger.debug("+");
        if (!SecurityGuard.canManage(user, containerId))
            throw new AccessDeniedException("Forbidden");
        logger.debug("-");
    }
}
