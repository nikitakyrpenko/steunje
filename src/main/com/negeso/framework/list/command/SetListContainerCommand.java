/*
 * @(#)SetListContainerCommand.java       @version    18.02.2004
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
import com.negeso.framework.list.domain.List;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author      Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetListContainerCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(SetListContainerCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ID = "listId";
    public static final String INPUT_CONTAINER_ID = "containerId";

    /*
     * Essential rights: manager both to the source and target containers.
     * The Container Id parameter may contain a null value.
     */
    @SetSearchIndexDirty
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            Long listId = request.getLong(INPUT_LIST_ID);
            Long containerId = request.getLong(INPUT_CONTAINER_ID);
            List list = List.getById(conn, listId);
            checkPermission(user, list.getContainerId(), containerId);
            list.setContainerId(containerId);
            list.update(conn);
            conn.commit();
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
            return response;
        }
        catch (Exception ex) {
            response.setResultName(ex instanceof AccessDeniedException ? 
                RESULT_ACCESS_DENIED : RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        return response;
    }
    
    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ID) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation
     */
    private void checkPermission(User user, Long sourceContainerId,
        Long targetContainerId) throws AccessDeniedException
    {
        if (!SecurityGuard.canManage(user, sourceContainerId) ||
            !SecurityGuard.canManage(user, targetContainerId))
            throw new AccessDeniedException("Forbidden");
    }
}
