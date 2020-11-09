package com.negeso.framework.list.generators;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;

/**
 * @author Sergiy Oliynyk
 * @version 1.0
 * XML Builder class that provides an XML output of the List structure
 * for both administrative and visitor part of the framework. It was created
 * after completely reworking of this mechanism for Lists because the
 * productivity was too low when domain objects were used.
 */
public class ListVisitorXmlBuilder extends ListXmlBuilder {

    private static Logger logger = Logger.getLogger(
        ListVisitorXmlBuilder.class);

    private static ListXmlBuilder instance = new ListVisitorXmlBuilder();
    
    private ListVisitorXmlBuilder() {}

    public static ListXmlBuilder getInstance() {
        return instance;
    }

    // And this query for build an XML of list for the page building
    private static final String itemsQuery =
        "select list_item.id, list_item.list_id, list_item.title, " +
        "list_item.link, list_item.image_link, list_item.thumbnail_link," +
        "list_item.document_link, list_item.order_number, " +
        "list_item.view_date, list_item.parameters, " +
        "article.id as articleId, article.head, article.text," +
        "article.class as articleClass, article.container_id as container_id " +
        "from list_item join article on (list_item.teaser_id = article.id) " +
        "where list_id = ?  and list_item.site_id = ? " +
        " and ((publish_date <= now() and expired_date >= now()) " +
        " or expired_date is null) " +
        " order by list_item.order_number";

    protected void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException, CriticalException
    {        
        logger.debug("+");
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
            rs.getLong("articleId"), rs.getString("articleClass"),
            rs.getString("head"), rs.getString("text")));
        logger.debug("-");
    }
    
    protected String getItemsQuery() {
        return itemsQuery;
    }
}
