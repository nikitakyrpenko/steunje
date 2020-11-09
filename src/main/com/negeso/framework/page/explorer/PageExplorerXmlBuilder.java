/*
 * @(#)PageExplorerXmlBuilder.java       @version    09.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page.explorer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.menu.service.MenuService;
import com.negeso.framework.module.Module;
import com.negeso.framework.security.SecurityGuard;

/**
 * @author Sergiy Oliynyk
 *
 */
public class PageExplorerXmlBuilder {

    private static Logger logger = Logger.getLogger(
        PageExplorerXmlBuilder.class);
    
    private static final PageExplorerXmlBuilder instance =
        new PageExplorerXmlBuilder();

    private PageExplorerXmlBuilder() {}
    
    public static PageExplorerXmlBuilder getInstance() { return instance; }

    public Document getDocument(Connection conn, Long userId)
        throws CriticalException
    {
    	logger.debug("+");
        try {
            Document doc = Env.createDom();
            Element docElement = getElement(doc, conn, userId);
            docElement.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
            logger.debug("-");
            return doc;
        }
        catch (Exception ex) {
            logger.error("- Cannot build XML");
            throw new CriticalException(ex);
        }
    }

    private Element getElement(Document doc, Connection conn, Long userId)
        throws CriticalException
    {
    	logger.debug("+");
        Element rootElement = Env.createDomElement(doc, "root");
        doc.appendChild(rootElement);        
        try {
            Counter c = new Counter();
            buildMenuTree(doc, userId, c);            
            buildPopupsTree(conn, doc, userId, c);
            buildMessagesTree(conn, doc, userId, c);
            buildModulesTree(conn, doc, userId, c);
            buildUnlinkedPages(conn, doc, userId, c);
        }
        catch (SQLException ex) {
            throw new CriticalException(ex);
        } catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        logger.debug("-");
        return rootElement;
    }
    private void buildMenuTree(Document doc, Long userId,Counter c) throws CriticalException, ObjectNotFoundException{
        logger.debug("+");
        Element rootElement = doc.getDocumentElement();
        Element menuElement = 
            Env.createDomElement(doc, "class");
        menuElement.setAttribute("id", String.valueOf(c.inc()));        
        menuElement.setAttribute("type", "menu");
        menuElement.setAttribute("title", "menu");
        menuElement.setAttribute("top", "true");
        User user = User.findById(userId);
        for (Language lang : Language.getItems()) {            
            MenuService.getSpringInstance().getMenuExplorerXml(menuElement, true, lang.getCode(), user);
            rootElement.appendChild(menuElement);
		}
        
        

        logger.debug("-");
    }
    
    private void buildPopupsTree(Connection conn, Document doc, Long userId,
        Counter c) throws SQLException
    {
        logger.debug("+");
        Element rootElement = doc.getDocumentElement();
    
        Statement stmt = conn.createStatement();
    
        ResultSet res = stmt.executeQuery(
            " select * from page where category ='popup' " +
            " and site_id=" + Env.getSiteId() +
            " order by title");
    
        Element element = 
            Env.createDomElement(doc, "class");
        element.setAttribute("id", String.valueOf(c.inc()));
        element.setAttribute("type", "popups");
        element.setAttribute("top", "true");
        element.setAttribute("title", "Popup windows");
        rootElement.appendChild(element);
                    
        while (res.next()) {
            Long containerId = 
                makeLong(res.getLong("container_id"));
            if (SecurityGuard.canView(userId, containerId)) {
                Element pageElement = 
                    Env.createDomElement(doc, "page");
                setPageAttributes(pageElement, res);
                element.appendChild(pageElement);
            }
        }
    
        res.close();
        stmt.close();
        logger.debug("-");
    }

    private void buildMessagesTree(Connection conn, Document doc, Long userId,
        Counter c) throws SQLException
    {
        logger.debug("+");
        Element rootElement = doc.getDocumentElement();
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(
            " SELECT * FROM page " +
            " WHERE NOT category IN('page', 'popup') " +
            " AND site_id = " + Env.getSiteId() +
            " ORDER BY title ");
        Element element = Env.createDomElement(doc, "class");
        element.setAttribute("id", String.valueOf(c.inc()));
        element.setAttribute("type", "special");
        element.setAttribute("top", "true");
        element.setAttribute("title", "Messages");
        rootElement.appendChild(element);
        while (res.next()) {
            Long containerId = 
                makeLong(res.getLong("container_id"));
            if (SecurityGuard.canView(userId, containerId)) {
                Element pageElement = Env.createDomElement(doc, "page");
                setPageAttributes(pageElement, res);
                element.appendChild(pageElement);
            }
        }
        res.close();
        stmt.close();
        logger.debug("-");
    }

