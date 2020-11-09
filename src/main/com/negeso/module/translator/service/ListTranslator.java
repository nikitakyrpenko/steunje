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

import com.negeso.framework.domain.*;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.module.translator.exception.TranslationExeption;
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
public class ListTranslator extends AbstractTranslator {
	
	private static final Logger logger = Logger.getLogger(ListTranslator.class);
	
	private Long moduleId;

	private static final String SELECT_LISTS_SQL_DESC = "SELECT * FROM list WHERE lang_id = ? %s ORDER BY parent_list_item_id DESC";
	private static final String SELECT_LISTS_ASC = "SELECT * FROM list WHERE lang_id = ? %s ORDER BY id ASC";
	private static final String SELECT_LIST_ITEMS = "SELECT * FROM list_item WHERE list_id=? ORDER BY order_number";
	
	private static final String INSERT_LIST_ITEM_TRANSLATION_STATISTICS_SQL = "INSERT INTO tr_list_item_statistics (from_page_id, from_lang_id, to_lang_id, to_page_id) VALUES (?, ?, ?, ?)";
	
	private static final String DELETE_LIST_SQL = "DELETE FROM list WHERE id = ?";
	
	private Map<Long, Long> listIdsMap = new HashMap<Long, Long>();
	
	public ListTranslator(ITranslateService translateService, Long moduleId) {
		this(translateService);
		this.moduleId = moduleId;
	}
	
	public ListTranslator(ITranslateService translateService) {
		super(translateService);
	} 
	
	@Override
	public void copyAndTranslate(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Map<Long, Long> parentListItemIdsMap = new HashMap<Long, Long>();
		setProgressIndication("News");
		try {
			stmt = con.prepareStatement(String.format(SELECT_LISTS_ASC, getModuleCondition()));
			stmt.setLong(1, from.getId());
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				List list = new List();
				list.load(rs);
				list.setLangId(to.getId());
				list.setInstanceName(translateService.translate(list.getInstanceName(), from.getCode(), to.getCode()));
				list.setName(translateService.translate(list.getName(), from.getCode(), to.getCode()));
				list.setParentListItemId(parentListItemIdsMap.get(list.getParentListItemId()));
				Long fromListId = list.getId();
				list.insert(con);
				listIdsMap.put(fromListId, list.getId());
				
				stmt = con.prepareStatement(SELECT_LIST_ITEMS);
				stmt.setLong(1, fromListId);
				ResultSet rs2 = stmt.executeQuery();
				while (rs2.next()) {
					ListItem listItem = new ListItem();
					listItem.load(rs2);
					Long originalListItemId = listItem.getId();
					listItem.setListId(list.getId());
					
					copyAndTranslateListItem(con, from, to, listItem);
					
					parentListItemIdsMap.put(originalListItemId, listItem.getId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TranslationExeption("Unable to create list structure for destination lang: " + to.getCode() + getModuleCondition(), e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	public void copyAndTranslateListItem(Connection con, Language from,
                                         Language to, ListItem listItem) throws ObjectNotFoundException, CriticalException, SQLException {
		listItem.setTitle(translateService.translate(listItem.getTitle(), from.getCode(), to.getCode()));
		listItem.setTeaserId(copyAndTranslateArticle(con, listItem.getTeaserId(), from, to, translateService));
		listItem.setArticleId(copyAndTranslateArticle(con, listItem.getArticleId(), from, to, translateService));
		insertIntoPegeTranslationStatistics(listItem.getId(), listItem.insert(con), con, from, to);
	}
	
	public void copyAndTranslateListItem(Connection con, Language from,
                                         Language to, ListItem sourceListItem, ListItem destListItem) throws ObjectNotFoundException, CriticalException, SQLException {
		destListItem.setTitle(translateService.translate(sourceListItem.getTitle(), from.getCode(), to.getCode()));
		Long teaserId = destListItem.getTeaserId();
		Long articleId = destListItem.getArticleId();
		destListItem.setTeaserId(copyAndTranslateArticle(con, sourceListItem.getTeaserId(), from, to, translateService));
		destListItem.setArticleId(copyAndTranslateArticle(con, sourceListItem.getArticleId(), from, to, translateService));
		destListItem.update(con);
		if (teaserId != null) {
			Article teaserArticle = Article.findById(con, teaserId);
			teaserArticle.delete(con);
		}
		if (articleId != null) {
			Article article = Article.findById(con, articleId);
			article.delete(con);
		}
	}
	
	private void insertIntoPegeTranslationStatistics(Long fromItemId, Long id,
                                                     Connection con, Language from, Language to) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(INSERT_LIST_ITEM_TRANSLATION_STATISTICS_SQL);
			stmt.setLong(1, fromItemId);
			stmt.setLong(2, from.getId());
			stmt.setLong(3, to.getId());
			stmt.setLong(4, id);
			stmt.execute();			
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} finally {
			DBHelper.close(stmt);
		}
	}

	@Override
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(String.format(SELECT_LISTS_SQL_DESC, getModuleCondition()));
			stmt.setLong(1, to.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				Long listId = rs.getLong("id");
				stmt = con.prepareStatement(DELETE_LIST_SQL);
				stmt.setLong(1, listId);
				stmt.execute();
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to clear list structure for destination lang: " + to.getCode() + getModuleCondition(), e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
		
	}
	
	private String getModuleCondition(){
		return moduleId == null ? "" : " AND module_id = " + moduleId;
	}

	@Override
	public void clean(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyAndTranslate(SessionFactory factory) {
		
	}

	public Map<Long, Long> getListIdsMap() {
		return listIdsMap;
	}
}

