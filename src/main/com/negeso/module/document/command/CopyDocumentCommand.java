/*
 * @(#)$CopyDocumentCommand.java$
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.domain.DocumentEntry;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class CopyDocumentCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(CopyDocumentCommand.class);

    //  Input parameters
    public static final String INPUT_ID = "id";
    public static final String INPUT_CATEGORY_ID = "categoryId";
    public static final String INPUT_MOVE = "move";

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
            copyDocument(conn, request);
            conn.commit();
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

    private void copyDocument(Connection conn, RequestContext request)
        throws CriticalException, AccessDeniedException
    {
        logger.debug("+");
        User user = request.getSession().getUser();
        Long id = request.getLong(INPUT_ID);
        DocumentEntry document = DocumentEntry.getById(conn, id);
        Category category = Category.getById(conn, document.getCategoryId());
        Long targetCategoryId = request.getLong(INPUT_CATEGORY_ID);
        Category targetCategory = Category.getById(conn, targetCategoryId);
        boolean isMove = "true".equalsIgnoreCase(request.getParameter(
            INPUT_MOVE));
        checkPermission(user, category, targetCategory, isMove);       
        if (!documentExists(conn, targetCategoryId, document.getDocumentLink())) 
        {
            document.setCategoryId(targetCategoryId);
            if (isMove) {
                document.update(conn);
            }
            else {
                document.insert(conn);
            }
        }
        logger.debug("-");
    }

    private boolean documentExists(Connection conn, Long categoryId, 
        String documentLink) throws CriticalException 
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        boolean result = false;
        try {
            stmt = conn.prepareStatement(
                "select id from dc_document where category_id = ?" +
                " and document_link = ?");
            stmt.setObject(1, categoryId);
            stmt.setObject(2, documentLink);
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
        if (request.getLong(INPUT_ID) == null ||
            request.getLongs(INPUT_CATEGORY_ID) == null) 
            throw new CriticalException("Parameter missing");
    }

    private void checkPermission(User user, Category source, Category target, 
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
}
