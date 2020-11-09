/*
 * @(#)DictionaryScriptBuilder.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dictionary.generators;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.dictionary.domain.DictionaryFile;

/**
 * @author Sergiy Oliynyk
 * 
 */
public class DictionaryScriptBuilder {

    private static Logger logger = Logger.getLogger(
        DictionaryScriptBuilder.class);

    private static final DictionaryScriptBuilder instance = 
        new DictionaryScriptBuilder();
    
    private DictionaryScriptBuilder() {}

    public static DictionaryScriptBuilder getInstance() {
        return instance;
    }

    public void generate(Connection conn, String fileName, String[] languages)
        throws CriticalException
    {
        logger.debug("+");
        try {
            PrintWriter out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
            printSqlScript(conn, out, languages);
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
    }

    /**
     * Method outputs the dictionary as SQL file.
     * @param conn Connection to database
     * @param languages the set of languages
     * @throws SQLException
     * @throws IOException
     */
    private void printSqlScript(Connection conn, PrintWriter out, 
        String[] languages) throws SQLException
    {
        logger.debug("+");
        printHeader(out);
        printLanguages(conn, out, languages);
        ArrayList files = new ArrayList(50);
        printDictionaryFiles(conn, out, files);
        printEntries(conn, out, files, languages);
        out.println();
        out.println("commit transaction;");
        out.close();
        logger.debug("-");
    }

    private void printLanguages(Connection conn, PrintWriter out, 
        String[] languages) throws SQLException
    {
        logger.debug("+");
        Statement stmt = conn.createStatement();
        String query = "select id, language, code from interface_language " +
            "order by id";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String code = rs.getString("code");
            int i = 0;
            for (;i < languages.length && !languages[i].equals(code); i++) {}
            if (i < languages.length) {
                out.print("INSERT INTO interface_language");
                out.print("(");
                out.print("id, language, code");
                out.print(") VALUES (");
                out.print(rs.getInt("id") + ", ");
                out.print("'" + rs.getString("language") + "', ");
                out.print("'" + code + "'");
                out.println(");");
            }
        }
        DBHelper.close(rs);
        DBHelper.close(stmt);
        logger.debug("-");
    }

    private void printDictionaryFiles(Connection conn, PrintWriter out,
        ArrayList files) throws SQLException
    {
        logger.debug("+");
        String query = "select id, file_name, description " +
            "from dictionary_file order by id";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        out.println();
        int count = 0;
        while (rs.next()) {
            count++;
            out.print("INSERT INTO dictionary_file");
            out.print("(");
            out.print("file_name, description");
            out.print(") VALUES (");
            out.print("'" + rs.getString("file_name") + "', ");
            out.print("'" + rs.getString("description") + "'");
            out.println(");");
            files.add(new DictionaryFile(DBHelper.makeLong(rs.getLong("id")), 
                rs.getString("file_name"), rs.getString("description")));
            count++;
        }
        rs.close();
        stmt.close();
        out.println();
        out.println("SELECT setval('dictionary_file_id_seq', " + count + ");");
        logger.debug("-");
    }

    private void printEntries(Connection conn, PrintWriter out, 
        ArrayList<DictionaryFile> files, String[] languages) throws SQLException
    {
        logger.debug("+");
        PreparedStatement stmt = conn.prepareStatement(
            getQueryString(languages));
        int count = 0;
        int fileCount = 0;
        for (DictionaryFile file : files) {
            fileCount++;
            out.println();
            out.println("-- " + file.getFileName());
            out.println("-- " + file.getDescription());
            stmt.setObject(1, file.getId());
            ResultSet rs = stmt.executeQuery();
            boolean empty = true;
            while (rs.next()) {
                empty = false;
                out.print("INSERT INTO dictionary");
                out.print("(");
                out.print("dictionary_file_id, entry_id, lang_code, entry");
                out.print(") VALUES(");
                out.print(fileCount + ", ");
                out.print("'" + rs.getString("entry_id") + "', ");
                out.print("'" + rs.getString("lang_code") + "', ");
                out.print("'" + rs.getString("entry").replaceAll(
                    "'", "\\\\'").trim() + "'");
                out.println(");");
                count++;
            }
            if (empty)
                out.println("-- file is empty");
        }
        out.println();
        out.println("SELECT setval('dictionary_id_seq', " + count + ");");
        logger.debug("-");
    }

    /**
     * Prints the header of SQL file.
     * @param out PrintWriter
     */
    private static void printHeader(PrintWriter out) {
        logger.debug("+");
        out.println("/*           Dictionary                */");
        out.println("/*  Copyright (c) 2004-2005 Negeso     */");
        out.println("/*      generated, do not edit         */");
        out.println();
        out.println("drop table dictionary;");
        out.println("drop table dictionary_file;");
        out.println("drop table interface_language CASCADE;");
        out.println();
        out.println("/*====================================================*/");
        out.println("/* Table: interface_language                          */");
        out.println("/*====================================================*/");
        out.println("create table interface_language (");
        out.println("id                   SERIAL not null,");
        out.println("language             VARCHAR(100)      not null,");
        out.println("code                 CHAR(2)           unique not null,");
        out.println("constraint PK_INTERFACE_LANGUAGE primary key (id)");
        out.println(");");
        out.println();
        out.println("/*====================================================*/");
        out.println("/* Table: dictionary_file                             */");
        out.println("/*====================================================*/");
        out.println("create table dictionary_file (");
        out.println("id                   SERIAL not null,");
        out.println("file_name            VARCHAR(255)      unique not null,");
        out.println("description          VARCHAR(255)      not null,");
        out.println("constraint PK_dictionary_FILE primary key (id)");
        out.println(");");
        out.println();
        out.println("/*====================================================*/");
        out.println("/* Table: dictionary                                  */");
        out.println("/*====================================================*/");
        out.println("create table dictionary (");
        out.println("id                   SERIAL            not null,");
        out.println("dictionary_file_id   INT4              not null,");
        out.println("entry_id             VARCHAR(200)      not null,");
        out.println("lang_code            CHAR(2)           not null,");
        out.println("entry                VARCHAR(2000)     not null,");
        out.println("constraint PK_DICTIONARY primary key (id),");
        out.println("constraint FK_DICTIONARY_REFERENCE_FILE foreign key " +
            "(dictionary_file_id)");
        out.println("references dictionary_file (id)");
        out.println("on delete cascade,");
        out.println("constraint FK_DICTIONARY_REFERENCE_LANGUAGE foreign key " +
            "(lang_code)");
        out.println("references interface_language (code)");
        out.println("on delete cascade");
        out.println(");");
        out.println();
        out.println("create index dictionary_file_index on dictionary " +
            "(dictionary_file_id);");
        out.println();
        out.println("begin transaction;");
        out.println();
        logger.debug("-");
    }

    private String getQueryString(String[] languages) {
        logger.debug("+");
        StringBuffer sb = new StringBuffer("select dictionary_file_id,");
        sb.append("entry_id, lang_code, entry from dictionary");
        if (languages != null && languages.length > 0) {
            sb.append(" where dictionary_file_id = ? and lang_code in (");
            for (int i = 0; i < languages.length; i++) {
                sb.append('\'');
                sb.append(languages[i]);
                sb.append('\'');
                if (i < languages.length-1)
                    sb.append(',');
            }
            sb.append(')');
        }
        sb.append(" order by entry_id, id");
        logger.debug("-");
        return sb.toString();
    }
}
