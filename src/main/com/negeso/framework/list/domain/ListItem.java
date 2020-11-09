/*
 * @(#)ListItem.java       @version	24.10.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.domain;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.framework.page.PageService;
import com.negeso.module.social.bo.SocialNetwork;

/**
 * List item domain entity incapsulation.
 *
 * @author  Sergiy Oliynyk
 */
public class ListItem implements DbObject {
    
	private static final long serialVersionUID = 917915077310576650L;

	private static Logger logger = Logger.getLogger(ListItem.class);
    
	public static final String tableId = "list_item";
    private static final int fieldCount = 22;

	public final static String selectFrom =
		" SELECT " +
        " id, list_id, title, teaser_id, article_id, order_number, " +
        " link, image_link, document_link, view_date, " +
        " publish_date, expired_date, created_by, created_date, " +
        " last_modified_by, last_modified_date, thumbnail_link, " +
        " list_item_link, parameters, container_id, per_lang_id FROM list_item";

	private final static String findByIdSql =
        ListItem.selectFrom + " WHERE id = ?";
	
	private final static String findByPerLangIdSql =
		" SELECT list_item.*, list.lang_id" +        
        " FROM list_item" +
		" LEFT JOIN list ON list.id = list_item.list_id" + 
        " WHERE per_lang_id = ? AND list.lang_id = ?";

	private final static String updateSql =
		"UPDATE list_item " +
        "SET id = ?, list_id = ?, title = ?, teaser_id = ?, article_id = ?, " +
        "order_number = ?, link = ?, image_link = ?, document_link = ?, " +
        "view_date = ?, publish_date = ?, expired_date = ?, created_by = ?, " +
        "created_date = ?, last_modified_by = ?, last_modified_date = ?, " +
        "thumbnail_link = ?, list_item_link = ?, parameters = ?, " +
        "container_id = ?, " +
        "site_id = ?, per_lang_id = ? " +
        "WHERE id = ?";

	private final static String insertSql =
		" INSERT INTO list_item" +
        " (id, list_id, title, teaser_id, article_id, order_number," +
        " link, image_link, document_link, view_date," +
        " publish_date, expired_date, created_by, created_date," +
        " last_modified_by, last_modified_date, thumbnail_link," +
        " list_item_link, parameters, container_id, site_id, per_lang_id) " +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private final static String incOrderSql =
		" UPDATE list_item " +
        " SET order_number=order_number + 1 " +
        " WHERE list_id = ?";

    private final static String updateOrderSql = 
        "UPDATE list_item set order_number = ? " +
        "where list_id = ? and order_number = ?";

    private Long id;
	private Long listId;
	private String title = "No title";
	private int orderNumber;
	private Long teaserId;
    private Long articleId;
    private String link;
	private String imageLink;
    private String documentLink;
	private String thumbnailLink;
    private Long listItemLink;
	private Timestamp viewDate;
	private Timestamp publishDate;
	private Timestamp expiredDate;
    private Long createdBy;
    private Timestamp createdDate;
    private Long lastModifiedBy;
    private Timestamp lastModifiedDate;
    private String parameters;
    private Long containerId;
    private Long perLangId;
    
    private Set<SocialNetwork> publishedTo = new HashSet<SocialNetwork>();
    
	/**
	 * Default Construction
	 */
	public ListItem() {}

