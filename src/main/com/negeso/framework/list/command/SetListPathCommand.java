/*
 * @(#)SetListPathCommand.java       @version  25.10.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.command;

import java.sql.Connection;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.command.SetSearchIndexDirty;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author      Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetListPathCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(SetListPathCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_LIST_PATH = "listPath";
    public static final String INPUT_LIST_LEVEL = "listLevel";

    private String listPath = null;
    private String listLevel = null; 

    /*
     * Essential rights: user must be able to view the item
     */
    @SetSearchIndexDirty
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            Long listItemId = request.getLong(INPUT_LIST_ITEM_ID);
            listPath = request.getParameter(INPUT_LIST_PATH);
            listLevel = request.getParameter(INPUT_LIST_LEVEL);
            ListItem listItem = ListItem.getById(conn, listItemId);
            checkPermission(user, listItem.getContainerId());
            
            int[] path = getPathArray(listPath);
            int level = Long.valueOf(listLevel).intValue();
            if (level >= 0 && level < path.length) {
                path[level] = listItem.getId().intValue();
                listPath = getPathString(path);
            }
            request.setParameter(INPUT_LIST_PATH, listPath);
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }

        GetListCommand command = new GetListCommand();
        command.setRequestContext(request);
        logger.debug("-");
        return command.execute();
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ITEM_ID) == null ||
            request.getParameter(INPUT_LIST_PATH) == null ||
            request.getLong(INPUT_LIST_LEVEL) == null)
            throw new CriticalException("Parameter missing");
    }

    private void checkPermission(User user, Long containerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canView(user, containerId))
            throw new AccessDeniedException("Forbidden");
    }

    public static int[] getPathArray(String listPath) {
        logger.debug("+");
        StringTokenizer st = new StringTokenizer(listPath, ";");
        int[] path = new int[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            path[i++] = Integer.parseInt(st.nextToken());
        }
        logger.debug("-");
        return path;
    }
    
    public static String getPathString(int[] path) {
        logger.debug("+");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < path.length; i++) {
            sb.append(path[i]);
            if (i < path.length-1)
                sb.append(';');
        }
        logger.debug("-");
        return sb.toString();
    }
}
