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
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 *
 */
public class ListRollXmlBuilder {

    private static Logger logger = Logger.getLogger(ListRollXmlBuilder.class);

    private static final ListRollXmlBuilder instance = new ListRollXmlBuilder();

    private ListRollXmlBuilder() {}

    public static ListRollXmlBuilder getInstance() {
        return instance;
    }

    private String itemsQuery = 
        "select id, name from list where id != ? " +
        " and module_id = ? " +        
        " and lang_id = ? and (container_id is null or container_id in (";

    public Document getDocument(Connection conn, Long listId, Long userId, Long moduleId)
        throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, listId, userId, moduleId));
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

    public Element getElement(Connection conn, Document doc, Long listId, 
        Long userId, Long moduleId) throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null || listId == null || userId == null) {
            logger.debug("- Invalid argument");
            throw new CriticalException("Invalid argument");
        }
        Element listSetElement = Env.createDomElement(doc, "listsroll");
        PreparedStatement stmt = null;
        try {
            String query = itemsQuery + getContainers(conn, userId) + ")) " +
                "order by name";
            stmt = conn.prepareStatement(query);
            stmt.setObject(1, listId);
            stmt.setLong  (2, moduleId > 0 ? moduleId : Env.getSite().getModuleId(ModuleConstants.NEWS));
            stmt.setObject(3, List.getById(conn, listId).getLangId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "list");
                setItemAttributes(element, rs);
                listSetElement.appendChild(element);
            }
            rs.close();
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return listSetElement;
    }

    private String getContainers(Connection conn, Long userId)
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
                if (SecurityGuard.canContribute(userId, containerId)) {
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
    
    private void setItemAttributes(Element itemElement, ResultSet rs)
       throws SQLException
    {        
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("name"));
        logger.debug("-");
    }
}
