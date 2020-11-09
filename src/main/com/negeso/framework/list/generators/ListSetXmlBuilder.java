/*
 * @(#)$ListSetXmlBuilder.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.generators;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 *
 */
public class ListSetXmlBuilder {

    private static Logger logger = Logger.getLogger(ListSetXmlBuilder.class);

    private static final ListSetXmlBuilder instance = new ListSetXmlBuilder();

    private ListSetXmlBuilder() {}

    public static ListSetXmlBuilder getInstance() {
        return instance;
    }

    private String itemsQuery = 
        "select id, name from list where site_id = ";

    public Document getDocument(Connection conn, User user)
        throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, user));
            doc.getDocumentElement().setAttribute("xmlns:negeso", 
                Env.NEGESO_NAMESPACE);
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return doc;
    }

    public Element getElement(Connection conn, Document doc, User user)
        throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null) {
            logger.debug("- Invalid argument");
            throw new CriticalException("Invalid argument");
        }
        Element listSetElement = Env.createDomElement(doc, "listSet");
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String query = itemsQuery + Env.getSiteId() +
            	" and container_id in (" + getContainers(conn, user) + ')';
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "list");
                listSetElement.appendChild(element);
            }
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return listSetElement;
    }

    private String getContainers(Connection conn, User user)
        throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        StringBuffer sb = new StringBuffer();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id from containers");
            while (rs.next()) {
                Long containerId = DBHelper.makeLong(rs.getLong("id"));
                if (SecurityGuard.canContribute(user, containerId)) {
                    sb.append(containerId);
                    sb.append(',');
                }
            }
            int p = sb.lastIndexOf(",");
            if (p >= 0)
                sb.delete(p, p+1);
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
        return sb.length() > 0 ? sb.toString() : null;
    }
}
