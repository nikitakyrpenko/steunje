/*
 * @(#)ArchivedListItem.java       @version	21.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.archive.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;

/**
 * @author Sergiy Oliynyk
 */
public class ArchivedListItem implements DbObject {
    
	private static final long serialVersionUID = 99190397025920235L;

	private static Logger logger = Logger.getLogger(ArchivedListItem.class);

    private static final String tableId = "news_archive";
    private static final int fieldCount = 26;

    public final static String selectFrom =
        " SELECT * from news_archive";

    private final static String findByIdSql =
        selectFrom + " WHERE id = ?";
    
    private final static String findByPerLangIdSql =
        selectFrom + " WHERE per_lang_id = ? AND lang_id = ?";

    private final static String findByListItemIdSql =
        selectFrom + " WHERE list_item_id = ? ";
        
    private final static String updateSql =
        "UPDATE news_archive " +
        "SET id = ?, lang_id = ?, list_id = ?, list_item_id = ?, " +
        "list_item_link =?, title = ?, link = ?, image_link = ?, " +
        "document_link = ?, view_date = ?, publish_date = ?, " +
        "expired_date = ?, created_by = ?, created_date = ?, " +
        "last_modified_by = ?, last_modified_date = ?, thumbnail_link = ?, " +
        "article_lang_id = ?, teaser_head = ?, teaser_text = ?, " +
        "article_head = ?, article_text = ?, container_id = ?, is_public = ?, " +
        "site_id = ?, per_lang_id = ? " +
        "WHERE id = ?";

