/*
 * @(#)DictionaryXmlBuilder.java
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
import com.negeso.module.dictionary.domain.DictionaryFile;
import com.negeso.module.dictionary.domain.Language;

/**
 * @author Sergiy Oliynyk
 * 
 */
public class DictionaryXmlBuilder {

    private static Logger logger = Logger.getLogger(
        DictionaryXmlBuilder.class);

    private static final DictionaryXmlBuilder instance = 
        new DictionaryXmlBuilder();
    
    private DictionaryXmlBuilder() {}

    public static DictionaryXmlBuilder getInstance() {
        return instance;
    }

    // Used for translation from one language to another
    private final static String twoLanguagesItemsQuery =
        "select a.id, b.id as id2, a.entry, b.entry as entry2" +
        " from dictionary as a, dictionary as b" +
        " where a.dictionary_file_id = ?" +
        " and b.dictionary_file_id = a.dictionary_file_id" +
        " and a.entry_id = b.entry_id" +
        " and a.lang_code = ? and b.lang_code = ?" +
        " order by a.entry_id";

    // Used for management of dictionary structure
    private final static String englishItemsQuery =
        "select id, entry_id, entry from dictionary" +
        " where dictionary_file_id = ? and lang_code = 'en'" +
        " order by entry_id";

    public Document getDocument(Connection conn, Long dictionaryFileId,
        String langCodeFrom, String langCodeTo) throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, dictionaryFileId, langCodeFrom,
                langCodeTo));
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

    public Document getDocument(Connection conn, Long dictionaryFileId)
        throws CriticalException
    {
        logger.debug("+");
        Document doc = null;
        try {
            doc = Env.createDom();
            doc.appendChild(getElement(conn, doc, dictionaryFileId));
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

    public Element getElement(Connection conn, Document doc,
        Long dictionaryFileId, String langCodeFrom, String langCodeTo)
        throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null || dictionaryFileId == null || 
            langCodeFrom == null || langCodeTo == null)
            throw new CriticalException("- Illegal argument");
        Element dictionaryFileElement = null;
        PreparedStatement stmt = null;
        try {
            dictionaryFileElement = Env.createDomElement(doc, "dictionaryFile");
            setAttributes(conn, dictionaryFileElement, dictionaryFileId, 
                langCodeFrom, langCodeTo);
            stmt = conn.prepareStatement(twoLanguagesItemsQuery);
            stmt.setLong(1, dictionaryFileId.longValue());
            stmt.setString(2, langCodeFrom);
            stmt.setString(3, langCodeTo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, 
                    "dictionaryEntries");
                setItemsAttributes(element, rs);
                dictionaryFileElement.appendChild(element);
            }
            rs.close();
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return dictionaryFileElement;
    }

    public Element getElement(Connection conn, Document doc,
        Long dictionaryFileId) throws CriticalException
    {
        logger.debug("+");
        if (conn == null || doc == null || dictionaryFileId == null) 
            throw new CriticalException("- Illegal argument");
        Element dictionaryFileElement = null;
        PreparedStatement stmt = null;
        try {
            dictionaryFileElement = Env.createDomElement(doc, "dictionaryFile");
            setAttributes(conn, dictionaryFileElement, dictionaryFileId); 
            stmt = conn.prepareStatement(englishItemsQuery);
            stmt.setLong(1, dictionaryFileId.longValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "dictionary");
                setItemAttributes(element, rs);
                dictionaryFileElement.appendChild(element);
            }
            rs.close();
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return dictionaryFileElement;
    }

    private void setAttributes(Connection conn, Element dictionaryElement, 
        Long dictionaryFileId, String langCodeFrom, String langCodeTo)
        throws CriticalException
    {
        logger.debug("+");
        dictionaryElement.setAttribute("langCodeFrom", langCodeFrom);
        dictionaryElement.setAttribute("langCodeTo", langCodeTo);
        dictionaryElement.setAttribute("languageFrom",
            Language.getLanguageByCode(langCodeFrom));
        dictionaryElement.setAttribute("languageTo",
            Language.getLanguageByCode(langCodeTo));
        dictionaryElement.setAttribute("id", String.valueOf(dictionaryFileId));
        dictionaryElement.setAttribute("description",
            DictionaryFile.findById(conn, dictionaryFileId).getDescription());
        logger.debug("-");
    }
    
    private void setAttributes(Connection conn, Element dictionaryElement, 
        Long dictionaryFileId) throws CriticalException
    {
        logger.debug("+");
        dictionaryElement.setAttribute("id", String.valueOf(dictionaryFileId));
        dictionaryElement.setAttribute("description",
            DictionaryFile.findById(conn, dictionaryFileId).getDescription());
        logger.debug("-");
    }

    private void setItemsAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        Document doc = itemElement.getOwnerDocument();
        Element translateFrom = Env.createDomElement(doc, "dictionary");
        Element translateTo = Env.createDomElement(doc, "dictionary");
        itemElement.appendChild(translateFrom);
        itemElement.appendChild(translateTo);
        translateFrom.setAttribute("id", rs.getString("id"));
        translateFrom.setAttribute("entry", rs.getString("entry"));
        translateTo.setAttribute("id", rs.getString("id2"));
        translateTo.setAttribute("entry", rs.getString("entry2"));
        logger.debug("-");
    }
    
    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("entryId", rs.getString("entry_id"));
        itemElement.setAttribute("entry", rs.getString("entry"));
        logger.debug("-");
    }
}
