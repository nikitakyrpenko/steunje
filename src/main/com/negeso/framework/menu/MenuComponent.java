/*
 * @(#)$Id: MenuComponent.java,v 1.4, 2007-03-22 17:58:57Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.menu;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.menu.service.MenuService;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;


/**
 * 
 * @author      Stanislav Demchenko
 * @version     $Revision: 5$
 */
@SuppressWarnings("rawtypes")
public class MenuComponent extends AbstractPageComponent {
    
    
    private static Logger logger = Logger.getLogger(MenuComponent.class);
    
    public static final String ATTR_TRUNCATE = "truncate";
    public static final String ATTR_SELECTED_MENU_ID = "selectedMenuId";
    public static final String ATTR_MENU_BEHAVIOR = "is2ClickBehaviorMenu";
    public static final String INPUT_MENU_ID = "miId";
    
	public Element getElement(
            Document document,
            RequestContext request,
            Map parameters)
    {
        IMenuService menuService = MenuService.getSpringInstance();
        
        Element menu= null;
        try {
        	Document menuDoc = menuService.getMenuXml(request.getSession().getLanguage(), request.getSession().getUser());
        	menu = menuDoc.getDocumentElement();
		} catch (Exception e) {
			logger.error("Menu error: ", e);
		}
		
        if(menu != null) {
            Node child = document.importNode( menu, true );
            Long selectedMenuId = menuService.determinateSelectedMenuId(getFilename(request), request.getLong(INPUT_MENU_ID));
            Xbuilder.setAttr((Element)child.getFirstChild(), ATTR_SELECTED_MENU_ID, selectedMenuId);
			Xbuilder.setAttr((Element)child.getFirstChild(), ATTR_TRUNCATE, this.isVisitorMode(parameters));
			Xbuilder.setAttr((Element)child.getFirstChild(), ATTR_MENU_BEHAVIOR, Env.getProperty("MENU_BEHAVIOR","false"));
            return (Element) child;
        } else {
            logger.warn("- Menu is empty");
            return getStubElement(document, request, parameters);
        }
    }
    
        
    /**
     * @param request
     * @param filename
     * @return
     */
    private String getFilename(RequestContext request)
    {
        logger.debug("+");
        try {
            PageH page =
                PageService.getInstance().findById(new Long(request.getSession().getPageId()));
            logger.debug("-");
            return page.getFilename();
        } catch (Exception e) {
            logger.warn("- page not found by id", e);
            return "";
        }
    }


    public Element getStubElement(
            Document doc,
            RequestContext request,
            Map parameters)
    {
        logger.debug("+ -");
        return Env.createDomElement(doc, "menu");
    }
    
    
}

