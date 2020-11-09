/*
 * @(#)ShowArchivedListItem.java       @version	24.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.archive.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.AbstractCommand;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.archive.domain.ArchivedListItem;

/**
 * @author Sergiy Oliynyk
 */
public class GetArchivedListItemCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(
        GetArchivedListItemCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ID = "listId";
    public static final String INPUT_LIST_ITEM_ID = "listItemId";

    // Output parameters
    public static final String OUTPUT_DOCUMENT = "xml";

    /*
     * Essential rights: user must be able to view the item. Editing of 
     * the item from archive is not permitted.
     */
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        User user = request.getSession().getUser();
        Connection conn =  null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            ArchivedListItem listItem = ArchivedListItem.getById(conn,
                request.getLong(INPUT_LIST_ITEM_ID));
            checkPermission(user, listItem.getContainerId());

            Document doc = Env.createDom();
            Element element = listItem.getElement(doc);
            setAdditionalAttributes(conn, element, listItem);
            doc.appendChild(element);
            String attr = request.getParameter(INPUT_LIST_ID);
            doc.getDocumentElement().setAttribute(INPUT_LIST_ID, attr);

            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
        }
        catch (AccessDeniedException ex) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied", ex);
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        return response;
    }
    
    private void setAdditionalAttributes(Connection conn, Element itemElement, 
        ArchivedListItem listItem) throws Exception
    {
        logger.debug("+");
        if (listItem.getListItemLink() != null) {
            itemElement.setAttribute("linked", Language.findById(
                ArchivedListItem.getByListItemId(conn, 
                    listItem.getListItemLink()).getLangId()).getLanguage()); 
        }
        else {
            itemElement.setAttribute("mirrored", getLanguages(conn, listItem));
        }
        logger.debug("-");
    }

    /*
     * Searches for languages of lists in which the item is presented itself
     * or has links
     */

    private final static String selectLanguagesSql = 
        "select language from language " +
        "where id in " +
        "   (select lang_id from news_archive " +
        "   where (list_item_link = ? or id = ?) and site_id = ? order by lang_id)";

    private String getLanguages(Connection conn, ArchivedListItem listItem)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(selectLanguagesSql);
            stmt.setObject(1, listItem.getListItemId(), Types.BIGINT);
            stmt.setObject(2, listItem.getId(), Types.BIGINT);
            stmt.setObject(3, Env.getSiteId(), Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            StringBuffer sb = new StringBuffer();
            while (rs.next()) {
                sb.append(rs.getString("language"));
                sb.append(", ");
            }
            rs.close();
            int p = sb.lastIndexOf(", ");
            if (p >= 0)
                sb.delete(p, p+2);
            logger.debug("-");
            return sb.toString();
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
         if (request.getNonblankParameter(INPUT_LIST_ID) == null &&
             request.getLong(INPUT_LIST_ITEM_ID) == null)
            throw new CriticalException("Parameter missing");
    }
    
    /*
     * Checks permission to process this operation 
     */
    private void checkPermission(User user, Long listItemContainerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canView(user, listItemContainerId))
            throw new AccessDeniedException("Forbidden");
    }
}
