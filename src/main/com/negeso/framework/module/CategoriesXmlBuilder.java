/*
 * @(#)CategoriesXmlBuilder.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.framework.module.Module;

/**
 * @author Sergiy Oliynyk
 *
 */
public class CategoriesXmlBuilder {

    private static Logger logger = Logger.getLogger(CategoriesXmlBuilder.class);

    private static final CategoriesXmlBuilder instance = 
        new CategoriesXmlBuilder();
    
    private CategoriesXmlBuilder() {}

    public static CategoriesXmlBuilder getInstance() {
        return instance;
    }

    private final static String itemsQuery =
        "select id, instance_name, lang_id, container_id from list " +
        "where (module_id = ?) and (instance_name is not null) " +
        " and site_id = ? " +
        " order by name";

    public Document getDocument(Connection conn, Long moduleId, Long userId) 
        throws CriticalException
    {
        logger.debug("+");
        if (conn == null || moduleId == null || userId == null)
            throw new CriticalException("- Illegal argument");
        PreparedStatement stmt = null;
        try {
            Document doc = Env.createDom();
            Element categoriesElement = Env.createDomElement(doc, "categories");
            Module module = Module.getById(conn, moduleId);
            categoriesElement.setAttribute("module", module.getName());
            categoriesElement.setAttribute("module-title", module.getTitle());
            doc.appendChild(categoriesElement);
            stmt = conn.prepareStatement(itemsQuery);
            stmt.setObject(1, moduleId);
            stmt.setObject(2, Env.getSiteId() );            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long containerId = DBHelper.makeLong(rs.getLong("container_id"));
                if (SecurityGuard.canView(userId, containerId)) {
                    Element element = Env.createDomElement(doc, "category");
                    setItemAttributes(element, rs);
                    categoriesElement.appendChild(element);
                    XmlHelper.setRightsAttributes(element, userId, containerId);
                }
            }
            logger.debug("-");
            return doc;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }
    
    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("instance_name"));
        if (rs.getString("lang_id") != null)
            try {
                itemElement.setAttribute("language", Language.findById(
                    DBHelper.makeLong(rs.getLong("lang_id"))).getLanguage());
            }
            catch (Exception ex) {
                logger.error("Language not found");
        }
        itemElement.setAttribute("containerId", rs.getString("container_id"));
        logger.debug("-");
    }
}
