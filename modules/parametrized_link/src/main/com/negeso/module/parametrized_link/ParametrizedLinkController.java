/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.parametrized_link;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.parametrized_link.domain.ParametrizedLink;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class ParametrizedLinkController extends AbstractCommandController {
	
	private static Logger logger = Logger.getLogger(ParametrizedLinkController.class);
	
	private IParametrizedLinkService parametrizedLinkService;
	
    public ParametrizedLinkController() {
    	setCommandClass(ParametrizedLink.class);
    }
	
	private void doSave(Object command, HttpServletRequest request) {
		logger.debug("+");
		ParametrizedLink parametrizedLink = (ParametrizedLink) command;
		logger.debug(parametrizedLink);
		parametrizedLink.setSiteId(Env.getSiteId());
		parametrizedLink.setLangId(SessionData.getLangId(request));
		if (parametrizedLink.getId() != null) {
			doUpdate(parametrizedLink);
		}
		else {
			doAdd(parametrizedLink, request);			
		}
	}
	
	private void doUpdate(ParametrizedLink parametrizedLink) {
		parametrizedLinkService.updateParametrizedLink(parametrizedLink);
	}

	private void doAdd(ParametrizedLink parametrizedLink,
					   HttpServletRequest request) {
		parametrizedLinkService.addParametrizedLink(parametrizedLink);
	}

	private ModelAndView getListModel(HttpServletRequest request) {		
		RequestUtil.getHistoryStack(request).push( new Link("parametrizedLinks.link", "/admin/parametrized_links.html", 0) );
		return new ModelAndView("pl_parametrized_links",
				"parametrizedLinks",
				parametrizedLinkService.getParametrizedLinks(SessionData.getLangId(request)));
	}
	
	private Long getLinkId(HttpServletRequest request) {
		return Long.parseLong(request.getParameter("id"));
	}
	
	private ParametrizedLink getParametrizedLink(HttpServletRequest request) {
		return (request.getParameter("id") == null
				 ? new ParametrizedLink() 
				 : parametrizedLinkService.getParametrizedLink(getLinkId(request)));
	}
	
	private ModelAndView getItemModel(HttpServletRequest request) {
		RequestUtil.getHistoryStack(request).push( new Link("parametrizedLinkDetails.link", "/admin/pl_details.html") );
		return new ModelAndView("pl_details",
					"parametrizedLink",
					getParametrizedLink(request)
					);
	}
	
    @Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException arg3)
			throws Exception {
    	
		logger.debug("+");
		
		String action = request.getParameter("act");
		logger.debug("action = " + action);
		if (action != null) {
			if ( "delete".equals(action) ) {
				doDelete(request);
				return getListModel(request);
				
			} else if ("save".equals(action)) {
				
				doSave(command, request);
				return getListModel(request);
				
			} else if ("new".equals(action)) {
				return getItemModel(request);
				
			} else if ("update".equals(action)) {
				return getItemModel(request); 
			}
		}	
		logger.debug("-");
		return getListModel(request);
    }
	
	private void doDelete(HttpServletRequest request) {
		
		parametrizedLinkService.deleteLink( request.getParameter("id") );
		
	}

	public void setParametrizedLinkService(
			IParametrizedLinkService parametrizedLinkService) {
		
		this.parametrizedLinkService = parametrizedLinkService;
		
	}

}

