/*
 * @(#)$Id: PagesXmlBuilder.java,v 1.15, 2005-12-05 10:30:59Z, Svetlana Bondar$
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
 * @author Sergiy.Oliynyk
 */
public class PagesXmlBuilder implements DynamicsXmlBuilder {

    private static Logger logger = Logger.getLogger(PagesXmlBuilder.class);

    private static final String itemsQuery =
        " SELECT " +
        " title, publish_date, last_modified, expired_date, filename, " +
        " category, days_left(publish_date, expired_date) AS days_left " +
        " FROM page WHERE lang_id = ? ";

    private static final String countItemsQuery =
        " SELECT count(id) FROM page WHERE lang_id = ? ";
        
    public Element getElement(Connection conn, Document doc, DynamicsForm form, 
        Language lang)
    {
        logger.debug("+");
        if (conn == null || doc == null || form == null || lang == null) {
            logger.error("- Illegal argument");
            throw new IllegalArgumentException();
        }
        checkParameters(form);
        Element dynamicsElement = Env.createDomElement(doc, "dynamics");
        dynamicsElement.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
        dynamicsElement.setAttribute("totalPages", String.valueOf(
            getTotalPages(conn, form, lang)));
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                itemsQuery + getDaysFilter(form) +
                " ORDER BY " + form.getOrder() + " LIMIT ? OFFSET ?");
            stmt.setObject(1, lang.getId());
            stmt.setInt(2, form.getPaging());
            stmt.setInt(3, (form.getPageNumber()-1) * form.getPaging());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "page");
                setItemAttributes(element, rs);
                dynamicsElement.appendChild(element);
            }
        }
        catch (Exception ex) {
            logger.error("- Cannot build xml", ex);
        }
        finally {
            DBHelper.close(stmt);
        }
        logger.debug("-");
        return dynamicsElement;
    }

    private void setItemAttributes(Element itemElement, ResultSet rs)
        throws Exception
    {
        logger.debug("+");
        itemElement.setAttribute("name", rs.getString("title"));
        Timestamp publish = rs.getTimestamp("publish_date");
        Timestamp expired = rs.getTimestamp("expired_date");
        XmlHelper.setDateAttribute(itemElement, "publish_date", publish);
        XmlHelper.setDateAttribute(itemElement, "expired_date", expired);
        XmlHelper.setDateAttribute(itemElement, "modified_date",
            rs.getTimestamp("last_modified"));
        itemElement.setAttribute("filename", rs.getString("filename"));
        itemElement.setAttribute("category", rs.getString("category"));
        itemElement.setAttribute("days_left", rs.getString("days_left"));
        logger.debug("-");
    }
    
    private int getTotalPages(Connection conn, DynamicsForm form, Language lang)
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        int numberOfPages = 0;
        try {
            stmt = conn.prepareStatement(countItemsQuery + getDaysFilter(form));
            stmt.setObject(1, lang.getId());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int numberOfItems = rs.getInt(1);
            numberOfPages = numberOfItems / form.getPaging();
            if (numberOfItems % form.getPaging() > 0)
                numberOfPages++;
        }
        catch (Exception ex) {
            logger.error("Cannot get total pages", ex);
        }
        logger.debug("-");
        return numberOfPages;
    }
    
    private String getDaysFilter(DynamicsForm form)
    {
        logger.debug("+ -");
        return form.getFilter() == 0
            ? ""
            : " AND is_dynamic(publish_date, expired_date, " +
                    form.getFilter() + ") ";
    }
    
    private void checkParameters(DynamicsForm form) {
        if (form.getOrder() == null || form.getOrder().length() == 0)
            form.setOrder("title asc");
    }
}
