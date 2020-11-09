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
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ProductModuleTranslator extends AbstractTranslator {
	
	private static final Logger logger = Logger.getLogger(ProductModuleTranslator.class);
	
	private static final String SELECT_ATTRIBUTE_TYPE_SQL = "SELECT * FROM pm_attribute_type";
	private static final String SELECT_PRODUCT_ATTRIBUTES_SQL = "SELECT * FROM pm_attribute_value WHERE lang_id = ?";
	private static final String INSERT_PRODUCT_ATTRIBUTES_SQL = "INSERT INTO pm_attribute_value (product_id, type_id, str_value, int_value, advstr_value, lang_id, money_value, real_value) VALUES (?,?,?,?,?,?,?,?)";
	
	private static final String SELECT_STRING_RESOURCES_SQL = "SELECT * FROM string_resource WHERE lang_id = ?";
	private static final String INSERT_STRING_RESOURCES_SQL = "INSERT INTO string_resource (id, lang_id, text) VALUES (?, ?, ?)";
	private static final String DELETE_STRING_RESOURCES_SQL = "DELETE FROM string_resource WHERE lang_id = ?";
	
	private static final String DELETE_PRODUCT_ATTRIBUTES_SQL = "DELETE FROM pm_attribute_value WHERE lang_id = ? AND type_id IN (SELECT id FROM pm_attribute_type WHERE is_i18n = true)";
	
	Map<Long, AttributeType> attributeTypes = null;
	
	public ProductModuleTranslator(ITranslateService translateService) {
		super(translateService);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		try {
			if (attributeTypes == null) {
				prepareAttributeTypes(con);
			}
			stmt = con.prepareStatement(DELETE_PRODUCT_ATTRIBUTES_SQL);
			stmt.setLong(1, to.getId());		
			stmt.execute();
			stmt = con.prepareStatement(DELETE_STRING_RESOURCES_SQL);
			stmt.setLong(1, to.getId());		
			stmt.execute();
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to translate products", e);
		} finally {
			DBHelper.close(stmt);
		}
		
	}

	@Override
	public void clean(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyAndTranslate(Connection con, Language from, Language to) {
		setProgressIndication("Product module");
		translateProducts(con, from, to);
		translateStringResources(con, from, to);
	}
	
	private void translateStringResources(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_STRING_RESOURCES_SQL);
			stmt.setLong(1, from.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				stmt = con.prepareStatement(INSERT_STRING_RESOURCES_SQL);
				stmt.setString(1, rs.getString("id"));
				stmt.setLong(2, to.getId());
				stmt.setString(3, translateService.translate(rs.getString("text"), from.getCode(), to.getCode()));
				stmt.execute();
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to translate string resources to lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}		
	}

	private void translateProducts(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (attributeTypes == null) {
				prepareAttributeTypes(con);
			}
			stmt = con.prepareStatement(SELECT_PRODUCT_ATTRIBUTES_SQL);
			stmt.setLong(1, from.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				Long attributeTypeId = rs.getLong("type_id");
				AttributeType at = attributeTypes.get(attributeTypeId);
				if (at.isI18n) {
					AttributeValue attributeValue = new AttributeValue();
					attributeValue.load(rs);
					if ("string".equals(at.type_name) || "text".equals(at.type_name)) {
						attributeValue.str_value = translateService.translate(attributeValue.str_value, from.getCode(), to.getCode());
					} else if("article".equals(at.type_name)) {
						attributeValue.int_value = copyAndTranslateArticle(con, attributeValue.int_value, from, to, translateService);
					}
					attributeValue.lang_id = to.getId();
					stmt = con.prepareStatement(INSERT_PRODUCT_ATTRIBUTES_SQL);
					stmt.setLong(1, attributeValue.product_id);
					stmt.setLong(2, attributeValue.type_id);
					stmt.setString(3, attributeValue.str_value);
					stmt.setObject(4, attributeValue.int_value);
					stmt.setString(5, attributeValue.advstr_value);
					stmt.setLong(6, attributeValue.lang_id);
					stmt.setBigDecimal(7, attributeValue.money_value);
					stmt.setBigDecimal(8, attributeValue.real_value);
					stmt.execute();
				}
			}
			
		} catch (Exception e) {
			logger.error(e);
			throw new TranslationExeption("Unable to translate products", e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}
	
		void prepareAttributeTypes(Connection con) throws SQLException {
			PreparedStatement stmt = null;
			ResultSet rs = null;
			attributeTypes = new HashMap<Long, AttributeType>();
			try {
				stmt = con.prepareStatement(SELECT_ATTRIBUTE_TYPE_SQL);
				rs = stmt.executeQuery();
				while (rs.next()) {
					AttributeType at = new AttributeType();
					at.load(rs);
					attributeTypes.put(at.id, at);
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				DBHelper.close(rs, stmt, null);
			}
	} 

	@Override
	public void copyAndTranslate(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}
	
	private class AttributeType{
		Long id;
		String type_name;
		Boolean isI18n = false;
		
		void load(ResultSet rs) throws SQLException {
			this.id = rs.getLong("id");
			this.type_name = rs.getString("type_name");
			this.isI18n = rs.getBoolean("is_i18n");
		}
	}
	
	private class AttributeValue {
		Long id;
		Long product_id;
		Long type_id;
		String str_value;
		Long int_value;
		String advstr_value;
		Long lang_id;
		BigDecimal money_value;
		BigDecimal real_value;
		
		void load(ResultSet rs) throws SQLException {
			this.id = rs.getLong("id");
			this.product_id = (Long)rs.getObject("product_id");
			this.type_id = (Long)rs.getObject("type_id");
			this.str_value = rs.getString("str_value");
			this.int_value = (Long)rs.getObject("int_value");
			this.advstr_value = rs.getString("advstr_value");
			this.lang_id = (Long)rs.getObject("lang_id");
			this.money_value = rs.getBigDecimal("money_value");
			this.real_value = rs.getBigDecimal("real_value");
		}
		
	}

}

