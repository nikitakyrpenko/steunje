/*
 * @(#)$Id: PageComponentService.java,v 1.1, 2007-02-26 09:37:30Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.wcms.page.service;

import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.wcms.service.AbstractJdbcService;


/**
 * 
 * @TODO
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 2$
 *
 */
public class PageComponentService extends AbstractJdbcService {
	private static Logger logger = Logger.getLogger( PageComponentService.class );

	private static final String PAGE_COMPONENT_ID_SEQ = "page_component_id_seq";

	private static final String ARTICLE_COMPONENT_ID_PARAM = "id";

	private static final String ARTICLE_COMPONENT_NAME = "article-component";

	private static final String MENU_COMPONENT_NAME = "menu-component";

	public Long createComponent(Long pageId, String componentName) {
		Long id = null;
		try{
			id = DBHelper.getNextInsertId(this.getConnection(), PAGE_COMPONENT_ID_SEQ);
			PreparedStatement stmt = this.getConnection().prepareStatement(
				"INSERT INTO page_component (id, page_id, class_name) VALUES (?, ?, ?)"
			);
			stmt.setLong(1, id);
			stmt.setLong(2, pageId);
			stmt.setString(3, componentName);
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) { 
			logger.error("-error", e);
		}
		return id;
	}

	public void createParameter(Long componentId, String name, String value) {
		try{
			PreparedStatement stmt = this.getConnection().prepareStatement(
				"INSERT INTO page_component_params (element_id, name, value) VALUES (?, ?, ?)"
			);
			stmt.setLong(1, componentId);
			stmt.setString(2, name);
			stmt.setString(3, value);
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) { 
			logger.error("-error", e);
		}
	}

	public Long createSimpleComponent(Long pageId, String componentName, String paramName, String paramValue) {
		Long id = this.createComponent(pageId, componentName);
		this.createParameter(id, paramName, paramValue);
		return id;
	}

	public Long createArticleComponent(Long pageId, Long articleId) {
		if (articleId == null) {
			logger.error("articleId is NULL");
			throw new CriticalException("articleId is NULL");
		}
		if (pageId == null) {
			logger.error("pageId is NULL");
			throw new CriticalException("pageId is NULL");
		}
		return this.createSimpleComponent(
			pageId, ARTICLE_COMPONENT_NAME, ARTICLE_COMPONENT_ID_PARAM, articleId.toString()
		);
	}

	public Long createMenuComponent(Long pageId) {
		if (pageId == null) {
			logger.error("pageId is NULL");
			throw new CriticalException("pageId is NULL");
		}
		return this.createComponent(pageId, MENU_COMPONENT_NAME);
	}
}
