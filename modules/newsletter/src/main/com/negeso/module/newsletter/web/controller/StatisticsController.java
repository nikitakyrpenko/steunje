/*
 * @(#)Id: StatisticsController.java, 27.02.2008 18:12:00, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.PageNavigator;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.MailingState;
import com.negeso.module.newsletter.service.StatisticsService;
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
public class StatisticsController extends MultiActionController {

	private static final Logger logger = Logger.getLogger(StatisticsController.class);
	
	private final String PAGE_ID = "pid";
	
	private static String CONTROLLER_MAPPING = "/admin/nl_statistics";
	
	private StatisticsService statisticsService;
	private SubscriberService subscriberService;
	
	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public void setStatisticsService(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}

	public ModelAndView showStatistics(HttpServletRequest request, 
			HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		
		String status = ServletRequestUtils.getStringParameter(request, "status", "sent");
		Long pid = getPageIdFormRequest(request);
		
		MailingState state = statisticsService.getMailingState(status);
		
		RequestUtil.getHistoryStack(request).push( new Link("Mail statistics", 
				CONTROLLER_MAPPING + "?id=" + id + "&status=" + status + "&pid=" + pid, false) );

		if (state == null)
			return new PreparedModelAndView("nl_statistics").get();
		
		logger.debug("-");
		return new PreparedModelAndView("nl_statistics").
			addToModel("statlists", statisticsService.
					listSubscriberStatus(id, state.getId(), pid.intValue())).
			addToModel("pageNavigator", statisticsService.
					getPublicationPageNavigator(id, state.getId(), pid.intValue())).get();
	}
	
	public ModelAndView showCategoryStatistics(HttpServletRequest request, 
			HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		
		String status = ServletRequestUtils.getStringParameter(request, "status", "sent");
		Long pid = getPageIdFormRequest(request);
		
		MailingState state = statisticsService.getMailingState(status);
		
		RequestUtil.getHistoryStack(request).push( new Link("Mail statistics", 
				CONTROLLER_MAPPING + "?showCategoryStatistics?id=" + id + 
				"&status=" + status + "&pid=" + pid, false) );

		if (state == null)
			return new PreparedModelAndView("nl_statistics").get();
		
		logger.debug("-");
		return new PreparedModelAndView("nl_statistics").
			addToModel("statlists", statisticsService.
					listSummerySubscriberStatusByCategory(id, state.getId(), pid.intValue())).
			addToModel("pageNavigator", statisticsService.
					getCategoryPageNavigator(id, state.getId(), pid.intValue())).get();
	}
	
	public ModelAndView showSubscriptionRequests(HttpServletRequest request, 
			HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		Long pid = getPageIdFormRequest(request);
		request.getSession().setAttribute("pid", pid);
		
		SubscriberSearchInfo searchInfo = new SubscriberSearchInfo(null, null, false);
		int count = subscriberService.countSubscribers(searchInfo);
		
		PageNavigator pageNavigator = new PageNavigator(
				Configuration.getPagingPortionSize(),
				Configuration.getPagingItemsPerPage(),
				count,
				pid.intValue(),
				CONTROLLER_MAPPING
			);
		pageNavigator.checkAndPutAdditionalParam("action", "showSubscriptionRequests");
		
		RequestUtil.getHistoryStack(request).push( new Link("Subscription requests", 
				CONTROLLER_MAPPING + "?action=showSubscriptionRequests", false) );

		logger.debug("-");
		return new PreparedModelAndView("nl_requests").
			addToModel("subscribers", subscriberService.listSubscriber(searchInfo, pageNavigator.getCorrectedCurrentPid(),
					pageNavigator.getRecordsPerPage())).
					addToModel("pageNavigator", pageNavigator).
					get();
		
	}
	
	private Long getPageIdFormRequest(HttpServletRequest request){
		return ServletRequestUtils.getLongParameter(request, PAGE_ID, 0L);
	}
}
