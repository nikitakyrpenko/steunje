/*
 * @(#)$Id: GetMenuCommand.java,v 1.6, 2007-03-26 10:19:28Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
 
package com.negeso.framework.menu.command;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.menu.service.MenuFacade;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.user.domain.Container;
import com.negeso.module.user.domain.DefaultContainer;
 
/**
 *
 * Builds the menu tree. This class replaced MenuEditorView after W/CMS v 2.4.1.
 * 
 * @author		  E.Dzhentemirova
 * @version       $Revision: 7$
 */
public class GetMenuCommand extends AbstractCommand {
    
    
    private static Logger logger = Logger.getLogger(GetMenuCommand.class);
    
    
    public ResponseContext execute()
    {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_ACCESS_DENIED);
        SessionData session = request.getSession();
        User user = session.getUser();
        String role = SecurityGuard.resolveRole(user, null);        
        if (!SecurityGuard.canManage(user, null)) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied");
            return response;
        }
        PageService pageService = PageService.getInstance(); 
        PageH curPage = null;        
        try {
            curPage = pageService.findById(new Long(session.getPageId()));
        } catch (Exception e) {
            logger.warn("Cannot find page by id " + session.getPageId());
        }
        Document menuDoc = MenuFacade.getMenuXml(
            session.getLanguage(), curPage.getFilename(), user, false, null);
        if (menuDoc == null) {
        	response.setResultName(RESULT_SUCCESS);
            response.getResultMap().put(OUTPUT_XML, menuDoc);
            logger.debug("-");
            return response;
        }
        Element elMenu = menuDoc.getDocumentElement();
        elMenu.setAttribute("role-id-max", role);
        elMenu.setAttribute("role-id", role);
        elMenu.setAttribute("menu_max_depth", Env.getProperty("menu.max.depth"));
        elMenu.setAttribute("top_items_limit", Env.getProperty("menu.top.items.limit"));
        addContainersToModel(elMenu);
        response.setResultName(RESULT_SUCCESS);
        response.getResultMap().put(OUTPUT_XML, menuDoc);
        logger.debug("-");
        return response;
    }

    void addContainersToModel(Element elMenu) {
        Element elContext = Xbuilder.addEl(elMenu, "context", null);
        try {
            Element elContainers = Xbuilder.addEl(elContext, "containers", null);
            Iterator iter = Container.getContainers(Env.getSiteId()).iterator();
            
            int count = 0;
            while (iter.hasNext()) {
                Container container = (Container) iter.next();
                Element elContainer =
                    Xbuilder.addEl(elContainers, "container", null);
                Xbuilder.setAttr(elContainer, "id", "" + container.getId());
                Xbuilder.setAttr(elContainer, "name", "" + container.getName());
                if (DefaultContainer.isMyId(container.getId())) {
                	Xbuilder.setAttr(elContainer, "default", "1");
                }
                count++;
            }
            elContainers.setAttribute("totaly", "" + count);
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
        }
    }
    
}