    private void buildModulesTree(Connection conn, Document doc, Long userId,
        Counter c) throws SQLException
    {
        logger.debug("+");
        Element rootElement = doc.getDocumentElement();
        Statement stmt = conn.createStatement();
        PreparedStatement listStmt = conn.prepareStatement(
            "select id from list where module_id=?");
        PreparedStatement pagesStmt = conn.prepareStatement(
            " SELECT * FROM page " +
            " WHERE contents LIKE ? " +
            " AND site_id = " + Env.getSiteId());

        ResultSet res = stmt.executeQuery("SELECT * FROM module where site_id = " + Env.getSiteId());

        Element element = Env.createDomElement(doc, "class");
        element.setAttribute("id", String.valueOf(c.inc()));
        element.setAttribute("top", "true");
        element.setAttribute("type", "modules");
        element.setAttribute("title", "Modules");
        rootElement.appendChild(element);

        while (res.next()) {
            if (Module.isActive(res.getTimestamp("golive"),
                res.getTimestamp("expired")))
            {
                Element moduleElement =
                    Env.createDomElement(doc, "class");
                moduleElement.setAttribute("id", String.valueOf(c.inc()));
                moduleElement.setAttribute("module_id",
                    String.valueOf(res.getLong("id")));
                moduleElement.setAttribute("type", "module");
                moduleElement.setAttribute("title", res.getString("name"));

                listStmt.setInt(1, res.getInt("id"));
                ResultSet listRes = listStmt.executeQuery();
                while (listRes.next()) {
                    String likeStr = "%list_id=" + listRes.getInt("id") + "%"; 
                    pagesStmt.setString(1, likeStr);
                    ResultSet pagesRes = pagesStmt.executeQuery();                   
                    while (pagesRes.next()) {
                        Long containerId =
                            makeLong(pagesRes.getLong("container_id"));
                        if (SecurityGuard.canView(userId, containerId)) {
                            Element pageElement = 
                                Env.createDomElement(doc, "page");
                            setPageAttributes(pageElement, pagesRes);
                            pageElement.setAttribute("list-id",
                                String.valueOf(listRes.getInt("id")));
                            moduleElement.appendChild(pageElement);
                        }
                    }
                    pagesRes.close();
                }
                if (moduleElement.hasChildNodes())
                    element.appendChild(moduleElement);
                listRes.close();
            }
        }

        res.close();
        stmt.close();
        listStmt.close();
        pagesStmt.close();
        logger.debug("-");
    }

    private void buildUnlinkedPages(Connection conn, Document doc, Long userId,
        Counter c) throws SQLException
    {
        logger.debug("+");
        Element rootElement = doc.getDocumentElement();

        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(
        	" SELECT page.* FROM page " + 
        	" WHERE page.id  NOT IN " +
        	" (SELECT menu.page_id FROM menu WHERE menu.page_id IS NOT NULL )" +        	
        	" AND page.site_id = " + Env.getSiteId() + 
        	" ORDER BY lang_id, title");

        Element element = Env.createDomElement(doc, "class");
        element.setAttribute("id", String.valueOf(c.inc()));
        element.setAttribute("type", "unlinked");
        element.setAttribute("top", "true");
        element.setAttribute("title", "Unlinked Pages");
        rootElement.appendChild(element);
        
        while (res.next()) {
            Long containerId = 
                makeLong(res.getLong("container_id"));
            if (SecurityGuard.canView(userId, containerId)) {
                Element pageElement = 
                    Env.createDomElement(doc, "page");
                setPageAttributes(pageElement, res);
                element.appendChild(pageElement);
            }
        }
        res.close();
        stmt.close();
        logger.debug("-");
    }

    private void setPageAttributes(Element pageElement, ResultSet pagesSet)
        throws SQLException
    {
        logger.debug("+");
        pageElement.setAttribute("id", "" + pagesSet.getLong("id"));
        setLangCodeAttr(pageElement, pagesSet);
        pageElement.setAttribute("filename", pagesSet.getString("filename"));
        pageElement.setAttribute("title", pagesSet.getString("title"));
        pageElement.setAttribute("category", pagesSet.getString("category"));
        pageElement.setAttribute("protected", pagesSet.getString("protected"));
        logger.debug("-");
    }
    
    private void setLangCodeAttr(Element element, ResultSet rs) {
        logger.debug("+");
        try {
             element.setAttribute("lang_code", Language.findById(
                 makeLong(rs.getLong("lang_id"))).getCode());
         } catch (Exception ex) {
             logger.error("Language not found " + ex);
         }
         logger.debug("-");
    }
    
    public static Long makeLong(long value) {
        return value > 0 ? new Long(value) : null;
    }
}

class Counter {
    private int counter;
    public int inc() { return ++counter; }
}
