/*
 * @(#)Language.java       @version	19.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.generators.Xbuilder;

/**
 * Language domain. Represents language, decsribed by attributes language code
 * and language description.<br>
 * This class was optimized to use LanguageCache. When adding methods
 * which modify, add or delete languages, take caution to update the cache.
 *
 * @version 	1.1
 * @author 		Olexiy Strashko
 */
public class Language implements Entity {

	private static final long serialVersionUID = -4371365938428155388L;


	private static final Logger logger = Logger.getLogger(Language.class);
    
    
	protected static final String tableId = "language";
    
    
    protected Long id = null;
    
    
	private String code = null;
    
    
	private String language = null;
	
	private Map<String, String> countries = new HashMap<String, String>();
    
    
    public Language() { /* For serialization */ }
    
    
	public Language(Long id, String language, String code)
    {
		this.id = id;
		this.language = language;
		this.code = code;
        logger.debug("+ -");
	}
    
    
	/**
	 * Default language getter (by Env setting). 
	 * 
	 * @return
	 * @throws CriticalException
	 * @throws ObjectNotFoundExsception
	 */
	public static Language getDefaultLanguage() throws CriticalException {
        logger.debug("+ -");
		try {
			return Language.findByCode(Env.getDefaultLanguageCode());
		} catch (ObjectNotFoundException e) {
			logger.error("- ObjectNotFoundException", e);
			throw new CriticalException(e);
		}
	}
    
    
	/**
	 * Finds Language by id. If object is not found - id is seted to null
	 *
	 * @param id
	 * @return Language if object is found, null - if object not found
	 * @throws SQLException
	 */
	public static Language findById(Long id)
		throws CriticalException, ObjectNotFoundException
    {
		logger.debug("+");
		try {
            Iterator iter = LanguageCache.getCachedLanguages().iterator();
            while (iter.hasNext()) {
                Language language = (Language) iter.next();
                if(language.getId().equals(id)){
                    logger.debug("-");
                    return language;
                }
            }
            logger.debug("- throwing ObjectNotFoundException");
            throw new ObjectNotFoundException(
                "- Language not found by id: " + id);
		} catch (SQLException ex) {
			logger.debug("- SQLException", ex);
			throw new CriticalException(ex);
		}
	}
    
    
	/**
	 * Finds Language by code. If object is not found, throws
     * ObjectNotFoundException
	 *
	 * @param code
	 * @return Language if object is found, null - if object not found
	 * @throws SQLException
	 */
	public static Language findByCode(String code)
		throws CriticalException, ObjectNotFoundException
    {
        logger.debug("+");
        try {
            Iterator iter = LanguageCache.getCachedLanguages().iterator();
            while (iter.hasNext()) {
                Language language = (Language) iter.next();
                if(language.getCode() != null
                        && language.getCode().equals(code)) {
                    logger.debug("-");
                    return language;
                }
            }
            logger.debug("- throwing ObjectNotFoundException");
            throw new ObjectNotFoundException(
                "- Language not found by code: " + code);
        } catch (SQLException ex) {
            logger.error(ex, ex);
            throw new CriticalException(ex);
        }
	}
	
	public static Language findByCodeQuietly(String langCode) {
		try {
			return Language.findByCode(langCode);
		} catch (ObjectNotFoundException e) {
			//ignore
		}
		return null;
	}
	
	public static Language findByIdQuietly(Long langId) {
		try {
			return Language.findById(langId);
		} catch (ObjectNotFoundException e) {
			//ignore
		}
		return null;
	}
	
	public static String findLangCodeByCountryCode(String countryCode)
		throws CriticalException, ObjectNotFoundException
	{
	    logger.debug("+");
	    try {
	    	String langCode = LanguageCache.getCachedCountryCodes().get(countryCode);
	    	if (langCode != null) {
	    		return langCode;
	    	}
	        logger.debug("- throwing ObjectNotFoundException");
	        throw new ObjectNotFoundException(
	            "- Language not found by country code: " + countryCode);
	    } catch (SQLException ex) {
	        logger.error(ex, ex);
	        throw new CriticalException(ex);
	    }
	}
    
    
	/**
	 * Get all languages. Ordered by id.
	 *
	 * @throws CriticalException
	 */
	public static ArrayList<Language> getItems() {
		logger.debug("+");
		try {
            ArrayList<Language> copy = new ArrayList<Language>(LanguageCache.getCachedLanguages());
            logger.debug("-");
            return copy;
		} catch (SQLException ex) {
			logger.debug("- SQLException", ex);
			throw new CriticalException(ex);
		}
	}
	
    
	/**
	 * w3c DOM document getter
	 * 
	 * @param doc
	 * @return
	 */
	public Element getDomElement(Document doc) {
		Element el = Xbuilder.createEl(doc, "language", null);
		Xbuilder.setAttr(el, "id", this.getId().toString());
		Xbuilder.setAttr(el, "code", this.getCode());
        Xbuilder.setAttr(el, "name", this.language);
		if (this.getCode().equals(Env.getDefaultLanguageCode())) {
			Xbuilder.setAttr(el, "default", "true");
		}
		return el;
	}
    
    
    /**
     * Get languges w3c DOM element
     * 
     * @param doc
     * @return
     * @throws CriticalException
     */
    public static Element getDomItems(Document doc, Long selectedId) 
        throws CriticalException 
    {
        Language curr = null;
        Element el = Xbuilder.createEl(doc, "languages", null);
        Element curEl = null;
        for (Iterator i = getItems().iterator(); i.hasNext();) {
            curr = (Language) i.next();
            curEl = curr.getDomElement(doc);
            if ( selectedId != null ){
                if ( curr.getId().equals(selectedId) ){
                    Xbuilder.setAttr(curEl, "selected", "true");
                }
            }
            el.appendChild(curEl);
        }
        return el;
    }
    
