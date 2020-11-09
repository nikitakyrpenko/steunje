/*
 * @(#)SetListItemContainerCommand.java       @version    18.02.2004
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
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.command.SetSearchIndexDirty;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;


/**
 * @author      Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetListItemContainerCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(SetListItemContainerCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_CONTAINER_ID = "containerId";
    public static final String INPUT_PUBLIC = "public";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    /*
     * Essential rights: manager both of the source and target containers.
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
            Long listItemId = request.getLong(INPUT_LIST_ITEM_ID);
            Long containerId = request.getLong(INPUT_CONTAINER_ID);
            ListItem listItem = ListItem.getById(conn, listItemId);
            List list = List.getById(conn, listItem.getListId());
            checkPermission(user, list.getContainerId(),
                listItem.getContainerId(), containerId);

            String isPublic = request.getParameter(INPUT_PUBLIC);
            // if item's container is changing from "public" to "non-public"
            if (isPublic == null 
                && SecurityGuard.canView((Long)null, listItem.getContainerId())
                && !SecurityGuard.canView((Long)null, containerId))
            {
                Document doc = getResultDocument(INPUT_PUBLIC);
                Map resultMap = response.getResultMap();
                resultMap.put(OUTPUT_DOCUMENT, doc);
            }
            else {
                listItem.setContainerId(containerId);
                listItem.update(conn);
                Article summary = Article.findById(listItem.getTeaserId());
                if (request.getString(INPUT_PUBLIC, "false").equals("true"))
                    summary.setContainerId(containerId);
                else
                    summary.setContainerId(null);
                summary.update(conn);
                conn.commit();
            }
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
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
        if (request.getLong(INPUT_LIST_ITEM_ID) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Checks permission to process this operation 
     */
    private void checkPermission(User user, Long listContainerId,
        Long sourceContainerId, Long targetContainerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canManage(user, listContainerId) ||
            !SecurityGuard.canManage(user, sourceContainerId) ||
            !SecurityGuard.canManage(user, targetContainerId))
            throw new AccessDeniedException("Forbidden");
    }

    private Document getResultDocument(String resultName) throws Exception {
        logger.debug("+");
        Document doc = Env.createDom();
        Element resultNameElement = Env.createDomElement(doc, "resultName");
        resultNameElement.setAttribute("xmlns:negeso",
            Env.NEGESO_NAMESPACE);
        doc.appendChild(resultNameElement);
        resultNameElement.appendChild(doc.createTextNode(resultName));
        logger.debug("-");
        return doc;
    }
}