	public ListItem(
        Long id,
        Long listId,
        String title,
        int orderNumber,
        Long teaserId,
        Long articleId,
        String link,
        String imageLink,
        String documentLink,
        String thumbnailLink,
        Long listItemLink,
        Timestamp viewDate,
        Timestamp publishDate,
        Timestamp expiredDate,
        Long createdBy,
        Timestamp createdDate,
        Long lastModifiedBy,
        Timestamp lastModifiedDate,
        String parameters,
        Long containerId,
        Long perLangId)
    {
        this.id = id;
        this.listId = listId;
        this.title = title;
        this.orderNumber = orderNumber;
        this.teaserId = teaserId;
        this.articleId = articleId;
        this.link = link;
        this.imageLink = imageLink;
        this.documentLink = documentLink;
        this.thumbnailLink = thumbnailLink;
        this.listItemLink = listItemLink;
        this.viewDate = viewDate;
        this.publishDate = publishDate;
        this.expiredDate = expiredDate;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.parameters = parameters;
        this.containerId = containerId;
        this.perLangId = perLangId;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            listId = DBHelper.makeLong(rs.getLong("list_id"));
            title = rs.getString("title");
            teaserId = DBHelper.makeLong(rs.getLong("teaser_id"));
            articleId =  DBHelper.makeLong(rs.getLong("article_id"));
            orderNumber = rs.getInt("order_number");
            link = rs.getString("link");
            imageLink = rs.getString("image_link");
            thumbnailLink = rs.getString("thumbnail_link");
            documentLink = rs.getString("document_link");
            viewDate = rs.getTimestamp("view_date");
            publishDate = rs.getTimestamp("publish_date");
            expiredDate = rs.getTimestamp("expired_date");
            createdBy = DBHelper.makeLong(rs.getLong("created_by"));
            createdDate = rs.getTimestamp("created_date");
            lastModifiedBy = DBHelper.makeLong(rs.getLong("last_modified_by"));
            lastModifiedDate = rs.getTimestamp("last_modified_date");
            listItemLink = DBHelper.makeLong(rs.getLong("list_item_link"));
            parameters = rs.getString("parameters");
            containerId = DBHelper.makeLong(rs.getLong("container_id"));
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
        incOrder(con);
        orderNumber = 0;
        return DBHelper.insertDbObject(con, this);
    }

    public void update(Connection con) throws CriticalException {
        DBHelper.updateDbObject(con, this);
    }

    public void delete(Connection con) throws CriticalException {
        DBHelper.deleteObject(con, tableId, id, logger);
        if (listItemLink == null)
            try {
                if (articleId != null)
                    Article.findById(con, articleId).delete(con);
                if (teaserId != null)
                    Article.findById(con, teaserId).delete(con);
            }
	    	catch (ObjectNotFoundException e){
	    		logger.error("Article not found ", e);
	    	}
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
        stmt.setObject(2, listId);
        stmt.setString(3, title);
        stmt.setObject(4, teaserId);
        stmt.setObject(5, articleId);
        stmt.setInt(6, orderNumber);
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
        stmt.setObject(18, listItemLink);
        stmt.setString(19, parameters);
        stmt.setObject(20, containerId);
        stmt.setObject(21, Env.getSiteId());
        stmt.setObject(22, perLangId);
        logger.debug("-");
        return stmt;
    }

