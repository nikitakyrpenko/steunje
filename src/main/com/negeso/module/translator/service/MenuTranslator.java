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
import com.negeso.framework.menu.bo.Menu;
import com.negeso.module.translator.exception.TranslationExeption;
import com.negeso.module.translator.service.replace.PagesReplacer;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

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
public class MenuTranslator extends AbstractTranslator{

	private static final Logger logger = Logger.getLogger(ListTranslator.class);
	
	public MenuTranslator(ITranslateService translateService) {
		super(translateService);
	}

	private static final String SELECT_MENU_SQL = "SELECT *, COALESCE(parent_id, 0) as sorter FROM menu WHERE lang_id = ? order by sorter";
	
	private static final String INSERT_MENU_SQL = "INSERT INTO menu (id, parent_id, page_id, lang_id, title, link, order_number, publish_date, expired_date, keep_menu, site_id) " +
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String DELETE_MENU_SQL = "DELETE FROM menu WHERE lang_id = ?";
	
	private Map<Long, Long> pageIdsMap = new HashMap<Long, Long>();
	private PagesReplacer pagesReplacer;

	@Override
	public void copyAndTranslate(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Map<Long, Long> parentMenuIdsMap = new HashMap<Long, Long>();
		try {
			stmt = con.prepareStatement(SELECT_MENU_SQL);
			stmt.setLong(1, from.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				Menu menu = load(rs);
				Long fromMenuId = menu.getId();
				menu.setParentId(parentMenuIdsMap.get(menu.getParentId()));
				menu.setPageId(pageIdsMap.get(menu.getPageId()));
				menu.setLangId(to.getId());
				menu.setTitle(translateService.translate(menu.getTitle(), from.getCode(), to.getCode()));
				if (menu.getLink() != null && pagesReplacer != null) {
					menu.setLink(pagesReplacer.replaceBefore(menu.getLink()));
				}
				insert(con, menu);
				parentMenuIdsMap.put(fromMenuId, menu.getId());
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to create menu for destination lang: " + to.getCode(), e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	@Override
	public void copyAndTranslate(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}


	
	

	@Override
	public void clean(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}
	
	private Menu load(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getLong("id"));
        menu.setParentId(rs.getObject("parent_id") == null ? null : rs.getLong("parent_id"));
        menu.setPageId(rs.getObject("page_id") == null ? null : rs.getLong("page_id"));
        menu.setLangId(rs.getLong("lang_id"));
        menu.setTitle(rs.getString("title"));
        menu.setLink(rs.getString("link"));
        menu.setOrder(rs.getLong("order_number"));
        menu.setPublishDate(rs.getTimestamp("publish_date"));
        menu.setExpiredDate(rs.getTimestamp("expired_date"));
        menu.setKeepMenu(rs.getBoolean("keep_menu"));
        menu.setSiteId(rs.getLong("site_id"));
        return menu;
    }
	
	private Long insert(Connection con, Menu menu) throws SQLException {
		PreparedStatement stmt = null;
		try {			
			Long menuId = DBHelper.getNextInsertId(con, "menu_id_seq");
			stmt = con.prepareStatement(INSERT_MENU_SQL);
			stmt.setLong(1, menuId);
			stmt.setObject(2, menu.getParentId());
			stmt.setObject(3, menu.getPageId());
			stmt.setLong(4, menu.getLangId());
			stmt.setString(5, menu.getTitle());
			stmt.setString(6, menu.getLink());
			stmt.setObject(7, menu.getOrder());
			stmt.setTimestamp(8, menu.getPublishDate());
			stmt.setTimestamp(9, menu.getExpiredDate());
			stmt.setObject(10, menu.getKeepMenu());
			stmt.setLong(11, menu.getSiteId());
			stmt.execute();
			menu.setId(menuId);
			return menuId;
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} finally {
			DBHelper.close(stmt);
		}
	}

	public void setPageIdsMap(Map<Long, Long> pageIdsMap) {
		this.pageIdsMap = pageIdsMap;
	}

	@Override
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(DELETE_MENU_SQL);
			stmt.setLong(1, to.getId());
			stmt.execute(); 
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to clear menu for lang: " + to.getCode());
		} finally {
			DBHelper.close(stmt);
		}
		
	}

	public void setPagesReplacer(PagesReplacer pagesReplacer) {
		this.pagesReplacer = pagesReplacer;
	}
}