    private final static String insertSql =
        " INSERT INTO news_archive" +
        " (id, lang_id, list_id, list_item_id, list_item_link, title," +
        " link, image_link, document_link, view_date, publish_date," +
        " expired_date, created_by, created_date," +
        " last_modified_by, last_modified_date, thumbnail_link," +
        " article_lang_id, teaser_head, teaser_text, article_head," +
        " article_text, container_id, is_public, site_id, per_lang_id) " +
        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?, ?)";

    private Long id;
    private Long langId;
    private Long listId;
    private Long listItemId;
    private Long listItemLink;
    private String title;
    private String link; 
    private String imageLink;
    private String documentLink; 
    private Timestamp viewDate; 
    private Timestamp publishDate;
    private Timestamp expiredDate;
    private Long createdBy;
    private Timestamp createdDate;
    private Long lastModifiedBy;
    private Timestamp lastModifiedDate;
    private String thumbnailLink;
    private Long articleLangId;
    private String teaserHead;
    private String teaserText;
    private String articleHead;
    private String articleText;
    private Long containerId;
    private boolean isPublic;
    private Long perLangId;

    /**
     * Constructors
     */
    public ArchivedListItem() {}

    public ArchivedListItem(
        Long id,
        Long langId,
        Long listId,
        Long listItemId,
        Long listItemLink,
        String title,
        String link,
        String imageLink,
        String documentLink, 
        Timestamp viewDate, 
        Timestamp publishDate,
        Timestamp expiredDate,
        Long createdBy,
        Timestamp createdDate,
        Long lastModifiedBy,
        Timestamp lastModifiedDate,
        String thumbnailLink,
        Long articleLangId,
        String teaserHead,
        String teaserText,
        String articleHead,
        String articleText,
        Long containerId,
        boolean isPublic,
        Long perLangId)
    {
        this.id = id;
        this.langId = langId;
        this.listId = listId;
        this.listItemId = listItemId;
        this.listItemLink = listItemLink;
        this.title = title;
        this.link = link;
        this.imageLink = imageLink;
        this.documentLink = documentLink; 
        this.viewDate = viewDate;
        this.publishDate = publishDate;
        this.expiredDate = expiredDate;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.thumbnailLink = thumbnailLink;
        this.articleLangId = articleLangId;
        this.teaserHead = teaserHead;
        this.teaserText = teaserText;
        this.articleHead = articleHead;
        this.articleText = articleText;
        this.containerId = containerId;
        this.isPublic = isPublic;
        this.perLangId = perLangId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            langId = DBHelper.makeLong(rs.getLong("lang_id"));
            listId = DBHelper.makeLong(rs.getLong("list_id"));
            listItemId = DBHelper.makeLong(rs.getLong("list_item_id"));
            listItemLink = DBHelper.makeLong(rs.getLong("list_item_link"));
            title = rs.getString("title");
            link = rs.getString("link");
            imageLink = rs.getString("image_link");
            documentLink = rs.getString("document_link"); 
            viewDate = rs.getTimestamp("view_date");
            publishDate = rs.getTimestamp("publish_date");
            expiredDate = rs.getTimestamp("expired_date");
            createdBy = DBHelper.makeLong(rs.getLong("created_by"));
            createdDate = rs.getTimestamp("created_date");
            lastModifiedBy = DBHelper.makeLong(rs.getLong("last_modified_by"));
            lastModifiedDate = rs.getTimestamp("last_modified_date");
            thumbnailLink = rs.getString("thumbnail_link");
            articleLangId = DBHelper.makeLong(rs.getLong("article_lang_id"));
            teaserHead = rs.getString("teaser_head");
            teaserText = rs.getString("teaser_text");
            articleHead = rs.getString("article_head");
            articleText = rs.getString("article_text");
            containerId = DBHelper.makeLong(rs.getLong("container_id"));
            isPublic = rs.getBoolean("is_public");
            perLangId = DBHelper.makeLong(rs.getLong("per_lang_id"));
            logger.debug("-");
            return id;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }
    
    public Long insert(Connection con) throws CriticalException {
        return DBHelper.insertDbObject(con, this);
    }

    public void update(Connection con) throws CriticalException {
        DBHelper.updateDbObject(con, this);
    }

    public void delete(Connection con) throws CriticalException {
        DBHelper.deleteObject(con, tableId, id, logger);
    }

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException
    {
        logger.debug("+");
        stmt.setObject(1, id);
        stmt.setObject(2, langId);
        stmt.setObject(3, listId);
        stmt.setObject(4, listItemId);
        stmt.setObject(5, listItemLink);
        stmt.setString(6, title);
        stmt.setString(7, link);
        stmt.setString(8, imageLink);
        stmt.setString(9, documentLink);
        stmt.setTimestamp(10, viewDate);
        stmt.setTimestamp(11, publishDate);
        stmt.setTimestamp(12, expiredDate);       
        stmt.setObject(13, createdBy);
        stmt.setTimestamp(14, createdDate);
        stmt.setObject(15, lastModifiedBy);
        stmt.setTimestamp(16, lastModifiedDate);
        stmt.setString(17, thumbnailLink);
        stmt.setObject(18, articleLangId);
        stmt.setString(19, teaserHead);
        stmt.setString(20, teaserText);
        stmt.setString(21, articleHead);
        stmt.setString(22, articleText);
        stmt.setObject(23, containerId);
        stmt.setBoolean(24, isPublic);
        stmt.setObject(25, Env.getSiteId());
        stmt.setObject(26, perLangId);
        logger.debug("-");
        return stmt;
    }

    private static ArchivedListItem find(Connection conn, String query, Long id)
       throws CriticalException
    {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setObject(1, id, Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            ArchivedListItem li = null;
            if (rs.next()) {
                li = new ArchivedListItem();
                li.load(rs);
            }
            logger.debug("-");
            return li;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }
    
    public static ArchivedListItem findByPerLangIdSql(Connection conn, Long perLangId, Long langId)
	    throws CriticalException
	 {
	     PreparedStatement stmt = null;
	     try {
	         stmt = conn.prepareStatement(findByPerLangIdSql);
	         stmt.setObject(1, perLangId, Types.BIGINT);
	         stmt.setObject(2, langId, Types.BIGINT);
	         ResultSet rs = stmt.executeQuery();
	         ArchivedListItem li = null;
	         if (rs.next()) {
	             li = new ArchivedListItem();
	             li.load(rs);
	         }
	         logger.debug("-");
	         return li;
	     }
	     catch (SQLException ex) {
	         logger.error("- Throwing new CriticalException");
	         throw new CriticalException(ex);
	     }
	     finally {
	         DBHelper.close(stmt);
	     }
	 }

    public static ArchivedListItem findById(Connection conn, Long id)
       throws CriticalException
    {
        return find(conn, findByIdSql, id);
    }

    public static ArchivedListItem findByListItemId(Connection conn, 
        Long listItemId) throws CriticalException
    {
        return find(conn, findByListItemIdSql + " and site_id = " + Env.getSiteId(), listItemId);
    }

    private static ArchivedListItem get(Connection conn, String query, Long id)
        throws CriticalException
    {
        ArchivedListItem li = find(conn, query, id);
        if (li == null)
            throw new CriticalException(
                "ArchivedListItem not found  by id=" + id);
        return li;
    }

    public static ArchivedListItem getById(Connection conn, Long id)
        throws CriticalException
    {
        return get(conn, findByIdSql, id);
    }

    public static ArchivedListItem getByListItemId(Connection conn, 
         Long listItemId) throws CriticalException
    {
        return find(conn, findByListItemIdSql + " and site_id = " + Env.getSiteId(), listItemId);
    }
 
    public Element getElement(Document doc) throws CriticalException {
        logger.debug("+");
        Element itemElement = Env.createDomElement(doc, "archivedListItem");
        itemElement.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
        itemElement.setAttribute("id", String.valueOf(id));
        itemElement.setAttribute("listId", String.valueOf(listId));
        itemElement.setAttribute("title", title);
        itemElement.setAttribute("link", link);
        itemElement.setAttribute("imageLink", imageLink);
        itemElement.setAttribute("documentLink", documentLink);
        itemElement.setAttribute("thumbnailLink", thumbnailLink);        
        itemElement.setAttribute("teaserHead", teaserHead);
        itemElement.setAttribute("teaserText", teaserText);
        itemElement.setAttribute("articleHead", articleHead);
        itemElement.setAttribute("articleText", articleText);
        setDateAttribute(itemElement, "viewDate", viewDate);
        setDateAttribute(itemElement, "publishDate", publishDate);
        setDateAttribute(itemElement, "expiredDate", expiredDate);
        setDateAttribute(itemElement, "createdDate", createdDate);
        setDateAttribute(itemElement, "lastModifiedDate", lastModifiedDate);
        Xbuilder.setAttr(itemElement, "perLangId", perLangId);
        try {        
            if (createdBy != null) {
                itemElement.setAttribute("createdBy", 
                    User.findById(createdBy).getName());
            }
            if (lastModifiedBy != null) {
                itemElement.setAttribute("lastModifiedBy",
                    User.findById(lastModifiedBy).getName());
            }
        }
        catch (ObjectNotFoundException ex) {
            logger.debug("User not found", ex);
        }
        logger.debug("-");
        return itemElement;
    }

    private void setDateAttribute(Element element, String name, Timestamp value)
    {
        if (value != null) {
            String dateStr = value.toString();
            element.setAttribute(name,
                dateStr.substring(0, dateStr.indexOf(' ')));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ArchivedListItem : ");
        sb.append(" id=");
        sb.append(id);
        sb.append(" lang_id=");
        sb.append(langId);
        sb.append(" listId=");
        sb.append(listId);
        sb.append(" listItemId=");
        sb.append(listItemId);
        sb.append(" listItemLink");
        sb.append(listItemLink);
        sb.append(" title=");
        sb.append(title);
        sb.append(" link=");
        sb.append(link);
        sb.append(" imageLink=");
        sb.append(imageLink);
        sb.append(" documentLink=");
        sb.append(documentLink);
        sb.append(" viewDate=");
        sb.append(viewDate);
        sb.append(" publishDate=");
        sb.append(publishDate);
        sb.append(" expiredDate=");
        sb.append(expiredDate);
        sb.append(" createdBy=");
        sb.append(createdBy);
        sb.append(" createdDate=");
        sb.append(createdDate);
        sb.append(" lastModifiedBy=");
        sb.append(lastModifiedBy);
        sb.append(" lastModifiedDate=");
        sb.append(lastModifiedDate);
        sb.append(" thumbnailLink=");
        sb.append(thumbnailLink);
        sb.append(" articleLangId=");
        sb.append(articleLangId);
        sb.append(" teaserHead=");
        sb.append(teaserHead);
        sb.append(" teaserText=");
        sb.append(teaserText);
        sb.append(" articleHead=");
        sb.append(articleHead);
        sb.append(" articleText=");
        sb.append(articleText);
        sb.append(" container_id=");
        sb.append(containerId);
        sb.append(" is_public=");
        sb.append(isPublic);
        return sb.toString();
    }

    public Long getLangId() {
        return langId;
    }
    
    public void setLangId(Long langId) {
        this.langId = langId;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Long getListItemId() {
        return listItemId;
    }
    
    public void setListItemId(Long listItemId) {
        this.listItemId = listItemId;
    }
    
    public Long getListItemLink() {
        return listItemLink;
    }
    
    public void setListItemLink(Long listItemLink) {
        this.listItemLink = listItemLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getImageLink() {
        return imageLink;
    }
    
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    
    public String getDocumentLink() { 
        return documentLink;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
    }

    public Timestamp getViewDate() { 
        return viewDate; 
    }
    
    public void setViewDate(Timestamp viewDate) {
        this.viewDate = viewDate;
    }
    
    public Timestamp getPublishDate() {
        return publishDate;
    }
    
    public void setPublishDate(Timestamp publishDate) {
        this.publishDate = publishDate;
    }
    
    public Timestamp getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    
    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }
    
    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(Timestamp lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public String getThumbnailLink() {
        return thumbnailLink;
    }
    
    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public Long getArticleLangId() {
        return articleLangId;
    }

    public void setArticleLangId(Long articleLangId) {
        this.articleLangId = articleLangId;
    }

    public String getTeaserHead() {
        return teaserHead;
    }
    
    public void setTeaserHead(String teaserHead) {
        this.teaserHead = teaserHead;
    }
    
    public String getTeaserText() {
        return teaserText;
    }
    
    public void setTeaserText(String teaserText) {
        this.teaserText = teaserText;
    }
    
    public String getArticleHead() {
        return articleHead;
    }
    
    public void setArticleHead(String articleHead) {
        this.articleHead = articleHead; 
    }

    public String getArticleText() {
        return articleText;
    }
    
    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }
    
    public Long getContainerId() {
        return containerId;
    }
    
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

	public Long getPerLangId() {
		return perLangId;
	}

	public void setPerLangId(Long perLangId) {
		this.perLangId = perLangId;
	}
}
