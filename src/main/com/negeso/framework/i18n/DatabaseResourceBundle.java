/*
 * @(#)$Id: DatabaseResourceBundle.java,v 1.4, 2007-01-09 18:27:10Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.i18n;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.module.custom_consts.service.CustomConstsService;

/**
 * 
 * A resource bundle that reads resources from a database.
 *
 * @author		Anatoliy Pererva
 * @version		$Revision: 5$
 *
 */
public class DatabaseResourceBundle extends ResourceBundle {

	/**
	 * The enumeration class used for keys.
	 * @author apererva
	 *
	 */
	static class SetEnumeration implements Enumeration<String> {
		Iterator<String> iterator;
		
		public SetEnumeration(Set<String> set) {
			iterator = set.iterator();
		}

		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		public String nextElement() {
			return iterator.next();
		}
	}

	/**
	 * Default dictionary file identifier. When dictionary file isn't passed
	 * into the constructor, this default name is used.
	 */
	public static final String DICT_COMMON_XSL = "dict_common.xsl";

    private static Logger logger = Logger.getLogger(DatabaseResourceBundle.class);

    /*
     * The SQL query which obtains constants from dictionary files. Constants
     * are stored into the 'lookup' map instance.
     */
    private static final String ITEMS_QUERY =
    	"select dictionary.entry_id, dictionary.entry"
    	+ " from dictionary, dictionary_file"
    	+ " where dictionary_file.file_name = ? and"
    	+ " dictionary_file.id = dictionary.dictionary_file_id and"
    	+ " dictionary.lang_code = ? order by dictionary.entry_id";

    /*
     * The map of database resource bundles.
     */
    private static Map<String, Map<String, DatabaseResourceBundle>> instances =
    	new HashMap<String, Map<String, DatabaseResourceBundle>>();
    
    private static CustomConstsService customConstsService; 
    
	private Map<String, String> lookup;
	private Map<String, String> customConstsLookup;
	
	private String dictionaryFileName;
	private String language;
	
	public DatabaseResourceBundle(String dictionaryFileName, String language) {
		this.dictionaryFileName = dictionaryFileName;
		this.language = language;
	}

	/**
	 * Returns the database resource bundle for default dictionary. The
	 * default dictionary is "dict_common.xsl".
	 * @param language The language for messages.
	 * @return The resource bundle.
	 */
	public static synchronized DatabaseResourceBundle getInstance(String language) {
		return getInstance(DICT_COMMON_XSL, language);
	}

	/**
	 * Returns the database resource bundle for arbitrary dictionary.
	 * @param dictionaryFileName The dictionary name.
	 * @param language The language for messages.
	 * @return
	 */
	public static synchronized DatabaseResourceBundle getInstance(
            String dictionaryFileName, String language) {
		Map<String, DatabaseResourceBundle> bundlesMap = instances.get(dictionaryFileName);
		if (bundlesMap == null) {
			bundlesMap = new HashMap<String, DatabaseResourceBundle>();
			instances.put(dictionaryFileName, bundlesMap);
		}
		DatabaseResourceBundle bundle = bundlesMap.get(language);
		if (bundle == null) {
			bundle = new DatabaseResourceBundle(dictionaryFileName, language);
			bundlesMap.put(language, bundle);
		}
		return bundle;
	}

	/**
	 * Returns an instance of resource bundle which references to the chain of
	 * database resource bundles. The chain is defined by dictionary file
	 * identities from parameters. When key isn't found in first
	 * resource bundle, then it is searched in next resource bundles until
	 * it will be found.
	 * @param dictionaries The identities of dictionary files.  
	 * @param language The language for messages.
	 * @return The resource bundle instance.
	 */
	public static synchronized ResourceBundle getChainResourceBundle(
			final String[] dictionaries, final String language) {
		return new ResourceBundle() {
			@Override
			protected Object handleGetObject(String key) {
				for (String dictionary : dictionaries) {
					Object value = getInstance(dictionary, language).getValue(key);
					if (value != null)
						return value;
				}
				return createNotFoundKeyMessage(key);
			}

			@Override
			public Enumeration<String> getKeys() {
				return null;
			}
		};
	}
	
	private synchronized void initMapIfNecessary() {
		if (lookup == null) {
			Map<String, String> localLookup = new HashMap<String, String>();
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
	        try {
	        	initCustomConsts();
				conn = getConnection();
	            stmt = conn.prepareStatement(ITEMS_QUERY);
	            stmt.setObject(1, dictionaryFileName);
	            stmt.setObject(2, language);
	            rs = stmt.executeQuery();
	            while (rs.next()) {
	                String readKey = rs.getString("entry_id");
	                String readValue = rs.getString("entry");
	                localLookup.put(readKey, readValue);
	            }
	            lookup = localLookup;
	        }
	        catch (Exception e) {
	        	logger.error("Database error: ", e);
	        }
	        finally {
	        	DBHelper.close(rs, stmt, conn);
	        }
		}
	}
	
	private void initCustomConsts() {
		if (customConstsLookup == null) {
			Map<String, String> localMap = new HashMap<String, String>();
			localMap.putAll(customConstsService.getTranslations(dictionaryFileName, language ));
			customConstsLookup = localMap;
		}
	}
	
	public static void clearModuleConsts(String moduleName) {
		 if (instances.get(moduleName)!= null)
		 	instances.get(moduleName).clear();
	}
	
	/*
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 * @param key key value is coverted in UPPER CASE.
	 */
	@Override
	protected Object handleGetObject(String key) {
		Object res = getValue(key);
		if (res == null) {
			logger.debug("Can't find value for key '" + key + "'");
			res = createNotFoundKeyMessage(key); 
		}
		return res;
	}

	private Object getValue(String key) {
		initMapIfNecessary();
		String customValue = customConstsLookup != null ? customConstsLookup.get(key.toUpperCase()) : null;
		if (customValue == null) {
			return lookup != null ? lookup.get(key.toUpperCase()) : null;
		} else {
			return customValue;
		}
	}
	
	private static String createNotFoundKeyMessage(String key) {
		return "!!!" + key + "!!!";
	}
	
	@Override
	public Enumeration<String> getKeys() {
		Set<String> globalLookup = lookup.keySet();
		Set<String> customLookup = customConstsLookup.keySet();
		globalLookup.addAll(customLookup);
		return new SetEnumeration(globalLookup);
	}

	protected Connection getConnection() throws SQLException {
		return DBHelper.getConnection();
	}

	public static void setCustomConstsService(
			CustomConstsService customConstsService) {
		DatabaseResourceBundle.customConstsService = customConstsService;
	}

	public static String getTranslation(String dictionary, String langCode,  String key) {
		return DatabaseResourceBundle.getInstance(dictionary, langCode).getString(key);
	}

}
