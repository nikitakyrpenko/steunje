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
import com.negeso.module.translator.exception.TranslationExeption;
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
public class CustomConstTranslator extends AbstractTranslator {
	
	private static final Logger logger = Logger.getLogger(CustomConstTranslator.class);
	
	private static final String SELECT_CORE_REFERENCE_SQL = 
			" SELECT core_reference.* FROM core_reference " +
			" LEFT JOIN core_reference_type ON core_reference_type.id = core_reference.reference_type " +
			" WHERE core_reference_type.name = 'languages' AND core_reference.code = ?";
	
	private static final String INSERT_CORE_REFERENCE_SQL = "INSERT INTO core_reference (id, reference_type, code, name) VALUES (?, (SELECT id FROM core_reference_type WHERE name = 'languages'), ?, ?)";
	private static final String INSERT_CUSTOM_TRANSLATIONS_SQL = "INSERT INTO dic_custom_translation (const_id, lang_id, translation) VALUES (?,?,?)";
	
	private static final String UPDATE_CUSTOM_TRANSLATIONS_SQL = "UPDATE dic_custom_translation set translation = ? where id = ? ";
	
	private static final String DELETE_CUSTOM_TRANSLATIONS_SQL = "DELETE FROM dic_custom_translation WHERE lang_id = (SELECT id FROM core_reference WHERE code = ? AND core_reference.reference_type = 1)";
	
	private static final String SELECT_CUSTOM_TRANSLATIONS_SQL = "SELECT dcc.key, dct.id, dct.translation, dcc.id as const_id, dct.lang_id FROM dic_custom_const dcc" +
																" LEFT JOIN dic_custom_translation dct ON dct.const_id = dcc.id and dct.lang_id = ?" +
																" ORDER BY dct.const_id, dct.lang_id";
	
	private static final String FROM_LANGUAGE = "en";
	private static final Long FROM_LANGUAGE_ID = 1L;
	
	private String[] currentCoreRecerencesLangs = {"en", "nl", "de", "fr", "es", "it"};
	
	private static final int BATCH_TRANSLATION_SIZE = 10;

	public CustomConstTranslator(ITranslateService translateService) {
		super(translateService);
	}

	@Override
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_CORE_REFERENCE_SQL);
			stmt.setString(1, to.getCode());
			rs = stmt.executeQuery();
			if (rs.next()) {
				stmt = con.prepareStatement(DELETE_CUSTOM_TRANSLATIONS_SQL);
				stmt.setString(1, to.getCode());
				stmt.execute();
			}
		} catch (SQLException e) {
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
		setProgressIndication(String.format("Custom constant %s", StringUtils.EMPTY));
		try {
			Long fromId = prepareCoreReferenceLangId(con, from);
			Long toId = prepareCoreReferenceLangId(con, to);
			stmt = con.prepareStatement(SELECT_CUSTOM_TRANSLATIONS_SQL);
			stmt.setLong(1, fromId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				CustomTranslation customTranslation = new CustomTranslation();
				customTranslation.load(rs);
				stmt = con.prepareStatement(INSERT_CUSTOM_TRANSLATIONS_SQL);
				stmt.setLong(1, customTranslation.constId);
				stmt.setLong(2, toId);
				setProgressIndication(String.format("Custom constant %s", rs.getString("key")));
				
				stmt.setString(3, translateService.translate(customTranslation.translation, from.getCode(), to.getCode()));
				stmt.execute();
			}
		} catch (SQLException e) {
			logger.error("Error: ", e);
			throw new TranslationExeption("Unable to translate custom constants to lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	private Long prepareCoreReferenceLangId(Connection con, Language to) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Long toLangId = null;
		try {
			stmt = con.prepareStatement(SELECT_CORE_REFERENCE_SQL);
			stmt.setString(1, to.getCode());
			rs = stmt.executeQuery();
			if (rs.next()) {
				toLangId = rs.getLong("id");
			} else {
				toLangId = DBHelper.getNextInsertId(con, "core_reference_id_seq");
				stmt = con.prepareStatement(INSERT_CORE_REFERENCE_SQL);
				stmt.setLong(1, toLangId);
				stmt.setString(2, to.getCode());
				stmt.setString(3, to.getLanguage());
				stmt.execute();
			}
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} finally {
			DBHelper.close(rs, stmt, null);
		}
		return toLangId;
	}
	
	private class CustomTranslation {
		Long id;
		Long constId;
		Long langId;
		String translation;
		
		void load(ResultSet rs) throws SQLException {
			this.id = rs.getLong("id");
			this.constId = rs.getLong("const_id");
			this.langId = rs.getLong("lang_id");
			this.translation = rs.getString("translation");
			if (translation == null) {
				translation = StringUtils.EMPTY;
			}
		}
		
		void update (Connection con) throws SQLException {
			PreparedStatement stmt = con.prepareStatement(UPDATE_CUSTOM_TRANSLATIONS_SQL);
			stmt.setString(1, translation);
			stmt.setLong(2, id);
			stmt.execute();
		}
	}

	@Override
	public void copyAndTranslate(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}
	
	private static final String SELECT_BY_CODE = "SELECT * FROM dic_custom_translation WHERE const_id = ? AND  lang_id = (SELECT id FROM core_reference WHERE code = ? AND core_reference.reference_type = 1)";
	private static final String INSERT_BY_CODE = "INSERT INTO dic_custom_translation (const_id, lang_id, translation) VALUES (?,(SELECT id FROM core_reference WHERE code = ? AND core_reference.reference_type = 1),?)";

	public void translateSimpleConst(String fromLangCode, String[] toLangCodes,
			Long constId) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			con = DBHelper.getConnection();
			stmt = con.prepareStatement(SELECT_BY_CODE);
			stmt.setLong(1, constId);
			stmt.setString(2, fromLangCode);
			rs = stmt.executeQuery();
			if (rs.next()) {
				CustomTranslation fromConst = new CustomTranslation();
				fromConst.load(rs);
				for (String toLangCode : toLangCodes) {
					stmt = con.prepareStatement(SELECT_BY_CODE);
					stmt.setLong(1, constId);
					stmt.setString(2, toLangCode);
					rs = stmt.executeQuery();
					if (rs.next()) {
						CustomTranslation toConst = new CustomTranslation();
						toConst.load(rs);
						toConst.translation = translateService.translate(fromConst.translation, fromLangCode, toLangCode);
						toConst.update(con);
					} else {
						stmt = con.prepareStatement(INSERT_BY_CODE);
						stmt.setLong(1, fromConst.constId);
						stmt.setString(2, toLangCode);
						stmt.setString(3, translateService.translate(fromConst.translation, fromLangCode, toLangCode));
						stmt.execute();
					}
				}
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			DBHelper.close(rs, stmt, con);
		}
		
	}

}

