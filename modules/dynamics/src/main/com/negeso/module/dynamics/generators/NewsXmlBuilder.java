/*
 * @(#)$Id: NewsXmlBuilder.java,v 1.17, 2006-10-09 15:12:01Z, Alexander Serbin$
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
import com.negeso.framework.domain.User;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.module.dynamics.DynamicsForm;

/**
 * @author Sergiy.Oliynyk
 */
public class NewsXmlBuilder implements DynamicsXmlBuilder {

    private static Logger logger = Logger.getLogger(NewsXmlBuilder.class);

    private static final String itemsQuery =
        " SELECT " +
        " list_item.id, title, publish_date, expired_date, created_by, " +
        " created_date, last_modified_by, last_modified_date, " +
        " days_left(publish_date, expired_date) AS days_left " +
        " FROM list_item, list " +
        " WHERE list_id = list.id " +
        " AND list.lang_id = ? " +
        " AND list.module_id in (?, ?) ";

    private static final String countItemsQuery =
        " SELECT count(list_item.id) FROM list_item, list " +
        " WHERE list_id = list.id " +
        " AND list.lang_id = ? AND list.module_id IN (?, ?)";

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
            stmt.setLong  (2, Env.getSite().getModuleId(ModuleConstants.NEWS));
            stmt.setLong  (3, Env.getSite().getModuleId(ModuleConstants.FAQ));            
            stmt.setInt(4, form.getPaging());
            stmt.setInt(5, (form.getPageNumber()-1) * form.getPaging());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Element element = Env.createDomElement(doc, "news_item");
                setItemAttributes(element, rs);
                dynamicsElement.appendChild(element);
            }
        }
        catch (Exception ex) {
            logger.error("Cannot build xml", ex);
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
        itemElement.setAttribute("id", rs.getString("id"));
        itemElement.setAttribute("name", rs.getString("title"));
        XmlHelper.setDateAttribute(itemElement, "created_date", 
            rs.getTimestamp("created_date"));
        Long createdBy = DBHelper.makeLong(rs.getLong("created_by"));
        if (createdBy != null) {
           itemElement.setAttribute("created_by", 
               User.findById(createdBy).getName());
        }
        Timestamp publish = rs.getTimestamp("publish_date");
        Timestamp expired = rs.getTimestamp("expired_date");
        XmlHelper.setDateAttribute(itemElement, "publish_date", publish);
        XmlHelper.setDateAttribute(itemElement, "expired_date", expired);
        itemElement.setAttribute("days_left", rs.getString("days_left"));
        XmlHelper.setDateAttribute(itemElement, "modified_date",
            rs.getTimestamp("last_modified_date"));
        Long modifiedBy = DBHelper.makeLong(rs.getLong("last_modified_by"));
        if (modifiedBy != null) {
           itemElement.setAttribute("modified_by",
               User.findById(modifiedBy).getName());
        }
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
            stmt.setLong  (2, Env.getSite().getModuleId(ModuleConstants.NEWS));
            stmt.setLong  (3, Env.getSite().getModuleId(ModuleConstants.FAQ));                        
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
            form.setOrder("publish_date desc");
    }
}