    public static void delete(Connection con, Language language) {
    	PreparedStatement stmt = null;
    	try {
			stmt = con.prepareStatement("DELETE FROM language WHERE id = ?");
			stmt.setLong(1, language.getId());
			stmt.execute();
		} catch (SQLException e) {
			logger.error("Unable to delete lang " + language.getCode(), e);
		} finally {
			DBHelper.close(stmt);
		}
    }

    
    /**
     * Get languges w3c DOM element
     * 
     * @param doc
     * @return
     * @throws CriticalException
     */
    public static Element getDomItems(Document doc) throws CriticalException {
        return Language.getDomItems(doc, null);
    }
    

    public String toString()
    {
		return "Language: id:"
			+ this.getId()
			+ " code:"
			+ this.getLanguage()
			+ " language:"
			+ this.getCode();
	}
    
    
    public Long getId()
    {
        return this.id;
    }
    
    
	public String getLanguage() {
		return this.language;
	}
    
    
	public String getCode() {
		return code;
	}


	public static Language getByCode(String langCode) {
		try {
			return Language.findByCode(langCode);
		} catch (ObjectNotFoundException e) {
			logger.error("-error", e);
		}
		return Language.getDefaultLanguage();
	}


	public void setCode(String code) {
		this.code = code;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setLanguage(String language) {
		this.language = language;
	}
    
	public static void resetCache() {
		LanguageCache.resetCache();
	}


	public Map<String, String> getCountries() {
		return countries;
	}


	public void setCountries(Map<String, String> countries) {
		this.countries = countries;
	}
}


/**
 * Method getCachedLanguages() resides in a separate class LanguageCache
 * to deny access to cachedLanguagesSortedById from
 * class Language
 *
 * @author        Stanislav Demchenko
 * @version       $1.0$
 */
class LanguageCache{
    
    
    private final static String SELECT_ALL_ORDER_BY_ID =
        " SELECT * FROM language ORDER BY id ";
    
    private final static String SELECT_COUNTRY_CODES =
        " SELECT language_geoip.country_code, language.code FROM language_geoip " +
        " LEFT JOIN language ON language.id=language_geoip.lang_id " +
        " ORDER BY country_code ";
    
    /** Languages are cached once in runtime (at first request) */
    private static Collection<Language> CACHED_LANGUAGES_SORTED_BY_ID = null;
    private static Map<String, String> CACHED_COUNTRY_TO_LANGUAGE_CODES = null;
    
    
    private static Logger logger = Logger.getLogger(LanguageCache.class);
    
    
    /** Reads all languages, sorted by id, into an ordered ArrayList */
    static synchronized Collection<Language> getCachedLanguages() throws SQLException
    {
        logger.debug("+");
        if(CACHED_LANGUAGES_SORTED_BY_ID != null){
            logger.debug("- return cached languages");
            return CACHED_LANGUAGES_SORTED_BY_ID;
        }
        Connection conn = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        Collection<Language> languages = new ArrayList<Language>(10);
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement(SELECT_ALL_ORDER_BY_ID);
            rs = findStatement.executeQuery();
            while(rs.next()) {
                Language language = new Language(
                    new Long(rs.getLong("id")),
                    rs.getString("language"),
                    rs.getString("code").trim());
                languages.add(language);
            }
            logger.debug("-");
            CACHED_LANGUAGES_SORTED_BY_ID = languages;
            return languages;
        } finally {
            DBHelper.close(rs);
            DBHelper.close(findStatement);
            DBHelper.close(conn);
        }
    }
    
	static synchronized Map<String, String> getCachedCountryCodes()
			throws SQLException {
		if(CACHED_COUNTRY_TO_LANGUAGE_CODES != null){
            return CACHED_COUNTRY_TO_LANGUAGE_CODES;
        }
		Connection conn = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        Map<String, String> map = new HashMap<String, String>();
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement(SELECT_COUNTRY_CODES);
            rs = findStatement.executeQuery();
            while(rs.next()) {
            	map.put(rs.getString("country_code"), rs.getString("code"));
            }
            logger.debug("-");
            CACHED_COUNTRY_TO_LANGUAGE_CODES = map;
            return map;
        } finally {
            DBHelper.close(rs, findStatement, conn);
        }
	}
    
    static void resetCache() {
    	if (CACHED_LANGUAGES_SORTED_BY_ID != null){
	    	synchronized (CACHED_LANGUAGES_SORTED_BY_ID) {
	    		CACHED_LANGUAGES_SORTED_BY_ID = null;
			}
    	}
    	if (CACHED_COUNTRY_TO_LANGUAGE_CODES != null){
    		synchronized (CACHED_COUNTRY_TO_LANGUAGE_CODES) {
    			CACHED_COUNTRY_TO_LANGUAGE_CODES = null;
    		}
    	}
	}
    
    
}

