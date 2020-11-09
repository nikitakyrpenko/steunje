/*
 * @(#)DictionaryFile.java
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
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DbObject;
 
/**
 * @author Sergiy Oliynyk
 *
 */
public class DictionaryFile implements DbObject {

    private static Logger logger = Logger.getLogger(DictionaryFile.class);

    private static final String tableId = "dictionary_file";
    private static final int fieldCount = 3;

    public final static String selectFrom =
        "SELECT id, file_name, description from dictionary_file";

    private final static String findByIdSql =
        selectFrom + " where id = ?";
        
    private final static String findByFileNameSql =
        selectFrom + " where file_name = ?";

    private final static String updateSql =
        "UPDATE dictionary_file set id = ?, file_name = ?, description = ? "+
        "where id = ?";

    private final static String insertSql =
        "INSERT INTO dictionary_file (id, file_name, description) " +
        "values (?, ?, ?)";

    private Long id;
    private String fileName;
    private String description;

    /**
     *  Constructors
     */
    public DictionaryFile() {}

    public DictionaryFile(Long id, String fileName, String description)
    {
        this.id = id;
        this.fileName = fileName;
        this.description = description;
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
            fileName = rs.getString("file_name");
            description = rs.getString("description");
            logger.debug("-");
            return id;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
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
        stmt.setString(2, fileName);
        stmt.setObject(3, description);
        logger.debug("-");
        return stmt;
    }
    
    public static DictionaryFile findById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            DictionaryFile df = null;
            if (rs.next()) {
                df = new DictionaryFile();
                df.load(rs);
            }
            logger.debug("-");
            return df;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    public static DictionaryFile findByFileName(Connection conn, String fileName)
       throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findByFileNameSql);
            stmt.setString(1, fileName);
            ResultSet rs = stmt.executeQuery();
            DictionaryFile df = null;
            if (rs.next()) {
                df = new DictionaryFile();
                df.load(rs);
            }
            logger.debug("-");
            return df;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    public static DictionaryFile getById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        DictionaryFile df = findById(conn, id);
        if (df == null)
            throw new CriticalException("DictionaryFile not found by id=" + id);
        logger.debug("-");
        return df;
    }

    public static DictionaryFile getByFileName(Connection conn, String fileName)
       throws CriticalException
    {
        logger.debug("+");
        DictionaryFile df = findByFileName(conn, fileName);
        if (df == null)
            throw new CriticalException(
                "DictionaryFile not found by fileName=" + fileName);
        logger.debug("-");
        return df;
    }

    public Element getElement(Document doc) {
        logger.debug("+");
        Element element = Env.createDomElement(doc, "dictionaryFile");
        element.setAttribute("id", String.valueOf(id));
        element.setAttribute("fileName", fileName);
        element.setAttribute("description", description);
        logger.debug("-");
        return element;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("DictionaryFile [");
        sb.append("id=");
        sb.append(id);
        sb.append(" fileName=");
        sb.append(fileName);
        sb.append(" description=");
        sb.append(description);
        return sb.toString();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
