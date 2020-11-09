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
package com.negeso.framework.friendly_url;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.site_map.PageDescriptor;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FriendlyUrlService {
	
	private static Logger logger = Logger.getLogger(FriendlyUrlService.class);
	
	private static final String table = "friendly_url";
	
	private static final String findSql = " SELECT link FROM " + table + " WHERE entity_type_id = ? AND entity_id = ? AND lang_id = ?";
	private static final String findAlternativeSql = " SELECT COALESCE(alternative_link, link) as link FROM " + table + " WHERE entity_type_id = ? AND entity_id = ? AND lang_id = ?";
	private static final String findAllSql = " SELECT link, entity_type_id, entity_id FROM " + table + " WHERE (entity_type_id = 0 OR entity_type_id = 1) AND lang_id = ?";
	private static final String findAllAlternativeSql = " SELECT COALESCE(alternative_link, link) as link, entity_type_id, entity_id FROM " + table + " WHERE (entity_type_id = 0 OR entity_type_id = 1) AND lang_id = ?";
	private static final String deletePmCategoryDeadLinksSql = "DELETE FROM friendly_url WHERE friendly_url.entity_id NOT IN (SELECT id FROM pm_category) AND friendly_url.entity_type_id=0"; 
	private static final String deletePmProductDeadLinksSql = "DELETE FROM friendly_url WHERE friendly_url.entity_id NOT IN (SELECT id FROM pm_category) AND friendly_url.entity_type_id=0";

	private static final String selectSql = "SELECT entity_id, lang_id FROM " + table + " WHERE entity_type_id = ? AND (lower(link) = ? OR lower(alternative_link) = ?)";
	
	public static void updateHierarchically(Connection conn, Long entityId, String oldUrl, String newUrl, Long langId, UrlEntityType type, UrlEntityType type2) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(" UPDATE " + table + " SET link = replace(link, ?, ?),  alternative_link = replace(alternative_link, ?, ?)" +
										 " WHERE entity_type_id IN (?,?)  AND lang_id = ? AND (entity_id = ? OR link like ? )");
			stmt.setString(1, oldUrl);
			stmt.setString(2, newUrl);
			stmt.setString(3, oldUrl);
			stmt.setString(4, newUrl);
			stmt.setInt(5, type.ordinal());
			stmt.setInt(6, type2.ordinal());
			stmt.setLong(7, langId);
			stmt.setString(8, String.valueOf(entityId));
			stmt.setString(9, oldUrl + "%");
			stmt.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(stmt);
		}
	}
	
	public static void update(Connection conn, Long entityId, String link, String altLink, Long langId, UrlEntityType type) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(" UPDATE " + table + " SET link = ?, alternative_link = ? " +
										 " WHERE entity_type_id = ? AND entity_id = ? AND lang_id = ?");
			stmt.setString(1, link);
			stmt.setString(2, altLink);
			stmt.setInt(3, type.ordinal());
			stmt.setString(4, String.valueOf( entityId));
			stmt.setLong(5, langId);
			stmt.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(stmt);
		}
	}
	
	public static void delete(Connection conn, Long entityId, Long langId, UrlEntityType type) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(" DELETE FROM " + table + " WHERE entity_type_id = ? AND entity_id = ? AND lang_id = ?");
			stmt.setInt(1, type.ordinal());
			stmt.setString(2, String.valueOf(entityId) );
			stmt.setLong(3, langId);
			stmt.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(stmt);
		}
	}
	
	public static void deleteDeadFriendlyUrls(){
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(deletePmCategoryDeadLinksSql);
			stmt.execute();			
			stmt = conn.prepareStatement(deletePmProductDeadLinksSql);
			stmt.execute();			
			DBHelper.close(stmt);
		} catch (SQLException e) {
			logger.error("Unacpected error occured: ", e);
		} finally {
			DBHelper.close(conn);
		}
	}
	
	public static void insert(Connection conn, Long entityId, String link, String altLink, Long langId, UrlEntityType type) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO " + table + 
											" (entity_id, lang_id, entity_type_id, link, alternative_link) VALUES (?, ?, ?, ?, ?)");
			stmt.setString(1, String.valueOf(entityId));
			stmt.setLong(2, langId);
			stmt.setInt(3, type.ordinal());
			stmt.setString(4, link);
			stmt.setString(5, altLink);
			stmt.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(stmt);
		}
	}
	
	public static String find(Connection conn, Long entityId, int langId, UrlEntityType type) throws SQLException {

		return find(conn, String.valueOf(entityId), langId, type);
	}

	public static String find(Connection conn, String entityId, int langId, UrlEntityType type) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(Env.isUseLangCodeAsUrlStart() ? findAlternativeSql : findSql);
			stmt.setInt(1, type.ordinal());
			stmt.setString(2, entityId);
			stmt.setInt(3, langId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("link");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(rs);
			DBHelper.close(stmt);
		}
		return null;
	}

	public static String find(Connection conn, Long entityId, int langId, UrlEntityType type, String root) throws SQLException {
		String url = find(conn, entityId, langId, type);
		return url != null ? 
				root + "/" + url : 
					null;
	}
		
	public static List<PageDescriptor> getFriendlyUrlPages(Connection conn, int langId, String root, Long pageId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<PageDescriptor> content = new ArrayList<PageDescriptor>();
		try {
			stmt = conn.prepareStatement(Env.isUseLangCodeAsUrlStart() ? findAllAlternativeSql : findAllSql);
			stmt.setInt(1, langId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				if(rs.getString("link") != null){					
					PageDescriptor pageDescriptor = new PageDescriptor();
					pageDescriptor.setFriendlyHref(root + "/" + rs.getString("link") + ".html");
					pageDescriptor.setType((rs.getLong("entity_type_id") == 0 ) ? UrlEntityType.PRODUCT_CATEGORY : UrlEntityType.PRODUCT);
					pageDescriptor.setId(rs.getLong("entity_id"));
					pageDescriptor.setLeaf(false);
					pageDescriptor.setPageId(pageId);
					content.add(pageDescriptor);
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(rs);
			DBHelper.close(stmt);
		}
		return content;
	}
	
	public static void setUrl (Connection conn, Long entityId, Language lang, String link, UrlEntityType type) throws SQLException {
		String uniquePart = getUniqueUrl(link, entityId, lang, type, 0, lang.getId());
		String oldLink = find(conn, entityId, lang.getId().intValue(), type);
		if (oldLink == null) {
			insert(conn, entityId, buildUniqueLink(link, uniquePart, lang.getCode()), buildUniqueLink(link, uniquePart, null), lang.getId(), type);
		} else if (!oldLink.equals(link)) {
			update(conn, entityId, buildUniqueLink(link, uniquePart, lang.getCode()), buildUniqueLink(link, uniquePart, null), lang.getId(), type);
		}
	}
	
	public static void setUrl (Connection conn, Long entityId, Long langId, String link, UrlEntityType type, UrlEntityType type2) throws SQLException {
		String uniquePart = getUniqueUrl(link, entityId, null, type, 0, langId);
		link = buildUniqueLink(link, uniquePart, null);
		String oldLink = find(conn, entityId, langId.intValue(), type);
		if (oldLink == null) {
			insert(conn, entityId, link, null, langId, type);
		} else {
			updateHierarchically(conn, entityId, oldLink, link, langId, type, type2);
		}
	}

	@Deprecated
	public static Long[] findByUrl(String link, UrlEntityType type) throws ObjectNotFoundException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(selectSql);
			stmt.setInt(1, type.ordinal());
			stmt.setString(2, link);
			stmt.setString(3, link);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				logger.error("- throwing 'ObjectNotFoundException'");
	            throw new ObjectNotFoundException(
	            		"Entity " + type + " cannot be found by link: " + link);
			}
			return new Long[] {rs.getLong("entity_id"), rs.getLong("lang_id")};
		} catch (SQLException e) {
			logger.error("Error - link: " + link, e);
		} finally {
			DBHelper.close(rs, stmt, conn);
		}
		return null;
	}

	public static String[] findByLink(String link, UrlEntityType type) throws ObjectNotFoundException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(selectSql);
			stmt.setInt(1, type.ordinal());
			stmt.setString(2, link);
			stmt.setString(3, link);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				logger.error("- throwing 'ObjectNotFoundException'");
				throw new ObjectNotFoundException(
						"Entity " + type + " cannot be found by link: " + link);
			}
			return new String[] {rs.getString("entity_id"), String.valueOf(rs.getLong("lang_id")) };
		} catch (SQLException e) {
			logger.error("Error - link: " + link, e);
		} finally {
			DBHelper.close(rs, stmt, conn);
		}
		return null;
	}
	
	public static boolean isUniqueUrl(String link, Long entityId, Long langId, UrlEntityType type) {
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT link FROM " + table +
					" WHERE entity_type_id = ? AND lang_id = ? AND link = ?");
			stmt.setInt(1, type.ordinal());
			stmt.setLong(2, langId);
			stmt.setString(3, link);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return false;
			}
			DBHelper.close(rs);
			DBHelper.close(stmt);
		} catch (SQLException e) {
			logger.error("Unacpected error occured: ", e);
		} finally {
			DBHelper.close(conn);
		}
		return true;
	}
	
	private static String getUniqueUrl(String link, Long entityId, Language lang, UrlEntityType type, int inc, Long langId) {
		String uniquePart = inc > 0 ? inc + "" : "";
		boolean isUnique = isUniqueUrl(buildUniqueLink(link, uniquePart, lang != null ? lang.getCode() : null), entityId, langId, type);
		if (!isUnique) {
			return getUniqueUrl(link, entityId, lang, type, ++ inc, langId);
		}
		return uniquePart;
	}
	
	private static String buildUniqueLink (String link, String uniquePart, String langCode) {
		boolean isLastCharSlesh = false;
		if (link.lastIndexOf("/") == link.length() - 1) {
			link = link.substring(0, link.length() - 1);
			isLastCharSlesh = true;
		} 
		StringBuffer uniqueLink = new StringBuffer (1024).append(link);
		if (StringUtils.hasText(uniquePart)) {
			uniqueLink.append("_").append(uniquePart);
		}
		if (StringUtils.hasText(langCode)) {
			uniqueLink.append("_").append(langCode);
		}
		if (isLastCharSlesh) {
			uniqueLink.append("/");
		}
		return uniqueLink.toString();
	}
	
	public static String getModuleFriendlyUrlRoot (Long langId, String moduleName, String constKey) {
		try {
			String code = Language.findById(langId).getCode();
			return (Env.isUseLangCodeAsUrlStart() ? code + "/" : "") +  
				DatabaseResourceBundle.getTranslation(moduleName, code, constKey);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
}