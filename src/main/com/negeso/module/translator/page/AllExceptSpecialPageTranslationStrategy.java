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
package com.negeso.module.translator.page;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.translator.exception.TranslationExeption;
import com.negeso.module.translator.service.AbstractTranslator;
import com.negeso.module.translator.service.ITranslateService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class AllExceptSpecialPageTranslationStrategy extends PageTranslationStrategy {
	
	private static final Logger logger = Logger.getLogger(AllExceptSpecialPageTranslationStrategy.class);
	
	private static final String WHERE_SECTION_SQL =  " WHERE lang_id = ? AND id NOT IN (SELECT page_id FROM menu)";											 
	
	private static final String DELETE_PAGES_SQL = " DELETE FROM page " + WHERE_SECTION_SQL;
	private static final String DELETE_STAT_COUNTER_SQL = "DELETE FROM stat_counter WHERE page_id IN ( SELECT id FROM page" + WHERE_SECTION_SQL + ")";
	
	

	public AllExceptSpecialPageTranslationStrategy(
            ITranslateService translateService, AbstractTranslator translator) {
		super(translateService, translator);
	}
	
	@Override
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(DELETE_STAT_COUNTER_SQL);
			stmt.setLong(1, to.getId());
			stmt.execute();
			stmt = con.prepareStatement(DELETE_PAGES_SQL);
			stmt.setLong(1, to.getId());
			stmt.execute();
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to clear pages for lang: " + to.getCode(), e);
		} finally {
			DBHelper.close(stmt);
		}
	}

}

