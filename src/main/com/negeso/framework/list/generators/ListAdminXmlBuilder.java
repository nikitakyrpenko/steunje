package com.negeso.framework.list.generators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
public class ListAdminXmlBuilder extends ListXmlBuilder {

    private static Logger logger = Logger.getLogger(ListAdminXmlBuilder.class);

    private static final ListXmlBuilder instance = new ListAdminXmlBuilder();
    
    private static final String itemsQuery = 
        "select id, list_id, title, order_number, " +
        "thumbnail_link, list_item_link, " +
        "publish_date, view_date, expired_date, " +
        "container_id from list_item " +
        "where list_id = ? and site_id = ? " +
        " order by order_number";
    
    private ListAdminXmlBuilder() {}

    public static ListXmlBuilder getInstance() {
        return instance;
    }

    /**
     * Appends attributes to the List Item element
     * @param itemElement
     * @param rs An active ResultSet with a result of query
     */
    protected void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException, CriticalException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("listId", rs.getString("list_id"));
        itemElement.setAttribute("title", rs.getString("title"));
        itemElement.setAttribute("orderNumber", rs.getString("order_number"));    
        itemElement.setAttribute("linked", rs.getString("list_item_link"));
        XmlHelper.setThumbnailLinkAttribute(itemElement,
            rs.getString("thumbnail_link"));
        try {
			XmlHelper.setDateAttribute(itemElement, "viewDate", 
				Env.formatSimpleRoundDate(rs.getTimestamp("view_date")));
			XmlHelper.setDateAttribute(itemElement, "publishDate", 
				Env.formatSimpleRoundDate(rs.getTimestamp("publish_date")));
			XmlHelper.setDateAttribute(itemElement, "expiredDate",
				Env.formatSimpleRoundDate(rs.getTimestamp("expired_date")));			
		} catch (Exception e) {
			e.printStackTrace();
		}
        itemElement.setAttribute("containerId", rs.getString("container_id"));
        logger.debug("-");
    }

    protected String getItemsQuery() {
        return itemsQuery;
    }
}
