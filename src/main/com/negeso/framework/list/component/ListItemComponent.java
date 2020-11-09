/*
 * @(#)ListItemComponent.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.archive.domain.ArchivedListItem;
import com.negeso.module.dictionary.DictionaryUtil;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.page.AbstractPageComponent;

/**
 * @author Sergiy Oliynyk
 */
public class ListItemComponent extends AbstractPageComponent {

    private static Logger logger = Logger.getLogger(ListItemComponent.class);

    private static final String FindListPageSql =
        "SELECT filename FROM page WHERE contents LIKE ?";

    // Input parameters
    public static final String INPUT_LIST_ITEM_ID = "id";
    public static final String INPUT_STM_LIST_ITEM_ID = "stm_id";

    public Element getElement(Document document, 
        RequestContext request, Map parameters )
    {
        logger.debug("+");
        User user = request.getSession().getUser();
        Connection conn = null;
        Element element = null;
        try {
        	ListItem listItem = null;
        	Long perLangId = request.getLong(INPUT_STM_LIST_ITEM_ID);
        	Long listItemId = request.getLong(INPUT_LIST_ITEM_ID);
        	conn = DBHelper.getConnection();
        	if (perLangId != null) {
        		listItem = ListItem.findByPerLangId(conn, perLangId, request.getSessionData().getLanguage().getId());
        	}else {
        		if (listItemId == null) {
        			logger.debug("- id is not specified");
        			return getStubElement(document);
        		}
        		listItem = ListItem.findById(conn, listItemId);
        	}
            if (listItem != null) {
                checkPermission(user, listItem.getContainerId());
                element = listItem.getElement(document);
                element.setAttribute("return-link", getPageLink(conn, listItem));
            }
            else {
                logger.debug("ListItem not found. Looking at the archive");
                ArchivedListItem archivedListItem = null;
                if (perLangId != null) {
                	archivedListItem = ArchivedListItem.findByPerLangIdSql(conn, perLangId, request.getSessionData().getLanguage().getId());
            	}else if (listItemId != null){
            		archivedListItem = ArchivedListItem.findByListItemId(conn, listItemId);
            	}
                if (archivedListItem != null) {
                    checkPermission(user, archivedListItem.getContainerId());
                    element = archivedListItem.getElement(document);
                }
                else {
                    logger.debug("Item not found");
                    element = getStubElement(document);
                }
            }
        }
        catch (AccessDeniedException ex) {
            logger.debug("Access denied");
            element = getAccessDeniedElement(conn, document, request);
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            element = getStubElement(document);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return element;
    }

    /*
     * Checks permission to process this operation 
     */
    private void checkPermission(User user, Long listContainerId)
        throws AccessDeniedException
    {
        if (!SecurityGuard.canView(user, listContainerId))
            throw new AccessDeniedException("Forbidden");
    }

    private Element getStubElement(Document doc) {
        return Env.createDomElement(doc, "listItem");
    }

    /**
     * Builds XML that contains a message.
     */
	public Element getAccessDeniedElement(Connection conn, Document doc, 
        RequestContext request)
    { 
        logger.debug("+");
        String langCode = request.getSession().getLanguageCode();
        String authorized = DictionaryUtil.findEntry(conn, 
            "FOR_AUTHORIZED_VISITORS", langCode); 
        Element itemElement = Env.createDomElement(doc, "listItem");
        itemElement.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
        Element detailsElement = Env.createDomElement(doc, "details");
        itemElement.appendChild(detailsElement);
        Element articleElement = Env.createDomElement(doc, "article");
        detailsElement.appendChild(articleElement);
        Element textElement = Env.createDomElement(doc, "text");
        articleElement.appendChild(textElement);
        textElement.appendChild(doc.createTextNode(authorized));
        logger.debug("-");
		return itemElement;
	}
    
    /**
     * Find page with included list. List is identified by list item. 
     * 
     * @param item      The ListItem object
     * @return          The valid URL to list page
     */
    private String getPageLink(Connection conn, ListItem item) {
        logger.debug("+");
        String pageName = "";
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement(FindListPageSql);
            stmt.setString(1, "%list_id=" + item.getListId() + "%");
            ResultSet rs = stmt.executeQuery();
            // uses the first-retrieved page with list included, 
            // all other pages - ignored
            if ( rs.next() ){
                pageName = rs.getString("filename");    
            }
            rs.close();
        }
        catch(SQLException e) {
            logger.error("error", e);
        }
        finally{
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return Env.getHostName() + pageName;
    }
}
