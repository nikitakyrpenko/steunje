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
package com.negeso.framework.list.generators;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class DynamicItemsXmlBuilder extends ListXmlBuilder{
	
	private static final String orderBy = "dim.orderBy";
	private static final String orderNumberVal = "order_number";
	private static final String orderNumberSql = "list_item.order_number";
	private static final String rundomOrderVal = "random";
	private static final String rundomOrderSql = "random()";
	
	 private static ListXmlBuilder instance = new DynamicItemsXmlBuilder();
	
	public static ListXmlBuilder getInstance() {
        return instance;
    }
	
    private static final String itemsQuery =
        " SELECT list_item.id, list_item.list_id, list_item.title, " +
        " 	list_item.link, list_item.image_link, list_item.thumbnail_link," +
        " 	list_item.document_link, list_item.order_number, " +
        " 	list_item.view_date, list_item.parameters, list_item.container_id, " +
        " 	t_article.text as teaser, a_article.text as text " +
        " FROM list_item " +
        " LEFT JOIN article t_article " +
        "	ON list_item.teaser_id = t_article.id " +
        " LEFT JOIN article a_article " +
        "	ON list_item.article_id = a_article.id " +
        " WHERE list_id = ?  AND list_item.site_id = ? " +
        " 	AND ((publish_date <= now() AND expired_date >= now()) " +
        " 	OR expired_date IS NULL) " +
        " ORDER BY %s";

	@Override
	protected String getItemsQuery() {
		String orderBy = Env.getProperty(DynamicItemsXmlBuilder.orderBy, DynamicItemsXmlBuilder.orderNumberVal);
		return String.format(itemsQuery, (rundomOrderVal.equals(orderBy) ? rundomOrderSql : orderNumberSql));
	}

	@Override
	protected void setItemAttributes(Element itemElement, ResultSet rs)
			throws SQLException, CriticalException {      
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("listId", rs.getString("list_id"));
        itemElement.setAttribute("orderNumber", rs.getString("order_number"));
        itemElement.setAttribute("title", rs.getString("title"));
        itemElement.setAttribute("parameters", rs.getString("parameters"));
        String hr = rs.getString("link");
        if (hr != null && hr.trim().length() > 0)
        	itemElement.setAttribute("href", rs.getString("link"));
        
        XmlHelper.setImageLinkAttribute(itemElement, rs.getString("image_link"));
        XmlHelper.setThumbnailLinkAttribute(itemElement,
            rs.getString("thumbnail_link"));
        itemElement.setAttribute("documentLink", rs.getString("document_link"));
        XmlHelper.setDateAttribute(itemElement, "viewDate", 
            rs.getString("view_date"));
        itemElement.appendChild(XmlHelper.getArticleElement(
            itemElement.getOwnerDocument(), "teaser", 
            0, "teaser",
            "teaser", rs.getString("teaser")));
        itemElement.appendChild(XmlHelper.getArticleElement(
                itemElement.getOwnerDocument(), "text", 
                0, "text",
                "text", rs.getString("text")));
	}

}

