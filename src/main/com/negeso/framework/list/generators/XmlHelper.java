/*
 * Created on 27.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.framework.list.generators;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.ImageResource;

/**
 * @author Sergiy Oliynyk
 */
public class XmlHelper {
    
    private static Logger logger = Logger.getLogger(XmlHelper.class);
    
    private XmlHelper() {}

    private static SimpleDateFormat formatter =
        new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Appens an XML of article to List Item element 
     * @param listItemElement
     * @param rs
     */
    public static Element getArticleElement(Document doc, String name, 
        long articleId, String articleClass, String articleHead, 
        String articleText)
    {
        logger.debug("+");
        // summary or details
        Element element = Env.createDomElement(doc, name);
        
        // negeso article
        Element articleElement = Env.createDomElement(doc, "article");
        element.appendChild(articleElement);
        articleElement.setAttribute("id", String.valueOf(articleId));
        articleElement.setAttribute("class", articleClass);

        // head of the article
        Element headElement = Env.createDomElement(doc, "head");
        articleElement.appendChild(headElement);
        headElement.appendChild(doc.createTextNode(articleHead));
        
        // text of the article
        Element textElement = Env.createDomElement(doc, "text");
        articleElement.appendChild(textElement);
        textElement.appendChild(doc.createTextNode(articleText));

        logger.debug("-");
        return element;
    }

    /**
     * Sets a page link atribute for the List Item element
     * @param itemElement
     * @param value
     * @throws CriticalException
     */
    public static void setLinkAttribute(Element itemElement, String value) {
        logger.debug("+");
        if (value != null && value.length() > 0) {
            itemElement.setAttribute("href", value);
            try {
                String title = PageService.getInstance().findByFileName(value).getTitle();
                itemElement.setAttribute("title", title);
            } 
            catch (Exception ex) {
                logger.error("Page not found "  + value);
            }
        }
        logger.debug("-");
    }
    
    /**
     * Sets an image link atribute for the List Item element
     * @param itemElement
     * @param value
     * @throws CriticalException
     */
    public static void setImageLinkAttribute(Element itemElement, 
        String value)
    {
        logger.debug("+");
        // add imageLink path, height and width
        if (value != null) {
            try {
                ImageResource res = Repository.get().getImageResource(value);
                itemElement.setAttribute("img_width",
                    res.getWidth().toString());
                itemElement.setAttribute("img_height",
                    res.getHeight().toString());
                itemElement.setAttribute("imageLink", value);
            }
            catch(RepositoryException ex){
                logger.error("Cannot set image attributes ", ex);
            }
        }
        logger.debug("-");
    }

    /**
     * Sets a thumbnail link atribute for the List Item element
     * @param itemElement
     * @param value
     * @throws CriticalException
     */
    public static void setThumbnailLinkAttribute(Element itemElement, 
        String value)
    {
        logger.debug("+");
        // add imageLink path, height and width
        if (value != null) {
            try {
                ImageResource res = Repository.get().getImageResource(value);
                itemElement.setAttribute(
                    "th_width", res.getWidth().toString());
                itemElement.setAttribute(
                    "th_height", res.getHeight().toString());
                itemElement.setAttribute("thumbnailLink", value);
            }
            catch(RepositoryException ex) {
                logger.error("Cannot set thumbnail attributes ", ex);
            }
        }
        logger.debug("-");
    }
    
    /*
     * Set user's rights
     */
    public static void setRightsAttributes(Element element, Long userId, 
        Long containerId)
    {
        logger.debug("+");
        element.setAttribute("canManage", 
            String.valueOf(SecurityGuard.canManage(userId, containerId)));
        element.setAttribute("canContribute", 
            String.valueOf(SecurityGuard.canContribute(userId, containerId)));
        logger.debug("+");
    }

    public static void setDateAttribute(Element element, String name, 
        Timestamp value)
    {
        logger.debug("+");
        if (value != null) {
            String dateStr = value.toString();
            element.setAttribute(name,
                dateStr.substring(0, dateStr.indexOf(' ')));
        }
        logger.debug("-");
    }
    
    public static void setTimeStampAttribute(Element element, String name, 
            Timestamp value)
        {
            logger.debug("+");
            if (value != null) {
            	element.setAttribute(name,
            	Env.formatSimpleRoundDate(new Date(value.getTime())));
            }
            logger.debug("-");
        }

    public static void setDateAttribute(Element element, String name, 
        String value)
    {
        if (value != null) {
            element.setAttribute(name, value);
        }
    }
    
	public static String formatDate(java.util.Date date)
    {
        synchronized(formatter){
            return date != null ? formatter.format(date) : null;
        }
    }
}
