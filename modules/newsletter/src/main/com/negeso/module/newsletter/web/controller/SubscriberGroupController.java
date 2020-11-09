/*
 * @(#)Id: SubscriberGroupController.java, 06.03.2008 15:46:55, Dmitry Fedotov
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
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.service.SubscriberGroupService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriberGroupController extends SimpleFormController {
	
	private static final Logger logger = Logger.getLogger(SubscriberGroupController.class);
	
	private SubscriberGroupService subscriberGroupService;
	
	public SubscriberGroupController(){
		setCommandClass(SubscriberGroup.class);
		setCommandName("group");
	}
	
	@Override
	protected ModelAndView showForm(HttpServletRequest request,
			HttpServletResponse response, BindException errors)
			throws Exception {
		if (!isFormSubmission(request)){
			
			Long id = ServletRequestUtils.getLongParameter(request, "gid");
			
			if (subscriberGroupService.getGroupCount() >= Configuration.getMaxGroupNumber()
                    && id == null){
				
				RequestUtil.getHistoryStack(request).goBack();
				
				return new PreparedModelAndView("nl_grouplist").addToModel(
						"grouplist",
						subscriberGroupService.listOrderedGroups()).addError("max number of groups").get();
			}
		}
		return super.showForm(request, response, errors);
	}
	
	public Object formBackingObject(HttpServletRequest request) throws Exception {
		logger.debug("+");
		
		Long id = ServletRequestUtils.getLongParameter(request, "gid");
		
		SubscriberGroup group = null;
		
		if (id == null){
			group = new SubscriberGroup();
			RequestUtil.getHistoryStack(request).push( new Link( "New group", 
					"/admin/nl_editgroup", false) );
		}else {
			group = subscriberGroupService.findById(id);
			RequestUtil.getHistoryStack(request).push( new Link( group.getTitle(), 
					"/admin/nl_editgroup?groupId=" + id, false) );
		}
		
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		group.setLang_id(language.getId());
		
		logger.debug("-");
		return group;
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("languages", Language.getItems());
		return model;
	}
	
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
			Object command, BindException errors) throws Exception {
		logger.debug("+");
		
		SubscriberGroup group = (SubscriberGroup) command;
		
		if (!group.isI18n() || group.getId() < 1){
			group.copyDescriptionToAllLanguages();
			group.copyTitleToAllLanguages();
		}
		if (group.getOrderNumber() == null) {
			group.setOrderNumber(subscriberGroupService.getNextOrderNumber());
		}
		subscriberGroupService.save(group);
		
		RequestUtil.getHistoryStack(request).push( new Link( "Group list", 
				"/admin/nl_grouplist", false) );
		
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		logger.debug("-");
		return new PreparedModelAndView("nl_grouplist").
				addToModel("grouplist",
						subscriberGroupService.listAllSubscriberGroups(language.getId())).get();
	}

	public void setSubscriberGroupService(SubscriberGroupService service) {
		this.subscriberGroupService = service;
	}

}
