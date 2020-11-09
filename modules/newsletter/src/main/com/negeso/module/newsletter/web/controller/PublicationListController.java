/*
 * @(#)Id: PublicationListController.java, 22.02.2008 17:12:20, Dmitry Fedotov
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
import com.negeso.module.newsletter.PreparedModelAndView;
import com.negeso.module.newsletter.bo.MailTemplate;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.bo.SubscriptionCategory;
import com.negeso.module.newsletter.service.MailTemplateService;
import com.negeso.module.newsletter.service.MailingService;
import com.negeso.module.newsletter.service.NewPublicationService;
import com.negeso.module.newsletter.service.SubscriberGroupService;
import com.negeso.module.newsletter.service.SubscriptionCategoryService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class PublicationListController extends MultiActionController {

	private static final Logger logger = Logger.getLogger(PublicationListController.class);
	
	private SubscriptionCategoryService subscriptionCategoryService;
	private SubscriberGroupService subscriberGroupService;
	private NewPublicationService publicationService;
	private MailTemplateService mailTemplateService;
	private MailingService mailingService;
	
	public void setSubscriptionCategoryService( SubscriptionCategoryService service) {
		this.subscriptionCategoryService = service;
	}
	
	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public void setPublicationService(NewPublicationService publicationService) {
		this.publicationService = publicationService;
	}

	public ModelAndView showPublications(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		Long categoryId = ServletRequestUtils.getLongParameter(request, "categoryId");
		
		SubscriptionCategory category = subscriptionCategoryService.findBySubscriptionCategoryId(categoryId);
		
		RequestUtil.getHistoryStack(request).push( new Link(category.getTitle(), 
				"/admin/nl_publicationlist?categoryId=" + categoryId, false) );

		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		List<MailTemplate> mailTemplates = mailTemplateService.findAllMailTemplates(language.getId());
		
		logger.debug("-");
		return new PreparedModelAndView("nl_publicationlist").
					addToModel("category", category).
					addToModel("mailtemplates", mailTemplates).get(); 
	}
	
	public ModelAndView deletePublication(HttpServletRequest request,
                                          HttpServletResponse response) throws Exception{
		logger.debug("+");

		Long id = ServletRequestUtils.getLongParameter(request, "id");
		
		Publication publication = publicationService.getPublicationById(id);
		publicationService.deletePublication(publication);
		
		RequestUtil.getHistoryStack(request).goBack();
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_publicationlist?categoryId="+
				publication.getSubscriptionCategory().getId());
	}

	public ModelAndView addPublication(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		String title = request.getParameter("title");

		Long mailTemplateId = ServletRequestUtils.getLongParameter(request, "mail_template_id");
		
		Publication publication = new Publication();
		publication.setTitle(title);
		publication.copyTitleToAllLanguages();
		
		MailTemplate mailTemplate = mailTemplateService.getMailTemplate(mailTemplateId);
		
		mailTemplate.setCurrentLangId(language.getId());
		publication.setLangId(language.getId());
		
		publication.setText(mailTemplate.getText());
		
		List<SubscriberGroup> groups = subscriberGroupService.listAllSubscriberGroups(language.getId());
		
		RequestUtil.getHistoryStack(request).push( new Link( "New publication", 
				"/admin/nl_publicationlist?action=addPublication", false) );
		logger.debug("-");
		
		return new PreparedModelAndView("nl_editpublication").
				addToModel("publication", publication).
				addToModel("groups", groups).get();
	}
	
	public ModelAndView proof(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");

		Long langId;
		if (request.getParameter("lang_id")!=null){
			langId = Long.valueOf(request.getParameter("lang_id"));		
		}else{
			Language language = NegesoRequestUtils.getInterfaceLanguage(request);
			langId =language.getId();
		}

		Long id = ServletRequestUtils.getLongParameter(request, "publicationId");
		
		Publication publication = publicationService.getPublicationById(id);

		mailingService.proof(publication.getId(),langId);
		
		RequestUtil.getHistoryStack(request).goBack();
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_publicationlist?categoryId="+
				publication.getSubscriptionCategory().getId());
	}
	
	public ModelAndView unSchedule(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		Long publicationId = ServletRequestUtils.getLongParameter(request, "id");
		
		publicationService.unSchedule(publicationId);
		
		RequestUtil.getHistoryStack(request).goBack();
		
		logger.debug("-");
		return new ModelAndView("redirect:/admin/nl_publicationlist" +
				"?action=showSheduled");
	}
	
	public ModelAndView showSheduled(HttpServletRequest request, 
			HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		RequestUtil.getHistoryStack(request).push( new Link("Sheduled publications", 
				"/admin/nl_publicationlist?action=showSheduled", false) );
		
		logger.debug("-");
		return new ModelAndView("nl_sheduled", "publications", 
				publicationService.listPublicationByScheduledDate());
	}

	public void setMailingService(MailingService mailingService) {
		this.mailingService = mailingService;
	}

	public void setSubscriberGroupService(SubscriberGroupService service) {
		this.subscriberGroupService = service;
	}
	
}
