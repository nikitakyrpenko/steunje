/*
 * @(#)$Id: ProductsXmlBuilder.java,v 1.5, 2006-04-19 16:25:32Z, Dmitry Dzifuta$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dynamics.generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.module.dynamics.DynamicsForm;

/**
 * @author Stanislav Demchenko
 */
public class ProductsXmlBuilder implements DynamicsXmlBuilder {

    private static Logger logger = Logger.getLogger(ProductsXmlBuilder.class);

    private static final String itemsQuery =
        " SELECT " +
        " pm_product.id, publish_date, expired_date," +
        " order_number, days_left(publish_date, expired_date) AS days_left " +
        " FROM pm_product, pm_prod2lang_presence " +
        " WHERE site_id = ? AND NOT " +
        " (pm_product.id = product_id AND lang_id = ? AND is_present = false) ";

    private static final String countItemsQuery = 
        " SELECT count(pm_product.id) FROM pm_product, pm_prod2lang_presence " +
        " WHERE site_id = ? AND NOT " +
        " (pm_product.id = product_id AND lang_id = ? AND is_present = false) ";

    public Element getElement(Connection conn, Document doc, DynamicsForm form, 
        Language lang)
    {
        logger.debug("+");
        if (conn == null || doc == null || form == null) {
            logger.error("- Illegal argument");
            throw new IllegalArgumentException();
        }
        checkParameters(form);
        Element dynamicsElement = Env.createDomElement(doc, "dynamics");
        dynamicsElement.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
        dynamicsElement
            .setAttribute("totalPages", "" + getTotal(conn, form, lang));
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                itemsQuery + getFilter(form) +
                " ORDER BY " + form.getOrder() + " LIMIT ? OFFSET ?");
            stmt.setObject(1, Env.getSiteId());
            stmt.setObject(2, lang.getId());
            stmt.setInt(3, form.getPaging());
            stmt.setInt(4, (form.getPageNumber()-1) * form.getPaging());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "product");
                setItemAttributes(element, rs);
                dynamicsElement.appendChild(element);
            }
        } catch (Exception e) {
            logger.error("- Cannot build xml", e);
        } finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return dynamicsElement;
    }

    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws SQLException
    {
        logger.debug("+");
        Timestamp publish = rs.getTimestamp("publish_date");
        Timestamp expired = rs.getTimestamp("expired_date");
        itemElement.setAttribute("id", rs.getString("id"));
//        itemElement.setAttribute("name", rs.getString("name"));
        XmlHelper.setDateAttribute(itemElement, "publish_date", publish);
        XmlHelper.setDateAttribute(itemElement, "expired_date", expired);
        itemElement.setAttribute("days_left", rs.getString("days_left"));
//        long days = 0;
//        if (expired != null)
//            days = (expired.getTime() - System.currentTimeMillis()) / 
//                (1000 * 60 * 60 * 24);
//        if (expired == null || days > 0)
//            itemElement.setAttribute("url", rs.getString("url"));
//        else
//            itemElement.setAttribute("url", "");
        itemElement.setAttribute("order_number", rs.getString("order_number"));
        logger.debug("-");
    }

    private int getTotal(Connection conn, DynamicsForm form, Language lang)
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        int numberOfPages = 0;
        try {
            stmt = conn.prepareStatement(countItemsQuery + getFilter(form));
            stmt.setObject(1, Env.getSiteId());
            stmt.setObject(2, lang.getId());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int numberOfItems = rs.getInt(1);
            numberOfPages = numberOfItems / form.getPaging();
            if (numberOfItems % form.getPaging() > 0)
                numberOfPages++;
        } catch (Exception e) {
            logger.error("Cannot get total", e);
        }
        logger.debug("-");
        return numberOfPages;
    }
    
    private String getFilter(DynamicsForm form)
    {
        logger.debug("+ -");
        return form.getFilter() == 0
            ? ""
            : " AND is_dynamic(publish_date, expired_date, " +
                form.getFilter() + ") ";
    }

    private void checkParameters(DynamicsForm form) {
        if (form.getOrder() == null || form.getOrder().length() == 0) {
            form.setOrder("order_number asc");
        }
    }
    
}
