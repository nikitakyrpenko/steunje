/*
 * @(#)Language.java       @version 19.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dictionary.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

/*
 * @author      Sergiy Oliynyk
 */
public class Language implements DbObject {

    private static Logger logger = Logger.getLogger(Language.class);
    
    public static final String tableId = "interface_language";
    private static final int fieldCount = 3;

    private final static String findByIdSql =
        "select id, language, code from interface_language where id=?";

    private Long id;
    private String language;
    private String code;

    private static Locale defLocale = new Locale("en");

    /*
     * Constructors
     */
    public Language() {}
    
    public Language(Long id, String language, String code) {
        this.id = id;
        this.language = language;
        this.code = code;
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
            language = rs.getString("language");
            code = rs.getString("code");
            logger.debug("-");
            return id;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }
    
    public Long insert(Connection con) throws CriticalException { return null; }

    public void update(Connection con) throws CriticalException {}

    public void delete(Connection con) throws CriticalException {}

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }

    public String getUpdateSql() { return null; }

    public String getInsertSql() { return null; }

    public int getFieldCount() {
        return fieldCount;
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
       throws SQLException
    {
        return stmt;
    }
    
    public Element getElement(Document doc) {
        logger.debug("+");
        Element languageElement = Env.createDomElement(doc, "language");
        languageElement.setAttribute("id", String.valueOf("id"));
        languageElement.setAttribute("name", language);
        languageElement.setAttribute("code", code);
        logger.debug("-");
        return languageElement;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static String getLanguageByCode(String code) {
        return new Locale(code).getDisplayLanguage(defLocale);
    }
}
