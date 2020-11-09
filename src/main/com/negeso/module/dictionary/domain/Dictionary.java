/*
 * @(#)Dictionary.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dictionary.domain;

import java.sql.*;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.CriticalException;

/**
 * @author Sergiy Oliynyk
 */
public class Dictionary implements DbObject {

    private static Logger logger = Logger.getLogger(Dictionary.class);

    private static final String tableId = "dictionary";
    private static final int fieldCount = 5;

    public final static String selectFrom =
        "SELECT id, dictionary_file_id, entry_id, lang_code, entry" +
        " from dictionary";

    private final static String findByIdSql =
        selectFrom + " where id = ?";

    private final static String findByEntryIdSql =
        selectFrom + " where entry_id = ? and lang_code = ?";

    private final static String updateSql =
        "UPDATE dictionary set id = ?, dictionary_file_id = ?," +
        " entry_id = ?, lang_code = ?, entry = ? where id = ?";

    private final static String insertSql =
        "INSERT INTO dictionary" +
        " (id, dictionary_file_id, entry_id, lang_code, entry)" +
        " values (?, ?, ?, ?, ?)";

    private Long id;
    private Long dictionaryFileId;
    private String entryId;
    private String langCode;
    private String entry;

    /**
     * Constructors
     */
    public Dictionary() {}

    public Dictionary(Long id, Long dictionaryFileId, String entryId,
        String langCode, String entry)
    {
        this.id = id;
        this.dictionaryFileId = dictionaryFileId;
        this.entryId = entryId;
        this.langCode = langCode;
        this.entry = entry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            dictionaryFileId = DBHelper.makeLong(rs.getLong(
                "dictionary_file_id"));
            entryId = rs.getString("entry_id");
            langCode = rs.getString("lang_code");
            entry = rs.getString("entry");
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return id;
    }

    public Long insert(Connection con) throws CriticalException {
        return DBHelper.insertDbObject(con, this);
    }

    public void update(Connection con) throws CriticalException {
        DBHelper.updateDbObject(con, this);
    }

    public void delete(Connection con) throws CriticalException {
        DBHelper.deleteObject(con, tableId, id, logger);
    }

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException
    {
        logger.debug("+");
        stmt.setObject(1, id);
        stmt.setObject(2, dictionaryFileId);
        stmt.setString(3, entryId);
        stmt.setString(4, langCode);
        stmt.setString(5, entry);
        logger.debug("-");
        return stmt;
    }
    
    public static Dictionary findById(Connection conn, Long id)
       throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        Dictionary dict = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dict = new Dictionary();
                dict.load(rs);
            }
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return dict;
    }

    public static Dictionary findByEntryId(Connection conn, String entryId, 
        String langCode) throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        Dictionary dict = null;
        try {
            stmt = conn.prepareStatement(findByEntryIdSql);
            stmt.setString(1, entryId);
            stmt.setString(2, langCode);    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dict = new Dictionary();
                dict.load(rs);
            }
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return dict;
    }

    public static Dictionary getById(Connection conn, Long id)
       throws CriticalException
    {
        Dictionary de = findById(conn, id);
        if (de == null)
            throw new CriticalException("DictionaryEntry not found by id=" + id);
        return de;
    }

    public static Dictionary getByEntryId(Connection conn, String entryId,
        String langCode) throws CriticalException
    {
        Dictionary de = findByEntryId(conn, entryId, langCode);
        if (de == null)
            throw new CriticalException(
                "DictionaryEntry not found by entry_id=" + entryId);
        return de;
    }

    public Element getElement(Document doc) {
        logger.debug("+");
        Element element = Env.createDomElement(doc, "dictionary");
        element.setAttribute("id", String.valueOf(id));
        element.setAttribute("dictionaryFileId", String.valueOf(
            dictionaryFileId));
        element.setAttribute("entryId", entryId);
        element.setAttribute("langCode", langCode);
        element.setAttribute("entry", entry);
        logger.debug("-");
        return element;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("DictionaryEntry [");
        sb.append("id=");
        sb.append(id);
        sb.append(" dictionaryFileId=");
        sb.append(dictionaryFileId);
        sb.append(" entryId=");
        sb.append(entryId);
        sb.append(" langCode=");
        sb.append(langCode);
        sb.append(" entry=");
        sb.append(entry);
        return sb.toString();
    }

    public Long getDictionaryFileId() {
        return dictionaryFileId;
    }

    public void setDictionaryFileId(Long dictionaryFileId) {
        this.dictionaryFileId = dictionaryFileId;
    }

    public String getEntryId() {
        return entryId;
    }
    
    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getLangCode() {
        return langCode;
    }
    
    public void setlangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getEntry() {
        return entry;
    }
    
    public void setEntry(String entry) {
        this.entry = entry;
    }
}
