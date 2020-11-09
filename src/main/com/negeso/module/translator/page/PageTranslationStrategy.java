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
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.page.PageComponentRecord;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.module.core.controller.SiteSettingsController;
import com.negeso.module.translator.exception.TranslationExeption;
import com.negeso.module.translator.service.AbstractTranslator;
import com.negeso.module.translator.service.ITranslateService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PageTranslationStrategy {
	
	private static final Logger logger = Logger.getLogger(PageTranslationStrategy.class);
	
	protected ITranslateService translateService;
	protected AbstractTranslator translator;
	
	public PageTranslationStrategy(ITranslateService translateService, AbstractTranslator translator) {
		this.translateService = translateService;
		this.translator = translator;
	}
	
	
	private static final String DELETE_STAT_COUNTER_SQL = "DELETE FROM stat_counter WHERE page_id IN (SELECT id FROM page WHERE lang_id = ?) ";
	private static final String DELETE_PAGE_SQL = "DELETE FROM page WHERE lang_id = ? ";
	private static final String SELECT_PAGES_SQL = "SELECT * FROM page WHERE lang_id = ? ORDER BY id";
	private static final String SELECT_PAGE_COMPONENT_PARAMS_SQL = "SELECT * FROM page_component_params WHERE element_id = ? ORDER BY id";
	private static final String SELECT_WCMS_ATTR_SET_SQL = "SELECT * FROM wcms_attribute_set WHERE wcms_attribute_set_id = ?";
	private static final String SELECT_WCMS_ATTRIBUTES_SQL = "SELECT * FROM wcms_attribute WHERE wcms_attribute_set_id = ?";
	private static final String SELECT_IMAGE_SET_SQL = "SELECT * FROM images_set WHERE image_set_id = ?";
	private static final String SELECT_IMAGES_SQL = "SELECT * FROM image WHERE image_set_id = ?";
	private static final String SELECT_ANIMATION_PROPS_SQL = "SELECT * FROM animation_properties WHERE image_set_id = ?";
		
	private static final String INSERT_PAGE_COMPONENT_RECORD_SQL = "INSERT INTO page_component (id, page_id, class_name) VALUES (?, ?, ?)";
	private static final String INSERT_PAGE_COMPONENT_PARAM_SQL = "INSERT INTO page_component_params (element_id, name, value) VALUES (?, ?, ?)";
	private static final String INSERT_WCMS_ATTR_SET_SQL = "INSERT INTO wcms_attribute_set (wcms_attribute_set_id, wcms_templ_id) VALUES (?, ?)";
	private static final String INSERT_WCMS_ATTRIBUTES_SQL = "INSERT INTO wcms_attribute (wcms_attr_type_id, name, image_set_id, wcms_attribute_set_id, str_value, class) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String INSERT_IMAGE_SET_SQL = "INSERT INTO images_set (image_set_id, name, required_width, required_height, max_width, max_height, min_height, min_width, number_of_images) VALUES (?,?,?,?,?,?,?,?,?)";
	private static final String INSERT_IMAGES_SQL = "INSERT INTO image (image_set_id, src, alt, max_width, max_height, min_width, min_height, required_height, required_width, _order, link, target) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String INSERT_ANIMATION_PROPS_SQL = "INSERT INTO animation_properties (image_set_id, delay, speed_of_animation, step) VALUES (?,?,?,?)";
	
	private static final String INSERT_PAGE_TRANSLATION_STATISTICS_SQL = "INSERT INTO tr_page_statistics (from_page_id, from_lang_id, to_lang_id, to_page_id) VALUES (?, ?, ?, ?)";
	
	private static final String SELECT_FRONT_PAGE_PROPERTY_SQL = "SELECT * FROM core_property WHERE name = ?";
	private static final String UPDATE_FRONT_PAGE_PROPERTY_SQL = "UPDATE core_property set value = ? WHERE name = ?";
	private static final String PAGE_NAME = "filename";
	
	private static final String FRONT_PAGE = "frontpage";
	
	protected Map<Long, Long> pageIdsMap = new HashMap<Long, Long>();
	private Map<Long, Long> throughArticleIdsMap = new HashMap<Long, Long>();
	protected Map<Long, Long> listIdsMap = null;
	protected Map<String, String> pages = new HashMap<String, String>();
	
	
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(DELETE_STAT_COUNTER_SQL);
			stmt.setLong(1, to.getId());
			stmt.execute();
			stmt = con.prepareStatement(DELETE_PAGE_SQL);
			stmt.setLong(1, to.getId());
			stmt.execute();
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption("Unable to clear pages for lang: " + to.getCode(), e);
		} finally {
			DBHelper.close(stmt);
		}
	}

	public void copyAndTranslate(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PageH page = new PageH();
		try {
			stmt = con.prepareStatement(SELECT_PAGES_SQL);
			stmt.setLong(1, from.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				page = PageH.load(rs);
				copyAndTranslatePage(con, from, to, page);
			}
		} catch (Throwable e) {
			logger.error(e);
			throw new TranslationExeption("Unable to copy and translate pages for destination lang: " + to.getCode() + " page: " + page.getFilename(), e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	public PageH copyAndTranslatePage(Connection con, Language from,
                                      Language to, PageH page) throws SQLException {
		translator.setProgressIndication(page.getFilename());
		PageH newPage = page.clone();
		String translatedName = null;
		if (!pages.isEmpty()) {
			translatedName = pages.get(page.getFilename());
		}
		if (translatedName == null) {
			translatedName = translateService.translatePageName(page.getFilename(), from.getCode(), to.getCode());
			translatedName = UniquePageChecker.getUniquePageName(translatedName, con, to.getCode());
		}
		
		newPage.setId(0L);
		newPage.setFilename(translatedName);
		
		String [] textArray = {page.getMetaTitle(), page.getTitle(), page.getMetaDescription(), page.getMetaKeywords()};
		textArray = translateService.translate(textArray, from.getCode(), to.getCode());		
		newPage.setMetaTitle(textArray[0]);
		newPage.setTitle(textArray[1]);
		newPage.setMetaDescription(textArray[2]);
		newPage.setMetaKeywords(textArray[3]);
		
		Long fromPageId = page.getId();
		Long attributeSetId = prepareAttributeSet(page.getAttributeSetId(), con);
		newPage.setAttributeSetId(attributeSetId);
		newPage.setLangId(to.getId());
		PageService.getInstance().insert(con, newPage);
		copyAndTranslateComponents(fromPageId, newPage.getId(), con, from, to);
		insertIntoPegeTranslationStatistics(fromPageId, newPage.getId(), con, from, to);
		pageIdsMap.put(fromPageId, newPage.getId());
		if(FRONT_PAGE.equals(page.getClass()) || FRONT_PAGE.equals(page.getCategory())) {
			setFrontPageProperty(con, newPage, to);
		}
		return newPage;
	}
	
	private void setFrontPageProperty(Connection con, PageH page, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_FRONT_PAGE_PROPERTY_SQL);
			stmt.setString(1, SiteSettingsController.FRONTPAGE_PARAM_NAME);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String searchValue = to.getCode() + "=index_" + to.getCode() + ".html;";
				String value = rs.getString("value");
				if (!value.contains(searchValue)) {
					stmt = con.prepareStatement(UPDATE_FRONT_PAGE_PROPERTY_SQL);
					stmt.setString(1, value + searchValue);
					stmt.setString(2, SiteSettingsController.FRONTPAGE_PARAM_NAME);
					stmt.execute();
				}
			}
		} catch (SQLException e) {
			logger.error("Unable to update front page propery for lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	private void insertIntoPegeTranslationStatistics(Long fromPageId, Long id,
                                                     Connection con, Language from, Language to) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(INSERT_PAGE_TRANSLATION_STATISTICS_SQL);
			stmt.setLong(1, fromPageId);
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

	private Long prepareAttributeSet(Long attributeSetId, Connection con) throws SQLException {
		if (attributeSetId != null) {
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.prepareStatement(SELECT_WCMS_ATTR_SET_SQL);
				stmt.setLong(1, attributeSetId);
				rs = stmt.executeQuery();
				if (rs.next()) {
					Long newAttributeSetId = DBHelper.getNextInsertId(con, "wcms_attribute_set_wcms_attribute_set_id_seq");
					stmt = con.prepareStatement(INSERT_WCMS_ATTR_SET_SQL);
					stmt.setLong(1, newAttributeSetId);
					stmt.setObject(2, rs.getObject("wcms_templ_id"));
					stmt.execute();
					copyAttributes(attributeSetId, newAttributeSetId, con);
					return newAttributeSetId;
				}
				
			} catch (SQLException e) {
				logger.error(e);
				throw e;
			} finally {
				DBHelper.close(rs, stmt, null);
			}
		}
		return null;
	}

	private void copyAttributes(Long fromAttributeSetId, Long toAttributeSetId, Connection con) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_WCMS_ATTRIBUTES_SQL);
			stmt.setLong(1, fromAttributeSetId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Long fromImageSetId = rs.getLong("image_set_id");
				Long toImageSetId = null;
				if (fromImageSetId != null) {
					toImageSetId = prepareImageSet(fromImageSetId, con);					
				}
				stmt = con.prepareStatement(INSERT_WCMS_ATTRIBUTES_SQL);
				stmt.setObject(1, rs.getObject("wcms_attr_type_id"));
				stmt.setString(2, rs.getString("name"));
				stmt.setLong(3, toImageSetId);
				stmt.setLong(4, toAttributeSetId);
				stmt.setString(5, rs.getString("str_value"));
				stmt.setString(6, rs.getString("class"));
				stmt.execute();
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	private Long prepareImageSet(Long fromImageSetId, Connection con) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_IMAGE_SET_SQL);
			stmt.setLong(1, fromImageSetId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Long newImageSetId = DBHelper.getNextInsertId(con, "images_set_image_set_id_seq");
				stmt = con.prepareStatement(INSERT_IMAGE_SET_SQL);
				stmt.setLong(1, newImageSetId);
				stmt.setString(2, rs.getString("name"));
				stmt.setObject(3, rs.getObject("required_width"));
				stmt.setObject(4, rs.getObject("required_height"));
				stmt.setObject(5, rs.getObject("max_width"));
				stmt.setObject(6, rs.getObject("max_height"));
				stmt.setObject(7, rs.getObject("min_height"));
				stmt.setObject(8, rs.getObject("min_width"));
				stmt.setObject(9, rs.getObject("number_of_images"));
				stmt.execute();
				copyImages(fromImageSetId, newImageSetId, con);
				copyAnimationProperties(fromImageSetId, newImageSetId, con);
				return newImageSetId;
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} finally {
			DBHelper.close(rs, stmt, null);
		}
		return null;
	}

	private void copyAnimationProperties(Long fromImageSetId,
			Long newImageSetId, Connection con) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_ANIMATION_PROPS_SQL);
			stmt.setLong(1, fromImageSetId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				stmt = con.prepareStatement(INSERT_ANIMATION_PROPS_SQL);
				stmt.setLong(1, newImageSetId);
				stmt.setObject(2, rs.getObject("delay"));
				stmt.setObject(3, rs.getObject("speed_of_animation"));
				stmt.setObject(4, rs.getObject("step"));
				stmt.execute();
			}
		} catch (SQLException e) {
			logger.error("Not crirical if table animation_properties does not exist", e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	private void copyImages(Long fromImageSetId, Long newImageSetId,
			Connection con) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_IMAGES_SQL);
			stmt.setLong(1, fromImageSetId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				stmt = con.prepareStatement(INSERT_IMAGES_SQL);
				stmt.setLong(1, newImageSetId);
				stmt.setString(2, rs.getString("src"));
				stmt.setString(3, rs.getString("alt"));
				stmt.setLong(4, rs.getLong("max_width"));
				stmt.setLong(5, rs.getLong("max_height"));
				stmt.setLong(6, rs.getLong("min_width"));
				stmt.setLong(7, rs.getLong("min_height"));
				stmt.setLong(8, rs.getLong("required_height"));
				stmt.setLong(9, rs.getLong("required_width"));
				stmt.setLong(10, rs.getLong("_order"));
				stmt.setString(11, rs.getString("link"));
				stmt.setString(12, rs.getString("target"));
				stmt.execute();
			}
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	private void copyAndTranslateComponents(Long fromPageId, Long toPageId, Connection con, Language from, Language to) throws SQLException {
		PreparedStatement stmt = null;
		try {
			PageComponentRecord[] records = PageComponentRecord.getPageComponents(con, fromPageId);
			for (PageComponentRecord record : records) {
				stmt = con.prepareStatement(INSERT_PAGE_COMPONENT_RECORD_SQL);
				Long newComponentId = DBHelper.getNextInsertId(con, "page_component_id_seq");
				stmt.setLong(1, newComponentId);
				stmt.setLong(2, toPageId);
				stmt.setString(3, record.getName());
				stmt.execute();
				copyAndTranslateComponentParams(record, newComponentId, con, from, to);
			}			
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} finally {
			DBHelper.close(stmt);
		}
	}
	
	private void copyAndTranslateComponentParams(PageComponentRecord fromPageComponentRecord, Long toPageComponentRecordId, Connection con, Language from, Language to) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			List<PageComponentParam> params = new ArrayList<PageComponentParam>(); 
			stmt = con.prepareStatement(SELECT_PAGE_COMPONENT_PARAMS_SQL);
			stmt.setLong(1, fromPageComponentRecord.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				params.add(new PageComponentParam(rs.getString("name"), rs.getString("value")));
			}
			if (!params.isEmpty() && params.get(0).value != null) {
				if ("article-component".equals(fromPageComponentRecord.getName())) {
					PageComponentParam param = params.get(0);
					try {
						Long articleId = translator.copyAndTranslateArticle(con, Long.valueOf(param.value), from, to, translateService);
						param.value = articleId.toString();						
					} catch (ObjectNotFoundException e) {
						logger.error(e);
					}
				} else if("article-component-through".equals(fromPageComponentRecord.getName())){
					PageComponentParam param = params.get(0);
					Long articleId = Long.valueOf(param.value);
					try {
						Long thoughArticleId = throughArticleIdsMap.get(articleId);
						if (thoughArticleId == null) {
							thoughArticleId = translator.copyAndTranslateArticle(con, articleId, from, to, translateService);
							throughArticleIdsMap.put(articleId, thoughArticleId);
						}
						param.value = thoughArticleId.toString();
					} catch (ObjectNotFoundException e) {
						logger.error(e);
					}
				}
			}
			
			for (PageComponentParam param : params) {
				if (("listId".equals(param.name) || "lists".equals(param.name)) && param.value != null) {
					String[] listIds = param.value.split(",");
					String copiedAndTranslatedListIds = "";
					for (String strListId : listIds) {
						if (!"".equals(copiedAndTranslatedListIds)) {
							copiedAndTranslatedListIds = copiedAndTranslatedListIds + ",";
						}
						Long listId = Long.valueOf(strListId.trim());
						Long copiedAndTranslatedListId = listIdsMap.get(listId);
						if (copiedAndTranslatedListId != null) {
							copiedAndTranslatedListIds = copiedAndTranslatedListIds.concat(copiedAndTranslatedListId.toString());
						}
					}
					param.value = copiedAndTranslatedListIds;
				}
				insertComponentParams(con, toPageComponentRecordId, param.name, param.value);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new TranslationExeption(e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}
	
	public Map<String, String> getPageTranslationMap(Connection con, Language from, Language to) {
		pages.clear();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_PAGES_SQL);
			stmt.setLong(1, from.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				String pageName = rs.getString(PAGE_NAME);
				String translatedPageName = translateService.translatePageName(pageName, from.getCode(), to.getCode());
				translatedPageName = UniquePageChecker.getUniquePageName(translatedPageName, pages, to.getCode());
				pages.put(pageName, translatedPageName);
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption(e);
		} finally {
			DBHelper.close(rs, stmt, null);
		}
		return pages;
	}
	
	
	
	private void insertComponentParams(Connection con, Long componentId, String name, String value) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(INSERT_PAGE_COMPONENT_PARAM_SQL);
			stmt.setLong(1, componentId);
			stmt.setString(2, name);
			stmt.setString(3, value);
			stmt.execute();
		} catch (SQLException e) {
			logger.error(e);
			throw new TranslationExeption(e);
		} finally {
			DBHelper.close(stmt);
		}
	}
	
	private class PageComponentParam {
		String name;
		String value;
		
		public PageComponentParam(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
	}

	public Map<Long, Long> getListIdsMap() {
		return listIdsMap;
	}

	public void setListIdsMap(Map<Long, Long> listIdsMap) {
		this.listIdsMap = listIdsMap;
	}

	public void setTranslateService(ITranslateService translateService) {
		this.translateService = translateService;
	}

	public Map<Long, Long> getPageIdsMap() {
		return pageIdsMap;
	}
	

	
	
}

