/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 * @TODO
 * 
 * @author		Volodymyr Snigur
 * @version		$Revision: $
 *
 */
public class LanguagePresence {
	public static Logger logger = Logger.getLogger( LanguagePresence.class );

	private static final int INITIAL_MAP_SIZE = 10;
	private static final boolean LANG_DEFAULT_PRESENCE = false;
	private List<Integer> AddLanguageId;

	public static String PARAM_SUFFIX = "_lang2presence";
	public static String LANG_ROOT_XML = "languages";
	public static String LANG_CHILD_XML = "language";
	public static boolean DO_LANGUAGE_ADD = false;
	
	public String tableId = "job_vac2lang_presence";
	public String targetIdField = "vacancy_id";



	public LanguagePresence(String tableId, String targetIdField) {
		this.tableId = tableId;
		this.targetIdField = targetIdField;
	}

	/**
	 * Get language presence map for object. 
	 * 
	 * @param con			The Connection
	 * @param targetId		The target id
	 * @return				The <code>Map</code> of pairs: 
	 * 						langId(Long):isPresent(Boolean)  
	 * @throws CriticalException
	 */
	public Map<Long, Boolean> getPresenceMap(Connection con, Long targetId)
		throws CriticalException
	{
		logger.debug("+");
		Map<Long, Boolean> map = new LinkedHashMap<Long, Boolean>(INITIAL_MAP_SIZE);

		List items = Language.getItems();
		Language curLang = null;
		AddLanguageId = new ArrayList<Integer>();
		for (Iterator i = items.iterator(); i.hasNext(); ){
			curLang = (Language) i.next();  
			map.put(
				curLang.getId(), 
				Boolean.valueOf(this.hasLanguage(con, targetId, curLang.getId().intValue()))
			);
		}
		logger.debug("-");
		return map;
	}

	/**
 	 * Set language presence for object. 
	 * 
	 * @param con					The connection 
	 * @param targetId				The target id
	 * @param presenceMap			The <code>Map</code> of pairs: 
	 * 								langId(Long):isPresent(Boolean)  
	 * @param presenceMap			 
	 * @throws CriticalException
	 */	
	public void setPresenceMap(Connection con, Long targetId, Map presenceMap)
		throws CriticalException
	{
		logger.debug("+");
		Iterator i = presenceMap.keySet().iterator();
		Long curKey = null;		
		for ( ; i.hasNext(); ){
			curKey = (Long) i.next();
			this.setLanguage(
				con, 
				targetId, 
				curKey.intValue(), 
				((Boolean) presenceMap.get(curKey)).booleanValue()
			);
		}
		logger.debug("-");
	}
	
