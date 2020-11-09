/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.translator.service;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.dictionary.domain.Dictionary;
import com.negeso.module.translator.exception.TranslationExeption;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class DictionaryTranslator extends AbstractTranslator {
	
	private static final Logger logger = Logger.getLogger(DictionaryTranslator.class);
	
	@SuppressWarnings("unused")
	private static final String SELECT_DICTIONARIES_SQL = "SELECT * FROM dictionary WHERE lang_code = 'en' ORDER BY id";
	private static final String SELECT_DICTIONARIES2_SQL = "SELECT * FROM dictionary WHERE lang_code = ? OR lang_code = 'en' ORDER BY dictionary_file_id, entry_id, lang_code";
	private static final String FROM_LANGUAGE = "en";
	
	private static final String DELETE_DICTIONARIES_SQL = "DELETE FROM dictionary WHERE lang_code = ? ";
	
	private String[] currentDictionatyLangs = {"en", "nl", "de", "fr", "es", "it"};

	public DictionaryTranslator(ITranslateService translateService) {
		super(translateService);
	}

	@Override
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if(!ArrayUtils.contains(currentDictionatyLangs, to.getCode())) {
				stmt = con.prepareStatement(DELETE_DICTIONARIES_SQL);
				stmt.setString(1, to.getCode());
				stmt.execute();
			} 
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to clear dictionaries for lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	@Override
	public void clean(SessionFactory factory) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void copyAndTranslate(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		setProgressIndication("Dictionaries");
		try {
			if (!ArrayUtils.contains(currentDictionatyLangs, to.getCode())) {
//				stmt = con.prepareStatement(SELECT_DICTIONARIES_SQL);
//				rs = stmt.executeQuery();
//				while (rs.next()) {
//					Dictionary dictionary = new Dictionary(); 
//					dictionary.load(rs);
//					dictionary.setEntry(translateService.translate(dictionary.getEntry(), FROM_LANGUAGE, to.getCode()));
//					dictionary.setlangCode(to.getCode());
//					dictionary.insert(con);
//				}
			} else {
				stmt = con.prepareStatement(SELECT_DICTIONARIES2_SQL);
				stmt.setString(1, to.getCode());
				rs = stmt.executeQuery();
				Dictionary tempDictionary = new Dictionary();
				while (rs.next()) {
					Dictionary dictionary2 = new Dictionary();
					dictionary2.load(rs);					
					if (dictionary2.getEntryId() != null && dictionary2.getEntryId().equals(tempDictionary.getEntryId())) {
						if (FROM_LANGUAGE.equals(tempDictionary.getLangCode()) && StringUtils.isBlank(dictionary2.getEntry())) {
							dictionary2.setEntry(translateService.translate(tempDictionary.getEntry(), FROM_LANGUAGE, to.getCode()));
							dictionary2.update(con);
						} else if (FROM_LANGUAGE.equals(dictionary2.getLangCode()) && StringUtils.isBlank(tempDictionary.getEntry())) {
							tempDictionary.setEntry(translateService.translate(dictionary2.getEntry(), FROM_LANGUAGE, to.getCode()));
							tempDictionary.update(con);
						}
					}
					tempDictionary = dictionary2;
				}
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to translate dictionaries to lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}			
	

	@Override
	public void copyAndTranslate(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}

}

