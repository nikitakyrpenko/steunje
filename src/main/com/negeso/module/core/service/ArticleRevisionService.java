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
package com.negeso.module.core.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.module.core.dao.ArticleRevisionDao;
import com.negeso.module.core.domain.ArticleRevision;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ArticleRevisionService {
	
	private static Logger logger = Logger.getLogger(ArticleRevisionService.class);
	
	private static final String SQL_SELECT_ARTICLE_REVISIONS = 
		" SELECT * FROM (" +
		"		SELECT article_revision.*, 'page' as type, page.title || ' (' || page.filename || ')' as title" +
		"		FROM article_revision" +
		"		LEFT JOIN page_component_params ON article_revision.article_id::\"varchar\" = page_component_params.value" +
		"		LEFT JOIN page_component ON page_component_params.element_id = page_component.id" +
		"		LEFT JOIN page ON page_component.page_id = page.id" +
		"		WHERE page_component.class_name = 'article-component'" +
		"	UNION" +
		"		SELECT article_revision.*, 'news' as type, list_item.title as title" +
		"		FROM article_revision" +
		"		INNER JOIN list_item ON article_revision.article_id = list_item.teaser_id" +
		"	UNION" +
		"		SELECT article_revision.*, 'news' as type, list_item.title as title" +
		"		FROM article_revision" +
		"		INNER JOIN list_item ON article_revision.article_id = list_item.article_id " +
		" ) AS typed_revisions" +
		" ORDER by typed_revisions.%s %s";
	
	private ArticleRevisionDao articleRevisionDao;
	
	public ArticleRevision find(Long id) {
		return articleRevisionDao.read(id);
	}
	
	public void create(ArticleRevision articleRevision) {
		articleRevisionDao.create(articleRevision);
		articleRevisionDao.flush();
	}
	
	public void delete(ArticleRevision articleRevision) {
		articleRevisionDao.delete(articleRevision);
		articleRevisionDao.flush();
	}
	
	public List<ArticleRevision> listByArticleId(Long articleId) {
		return articleRevisionDao.listByArticleId(articleId);
	}

	public ArticleRevisionDao getArticleRevisionDao() {
		return articleRevisionDao;
	}

	public void setArticleRevisionDao(ArticleRevisionDao articleRevisionDao) {
		this.articleRevisionDao = articleRevisionDao;
	}
	
	
	public List<ArticleRevision> listRevisions(String sortBy, Boolean sortDirection) {
		List<ArticleRevision> revisions = new LinkedList<ArticleRevision>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBHelper.getConnection();
			stmt = con.prepareStatement(String.format(SQL_SELECT_ARTICLE_REVISIONS, sortBy, sortDirection ? "ASC" : "DESC"));
			rs = stmt.executeQuery();
			while (rs.next()) {
				ArticleRevision articleRevision = new ArticleRevision();
				articleRevision.setId(rs.getLong("id"));
				articleRevision.setDate(rs.getTimestamp("date"));
				articleRevision.setAuthor(rs.getString("author"));
				articleRevision.setText(rs.getString("text"));
				articleRevision.setType(rs.getString("type"));
				articleRevision.setTitle(rs.getString("title"));
				revisions.add(articleRevision);
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			DBHelper.close(rs, stmt, con);
		}
		return revisions;
	}
	
}

