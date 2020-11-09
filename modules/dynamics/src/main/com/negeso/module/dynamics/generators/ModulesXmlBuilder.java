/*
 * @(#)$Id: ModulesXmlBuilder.java,v 1.15, 2005-12-05 10:30:58Z, Svetlana Bondar$
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
import java.sql.Statement;
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
public class ModulesXmlBuilder implements DynamicsXmlBuilder {

    private static Logger logger = Logger.getLogger(ModulesXmlBuilder.class);

    private static final String itemsQuery =
        " SELECT id, name, golive, expired, url, order_number, " +
        " days_left(golive, expired) AS days_left " +
        " FROM module ";

    private static final String countItemsQuery = 
        " SELECT count(id) FROM module ";

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
        dynamicsElement.setAttribute("totalPages", String.valueOf(
            getTotalPages(conn, form)));
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                itemsQuery + getDaysFilter(form) +
                " ORDER BY " + form.getOrder() + " LIMIT ? OFFSET ?");
            stmt.setInt(1, form.getPaging());
            stmt.setInt(2, (form.getPageNumber()-1) * form.getPaging());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "module");
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
        throws SQLException
    {
        logger.debug("+");
        Timestamp golive = rs.getTimestamp("golive");
        Timestamp expired = rs.getTimestamp("expired");
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("name"));
        XmlHelper.setDateAttribute(itemElement, "publish_date", golive);
        XmlHelper.setDateAttribute(itemElement, "expired_date", expired);
        itemElement.setAttribute("days_left", rs.getString("days_left"));
        long days = 0;
        if (expired != null)
            days = (expired.getTime() - System.currentTimeMillis()) / 
                (1000 * 60 * 60 * 24);
        if (expired == null || days > 0)
            itemElement.setAttribute("url", rs.getString("url"));
        else
            itemElement.setAttribute("url", "");
        itemElement.setAttribute("order_number", rs.getString("order_number"));
        logger.debug("-");
    }

    private int getTotalPages(Connection conn, DynamicsForm form)
    {
        logger.debug("+");
        Statement stmt = null;
        ResultSet rs = null;
        int numberOfPages = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(countItemsQuery + getDaysFilter(form));
            rs.next();
            int numberOfItems = rs.getInt(1);
            numberOfPages = numberOfItems / form.getPaging();
            if (numberOfItems % form.getPaging() > 0)
                numberOfPages++;
        }
        catch (Exception ex) {
            logger.error("Cannot get total pages", ex);
        }finally{
        	DBHelper.close(rs);
        	DBHelper.close(stmt);
        }
        logger.debug("-");
        return numberOfPages;
    }
    
    private String getDaysFilter(DynamicsForm form)
    {
        logger.debug("+ -");
        return form.getFilter() == 0
            ? ""
            : " WHERE is_dynamic(golive, expired, " + form.getFilter() + ") ";
    }

    private void checkParameters(DynamicsForm form) {
        if (form.getOrder() == null || form.getOrder().length() == 0)
            form.setOrder("order_number asc");
    }
}
