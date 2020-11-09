/*
 * @(#)$Id: NewsLineXmlBuilder.java,v 1.0, 2006-09-04 09:30:40Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.newsline.generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.friendly_url.FriendlyUrlService;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 * @version 1.0
 */
public class NewsLineXmlBuilder {

    private static Logger logger = Logger.getLogger(NewsLineXmlBuilder.class);

    private static final NewsLineXmlBuilder instance = new NewsLineXmlBuilder();
    
    private NewsLineXmlBuilder() { /* Singleton */ }

    public static NewsLineXmlBuilder getInstance() {
        return instance;
    }
    
    public static final String NEWSLINE_MODULE_ROOT_FRIENDLY_URL_PART = "NEWSLINE_MODULE_ROOT_FRIENDLY_URL_PART";
    public static final String MODULE_NAME = "news_module";

    private static final String headOfQuery =
        "select list_item.id, list_id, title, link, image_link, "+
        "document_link, view_date, publish_date, " +
        "article.id as articleId, article.head, article.text, " +
        "article.class as articleClass, parameters " +
        "from list_item join article on (list_item.teaser_id = article.id) ";

    public Element getElement(Connection conn, Document doc, Long userId,
        NewsLineParameters parameters, int langId) throws CriticalException
    {
        logger.debug("+");
        String rootUrl = FriendlyUrlService.getModuleFriendlyUrlRoot(new Long(langId), MODULE_NAME, NEWSLINE_MODULE_ROOT_FRIENDLY_URL_PART);
		boolean isFriendlyUrlEnabled = Env.isFriendlyUrlEnabled() && rootUrl != null;
        checkParameters(conn, doc, parameters);
        String containers = getContainers(conn, userId);
        PreparedStatement stmt = null;
        int itemsPerPage = parameters.getItemsPerPage();
        int offset = (parameters.getPageNumber()-1) * itemsPerPage;
        try {
            Element listElement = Env.createDomElement(doc, "list");
            int numberOfItems = getNumberOfItems(conn, containers, parameters);
            int numberOfPages = numberOfItems / itemsPerPage;
            if (numberOfItems % itemsPerPage > 0)
                numberOfPages++;
            listElement.setAttribute("items", String.valueOf(numberOfItems));
            listElement.setAttribute("pages", String.valueOf(numberOfPages));
            stmt = conn.prepareStatement(headOfQuery + 
                getWhereClause(containers, parameters) +
                " and list_item.site_id = " + Env.getSiteId() +
                " order by publish_date desc, list_item.id desc limit ? offset ?");
            stmt.setInt(1, itemsPerPage);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element itemElement = Env.createDomElement(
                    listElement.getOwnerDocument(), "listItem");
                setItemAttributes(itemElement, rs, isFriendlyUrlEnabled);
                if (isFriendlyUrlEnabled) {
                	Xbuilder.setAttr(itemElement, "url", FriendlyUrlService.find(conn, rs.getLong("id"), langId, UrlEntityType.LIST_ITEM, rootUrl));
                }
                listElement.appendChild(itemElement);
            }
            rs.close();
            logger.debug("-");
            return listElement;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    private void checkParameters(Connection conn, Document doc,
        NewsLineParameters parameters) throws CriticalException
    {
        if (conn == null || doc == null || parameters == null ||
            parameters.getLists() == null || parameters.getLists().length() == 0 
            || parameters.getItemsPerPage() <= 0 
            || parameters.getPageNumber() <= 0)
        {
            logger.debug("- Invalid argument");
            throw new CriticalException("Invalid argument");
        }
    }

    private String getWhereClause(String containers,
        NewsLineParameters parameters)
    {
        StringBuffer sb = new StringBuffer("where list_item.list_id in (");
        sb.append(parameters.getLists());
        sb.append(") ");
        sb.append("and ( (publish_date <= now()) AND (expired_date >= now() OR expired_date is null) )");
        sb.append("and (article.container_id is null ");
        if (containers != null) {
            sb.append("or article.container_id in (");
            sb.append(containers);
            sb.append(')');
        }
        sb.append(") ");
        boolean and = true;
        if (parameters.getParameters() != null) {
            sb.append("and parameters = '").append(parameters.getParameters()).append("' ");
            and = false;
        }
        if (parameters.isIncludeNull()) {
            sb.append((and ? "and" : "or")  + " parameters is null ");
            and = false;
        }
        if (parameters.isIncludeNotNull())
            sb.append((and ? "and" : "or")  + " parameters is not null ");
        return sb.toString();
    }

    private String getContainers(Connection conn, Long userId)
        throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select id from containers");
            while (rs.next()) {
                Long containerId = DBHelper.makeLong(rs.getLong("id"));
                if (SecurityGuard.canView(userId, containerId)) {
                    sb.append(containerId);
                    sb.append(',');
                }
            }
            int p = sb.lastIndexOf(",");
            if (p >= 0)
                sb.delete(p, p+1);
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return sb.length() > 0 ? sb.toString() : null;
    }

    private int getNumberOfItems(Connection conn, String containers,
        NewsLineParameters parameters) throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        String query = 
            "select count(list_item.id) from list_item join article on " +
            "(list_item.teaser_id = article.id) " + 
            getWhereClause(containers, parameters) +
            " and list_item.site_id = " + Env.getSiteId();
        int result = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                result = rs.getInt(1);
            }
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return result;
    }

    protected void setItemAttributes(Element itemElement, ResultSet rs, boolean isFriendlyUrlEnabled)
        throws SQLException
    {        
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("listId", rs.getString("list_id"));
        itemElement.setAttribute("title", rs.getString("title"));
        String hr = rs.getString("link");
        if (hr != null && hr.trim().length() > 0)
        	itemElement.setAttribute("href", rs.getString("link"));
        XmlHelper.setImageLinkAttribute(itemElement, rs.getString("image_link"));
        itemElement.setAttribute("documentLink", rs.getString("document_link"));
        XmlHelper.setDateAttribute(itemElement, "viewDate", rs.getString("view_date"));
        XmlHelper.setDateAttribute(itemElement, "publishDate", rs.getString("publish_date"));
        itemElement.setAttribute("parameters", rs.getString("parameters"));
        itemElement.appendChild(XmlHelper.getArticleElement(
            itemElement.getOwnerDocument(), "teaser", 
            rs.getLong("articleId"), rs.getString("articleClass"),
            rs.getString("head"), rs.getString("text")));
        if (isFriendlyUrlEnabled) {
        	//itemElement.setAttribute("url", FriendlyUrlService.find(conn, rs.getLong("id"), langId, type, root));
        }
        logger.debug("-");
    }
}
