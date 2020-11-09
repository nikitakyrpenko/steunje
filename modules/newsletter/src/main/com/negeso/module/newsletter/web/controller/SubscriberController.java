/*
 * @(#)Id: SubscriberController.java, 12.03.2008 15:38:04, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.service.SubscriberGroupService;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriberController extends SimpleFormController {
	
	private static final Logger logger = Logger.getLogger("SubscriberController");

	private SubscriberService subscriberService;
	
	private SubscriberGroupService subscriberGroupService;
	
	public static final String ATTRIBUTE_PREFIX = "attribute_"; // TODO move to Configuration
	public static final String GROUP_PREFIX = "group_";
	
	public SubscriberController(){
		setCommandClass(Subscriber.class);
		setCommandName("subscriber");
	}
	
	public Object formBackingObject(HttpServletRequest request) throws Exception {
		logger.debug("+");
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		Subscriber subscriber = null;
		if (id == null){
			subscriber = new Subscriber();
			RequestUtil.getHistoryStack(request).push( new Link( "New subscriber", 
					"/admin/nl_editsubscriber", false) );
		}else {
			subscriber = subscriberService.findById(id);
			RequestUtil.getHistoryStack(request).push( new Link( 
					subscriber.getEmail(),
					"/admin/nl_editgroup?id=" + id, false) );
		}
		logger.debug("-");
		return subscriber;
	}

	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		logger.debug("+");
		Map<String, Object> model = new HashMap<String, Object>();
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		List<SubscriberGroup> groups = subscriberGroupService.listAllSubscriberGroups(language.getId());
		
		model.put("groups", groups);
		model.put("languages", Language.getItems());
		
		logger.debug("-");
		return model;
	}
	
	@Override
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
			Object command, BindException errors) throws Exception {
		logger.debug("+");

		Subscriber subscriber = (Subscriber) command;
		
		subscriber.getSubscriberGroups().clear();
		
		for (Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext() ;){
			
			String param_name = (String)i.next();
			
			if (param_name.startsWith(ATTRIBUTE_PREFIX)){
				subscriber.setAttribute(param_name.substring(ATTRIBUTE_PREFIX.length()),
                        request.getParameter(param_name));
			}
            else if (param_name.startsWith(GROUP_PREFIX)){
				param_name = param_name.substring(GROUP_PREFIX.length());
				
				SubscriberGroup group = subscriber.getGroupById(Long.valueOf(param_name));
				
				if (group == null){
					group = subscriberGroupService.findById(Long.valueOf(param_name));
				}

				subscriber.getSubscriberGroups().add(group);
			}
		}
		subscriberService.save(subscriber);
		RequestUtil.getHistoryStack(request).push( new Link( "Subscribers list", 
				"/admin/nl_subscriberslist", false) );
		logger.debug("-");
		return new PreparedModelAndView("redirect:nl_subscriberslist").get();
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public void setSubscriberGroupService(SubscriberGroupService service) {
		this.subscriberGroupService = service;
	}
	
}
