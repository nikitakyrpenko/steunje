/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.friendly_url;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.module.AbstractFriendlyUrlHandler;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.page.command.GetEditablePageCommand;
import com.negeso.framework.page.command.GetPageCommand;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ProcessFriendlyUrlCommand extends AbstractCommand {
	
	private static Logger logger = Logger.getLogger(ProcessFriendlyUrlCommand.class);
	
	public static final String NAME = "process-pm-friendly-url-command";

	@Override
	public ResponseContext execute() {
		
		RequestContext request = getRequestContext();
		String firstParam =  request.getString(FriendlyUrlHandler.INPUT_FIRST_PARAM, "");
		String secondParam = request.getString(FriendlyUrlHandler.INPUT_SECOND_PARAM, "");
//		Long langId = request.getSessionData().getLanguage().getId();
		String pageClass = request.getParameter(FriendlyUrlHandler.PAGE_CLASS);
		String pageComponent = request.getParameter(FriendlyUrlHandler.PAGE_COMPONENT);
		
		if (StringUtils.isNotEmpty(firstParam)) {
			try {
				firstParam += secondParam;
				
				String[] params = FriendlyUrlService.findByLink(firstParam.toLowerCase(), UrlEntityType.valueOf(request.getParameter(FriendlyUrlHandler.ENTITY_TYPE)));
				request.setParameter(request.getParameter(FriendlyUrlHandler.PARAM_NAME), params[0]);
				
			} catch (ObjectNotFoundException e) {
				String uri = request.getParameter(RequestContext.REQUEST_URI);
				String filename = uri.substring(uri.lastIndexOf("/") + 1);
				logger.error("Friendly url not found. Showing content of " + filename);
				request.setParameter(FriendlyUrlHandler.INPUT_FILENAME, filename);
			}
		}
		
		String switchToLang = request.getParameter("switchToLang");
		if (StringUtils.isNotEmpty(switchToLang)) {
			PageH page = new PageH();
			try {
				page = PageService.getInstance().findByClassAndObligatoryComponent(pageClass, Language.findByCode(switchToLang).getId(), 
						request.getParameter(FriendlyUrlHandler.PAGE_COMPONENT));
			} catch (ObjectNotFoundException e) {
				logger.error(" Page not found by class: " + pageClass + " and component name: " +
						pageComponent + ", langCode: " + switchToLang ,e);
			}
			request.setParameter(FriendlyUrlHandler.INPUT_FILENAME, page.getFilename());
		}
		
		AbstractCommand command = new GetPageCommand();
		if (request.getParameter(RequestContext.REQUEST_URI).contains(AbstractFriendlyUrlHandler.ADMIN)) {
			command = new GetEditablePageCommand();
		};
		command.setRequestContext(request);
		clearServiceAttributes(request);
		return command.execute();
	}
	
	private void clearServiceAttributes (RequestContext request) {
		request.getParameterMap().remove(FriendlyUrlHandler.PAGE_CLASS);
		request.getParameterMap().remove(FriendlyUrlHandler.PAGE_COMPONENT);
		request.getParameterMap().remove(FriendlyUrlHandler.PARAM_NAME);
		request.getParameterMap().remove(FriendlyUrlHandler.INPUT_FIRST_PARAM);
		request.getParameterMap().remove(FriendlyUrlHandler.INPUT_SECOND_PARAM);
		request.getParameterMap().remove(FriendlyUrlHandler.ENTITY_TYPE);
	}

}

