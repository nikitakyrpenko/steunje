/*
 * @(#)ModulesXmlBuilder.java
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
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.module.user.command.ManageContainersCommand;

/**
 * @author Sergiy Oliynyk
 *
 */
public class ModulesXmlBuilder {

    private static Logger logger = Logger.getLogger(ModulesXmlBuilder.class);

    private static final ModulesXmlBuilder instance = new ModulesXmlBuilder();
    
    private ModulesXmlBuilder() {}

    public static ModulesXmlBuilder getInstance() {
        return instance;
    }

    private static final String GET_MODULES_FOR_CUSTOMERS =
        "SELECT id, name, golive, expired, url, title, image, dict_key, " +
        " (SELECT count(*) FROM core_property WHERE module_id = module.id " +
        " AND core_property.visible = true) as parametersCount, " +
        " (SELECT count(*) FROM dic_custom_const WHERE module_id = module.id " +
        " ) as constsCount " +
        "FROM module " +
        "WHERE site_id=? " +
        "ORDER BY order_number ";

    private static final String GET_MODULES_FOR_WEBMASTERS =
        "SELECT id, name, golive, expired, url, title, image, dict_key, " +
        " (SELECT count(*) FROM core_property WHERE module_id = module.id) as parametersCount, " +
        " 1 as constsCount " +
        "FROM module " +
        "WHERE site_id=? " +
        "ORDER BY order_number ";
    
    private static final String GET_MODULE_SETTINGS =
        "SELECT * " +       
        "FROM module_setting " +
        "WHERE module_id=? " +
        "ORDER BY order_number ";
    
    public Document getDocumentForCustomers(Connection conn) throws CriticalException {
    	return getDocument(conn, GET_MODULES_FOR_CUSTOMERS);
    }
    
    public Document getDocumentForWebMasters(Connection conn) {
    	return getDocument(conn, GET_MODULES_FOR_WEBMASTERS);
    }
    
    private Document getDocument(Connection conn, String getModulesQuery) throws CriticalException {
        logger.debug("+");
        if (conn == null)
            throw new CriticalException("- Illegal argument");
        PreparedStatement stmt = null;
        try {
            Document doc = Env.createDom();
            Element modulesElement = Env.createDomElement(doc, "modules");
            doc.appendChild(modulesElement);
            stmt = conn.prepareStatement(getModulesQuery);
            stmt.setLong(1, Env.getSiteId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "module");
                setItemAttributes(element, rs);
                //add properties for security module
                if (rs.getString("name").equals("security_module")){
                	element.setAttribute("security.containers.management.enabled", 
                			ManageContainersCommand.MANAGEMENT_ENABLED?"true":"false");
                }
                modulesElement.appendChild(element);
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
        Timestamp golive = rs.getTimestamp("golive");
        Timestamp expired = rs.getTimestamp("expired");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("name"));
        XmlHelper.setDateAttribute(itemElement, "golive", golive);
        XmlHelper.setDateAttribute(itemElement, "expired", expired);
        itemElement.setAttribute("url", rs.getString("url"));
        itemElement.setAttribute("active", String.valueOf(
            Module.isActive(golive, expired)));
        itemElement.setAttribute("title", rs.getString("title"));
        itemElement.setAttribute("parametersCount", rs.getString("parametersCount"));
        itemElement.setAttribute("constsCount", rs.getString("constsCount"));
        itemElement.setAttribute("image", rs.getString("image"));
        itemElement.setAttribute("dict_key", rs.getString("dict_key"));
        setModuleSettings(rs.getStatement().getConnection(), itemElement, rs.getLong("id"));
        logger.debug("-");
    }
    
    private void setModuleSettings(Connection conn, Element module, Long moduleId) {
    	try {
			PreparedStatement stmt = conn.prepareStatement(GET_MODULE_SETTINGS);
			stmt.setLong(1, moduleId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Element moduleItemEl = Env.createDomElement(module.getOwnerDocument(), "moduleItem");
	        	moduleItemEl.setAttribute("dict_key", rs.getString("dict_key"));
	        	moduleItemEl.setAttribute("url", rs.getString("url"));
	        	moduleItemEl.setAttribute("order_number", rs.getLong("order_number") + "");
	        	Xbuilder.setAttr(moduleItemEl, "hide_from_user", rs.getBoolean("order_number"));	        	
				module.appendChild(moduleItemEl);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			throw new CriticalException("-- Critical exeption");
		}
    }
}
