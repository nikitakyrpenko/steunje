/*
 * @(#)$UpdateDocumentEntryCommand.java$
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
import java.sql.Timestamp;

import org.apache.log4j.Logger;


import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.TimeUtil;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.domain.DocumentEntry;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * @author Sergiy Oliynyk
 *
 */
public class UpdateDocumentEntryCommand extends AbstractCommand {

    private static Logger logger = 
        Logger.getLogger(UpdateDocumentEntryCommand.class);

    public static final String INPUT_ID = "document_id";
    public static final String INPUT_NAME = "name";
    public static final String INPUT_DOCUMENT_LINK = "link";
    public static final String INPUT_OWNER = "owner";
    public static final String INPUT_DESCRIPTION = "description";
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
            DocumentEntry document = DocumentEntry.getById(conn, id);
            Category category = Category.getById(conn, document.getCategoryId());
            checkPermission(user, category);
            setParametersFromRequest(request, document);
            document.update(conn);
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
        Command command = back ? new BrowseCategory() : new GetDocumentDetails();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }
    
    private void setParametersFromRequest(RequestContext request, 
        DocumentEntry document)
    {
        logger.debug("+");
        String name = request.getParameter(INPUT_NAME);
        String documentLink = request.getParameter(INPUT_DOCUMENT_LINK);
        String owner = request.getParameter(INPUT_OWNER);
        String description = request.getParameter(INPUT_DESCRIPTION);
        if (description != null && description.length() == 0)
            description = null;
        document.setName(name);
        document.setDocumentLink(documentLink);
        document.setOwner(owner);
        document.setDescription(description);
        document.setLastModified( new Timestamp(TimeUtil.getMidTime()));
        logger.debug("-");
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_ID) == null ||
            request.getParameter(INPUT_NAME) == null ||
            request.getParameter(INPUT_DOCUMENT_LINK) == null)
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
