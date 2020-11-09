/*
 * @(#)$Id: CreateMenuItemCommand.java,v 1.2, 2007-04-11 06:32:06Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu.command.creating;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.menu.IMenuService;
import com.negeso.framework.menu.bo.Menu;
import com.negeso.framework.menu.command.creating.exception.PageExistsException;
import com.negeso.framework.menu.service.MenuService;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.NegesoRequestUtils;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 3$
 *
 */
public class CreateMenuItemCommand extends AbstractCommand {
	
	private static Logger logger = Logger.getLogger(CreateMenuItemCommand.class);
	
	private ResponseContext response = new ResponseContext(); 
	
	private User getUser() {
		return getRequestContext().getSession().getUser();
	}
	
	private Long getContainerId() {
		return getRequestContext().getLong(CreateMenuItemConsts.INPUT_CONTAINER_ID);
	}

	public ResponseContext execute() {
        logger.debug("+");        
    	try {
    		HttpServletRequest request = RequestContext.getServletRequest();
    		IMenuService menuService = MenuService.getSpringInstance();
    		PageService pageService = PageService.getInstance();
    		checkPermissions();
    		Menu menu = new Menu();
    		menu = NegesoRequestUtils.bind(menu, request);    		
    		if (menu.getLink() == null){
    			checkPageExistsException(request.getParameter("filename"));
	    		PageH page = new PageH();
	    		page = NegesoRequestUtils.bind(page, request, Env.SIMPLE_DATE_FORMAT);
	    		String template = request.getParameter("template");
	    		if ("".equals(template)) template = "simple";    		
	    		pageService.createPage(page, template);
	    		menu.setPageId(page.getId());
    		}    		
    		menu.setSiteId(Env.getSiteId());
    		menu.setOrder(menuService.getNextOrder(menu.getParentId()));    		
    		menuService.saveMenu(menu);
            response.setResultName(RESULT_SUCCESS);
            getResponseMap().put(OUTPUT_XML, menu);
    	} catch (PageExistsException e) {    		
    		response.setResultName(RESULT_SUCCESS);
    		getResponseMap().put(    				
    				OUTPUT_XML, 
    				Xbuilder.createTopEl("pageExistsException", e.getMessage()).getOwnerDocument()
    		);
			logger.error("Exception - " + e.getMessage(), e);    	
    	} catch (Exception e) {    		
    		response.setResultName(RESULT_FAILURE);
			logger.error("Exception - " + e.getMessage(), e);
    	}	
        logger.debug("-");
        return response;
	}

	private void checkPermissions() throws AccessDeniedException {
      if (!SecurityGuard.canManage(getUser() , getContainerId())) {
            logger.debug("- You are not authorized for this operation");
            getResponseMap().put(
                OUTPUT_ERROR, "You are not authorized for this operation");
            throw new AccessDeniedException();
        }
	}
	
	private void checkPageExistsException(String fileName)throws PageExistsException {
		if (fileName != null){
			PageService pageService = PageService.getInstance();		
			PageH page = pageService.findByFileName(fileName);
			if (page != null){
				throw new PageExistsException("Page "
					+ fileName
					+ " already exists");
			}
		}
	}

	private Map<String, Object> getResponseMap() {
		return this.response.getResultMap();
	}

	public ResponseContext getResponse() {
		return response;
	}
	
}