    public static ListItem findById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id, Types.BIGINT);
            ResultSet rs = stmt.executeQuery();
            ListItem li = null;
            if (rs.next()) {
                li = new ListItem();
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
    
    public static ListItem findByPerLangId(Connection conn, Long perLangId, Long langId)
	    throws CriticalException
	{
	    logger.debug("+");
	    PreparedStatement stmt = null;
	    try {
	        stmt = conn.prepareStatement(findByPerLangIdSql);
	        stmt.setObject(1, perLangId, Types.BIGINT);
	        stmt.setObject(2, langId, Types.BIGINT);
	        ResultSet rs = stmt.executeQuery();
	        ListItem li = null;
	        if (rs.next()) {
	            li = new ListItem();
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

    public static ListItem getById(Connection conn, Long id)
       throws CriticalException
    {
        ListItem li = findById(conn, id);
        if (li == null)
            throw new CriticalException("ListItem not found by id=" + id);
        return li; 
    }

	/**
	 * Increment order in list_item
	 *
	 * @return
	 * @throws SQLException
	 */
	private void incOrder(Connection conn) throws CriticalException {
		logger.debug("+");
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(incOrderSql);
			stmt.setObject(1, listId, Types.BIGINT);
			stmt.execute();
		}
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
			throw new CriticalException(ex);
		}
        finally {
			DBHelper.close(stmt);
		}
        logger.debug("-");
	}

	/**
	 * Move up list item.
	 *
	 * @return
	 * @throws SQLException
	 */
	public void moveUp(Connection conn) throws CriticalException {
		logger.debug("+");
        if (orderNumber == 0) return;
        PreparedStatement stmt = null;
		try {
            stmt = conn.prepareStatement(updateOrderSql);
            stmt.setInt(1, orderNumber);
            stmt.setObject(2, listId, Types.BIGINT);
            stmt.setInt(3, orderNumber-1);
            if (stmt.executeUpdate() == 1) {
                --orderNumber;
                update(conn);
            }
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
    }

	/**
	 * Move down list item.
	 *
	 * @return
	 * @throws SQLException
	 */
	public void moveDown(Connection conn) throws CriticalException {
		logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(updateOrderSql);
            stmt.setInt(1, orderNumber);
            stmt.setObject(2, listId, Types.BIGINT);
            stmt.setInt(3, orderNumber+1);
            if (stmt.executeUpdate() == 1) {
                ++orderNumber;
                update(conn);
            }
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
	}

	public Element getElement(Document doc)
        throws CriticalException
    {
		logger.debug("+");
        Element itemElement = Env.createDomElement(doc, "listItem");        
        itemElement.setAttribute("id", String.valueOf(id));
        itemElement.setAttribute("listId", String.valueOf(listId));
        itemElement.setAttribute("orderNumber", String.valueOf(orderNumber));
        itemElement.setAttribute("title", title);
        itemElement.setAttribute("documentLink", documentLink);
        itemElement.setAttribute("parameters", parameters);
        itemElement.setAttribute("containerId", String.valueOf(containerId));
        Xbuilder.setAttr(itemElement, "perLangId", perLangId);

        if (link != null && link.trim().length() > 0){
        	itemElement.setAttribute("href", link);
        	//for popup
        	if (isPopupLink(link)){
        		link = link.substring(link.indexOf("'")+1, link.length());
        		link = link.substring(0, link.indexOf("'"));
        	}
	        try {
	            String title = PageService.getInstance().findByFileName(link).getTitle();
	            itemElement.setAttribute("link-page-name", title);
	        } 
	        catch (Exception ex) {
	        	itemElement.setAttribute("link-page-name", link);
	            logger.error("Page not found "  + link);
	        }
        }
        XmlHelper.setImageLinkAttribute(itemElement, imageLink);
        XmlHelper.setThumbnailLinkAttribute(itemElement, thumbnailLink);
        XmlHelper.setTimeStampAttribute(itemElement, "viewDate", viewDate);
        XmlHelper.setTimeStampAttribute(itemElement, "publishDate", publishDate);
        XmlHelper.setTimeStampAttribute(itemElement, "expiredDate", expiredDate);
        XmlHelper.setTimeStampAttribute(itemElement, "createdDate", createdDate);
        XmlHelper.setTimeStampAttribute(itemElement, "lastModifiedData", 
            lastModifiedDate);
        itemElement.appendChild(getSummaryArticleElement(doc));
        itemElement.appendChild(getDetailsArticleElement(doc));
		logger.debug("-");
		return itemElement;
	}
	
	private boolean isPopupLink(String link){
		return link.contains("javascript:");
	}
    
    private Element getSummaryArticleElement(Document doc)
        throws CriticalException
    {
        logger.debug("+");
        Element teaserElement =
            Env.createDomElement(doc, "teaser");
        if (this.teaserId != null) {
            try {
                teaserElement.appendChild(
                    Article.findById(this.teaserId).createDomElement(doc));
            }
            catch (ObjectNotFoundException e) {
                logger.error("- Throwing new CriticalException");
                throw new CriticalException(e);
            }
        }
        logger.debug("-");
        return teaserElement;
    }
    
    private Element getDetailsArticleElement(Document doc)
        throws CriticalException
    {
        logger.debug("+");
        Element teaserElement =
            Env.createDomElement(doc, "details");
        if (this.articleId != null) {
            try {
                teaserElement.appendChild(
                    Article.findById(this.articleId).createDomElement(doc));
            }
            catch (ObjectNotFoundException e) {
                logger.error("- Throwing new CriticalException");
                throw new CriticalException(e);
            }
        }
        logger.debug("-");
        return teaserElement;
    }

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DomainObject#getElement()
	 */

    public String toString() {
        StringBuffer sb = new StringBuffer("ListItem [");
        sb.append("id=");
        sb.append(id);
        sb.append(" listId=");
        sb.append(listId);
        sb.append(" title=");
        sb.append(title);
        sb.append(" orderNumber=");
        sb.append(orderNumber);
        sb.append(" teaserId=");
        sb.append(teaserId);
        sb.append(" articleId=");
        sb.append(articleId);
        sb.append(" imageLink=");
        sb.append(imageLink);
        sb.append(" thumbnailLink=");
        sb.append(thumbnailLink);
        sb.append(" documentLink=");
        sb.append(documentLink);
        sb.append(" link=");
        sb.append(link);
        sb.append(" listItemLink=");
        sb.append(listItemLink);
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
        sb.append("]");
        return sb.toString();
	}

	/**
	 * @return
	 */
	public String getLink() {
		return link;
	}

    public void setLink(String link) {
        this.link = link;
    }

	/**
	 * @return
	 */
	public Long getListId() {
		return listId;
	}
    
    public void setListId(Long listId) {
        this.listId = listId;
    }

	/**
	 * @return
	 */
	public int getOrderNumber() {
		return orderNumber;
	}

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

    public void setTitle(String title) {
        this.title = title;
    }

	/**
	 * @return
	 */
	public Long getTeaserId() {
		return teaserId;
	}

	/**
	 * @param
	 */
	public void setTeaserId(Long teaserId) {
		this.teaserId = teaserId;
	}
    
    /**
     * @return
     */
    public Long getArticleId() {
        return articleId;
    }

    /**
     * @param string
     */
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

	/**
	 * @return
	 */
	public Timestamp getViewDate() {
		return viewDate;
	}

	/**
	 * @param date
	 */
	public void setViewDate(Timestamp date) {
		this.viewDate = date;
	}

	/**
	 * @return
	 */
	public Timestamp getPublishDate() {
		return publishDate;
	}

	/**
	 * @param date
	 */
	public void setPublishDate(Timestamp publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @return
	 */
	public Timestamp getExpiredDate() {
		return expiredDate;
	}

	/**
	 * @param date
	 */
	public void setExpiredDate(Timestamp expiredDate) {
		this.expiredDate = expiredDate;
	}
	/**
	 * @return
	 */
	public String getImageLink() {
		return imageLink;
	}

	/**
	 * @param string
	 */
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	/**
	 * @return Returns the documentLink.
	 */
	public String getDocumentLink() {
		return documentLink;
	}

	/**
	 * @param documentLink The documentLink to set.
	 */
	public void setDocumentLink(String documentLink) {
		this.documentLink = documentLink;
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

	/**
	 * @return
	 */
	public String getThumbnailLink() {
		return thumbnailLink;
	}

	/**
	 * @param string
	 */
	public void setThumbnailLink(String string) {
		thumbnailLink = string;
	}
    
    public Long getListItemLink() { 
        return listItemLink;
    }

    public void setListItemLink(Long listItemLink) {
        this.listItemLink = listItemLink;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Long getContainerId() {
        return containerId;
    }
    
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

	public Set<SocialNetwork> getPublishedTo() {
		return publishedTo;
	}

	public void setPublishedTo(Set<SocialNetwork> publishedTo) {
		this.publishedTo = publishedTo;
	}

	public Long getPerLangId() {
		return perLangId;
	}

	public void setPerLangId(Long perLangId) {
		this.perLangId = perLangId;
	}
}
