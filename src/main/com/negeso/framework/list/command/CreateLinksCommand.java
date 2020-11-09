/*
 * @(#)CreateLinkCommand.java       @version	28.05.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Properties;

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
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateLinksCommand extends AbstractCommand {

    private static Logger logger =
        Logger.getLogger(CreateLinksCommand.class);

    // Input parameters
    public static final String INPUT_LIST_ITEM_ID = "listItemId";
    public static final String INPUT_LANGUAGES = "languages";

    // Output values
    public static final String OUTPUT_DOCUMENT = "xml";

    /*
     * Essential rights: user must view this item and be
     * a contributor of the containers of selected lists.
     * Method returns a DOM document
     */
    @SetSearchIndexDirty
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        User user = request.getSession().getUser();
        Connection conn = null;
        try {
            super.checkPermission(user);
            checkRequest(request);
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);
            ListItem listItem = ListItem.getById(conn,
                request.getLong(INPUT_LIST_ITEM_ID));
            String languages = request.getParameter(INPUT_LANGUAGES);
            if (languages == null) {
                Map resultMap = response.getResultMap();
                resultMap.put(OUTPUT_DOCUMENT, getDocument(conn, listItem,
                    user.getId()));
            }
            else createLinks(conn, listItem, user, languages);
            conn.commit();
            response.setResultName(RESULT_SUCCESS);
        }
        catch (Exception ex) {
            response.setResultName(ex instanceof AccessDeniedException ? 
                RESULT_ACCESS_DENIED : RESULT_FAILURE);
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }
    
    protected void checkRequest(RequestContext request)
        throws CriticalException
    {
        if (request.getLong(INPUT_LIST_ITEM_ID) == null)
            throw new CriticalException("Parameter missing");
    }

    /*
     * Set languages in which this item will be presented
     * languages constains the set of id divided by ';'
     */

    private static final String selectCategoriesSql = 
        "select list.id, lang_id, template_id, list.container_id, " +
        "list_item.id as linkId, list.name " +
        "from list left join list_item on " +
        "(list.id = list_item.list_id and list_item_link = ?) " +
        "where template_id = (select template_id from list where id = ?) " +
        " and list.site_id = ? ";

    private Properties parseLanguages(String languages) {
    	Properties props = new Properties();
    	String[] splittedLanguages = languages.split(";");
    	for (String s : splittedLanguages) {
    		String[] language = s.split(",");
    		if (language.length == 2) {
    			props.setProperty(language[0], language[1]);
    		}
    	}
    	return props;
    }
    
    private void createLinks(Connection conn, ListItem listItem, User user, 
        String languages) throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        ListItem link = createLink(listItem);
        try {
            stmt = conn.prepareStatement(selectCategoriesSql);
            stmt.setObject(1, listItem.getId(), Types.BIGINT);
            stmt.setObject(2, listItem.getListId(), Types.BIGINT);
            stmt.setObject(3, Env.getSiteId(), Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            Properties props = parseLanguages(languages); 
            while (rs.next()) {
                String langId = rs.getString("lang_id");
                Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
                // with helps of linkId we can sure that link exists in the list
                // with specified template id
                Long linkId = DBHelper.makeLong(rs.getLong("linkId"));
                long lListId = rs.getLong("id");
                Long listId = DBHelper.makeLong(lListId);
                String value = null;
                if ((value = props.getProperty(String.valueOf(lListId))) != null
                		&& value.equals(langId)
                		&& !listId.equals(listItem.getListId())) {
                    if (linkId == null) {
                        link.setListId(listId);
                        listItem.setListItemLink(listItem.getId());
                        link.setContainerId(containerId);
                        link.insert(conn);
                    }
                }
                else if (linkId != null) {
                    // delete link
                    if (SecurityGuard.canManage(user, containerId)) {
                        link.setId(linkId);
                        link.delete(conn);
                    }
                }
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }
    
    /**
     * Creates a link to the item of the list. The link is the same item
     * with the same identifiers of the articles but its field 'link' must 
     * be set to id of the item. So, the link cannot be modified. It's a copy 
     * of the item only. 
     * @param listItem
     * @return
     */
    public static ListItem createLink(ListItem listItem) {
        logger.debug("+");
        ListItem li = new ListItem(null, null,
            listItem.getTitle(), 0,
            listItem.getTeaserId(),
            listItem.getArticleId(),
            listItem.getLink(),
            listItem.getImageLink(),
            listItem.getDocumentLink(),
            listItem.getThumbnailLink(),
            listItem.getId(),
            listItem.getViewDate(),
            listItem.getPublishDate(),
            listItem.getExpiredDate(),
            listItem.getCreatedBy(),
            listItem.getCreatedDate(),
            listItem.getLastModifiedBy(),
            listItem.getLastModifiedDate(), 
            null, 
            null,
            listItem.getPerLangId());
        logger.debug("-");
        return li;
    }

    private Document getDocument(Connection conn, ListItem listItem, 
        Long userId) throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            Document doc = Env.createDom();
            Element languagesElement = Env.createDomElement(doc, "languages");
            doc.appendChild(languagesElement);
            languagesElement.setAttribute("xmlns:negeso", 
                Env.NEGESO_NAMESPACE);
            languagesElement.setAttribute("listItemId", 
                listItem.getId().toString());
            stmt = conn.prepareStatement(selectCategoriesSql);
            stmt.setObject(1, listItem.getId(), Types.BIGINT);
            stmt.setObject(2, listItem.getListId(), Types.BIGINT);
            stmt.setObject(3, Env.getSiteId(), Types.BIGINT);            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element langElement = Env.createDomElement(doc, "lang"); 
                languagesElement.appendChild(langElement);
                Long langId = DBHelper.makeLong(rs.getLong("lang_id"));
                langElement.setAttribute("id", String.valueOf(langId));
                langElement.setAttribute("name", 
                    Language.findById(langId).getLanguage());
                langElement.setAttribute("category_name", rs.getString("name"));
                langElement.setAttribute("category_id", rs.getString("id"));
                Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
                if (!SecurityGuard.canContribute(userId, containerId)) {
                    langElement.setAttribute("disabled", "true");
                }
                if (listItem.getListId().intValue() == rs.getLong("id"))
                    langElement.setAttribute("default", "true");
                if (rs.getLong("linkId") != 0)
                    langElement.setAttribute("selected", "true");
            }
            rs.close();
            logger.debug("-");
            return doc;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException("Cannot build XML ", ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }
}
