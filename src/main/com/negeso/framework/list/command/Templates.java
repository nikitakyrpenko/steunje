/*
 * @(#)ListManager.java       @version	30.01.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.list.command;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.negeso.framework.list.domain.List;
import com.negeso.framework.module.Module;

/**
 * @author  Sergiy.Oliynyk
 */
public class Templates {
    
    private static Logger logger = Logger.getLogger(Templates.class);

    private Templates() {}
    
    /**
	 * Retrive xsl document for styling list management of 
	 * particular type.
	 */	
    public static String getListXsl(Connection conn, List list) {
        logger.debug("+");
        if (list == null || list.getModuleId() == null) {
            logger.debug("- Invalid argument"); 
            return null;
        }
        String module = null;
        try {
            module = Module.findById(conn, list.getModuleId()).getName();
        }
        catch (Exception ex) {
            logger.error("-", ex);
            return null;
        }
        if (ModuleConstants.NEWS.equals(module))
            return "NEWSLIST_LIST_XSL";
        if (ModuleConstants.FAQ.equals(module))
            return "FAQ_LIST_XSL";
        if (ModuleConstants.PHOTO_ALBUM.equals(module))
            return "PHOTO_ALBUM_XSL";
        if (ModuleConstants.WEB_POLL.equals(module))
            return "WEBPOLL_XSL";
        logger.debug("-");
        return "NEWSLIST_LIST_XSL";
    }

	/**
	 * Retrive xsl document file name for styling edit list item form of 
	 * particular type.
	 */	
    public static String getEditItemXsl(Connection conn, List list) {
        logger.debug("+");
        if (list == null || list.getModuleId() == null) {
            logger.debug("- Invalid argument"); 
            return null;
        }
        String module = null;
        try {
            module = Module.findById(conn, list.getModuleId()).getName();
        }
        catch (Exception ex) {
            logger.error("-", ex);
            return null;
        }
        if (ModuleConstants.NEWS.equals(module))
            return "NEWS_EDIT_ITEM_XSL";
        if (ModuleConstants.FAQ.equals(module))
            return "FAQ_EDIT_ITEM_XSL";
        if (ModuleConstants.PHOTO_ALBUM.equals(module))
            return "PHOTO_ALBUM_EDIT_ITEM_XSL";
        if (ModuleConstants.WEB_POLL.equals(module))
            return "WEBPOLL_EDIT_ITEM_XSL";
        logger.debug("-");
        return "NEWS_EDIT_ITEM_XSL";
    }
}
