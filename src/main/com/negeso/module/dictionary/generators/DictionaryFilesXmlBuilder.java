/*
 * @(#)DictionaryFilesXmlBuilder.java
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
public class DictionaryFilesXmlBuilder {

    private static Logger logger = Logger.getLogger(
        DictionaryFilesXmlBuilder.class);

    private static final DictionaryFilesXmlBuilder instance = 
        new DictionaryFilesXmlBuilder();
    
    private DictionaryFilesXmlBuilder() {}

    public static DictionaryFilesXmlBuilder getInstance() {
        return instance;
    }

    private final static String itemsQuery =
        "select distinct id, file_name, description," +
        " (select count(id) from dictionary where lang_code = ?" +
        " and entry = '' and dictionary_file_id = dictionary_file.id) " +
        " as empty_strings " +
        " from dictionary_file order by id";

    public Document getDocument(Connection conn, String langCode)
        throws CriticalException
    {
        logger.debug("+");
        if (conn == null)
            throw new CriticalException("- Illegal argument");
        PreparedStatement stmt = null;
        try {
            Document doc = Env.createDom();
            Element dictionaryElement = Env.createDomElement(doc, 
                "dictionaries");
            dictionaryElement.setAttribute("xmlns:negeso",
                Env.NEGESO_NAMESPACE);
            doc.appendChild(dictionaryElement);
            stmt = conn.prepareStatement(itemsQuery);
            stmt.setString(1, langCode);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "dictionaryFile");
                setItemAttributes(element, rs);
                dictionaryElement.appendChild(element);
            }
            rs.close();
            logger.debug("-");
            return doc;
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }
    
    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("fileName", rs.getString("file_name"));
        itemElement.setAttribute("description", rs.getString("description"));
        itemElement.setAttribute("empty_strings", rs.getString("empty_strings"));
        logger.debug("-");
    }
}
