package com.negeso.module.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;

public class PageSearchProducer implements SearchEntryProducer {
	
	private static Logger logger = Logger.getLogger(PageSearchProducer.class);
	
	private static final String SQL_GET_TEXT_OF_PAGES = 
		" SELECT DISTINCT " +
		" 	page.id, " + 
		" 	page.filename, " + 
		" 	page.title, " + 
		" 	coalesce(page.container_id, 0)  AS container_id, " + 
		" 	coalesce(page.last_modified, '01.01.1970 00:00:00') AS lastmod, " + 
		" 	(SELECT code FROM language WHERE id = page.lang_id) AS code " + " FROM page " + 
		" WHERE (page.category='page' OR page.category='frontpage' OR page.category='popup') " + 
		" 	AND (page.expired_date > now() OR page.expired_date IS NULL ) " + 
		" 	AND page.site_id = ? ORDER BY page.filename ";
	
	private static final String SQL_ARTICLES_TEXT_FOR_PAGE = 
		" SELECT " +
		"	text " +
		" FROM article " +
		" LEFT JOIN page_component_params " + 
		"	ON page_component_params.name = 'id' AND page_component_params.value =  article.id::VARCHAR " +
		" LEFT JOIN page_component " +
		"	ON page_component.class_name = 'article-component' AND page_component_params.element_id = page_component.id " +
		" WHERE page_component.page_id = ? ORDER BY article.id";

	@Override
	public void indexEntries(SearchIndexCallBack callBack) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(SQL_GET_TEXT_OF_PAGES);
			stmt.setLong(1, Env.getSiteId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				SearchEntry entry = new SearchEntry(null, rs.getString("filename"), rs.getString("code"),
						rs.getString("title"), StringUtils.EMPTY, null, null, null);
				entry.setContainerId(rs.getLong("container_id"));
				entry.setDate(rs.getTimestamp("lastmod").getTime());
				entry.setContents(prepareContents(rs.getLong("id"), conn));
				callBack.index(entry);
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
		} finally {
			DBHelper.close(rs, stmt, conn);
		}
	}

	private String prepareContents(long pageId, Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder(StringUtils.EMPTY);
		try {
			stmt = conn.prepareStatement(SQL_ARTICLES_TEXT_FOR_PAGE);
			stmt.setLong(1, pageId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String text = rs.getString("text");
				if (StringUtils.isNotBlank(text)) {
					sb.append(text).append(' ');
				}
			}
		} finally {
			DBHelper.close(rs, stmt, null);
		}
		return sb.toString().trim();
	}

}
