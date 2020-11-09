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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.CustomTimestampEditor;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.Attachment;
import com.negeso.module.newsletter.bo.MailTemplate;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.PublicationState;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.bo.SubscriptionCategory;
import com.negeso.module.newsletter.service.MailTemplateService;
import com.negeso.module.newsletter.service.MailingService;
import com.negeso.module.newsletter.service.NewPublicationService;
import com.negeso.module.newsletter.service.PublicationSchedulerService;
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
public class PublicationController extends SimpleFormController {
	
	private static final Logger logger = Logger.getLogger(PublicationController.class);
	
	public static final String GROUP_PREFIX = "group_";
	public static final String SWF_SUFFIX = ".sfw";    

    private NewPublicationService publicationService;
	private MailTemplateService mailTemplateService;
	private SubscriberGroupService subscriberGroupService;
	private SubscriptionCategoryService subscriptionCategoryService;
	private PublicationSchedulerService publicationSchedulerService;
	private MailingService mailingService;
	
	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public PublicationController(){
		setCommandClass(Publication.class);
		setCommandName("publication");
	}
	
	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, new CustomTimestampEditor(Env.getRoundsimplefullformatter(), false));
	}
	
	public Object formBackingObject(HttpServletRequest request) throws Exception {
		logger.debug("+");
		Long id = ServletRequestUtils.getLongParameter(request, "id", 0);		
		Long langId = 1L;
		if (request.getParameter("lang_id")!=null){
			langId = Long.valueOf(request.getParameter("lang_id"));		
		}else{
			Language language = NegesoRequestUtils.getInterfaceLanguage(request);
			langId =language.getId();
		}
		Publication publication = null;
		
		if (id == 0L){
			publication = new Publication();
			PublicationState state = publicationService.getPublicationStateByName(Configuration.PUBLICATION_STATUS_SUSPENDED);
			if (state != null){
				publication.setPublicationState(state);
			}
			Long categoryId = ServletRequestUtils.getLongParameter(request, "category_id");
			String title = ServletRequestUtils.getStringParameter(request, "title", null);
			Long mailTemplateId = ServletRequestUtils.getLongParameter(request, "mail_template_id");

			SubscriptionCategory parentCategory = subscriptionCategoryService.findBySubscriptionCategoryId(categoryId);
			MailTemplate mailTemplate = mailTemplateService.getMailTemplate(mailTemplateId);
			mailTemplate.setCurrentLangId(langId);
			publication.setLangId(langId);
			publication.setTitle(title);
			publication.setMailTemplate(mailTemplate);
			publication.setText(mailTemplate.getText());
			publication.setSubscriptionCategory(parentCategory);
			
			if (Configuration.isSpecialPagesEnabled())
				publication.setAccessCode(publicationService.generateAccessCode());
			
			RequestUtil.getHistoryStack(request).push( new Link( "New publication", 
					"/admin/nl_editpublication?category_id=" + categoryId, false) );
		}else {
			
			if (ServletRequestUtils.getStringParameter(request, "duplicate") != null){
				
				publication = publicationService.getPublicationById(id);
				
				Publication p = new Publication();
				p.setTitle(publication.getTitle() + "(copy)");
				p.setDescription(publication.getDescription());
				p.setText(publication.getText());
				p.setLangId(langId);
				p.setMailTemplate(publication.getMailTemplate());
				p.setSubscriptionCategory(publication.getSubscriptionCategory());
				
				p.getAttachments().clear();
				p.getAttachments().addAll(publication.getAttachments());
				
				p.getSubscriberGroups().clear();
				p.getSubscriberGroups().addAll(publication.getSubscriberGroups());
				
				p.setAccessCode(publication.getAccessCode());

				p.setI18n(publication.isI18n());
				p.setFeedbackName(publication.getFeedbackName());
				p.setFeedbackEmail(publication.getFeedbackEmail());
				p.setPublishDate(publication.getPublishDate());
				p.setPublicationState(publication.getPublicationState());
				
				RequestUtil.getHistoryStack(request).goBack();
				
				RequestUtil.getHistoryStack(request).push( new Link( p.getTitle(), 
						"/admin/nl_editpublication?id=" + id, false) );
				
				return p;
				
			}else if (ServletRequestUtils.getStringParameter(request, "proof") != null) {
				publication = publicationService.getPublicationById(id);
				publication.setLangId(langId);
				mailingService.proof(publication.getId(), langId);
				
			} else if (ServletRequestUtils.getStringParameter(request, "send") != null) {
				publication = publicationService.getPublicationById(id);
				publication.setLangId(langId);
				mailingService.send(publication.getId(), langId);
					
			} else { 
				publication = publicationService.getPublicationById(id);
				
				if (Configuration.isSpecialPagesEnabled())
				publication.setPageLink(publicationService.
						getPublicationPageLink(publication, langId));
				
				publication.setLangId(langId);
			}
			
			RequestUtil.getHistoryStack(request).push( new Link( publication.getTitle(), 
					"/admin/nl_editpublication?id=" + id +"?lang_id="+langId, false) );
		}
		logger.debug("-");
		return publication;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("languages", Language.getItems());
		model.put("groups", subscriberGroupService.listAllSubscriberGroups(language.getId()));
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		if (id != 0L){
			Publication publication = publicationService.getPublicationById(id);
			model.put("attachments", StringUtils.join(publication.getAttachments().iterator(), ';'));
		}
		model.put("states", publicationService.getPublicationStates());
		return model;
	}
	
	@SuppressWarnings("rawtypes")
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
			Object command, BindException errors) throws Exception {
		logger.debug("+");
		
		Publication publication = (Publication) command;
		publication.getSubscriberGroups().clear();
		for (Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext() ;){
			String param_name = (String)i.next();
			if (param_name.startsWith(GROUP_PREFIX)){
				param_name = param_name.substring(GROUP_PREFIX.length());
				SubscriberGroup group = publication.getGroupById(Long.valueOf(param_name));
				if (group == null){
					group = subscriberGroupService.findById(Long.valueOf(param_name));
				}
				publication.getSubscriberGroups().add(group);
			}
		}
		
		publication.setPublishDate(
			new Timestamp(Env.parseSimpleRoundFullFormatter(ServletRequestUtils.getStringParameter(request, "publishDate")).getTime()));
		PublicationState state = publicationService.
			getPublicationState(ServletRequestUtils.getLongParameter(request, "state"));
		publication.setPublicationState(state);
		publication.getAttachments().clear();
		String attachmentLink = ServletRequestUtils.getStringParameter(request, "attachmentLink", "");
		if (attachmentLink != null && !attachmentLink.equals("")){
			List<String> l = Arrays.asList(attachmentLink.split(";"));
			for (String currAttachLink : l){
				Attachment attachment = new Attachment();
				attachment.setPublication(publication);
				attachment.setLink(currAttachLink);
				if (currAttachLink.trim().toLowerCase().endsWith(SWF_SUFFIX)){
					attachment.setEmbedded(true);
				}
				publication.getAttachments().add(attachment);
			}
		}
		if (!publication.isI18n() || publication.getId() < 1) {
            publication.copyToAllLanguages();
		}

		publicationService.setConnection(DBHelper.getConnection());
		
		if (Configuration.isSpecialPagesEnabled()) {
            if (publication.getId() > 0) {
                publicationService.updatePageLink(publication, publication.getLangId());
            } else {
                for (Language l : Language.getItems()) {
                    publicationService.addPageLink(publication, l.getId());
                }
            }
        }
		
		publicationService.save(publication);
		
		publicationSchedulerService.schedulePublication(publication, Configuration.PUBLICATION_STATUS_SCHEDULED.equals(state.getName()));
		
		RequestUtil.getHistoryStack(request).goBack();
		
		String saveParameter = ServletRequestUtils.getStringParameter(request, "save", "");
		if (saveParameter != null && saveParameter.equals("save_and_close")){
			logger.debug("-");
			return new ModelAndView("redirect:/admin/nl_publicationlist?categoryId=" + 
					publication.getSubscriptionCategory().getId());
		}else{
			logger.debug("-");
			Long langId = null;
			if (request.getParameter("lang_id")!=null){
				langId = Long.valueOf(request.getParameter("lang_id"));		
			}
			return new ModelAndView("redirect:/admin/nl_editpublication?id=" + 
					publication.getId()+"&lang_id="+langId);
		}
	}

	public void setPublicationService(NewPublicationService service) {
		this.publicationService = service;
	}

	public void setSubscriberGroupService(SubscriberGroupService service) {
		this.subscriberGroupService = service;
	}
	public void setSubscriptionCategoryService(SubscriptionCategoryService service) {
		this.subscriptionCategoryService = service;
	}

	public void setPublicationSchedulerService(PublicationSchedulerService service) {
		this.publicationSchedulerService = service;
	}
	public void setMailingService(MailingService mailingService) {
		this.mailingService = mailingService;
	}
}
