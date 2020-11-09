/*
 * @(#)CreateListItemCommand.java       @version	18.02.2004
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.framework.list.service.ListItemService;
import com.negeso.framework.module.Module;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.core.service.ModuleService;
import com.negeso.module.social.service.SocialNetworkService;

/**
 * @author 	Sergiy Oliynyk
 */
public class GetListItemCommand extends AbstractCommand {

	private static Logger logger = Logger.getLogger(GetListItemCommand.class);

	// Input parameters
	public static final String INPUT_LIST_ITEM_ID = "listItemId";

    // Parameters for tree list support
    public static final String INPUT_LIST_PATH = "listPath";
    public static final String INPUT_ROOT_LIST_ID = "rootListId";

    public static final String OUTPUT_DOCUMENT = "xml";
    public static final String OUTPUT_TEMPLATE = "xsl";

    /*
     * Essential rights: user must be a contributor of the item if he wants
     * to have an ability to edit it. If user can only view this item, editing
     * is prohibuted.
     */
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
			ListItem listItem = ListItemService.getInstance().findById(
					request.getLong(INPUT_LIST_ITEM_ID));
            List list = List.getById(conn, listItem.getListId());
            checkPermission(user, list.getContainerId(),
                listItem.getContainerId());

            Document doc = Env.createDom();
            Element element = listItem.getElement(doc);
            setAdditionalAttributes(conn, element, request, list, listItem);
            XmlHelper.setRightsAttributes(element, user.getId(),
                listItem.getContainerId());
            Module module = ((ModuleService)request.getService("moduleService")).getModuleById(list.getModuleId());
            if (module != null) {
            	element.setAttribute("moduleName", module.getName());
            }
            doc.appendChild(element);
            element.appendChild(SocialNetworkService.getInstance().getSocialNetworksElement(element, listItem.getPublishedTo()));
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, doc);
            resultMap.put(OUTPUT_TEMPLATE, Templates.getEditItemXsl(conn, list));
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
        RequestContext request, List list, ListItem listItem) throws Exception
    {
        logger.debug("+");
        String listPath = request.getParameter(INPUT_LIST_PATH);
        String rootListId = request.getParameter(INPUT_ROOT_LIST_ID);
        if (listPath != null)
            itemElement.setAttribute("listPath", listPath);
        if (rootListId != null)
            itemElement.setAttribute("rootListId", rootListId);

        // append thumbnail-width and thumbnail-height
        // to list item(from list)
        // purpose: thumbnail generating
        itemElement.setAttribute("thumbnail-width",
            String.valueOf(list.getImageWidth()));
        itemElement.setAttribute("thumbnail-height",
            String.valueOf(list.getImageHeight()));

        // if item is public then non-autorized visitors can view its articles
        itemElement.setAttribute("public", String.valueOf(
            SecurityGuard.canView((Long)null, listItem.getContainerId())));

        // if item is not a public, but its summary article is public,
        // non-authorized users can view the summary article only
        if (listItem.getTeaserId() != null) {
            // the summary article
            Article article = Article.findById(listItem.getTeaserId());
            itemElement.setAttribute("publicSummary", String.valueOf(
                article.getContainerId() == null || SecurityGuard.canView(
                    (Long)null, listItem.getContainerId())));
        }
        
        if (listItem.getListItemLink() != null) {
            itemElement.setAttribute("linked", 
                getDefaultLanguage(conn, listItem));
        }
        else {
            itemElement.setAttribute("mirrored", getLanguages(conn, listItem));
        }
        logger.debug("-");
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
        Long listItemContainerId) throws AccessDeniedException
    {
        if (!SecurityGuard.canView(user, listContainerId) ||
            !SecurityGuard.canView(user, listItemContainerId))
            throw new AccessDeniedException("Forbidden");
    }

    /*
     * Select the language of the item to which this item is linked
     */
    
    private final static String getDefaultLanguageSql = 
        "select language from language where id = " 
        + "(select lang_id from list where id="
        + "(select list_id from list_item where id=?))";

    private String getDefaultLanguage(Connection conn, ListItem listItem)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        String result = null;
        try {
            stmt = conn.prepareStatement(getDefaultLanguageSql);
            stmt.setObject(1, listItem.getListItemLink(), Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getString("language");
            }
            else {
                logger.error("Language not found");
            }
            rs.close();
            logger.debug("-");
            return result;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    /*
     * Searches for languages of lists in which the item is presented itself
     * or has links
     */

    private final static String selectLanguagesSql = 
        "select lang_id, language from list " +
        "join language on list.lang_id = language.id " +
        "where list.site_id = ? " +
        " and list.id in (select list_id from list_item " +
        "where list_item_link = ? or id = ?) order by lang_id";
    
    private String getLanguages(Connection conn, ListItem listItem)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(selectLanguagesSql);
            stmt.setObject(1, Env.getSiteId(), Types.BIGINT);
            stmt.setObject(2, listItem.getId(), Types.BIGINT);
            stmt.setObject(3, listItem.getId(), Types.BIGINT);
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
}
