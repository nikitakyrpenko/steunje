/*
 * @(#)$DictionaryUtil.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dictionary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;


import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.dictionary.domain.Dictionary;

public class DictionaryUtil {
    
    private static Logger logger = Logger.getLogger(DictionaryUtil.class);

    private DictionaryUtil() {}

    public static final String PATH = "WEB-INF/generated/dictionaries";

    public static final String CLASS_PATH = "WEB-INF/classes";

    private final static String findEntriesSql =
        Dictionary.selectFrom + " where dictionary_file_id = ? and entry_id = ?";

	private static ResourceBundleMessageSource messageSource;

    public static String findEntry(String entryId, String langCode) {
        logger.debug("+");
        Connection conn = null;
        String entry = null;
        try {
            conn = DBHelper.getConnection();
            entry = findEntry(conn, entryId, langCode);
        }
        catch (Exception ex) {
            logger.error(ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return entry;
    }

    public static String findEntry(Connection conn, String entryId,
        String langCode)
    {
        logger.debug("+");
        String entry = null;
        try {
            Dictionary dictionary = Dictionary.findByEntryId(conn, entryId,
                langCode);
            if (dictionary != null)
                entry = dictionary.getEntry();
        }
        catch (CriticalException ex) {
            logger.error(ex);
        }
        logger.debug("-");
        return entry;
    }

    public static Dictionary[] findEntries(Connection conn, 
        Long dictionaryFileId, String entryId) throws CriticalException
    {
        logger.debug("+");
        Dictionary[] entries = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findEntriesSql);
            stmt.setObject(1, dictionaryFileId);
            stmt.setString(2, entryId);
            ArrayList list = new ArrayList();
            ResultSet rs = stmt.executeQuery();
            Dictionary dict = null;
            while (rs.next()) {
                dict = new Dictionary();
                dict.load(rs);
                list.add(dict);
            }
            entries = new Dictionary[list.size()];
            entries = (Dictionary[])list.toArray(entries);
        }
        catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        logger.debug("-");
        return entries;
    }

    public static String getPath(String fileName) {
        String path = fileName.endsWith(".properties") ? CLASS_PATH : PATH;
        return Env.getRealPath(path + "/" + fileName);
    }
    
    public static String[] getLanguages(Connection conn)
        throws CriticalException
    {
        logger.debug("+");
        Statement stmt = null;
        String[] languages;
        try {
            stmt = conn.createStatement();
            ArrayList list  = new ArrayList();
            ResultSet rs = stmt.executeQuery(
                "select code from interface_language");
            while (rs.next()) {
                list.add(rs.getString("code"));
            }
            languages = new String[list.size()];
            languages = (String[])list.toArray(languages);
        }
        catch (Exception ex) {
            logger.debug("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return languages;
    }
    
    public static void setMessageSource (ResourceBundleMessageSource messageSource)	{
		DictionaryUtil.messageSource = messageSource;    	
    }
    
    public static String getMessage(String key, Object[] params, Locale locale)	{
    	String message = "";
    	try	{
    		message = messageSource.getMessage(key, params, locale);
    	} catch	(NoSuchMessageException ex) {
    		logger.error("No message found under key '" + key + "' for locale '" + locale + "'.");
    		try	{
    			message = messageSource.getMessage(
    				key, 
    				params, 
    				new Locale(Env.getDefaultLanguageCode()));
        	} catch	(NoSuchMessageException exc) {
        		message = key;
    		}	
    	}
    	return message;
    	
    }
    
    public static String getMessage(String key, Object[] params, String langCode) {
    	return getMessage(key, params, new Locale(langCode)); 
    }
    
    public static String getMessage(String key, String langCode) {
    	return getMessage(key, null, new Locale(langCode));
    }
    
}
