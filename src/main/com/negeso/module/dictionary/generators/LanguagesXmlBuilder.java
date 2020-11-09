/*
 * @(#)LanguagesXmlBuilder.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dictionary.generators;

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

/**
 * @author Sergiy Oliynyk
 * 
 */
public class LanguagesXmlBuilder {

    private static Logger logger = Logger.getLogger(
        LanguagesXmlBuilder.class);

    private static final LanguagesXmlBuilder instance = 
        new LanguagesXmlBuilder();
    
    private LanguagesXmlBuilder() {}

    public static LanguagesXmlBuilder getInstance() {
        return instance;
    }

    private final static String itemsQuery =
        "select id, language, code from interface_language order by id;";

    public Document getDocument(Connection conn) throws CriticalException
    {
        logger.debug("+");
        try {
            Document doc = Env.createDom();
            doc.appendChild(getElement(conn, doc));
            logger.debug("-");
            return doc;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }
    
    public Element getElement(Connection conn, Document doc)
        throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null)
            throw new CriticalException("- Illegal argument");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Element languagesElement = Env.createDomElement(doc, "languages");
            languagesElement.setAttribute("xmlns:negeso",
                Env.NEGESO_NAMESPACE);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(itemsQuery);
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "language");
                setItemAttributes(element, rs);
                languagesElement.appendChild(element);
            }
            logger.debug("-");
            return languagesElement;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
        	DBHelper.close(rs);
            DBHelper.close(stmt);
        }
    }
    
    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("language"));
        itemElement.setAttribute("code", rs.getString("code"));
        if (Env.getDefaultLanguageCode().equals(rs.getString("code")))
            itemElement.setAttribute("default", "true");
        logger.debug("-");
    }
}
