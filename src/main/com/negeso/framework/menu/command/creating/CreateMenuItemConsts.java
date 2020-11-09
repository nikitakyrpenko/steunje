/*
 * @(#)$Id: CreateMenuItemConsts.java,v 1.1, 2007-04-06 11:55:53Z, Volodymyr Snigur$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu.command.creating;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class CreateMenuItemConsts {
	
    /** Defines what shall be created: TYPE_URL, -POPUP, -PAGE, -ALIAS */
    public static final String INPUT_TYPE = "type";
    
    /** Title of new menu item (and of its page, if any) */
    public static final String INPUT_TITLE = "title";
    
    /** Mandatory (except for TYPE_URL) */
    public static final String INPUT_FILENAME = "filename";
    
    /**
     * If set, menu item shall be created in submenu. If neither
     * INPUT_PARENT_MENU, nor INPUT_PARENT_ITEM are set, the menu item shall
     * be created in the special menu
     */
    public static final String INPUT_PARENT_ITEM = "parent_menu_item_id";
    
    /**
     * If set, menu item shall be created this menu. If neither
     * INPUT_PARENT_MENU, nor INPUT_PARENT_ITEM are set, the menu item shall
     * be created in the special menu
     */
    public static final String INPUT_PARENT_MENU = "menu_id";
    
    /** Used to create popups of predefined size */
    public static final String INPUT_POPUP_SIZE = "popup_size";
    
    /** Used to create popups with predefined content */
    public static final String INPUT_ARTICLE_TEXT = "article_text";
    
    /** Page (or popup) publication date */
    public static final String INPUT_PUBLISH = "publish";
    
    /** Page (or popup) expiration date */
    public static final String INPUT_EXPIRE = "expire";
    
    /** Page container */
    public static final String INPUT_CONTAINER_ID = "container_id";
    
    /** Page visibility enforcement */
    public static final String INPUT_VISIBILITY = "force_visibility";

    public static final String INPUT_PAGE_TEMPLATE = "page_template";
    
    /** This parameter is specific to TYPE_URL. */
    public static final String INPUT_LINK = "link";
    
    public static final String INPUT_MULTI_LANGUAGE = "multi_language";
    
    /** Valid menu item types */
    public static final String TYPE_URL = "url";
    public static final String TYPE_POPUP = "popup";
    public static final String TYPE_PAGE = "page";
    public static final String TYPE_ALIAS = "alias";

    public static final String MI_META_DESCRIPTION = "meta_description";
    public static final String MI_META_KEYWORDS = "meta_keywords";
    public static final String MI_PROPERTY_VALUE = "property_value";
    public static final String MI_GOOGLE_SCRIPT = "google_script";
    
    public static final String IS_SEARCH_PARAMETER = "isSearch";

	public static final String DEFAULT_POPUP_SIZE = "400 400";
}

