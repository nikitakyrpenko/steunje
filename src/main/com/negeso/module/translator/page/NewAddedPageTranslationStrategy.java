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
import com.negeso.framework.menu.Menu;
import com.negeso.framework.page.PageH;
import com.negeso.module.translator.exception.TranslationExeption;
import com.negeso.module.translator.service.AbstractTranslator;
import com.negeso.module.translator.service.ITranslateService;
import org.apache.log4j.Logger;

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
public class NewAddedPageTranslationStrategy extends PageTranslationStrategy{
	
	public NewAddedPageTranslationStrategy(ITranslateService translateService, AbstractTranslator translator) {
		super(translateService, translator);
	}

	private static final Logger logger = Logger.getLogger(NewAddedPageTranslationStrategy.class);
	
	private static final String SELECT_ZERO_LEVEL_MENU_SQL = "SELECT * FROM menu WHERE lang_id = ? AND level = 0";
	private static final String SELECT_PAGES_FOR_UPDATE_SQL = "SELECT * FROM page WHERE lang_id = ? AND id NOT IN (SELECT from_page_id AS id FROM tr_page_statistics WHERE from_lang_id = ? AND to_lang_id = ?)";
	/*private static OrderControlHelper orderControlHelper = new OrderControlHelper("menu_item", "menu_id");
	static {
		orderControlHelper.setOrderFieldId("order_");
	}*/
	
	@Override
	public void copyAndTranslate(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = con.prepareStatement(SELECT_PAGES_FOR_UPDATE_SQL);
			stmt.setLong(1, from.getId());
			stmt.setLong(2, from.getId());
			stmt.setLong(3, to.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				PageH page = PageH.load(rs);
				copyAndTranslateToSpecialPage(page, con, from, to);
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to translate new pages to lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}
	
	public PageH copyAndTranslateToSpecialPage(PageH page, Connection con, Language from, Language to) throws SQLException {
		return copyAndTranslatePage(con, from, to, page);
		//MenuItem menuItem = new MenuItem();
		/*menuItem.setMenuId(zeroLevelMenuId);
		menuItem.setLink(page.getFilename());
		menuItem.setOrder(orderControlHelper.getNextInsertOrder(con, zeroLevelMenuId).intValue());
		menuItem.setTitle(page.getTitle());
		menuItem.setLinkType("page");
		menuItem.insert();*/
		
	}
	
	@Override
	public void clean(Connection con, Language to) {}
	
	@Deprecated
	public Long getZeroLevelMenuId(Connection con, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_ZERO_LEVEL_MENU_SQL);
			stmt.setLong(1, to.getId());
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getLong("id");
			}
			Menu zeroMenu = new Menu();
			zeroMenu.setLangId(to.getId());
			zeroMenu.setLevel(0);
			zeroMenu.setSiteId(1);
			zeroMenu.insert(con);
			return zeroMenu.insert(con);
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to get Special page menu for lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}
	
}