	/**
	 * 
	 * @param con
	 * @param doc
	 * @param productId
	 * @return
	 * @throws CriticalException
	 */
	public Element getPresenceElement( Connection con, Element parent, Long targetId ) 
		throws CriticalException
	{
		logger.debug("+");
		Element languages = Xbuilder.addEl(parent, LANG_ROOT_XML, null); 
		Map presenceMap = getPresenceMap(con, targetId);
		Iterator i = presenceMap.keySet().iterator();
		Long curLangId = null;
		Element curEl = null;
		try{
			for (; i.hasNext();){
				curLangId = (Long) i.next();
				curEl = Xbuilder.addEl(languages, LANG_CHILD_XML, null);
				Xbuilder.setAttr(curEl, "lang-id", curLangId.toString());
				Xbuilder.setAttr(curEl, "lang-code", Language.findById(curLangId).getCode());
				Xbuilder.setAttr(curEl, "value", presenceMap.get(curLangId).toString());
			}
		}
		catch(ObjectNotFoundException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
		logger.debug("-");
		return languages;
	}
	

	/**
	 * Update language presence for product. Parse requset parameters for pattern:
	 * %langId%_lang2presence. If parameter exists (non null) feature is setted.
	 * else - reseted.
	 * 
	 * @param con
	 * @param request
	 * @param product
	 */
	public void updateOrAddLanguagePresence(
		Connection con, RequestContext request, Long targetId, String idOnlyForRegion
	) 
		throws CriticalException
	{
		logger.debug("+");
		Iterator i = this.getPresenceMap(con, targetId).keySet().iterator();
		
		Long curLangId = null;
		
		HashMap<Long, Boolean> presenceMap = new LinkedHashMap<Long, Boolean>(20); 
		for ( ;i.hasNext(); ){
			curLangId = (Long) i.next();	
			if (request != null && request.getParameter( curLangId.toString() + PARAM_SUFFIX + idOnlyForRegion) == null){
				presenceMap.put(curLangId, Boolean.FALSE);
			}
			else {
				presenceMap.put(curLangId, Boolean.TRUE);
			}
		}		
		this.setPresenceMap(con, targetId, presenceMap);
		logger.debug("new Map:" + presenceMap.toString());
		logger.debug("-");
	}


	/**
	 * 
	 * @param con
	 * @param productId
	 * @param languageId
	 * @param newValue
	 * @throws CriticalException
	 */
	public void setLanguage(Connection con, Long targetId, int languageId, boolean newValue) 
		throws CriticalException
	{
		try {
			logger.debug("+");
			logger.debug("DO_LANGUAGE_ADD: " + LanguagePresence.isDoLanguageAdd() + 
					"CurLang:" + languageId+ " and addlangId: "+ this.AddLanguageId);
			
			if ( !LanguagePresence.DO_LANGUAGE_ADD && !this.AddLanguageId.contains(languageId)){
				doPresenceUpdate (con, newValue, targetId, languageId);
			}
			else {
				doPresenceInsert(con, newValue, targetId, languageId);
			}
			logger.debug("-");
		}
		catch(SQLException e){
			logger.error(e);
			throw new CriticalException(e);
		}
	}

	/**
	 * 
	 * @param con
	 * @param productId
	 * @param featureId
	 * @return
	 * @throws CriticalException
	 */

	private boolean hasLanguage(
			Connection con, Long targetId, int languageId
	) 
		throws CriticalException
	{
		try{
			PreparedStatement stmt = con.prepareStatement(
				"SELECT is_present FROM " + this.tableId + " " + 
				"WHERE " + this.targetIdField + " = ? AND lang_id = ?"
			);
			stmt.setLong(1, targetId.longValue());
			stmt.setInt(2, languageId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()){
				return rs.getBoolean("is_present");
			} else {
				AddLanguageId.add(languageId);
				return LANG_DEFAULT_PRESENCE;
			}
		}
		catch(SQLException e){
			logger.error(e);
			throw new CriticalException(e);
		}
	}
	
	public void doPresenceInsert(Connection con, boolean newValue, Long targetId, int languageId)
		throws SQLException
	{
		PreparedStatement stmt = con.prepareStatement(
			"INSERT INTO " + this.tableId + " " + 
			"(" + this.targetIdField + ", lang_id, is_present) " +
			"VALUES (?, ?, ?)"
		);
		stmt.setLong(1, targetId.longValue());
		stmt.setInt(2, languageId);
		stmt.setBoolean(3, newValue);
		stmt.executeUpdate();
		
	}

	public void doPresenceUpdate(Connection con, boolean newValue, Long targetId, int languageId)
		throws SQLException 
	{
		PreparedStatement stmt = con.prepareStatement(
			"UPDATE " + this.tableId + " SET is_present = ?" +
			"WHERE " + this.targetIdField + " = ? AND lang_id = ?"
		);
		stmt.setBoolean(1, newValue);
		stmt.setLong(2, targetId.longValue());
		stmt.setInt(3, languageId);
		stmt.executeUpdate();
	}


	public static boolean isDoLanguageAdd() {
		return DO_LANGUAGE_ADD;
	}

	public static void setDoLanguageAdd(boolean do_language_add) {
		DO_LANGUAGE_ADD = do_language_add;
	}

	
}