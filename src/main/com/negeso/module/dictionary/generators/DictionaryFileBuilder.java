/*
 * @(#)DictionaryFileBuilder.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.dictionary.generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.dictionary.DictionaryUtil;
import com.negeso.module.dictionary.domain.DictionaryFile;

/**
 * @author Sergiy Oliynyk
 * 
 */
public class DictionaryFileBuilder {
    
    private static Logger logger = Logger.getLogger(
        DictionaryFileBuilder.class);

    private static DictionaryFileBuilder instance = 
        new DictionaryFileBuilder();
    
    private DictionaryFileBuilder() {}

    public static DictionaryFileBuilder getInstance() {
        return instance;
    }

    private final static String itemsQuery =
        "select entry_id, lang_code, entry from dictionary" +
        " where dictionary_file_id = ? order by id";

    private final static String itemsQueryByLang =
        "select a.entry_id, a.entry, b.entry as default" +
        " from dictionary a, dictionary b" +
        " where a.dictionary_file_id = b.dictionary_file_id" +
        " and a.entry_id = b.entry_id and a.dictionary_file_id = ?" +
        " and a.lang_code = ? and b.lang_code='en'" +
        " order by a.entry_id, a.id";

    public void generate(boolean forceGeneration) throws CriticalException {
        logger.debug("+");
        if (!forceGeneration) {
	        File flagMustGenerateDictionaries =
	        	new File(Env.getRealPath("/WEB-INF/generated/dictionaries/flag"));
			if (!flagMustGenerateDictionaries.exists()) {
	        	logger.debug("- no need to generate dictionaries");
	        	return;
	        }
			flagMustGenerateDictionaries.delete();
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(DictionaryFile.selectFrom);
            DictionaryFile dictionaryFile = new DictionaryFile();
            while (rs.next()) {
                dictionaryFile.load(rs);
                generate(conn, dictionaryFile);
            }
        }
        catch (Exception ex) {
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(rs, stmt, conn);
        }
        logger.debug("-");
    }

    public void generate(Connection conn, DictionaryFile dictionaryFile)
        throws CriticalException
    {
        logger.debug("+");
        if (conn == null || dictionaryFile == null)
            throw new CriticalException("- Illegal argument");
        try {
            if (dictionaryFile.getFileName().endsWith(".js"))
                generateJs(conn, dictionaryFile);
            else if (dictionaryFile.getFileName().endsWith(".properties"))
                generateProperties(conn, dictionaryFile);
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException", ex);
            throw new CriticalException(ex);
        }
        logger.debug("-");
    }
    
    private final static String xslHeader =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<xsl:stylesheet version=\"1.0\"\n" +
        "   xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";

    @Deprecated
    private void generateXsl(Connection conn, DictionaryFile dictionaryFile)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        String prevEntryId = "";
        String entryDef = "";
        try {
            PrintWriter out = getWriter(dictionaryFile.getFileName());
            out.println(xslHeader);
            out.println();
            stmt = conn.prepareStatement(itemsQuery);
            stmt.setObject(1, dictionaryFile.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String entryId = rs.getString("entry_id");
                String entry = rs.getString("entry");
                if (!prevEntryId.equals(entryId)) {
                    if (!prevEntryId.equals("")) {
                        out.println("</xsl:choose>");
                        out.println("</xsl:template>");
                        out.println();
                    }
                    out.println("<xsl:template name=\"" + entryId + "\">");
                    out.println("<xsl:choose>");
                    entryDef = entry;
                }
                if (entry == null || entry.length() == 0)
                    entry = entryDef;
                out.println("<xsl:when test=\"$lang='" +
                    rs.getString("lang_code") + "'\">" + entry + "</xsl:when>"); 
                prevEntryId = entryId;
            }
            rs.close();
            if (prevEntryId.length() > 0) {
                out.println("</xsl:choose>");
                out.println("</xsl:template>");
            }
            out.println();
            out.println("</xsl:stylesheet>");
            out.close();
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }

    public void generateJs(Connection conn, DictionaryFile dictionaryFile)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        Statement langStmt = null;
        ResultSet langRs = null; 
        String fileName = dictionaryFile.getFileName();
        fileName = fileName.substring(0, fileName.length()-3) + '_';
        try {
            langStmt = conn.createStatement();
            stmt = conn.prepareStatement(itemsQueryByLang);
            langRs = langStmt.executeQuery(
                "select code from interface_language");
            while (langRs.next()) {
                String code = langRs.getString("code");
                PrintWriter out = getWriter(fileName + code + ".js");
                out.println("var Strings = new Object();");
                stmt.setObject(1, dictionaryFile.getId());
                stmt.setString(2, code);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String entry = rs.getString("entry");
                    if (entry == null || entry.length() == 0)
                        entry = rs.getString("default");
                    out.println("Strings." + rs.getString("entry_id") + 
                        " = \"" + entry + "\";");
                }
                DBHelper.close(rs);
                out.close();
            }
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
        	DBHelper.close(langRs);
            DBHelper.close(langStmt);
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }

    public void generateProperties(Connection conn, 
        DictionaryFile dictionaryFile) throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        Statement langStmt = null;
        String fileName = dictionaryFile.getFileName();
        fileName = fileName.substring(0, fileName.length()-3) + '_';
        try {
            langStmt = conn.createStatement();
            stmt = conn.prepareStatement(itemsQueryByLang);
            ResultSet langRs = langStmt.executeQuery(
                "select code from interface_language");
            while (langRs.next()) {
                String code = langRs.getString("code");
                stmt.setObject(1, dictionaryFile.getId());
                stmt.setString(2, code);
                ResultSet rs = stmt.executeQuery();
                Properties dict = new Properties();
                while (rs.next()) {
                    String entry = rs.getString("entry");
                    if (entry == null || entry.length() == 0)
                        entry = rs.getString("default");
                    dict.setProperty(rs.getString("entry_id"), entry); 
                }
                rs.close();
                FileOutputStream out = getFileOutputStream(fileName + code + 
                    ".properties");
                dict.store(out, dictionaryFile.getDescription());
                out.close();
            }
            langRs.close();
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(langStmt);
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }

    private PrintWriter getWriter(String fileName) 
        throws FileNotFoundException, UnsupportedEncodingException 
    {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
            new FileOutputStream(DictionaryUtil.getPath(fileName)), "UTF-8"));
        return out;
    }
    
    private FileOutputStream getFileOutputStream(String fileName)
        throws FileNotFoundException
    {
        FileOutputStream out = new FileOutputStream(DictionaryUtil.getPath(
            fileName));
        return out;
    }
}
