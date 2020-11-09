/*
 * @(#)$Id: GetMenuReportCommand.java,v 1.5, 2006-02-10 16:17:35Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
 
package com.negeso.framework.menu.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.ResourceMap;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.menu.service.MenuFacade;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.user.domain.Container;
 
/**
 *
 * 
 * @author        Stanislav 
 * @version       $Revision: 6$
 */
public class GetMenuReportCommand extends AbstractCommand {
    
    
    private static final String OUTPUT_EXPIRES =
        AbstractHttpView.HEADER_EXPIRES;
    
    
    private static final String OUTPUT_MIME_TYPE =
        AbstractHttpView.HEADER_MIME_TYPE;
    
    
    private static final String OUTPUT_FILE =
        AbstractHttpView.KEY_FILE;
    
    
    private static Logger logger = Logger.getLogger(GetMenuReportCommand.class);

    private DOMXPath xMainMenu;
    
    private DOMXPath xMenu;
    
    private DOMXPath xMenuItem;
    
    
    private Writer out;


    private DOMXPath xTitle;


    private DOMXPath xHref;
    
    
    private DOMXPath xPublish;
    
    
    private DOMXPath xExpired;
    
    
    private DOMXPath xContainerId;
    
    
    private static final String BORDER = " | ";
    
    
    /** Width of a column "Title" */
    private static final int W_TITLE = 50;
    
    
    /** Width of a column "Published" */
    private static final int W_PUBLISHED = 10;
    
    
    /** Width of a column "Expires" */
    private static final int W_EXPIRES = 10;
    
    
    /** Width of a column "Container" */
    private static final int W_CONTAINER = 20;
    
    
    /** Width of a column "Url" */
    private static final int W_URL = 30;
    
    
    /** Total width of all columns, including delimiters between them */
    private static final int W_TOTAL =
        W_TITLE + W_PUBLISHED + W_EXPIRES + W_CONTAINER + W_URL +
        BORDER.length() * 4;
    
    
    private static final String BR = "\r\n";
    
    
    private static final String HLINE = BR + repeat('-', W_TOTAL) + BR;
    
    
    private static final String heading =
        pad("Title", ' ', W_TITLE) + BORDER +
        pad("Published", ' ', W_PUBLISHED) + BORDER +
        pad("Expires", ' ', W_EXPIRES) + BORDER +
        pad("Container", ' ', W_CONTAINER) + BORDER +
        pad("URL", ' ', W_URL);
    
    
    private final HashMap containers = new HashMap();
        
    
    public ResponseContext execute()
    {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_ACCESS_DENIED);
        SessionData session = request.getSession();
        User user = session.getUser();
        if(!SecurityGuard.canManage(user, null)) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied");
            return response;
        }
        
        File file = new File(ResourceMap.getRealPath("MENU_REPORT_TXT"));
        try {
            cacheContainers();
            initXPath();
            out =
                new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(file), "UTF-8"));
            out.write(HLINE);
            out.write(heading);
            out.write(HLINE);
            
            Iterator iter = Language.getItems().iterator();

            while (iter.hasNext()) {
                Language lang = (Language) iter.next();
                
                Document menuDoc = MenuFacade.getMenuXml(
                		lang, null, user, true, null);
                
                ///Document menuDoc = menuService.getMenuXml(lang.getCode(), null, user, false, null);
                if ( menuDoc == null ) continue;
                
                out.write(lang.getLanguage());
                out.write(HLINE);
                Element menuEl = (Element)xMenu.selectSingleNode(menuDoc.getDocumentElement());                
                processMenu(menuEl, 0);
                out.write(HLINE);
            }
            out.flush();
            out.close();
            
            response.setResultName(RESULT_SUCCESS);
            Map map = response.getResultMap();
            map.put(OUTPUT_FILE, file);
            map.put(OUTPUT_EXPIRES, new Long(0));
            map.put(OUTPUT_MIME_TYPE, "text/plain; charset=UTF-8");
            logger.debug("-");
            return response;
        } catch (Exception e) {
            logger.error("- Exception", e);
            return response;
        } finally {
            if (out != null) {
                try { out.close(); }
                catch (Exception e) { logger.warn("Cannot close 'out'", e); }
            }
        }
    }


    private void processMenuItem(Element menuItem, int level) throws Exception
    {
        logger.debug("+");
        
        String indent = repeat(' ', level * 2);
        String title = xTitle.valueOf(menuItem);
        out.write(pad(indent + title, ' ', W_TITLE));
        out.write(BORDER);
        String date = xPublish.valueOf(menuItem);
        if(date != null && date.length() > 10) {
            date = date.substring(0, 10);
        }
        out.write(pad(date, ' ', W_PUBLISHED));
        out.write(BORDER);
        date = xExpired.valueOf(menuItem);
        if(date != null && date.length() > 10) {
            date = date.substring(0, 10);
        }
        out.write(pad(date, ' ', W_EXPIRES));
        out.write(BORDER);
        String contName = findContainerName(xContainerId.valueOf(menuItem));
        out.write(pad(contName, ' ', W_CONTAINER));
        out.write(BORDER);
        out.write(pad(xHref.valueOf(menuItem), ' ', W_URL));
        out.write("\r\n");
        
        logger.debug("-");
    }
    
    
    private void processMenu(Element menu, int level) throws Exception
    {
        logger.debug("+");
        List<Element> menuItems = (List<Element>)xMenuItem.selectNodes(menu);
        Iterator iterator = menuItems.iterator();
        while (iterator.hasNext()) {
            Element menuItem = (Element) iterator.next();
            processMenuItem(menuItem, level);
            processMenu(menuItem, level+1);
        }
        logger.debug("-");
    }
    
    
    /**
     * Complement short strings with filler (typically space character).
     * If <b>value</b> is equal or longer than <b>width</b>, it returns without
     * changes. Otherwise, <b>width - value.length()</b> fillers are appended. 
     *  */
    private static String pad(String value, char filler, int width)
    {
        logger.debug("+");
        if(value == null) {
            value = "";
        }
        if(value.length() >= width) {
            logger.debug("- extra long");
            return value;
        }
        logger.debug("-");
        return value + repeat(filler, width - value.length());
    }
    
    
    /** Constructs a string of <b>count</b> repetitions of <b>filler</b> */
    private static String repeat(char filler, int count)
    {
        logger.debug("+");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buffer.append(filler);
        }
        logger.debug("-");
        return buffer.toString();
    }
    
    
    /** Pre-compiles XPath expressions */
    void initXPath() throws JaxenException
    {
        logger.debug("+");
        xMainMenu = new DOMXPath("negeso:main_menu");
        xMainMenu.addNamespace(Env.NEGESO_PREFIX, Env.NEGESO_NAMESPACE);
        xMenu = new DOMXPath("negeso:menu");
        xMenu.addNamespace(Env.NEGESO_PREFIX, Env.NEGESO_NAMESPACE);
        xMenuItem = new DOMXPath("negeso:menu_item");
        xMenuItem.addNamespace(Env.NEGESO_PREFIX, Env.NEGESO_NAMESPACE);
        xTitle = new DOMXPath("@title");
        xHref = new DOMXPath("@href");
        xPublish = new DOMXPath("@publishDate");
        xExpired = new DOMXPath("@expiredDate");
        xContainerId = new DOMXPath("@container_id");
        logger.debug("-");
    }
    
    
    /**
     * Loads information about containers into a map for subsequent searches. 
     * 
     * @throws CriticalException if a serious db error occurs
     */
    private void cacheContainers() throws CriticalException
    {
        logger.debug("+");
        Iterator cIter = Container.getContainers(Env.getSiteId()).iterator();
        while (cIter.hasNext()) {
            Container container = (Container) cIter.next();
            containers.put(container.getId(), container.getName());
        }
        logger.debug("-");
    }
    
    
    /**
     * Looks for a container identified by id the cached map.
     * 
     * @return  name of the container (if found), or empty string
     */
    String findContainerName(String cid)
    {
        logger.debug("+");
        try {
            String contName = (String) containers.get(Long.valueOf(cid));
            if(contName != null) {
                logger.debug("-");
                return contName;
            } else {
                logger.warn("- container not found");
                return "";
            }
        } catch (Exception e) {
            if(cid == null || cid.equals("")) {
                logger.debug("- the page is not in a container");
                return "";
            } else {
                logger.error("- Exception for container id " + cid, e);
                return "";
            }
        }
    }
    
    
}

