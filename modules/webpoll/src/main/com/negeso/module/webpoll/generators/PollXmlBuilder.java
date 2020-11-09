package com.negeso.module.webpoll.generators;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.list.generators.ListXmlBuilder;
import com.negeso.framework.list.generators.XmlHelper;

/**
 * @author Sergiy Oliynyk
 * @version 1.0
 */
public class PollXmlBuilder extends ListXmlBuilder {
    
    private static ListXmlBuilder instance = new PollXmlBuilder();
    
    // And this query for build an XML of list for the page building
    private static final String itemsQuery =
        "select list_item.id, list_id, title, image_link, thumbnail_link, " +
            "order_number, list_item.container_id, " +
            "webpoll.counter, article.head, article.text, " +
            "article.id as articleId, article.class as articleClass " +
            "from list_item " +
            "left join webpoll on (list_item.id  = webpoll.list_item_id) " +
            "left join article on (list_item.teaser_id = article.id) " +
            "where list_id = ? and list_item.site_id = ? " +
            " and ((publish_date <= now() and expired_date >= now()) " +
            " or expired_date is null) " +
            "order by list_item.order_number ";

    private static Logger logger = Logger.getLogger(PollXmlBuilder.class);

    private PollXmlBuilder() {}

    public static ListXmlBuilder getInstance() {
        return instance;
    }

    protected void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException, CriticalException
    {
        logger.debug("+");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("listId", rs.getString("list_id"));
        itemElement.setAttribute("orderNumber", rs.getString("order_number"));
        itemElement.setAttribute("title", rs.getString("title"));
        XmlHelper.setImageLinkAttribute(itemElement, rs.getString("image_link"));
        XmlHelper.setThumbnailLinkAttribute(itemElement, 
            rs.getString("thumbnail_link"));
        if (rs.getString("text") != null)
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
