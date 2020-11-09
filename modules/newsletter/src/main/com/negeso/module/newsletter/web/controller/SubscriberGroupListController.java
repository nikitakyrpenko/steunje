/*
 * @(#)Id: GroupListController.java, 25.02.2008 11:53:06, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.service.SubscriberGroupService;
import com.negeso.module.newsletter.service.SubscriberService;
import com.negeso.SpringConstants;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriberGroupListController extends MultiActionController {

	private static final Logger logger = Logger.getLogger(SubscriberGroupListController.class); 
	
	private SubscriberGroupService subscriberGroupService;
	
	public void setSubscriberGroupService(SubscriberGroupService subscriberGroupService) {
		this.subscriberGroupService = subscriberGroupService;
	}

	public ModelAndView showGroups(HttpServletRequest request, HttpServletResponse response){
		logger.debug("+");
		
		List<SubscriberGroup> groups = subscriberGroupService.listOrderedGroups();
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		for (SubscriberGroup subscriberGroup : groups) {
			subscriberGroup.setLang_id(language.getId());
		}
		
		RequestUtil.getHistoryStack(request).push( new Link("Group list",
				"/admin/nl_grouplist", false, -1) );
		
		logger.debug("-");
		return new ModelAndView("nl_grouplist", "grouplist", groups);
	}

	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response)
				throws Exception{
		logger.debug("+");

		Long id = ServletRequestUtils.getLongParameter(request, "id");
		subscriberGroupService.delete(id);

		logger.debug("-");
		return showGroups(request, response);
	}
	
	public ModelAndView changeDirection(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		boolean direction = ServletRequestUtils.getBooleanParameter(request, "direction", false);
		
		subscriberGroupService.changeDirection(id, direction);
		
		logger.debug("-");
		return showGroups(request, response);
	}
	
}
