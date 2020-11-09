/*
 * @(#)Id: SubscriberController.java, 25.02.2008 17:56:00, Dmitry Fedotov
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
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.PageNavigator;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.service.SubscriberGroupService;
import com.negeso.module.newsletter.service.SubscriberSearchInfo;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriberListController extends MultiActionController {

	private static final Logger logger = Logger.getLogger(SubscriberListController.class);
	
	private static String CONTROLLER_MAPPING = "/admin/nl_subscriberslist";
	
	private SubscriberService subscriberService;
	private SubscriberGroupService subscriberGroupService;
	
	public ModelAndView showSubscribers(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		logger.debug("+");
		
		Language language = NegesoRequestUtils.getInterfaceLanguage(req);
		
		Long groupId = null;

        String strGroupId = req.getParameter("groupId");
        if (strGroupId != null && !"".equals(strGroupId)) {
            groupId = Long.valueOf(strGroupId);
        }

		String query = ServletRequestUtils.getStringParameter(req, "query", null);
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		Long sortAttributeId = ServletRequestUtils.getLongParameter(req, "sortAttributeId", 0L);
		
		req.getSession().setAttribute("groupId", groupId);
		req.getSession().setAttribute("query", query);
		req.getSession().setAttribute("pid", pid);
		req.getSession().setAttribute("sortAttributeId", sortAttributeId);
		
		List<SubscriberGroup> groups = subscriberGroupService.listAllSubscriberGroups(language.getId());

		SubscriberSearchInfo searchInfo = new SubscriberSearchInfo(groupId, query, true);
		searchInfo.setAttributeTypeId(sortAttributeId);
		int count = subscriberService.countSubscribers(searchInfo);
		
		PageNavigator pageNavigator = new PageNavigator(
				Configuration.getPagingPortionSize(),
				Configuration.getPagingItemsPerPage(),
				count,
				pid.intValue(),
				CONTROLLER_MAPPING
			);
		pageNavigator.checkAndPutAdditionalParam("groupId", strGroupId);
		pageNavigator.checkAndPutAdditionalParam("query", query);
		pageNavigator.checkAndPutAdditionalParam("sortAttributeId", sortAttributeId > 0 ? sortAttributeId + "" : "");
		
		RequestUtil.getHistoryStack(req).push( new Link("Subscribers list",
				CONTROLLER_MAPPING, false, 0) );
		logger.debug("-");
		return new PreparedModelAndView("nl_subscriberslist").
				addToModel("subscribers", subscriberService.listSubscriber(searchInfo, pageNavigator.getCorrectedCurrentPid(),
						pageNavigator.getRecordsPerPage())).
				addToModel("groups", groups).
				addToModel("pageNavigator", pageNavigator).
				get();
	}
	
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		
		subscriberService.delete(subscriberService.findById(id));
		
		RequestUtil.getHistoryStack(req).push( new Link("Subscribers list",
				CONTROLLER_MAPPING, false, -1) );
		
		logger.debug("-");
		return showSubscribers(req, resp);
	}
	
	public ModelAndView deleteFromStatistics(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		logger.debug("+");
		
		String param = ServletRequestUtils.getStringParameter(req, "param");
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		
		Long pid = ServletRequestUtils.getLongParameter(req, "pid");
		
		subscriberService.delete(subscriberService.findById(id));
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_statistics?action=" + param + "&id=" + pid
				+ "&status=not_sent");
}
	
	public ModelAndView deleteSubscriptionRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		
		subscriberService.delete(
				subscriberService.findById(id));
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_statistics?action=showSubscriptionRequests&pid=" + pid);
}

	public ModelAndView confirmSubscribe(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		logger.debug("+");		
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		Subscriber subscriber = subscriberService.findById(id);
		subscriber.setActivated(true);
		subscriberService.save(subscriber);		
		RequestUtil.getHistoryStack(req).push( new Link("Subscribers list",
				CONTROLLER_MAPPING, false, -1) );
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_statistics?action=showSubscriptionRequests&pid=" + pid);
	}

	public ModelAndView unSubscribe(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		Long publicationId = ServletRequestUtils.getLongParameter(req, "publicationId");
		
		subscriberService.unSubscribe(id);
		
		RequestUtil.getHistoryStack(req).push( new Link("Subscribers list",
				CONTROLLER_MAPPING, false, -1) );
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_statistics" +
			"?id=" + publicationId + "&status=bounced");
	}
	
	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public SubscriberGroupService getSubscriberGroupService() {
		return subscriberGroupService;
	}

	public void setSubscriberGroupService( SubscriberGroupService service) {
		this.subscriberGroupService = service;
	}

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

}
