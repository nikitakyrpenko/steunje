/*
 * @(#)$Id: SimpleNewsletterPageComponent.java,v 1.29, 2007-03-26 17:24:45Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.newsletter.web.component;

import static java.lang.String.format;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.SpringConstants;
import com.negeso.framework.Env;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.page.PageComponentRecord;
import com.negeso.framework.page.PreparedPageComponent;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.I18nUtil;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.bo.SubscriberGroup;
import com.negeso.module.newsletter.service.MailingService;
import com.negeso.module.newsletter.service.MailingService.NotificationStatus;
import com.negeso.module.newsletter.service.NewPublicationService;
import com.negeso.module.newsletter.service.SubscriberService;
import com.negeso.module.newsletter.web.component.SubscriberAttributeParameters;

/**
 *
 * Simple page with list of categories
 * 
 * @version		$Revision: 30$
 * @author		Olexiy Strashko
 * 
 */
public class SimpleNewsletterPageComponent extends PreparedPageComponent {

	private static final Logger logger = Logger.getLogger( SimpleNewsletterPageComponent.class );
	
    public static final String NEWSLETTER_PAGE_COMPONENT = "silver-newsletter-component";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
	
    @Override
	public String getName() {
		return "newsletter";
	}

	@Override
	public void buildXml() throws Exception {
		String action = this.getRequestContext().getNonblankParameter("action");
		
		if ("subscribe".equals(action)){
		    this.submitSubscribeForm();
		}
		else if ("unsubscribe".equals(action)){
		    this.submitUnsubscribeForm();
		}
		else if ("confirm".equals(action)){
		    this.confirmSubscribeRequest();
		}
		else if ("archive".equals(action)){
		    this.buildArchive();
		}
		else if ("publication".equals(action)){
		    this.buildPublication();
		}
		else {
		    this.buildSimpleEntryPage();
		}
	}
	
	private void buildPublication() {
		
		NewPublicationService publicationService =
			(NewPublicationService) getRequestContext().getService(ModuleConstants.NEWSLETTER_MODULE,
					SpringConstants.NEWSLETTER_MODULE_PUBLICATION_SERVICE);
		
		Long id = this.getRequestContext().getLong("pubId");  

		Publication pub = publicationService.getPublicationById(id);
		
		pub.setLangId(new Long(this.getLangId()));

        Element pubEl = Xbuilder.addEl(this.getModel(),"Publication", null);

        Xbuilder.addText(pubEl,pub.getText());

        Xbuilder.setAttr(pubEl, "title", pub.getTitle());
        Xbuilder.setAttr(pubEl, "id", pub.getId());

        Xbuilder.setDateAttrs(pubEl, pub.getPublishDate());
        Xbuilder.setAttr(this.getModel(), "view-type", "publication");
	}

	private void buildArchive() throws CriticalException, ObjectNotFoundException {
		
		NewPublicationService publicationService =
			(NewPublicationService) getRequestContext().getService(ModuleConstants.NEWSLETTER_MODULE,
					SpringConstants.NEWSLETTER_MODULE_PUBLICATION_SERVICE);
		
		List<Publication> publications = publicationService.getSentPublications(new Long(getLangId()));
		Element pubsEl = Xbuilder.addEl(this.getModel(), "Publications", null);
		
		int tmpMonth = 0;
		int tmpYear = 0;
		boolean switchMonth = false;
		Element yearEl = null;
		Element monthEl = null;
		Language lang = null;
		lang = Language.findById(new Long(getLangId()));
		SimpleDateFormat dateFormater = new SimpleDateFormat(
				"MMMM", 
				new Locale(lang.getCode() == null ? Env.getDefaultLanguageCode() : lang.getCode()));
		for (Publication pub: publications){
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(pub.getPublishDate());
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			logger.error("year:" + year);
			logger.error("month:" + month);
			
			if (year != tmpYear) {
				yearEl = Xbuilder.addEl(pubsEl, "Year", null);
				Xbuilder.setAttr(yearEl, "value", "" + year);
				tmpYear = year;
				switchMonth = true;
			}
			if (month != tmpMonth || switchMonth) {
				monthEl = Xbuilder.addEl(yearEl, "Month", null);
				Xbuilder.setAttr(monthEl, "value", "" + month);
				Xbuilder.setAttr(
					monthEl, "title", dateFormater.format(pub.getPublishDate())
				);
				tmpMonth = month;
				switchMonth = false;
			}
			
			Element pubEl = Xbuilder.addEl(monthEl, "Publication", null);
			Xbuilder.setAttr(pubEl, "title", pub.getTitle());
			Xbuilder.setAttr(pubEl, "id", pub.getId());

			Xbuilder.setDateAttrs(pubEl, pub.getPublishDate());
		}
		Xbuilder.setAttr(this.getModel(), "view-type", "archive");
	}

	public String getSubscribeResult(String email) throws MessagingException{
		return "Subscription email is sent to: " + email; 
	}
	
	private Element submitSubscribeForm() throws MessagingException 
    {
		logger.debug("+");
		
		Long langId = this.getRequestContext().getLong("input_languageId");
		String textType = this.getRequestContext().getNonblankParameter("input_text_type");
		
		SubscriberAttributeParameters sap = new SubscriberAttributeParameters();
		sap.bind(getRequestContext());
		
		if (logger.isDebugEnabled()) {
			logger.debug(format("parameters: langId=%s,textType=%s,attributes=%s,groups=%s",
					langId, textType, ReflectionToStringBuilder.toString(sap.getAttributeValues()), 
					ReflectionToStringBuilder.toString(sap.getGroupIds())));
		}

		MailingService mailingService =
			(MailingService) getRequestContext().getService(ModuleConstants.NEWSLETTER_MODULE,
					SpringConstants.NEWSLETTER_MODULE_MAILING_SERVICE);

		SubscriberService subscriberService =
			(SubscriberService) getRequestContext().getService(ModuleConstants.NEWSLETTER_MODULE,
					SpringConstants.NEWSLETTER_MODULE_SUBSCRIBER_SERVICE);
		
		String newsletterLink = this.getRequestContext().getString("pageLink", 
				PageComponentRecord.getBindPageName(this.getConnection(),
						NEWSLETTER_PAGE_COMPONENT, langId.intValue()));
		
		SubscriberAttributeType attr = subscriberService.
			getSubscriberAttributeByTitle(Configuration.ATTRIBUTE_EMAIL);
		
		if (attr != null){
			Element el = isAllowToAddSubscriber(sap.getAttributeValues().get(attr.getId()), 
					subscriberService); 
			if (el != null){
				el.setAttribute("status", "error");
				logger.debug("-");
				return el;
			}
		}
		
		String siteLink = this.getRequestContext().getString("siteLink", Env.getHostName());
		
		NotificationStatus ns = mailingService.sendNotification(sap, textType,
				langId, newsletterLink, siteLink,
				XmlHelper.createPageElement(this.getRequestContext()));
		
		Element el = this.buildResultMessageElement(ns.getMessage());
		if (ns.isOk()) {
			el.setAttribute("status", "success");
			el.setAttribute("email", ns.getEmail());
			
			if (Configuration.isNewslettersNotifyOnSubscribeEnable())
				((MailingService)getRequestContext().getService(
						ModuleConstants.NEWSLETTER_MODULE,
						SpringConstants.NEWSLETTER_MODULE_MAILING_SERVICE)
				).notifyAboutSubscriberUpdate(
						Configuration.getNewslettersBccEmail(), 
						ns.getEmail(), 
						"subscribed",
						prepareDetailsForSubject(ns.getEmail())
				);
			
		} else {
			el.setAttribute("status", "error");
		}
		logger.debug("-");
		return el;
    }
	
	private String prepareDetailsForSubject(String email) {
		return new StringBuilder()
			.append(' ')
			.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
			.append(' ')
			.append(((HttpServletRequest)getRequestContext().getIOParameters().get(WebFrontController.HTTP_SERVLET_REQUEST)).getRequestURL())
			.append(' ')
			.append(getRequestContext().getRemoteAddr())
			.append(' ')
			.append(email).toString();
	}
	
	private Element isAllowToAddSubscriber(String eMail, SubscriberService service){
		if (service.isExists(eMail)){
			return this.buildResultMessageElement("SUBSCRIBER_ALREADY_EXISTS");
		}else
		if (!service.isAllowByLimit()){
			return this.buildResultMessageElement("MAX_NUMBER_SUBSCRIBER");
		}
		return null;
	}

	private void submitUnsubscribeForm() throws ObjectNotFoundException  {
		SubscriberAttributeParameters sap = new SubscriberAttributeParameters();
		sap.bind(getRequestContext());
		
		SubscriberService subscriberService =
			(SubscriberService) getRequestContext().getService(ModuleConstants.NEWSLETTER_MODULE,
					SpringConstants.NEWSLETTER_MODULE_SUBSCRIBER_SERVICE);
		
		List<SubscriberAttributeType> types = subscriberService.listSubscriberAttributesTypes();
		SubscriberAttributeType emailType = subscriberService.findTypeByKey(Subscriber.ATTR_EMAIL, types);
		
		String email = sap.getAttributeValues().get(emailType.getId());
		
		try{
			List<Subscriber> subscribers = subscriberService.listAllSubscribers();
			Subscriber subscriber = subscriberService.findByEmail(subscribers, email); 
			
			if (subscriber == null){
		        throw new CriticalException("SUBSCRIBER_NOT_FOUND");
			}
			
			if (logger.isInfoEnabled()){
			    logger.info(
			        "Subscriber: " + subscriber.getId() + " email: " + subscriber.getEmail()
			    );
			}

			subscriberService.delete(subscriber);
			
			
			if (Configuration.isNewslettersNotifyOnUnsubscribeEnable())
				((MailingService)getRequestContext().getService(
						ModuleConstants.NEWSLETTER_MODULE,
						SpringConstants.NEWSLETTER_MODULE_MAILING_SERVICE)
				).notifyAboutSubscriberUpdate(
						Configuration.getNewslettersBccEmail(), 
						subscriber, 
						"unsubscribed",
						prepareDetailsForSubject(subscriber.getEmail())
				);
			

			
		}catch(CriticalException e){
		    this.buildResultMessageElement(e.getMessage());
		    return;
		}
			
		this.buildResultMessageElement("UNSUBSCRIBE_CONFIRMED");		 
    }

    
    private void buildSimpleEntryPage() throws ObjectNotFoundException { 
    	Xbuilder.setAttr(this.getModel(), "view-type", Configuration.SIMPLE_VIEW);

    	Element attributesEl = Xbuilder.addEl(this.getModel(), "newsletter-attributes", null);

        //TODO: replace with service usage
        List<SubscriberAttributeType> attributes = getHiberSession().
			createQuery("from SubscriberAttributeType type where type.visible = true").list();
		Language language = getRequestContext().getSessionData().getLanguage();
		
		ResourceBundle bundle = DatabaseResourceBundle.getChainResourceBundle(
				new String[] { "dict_newsletter.xsl", "dict_common.xsl", }, language.getCode());
		
		for (SubscriberAttributeType sat : attributes) {
			Element attributeElement = Xbuilder.createEl(attributesEl, "newsletter-attribute", null);
			Xbuilder.setAttr(attributeElement, "id", sat.getId());
			String value = bundle.getString("NL." + sat.getKey());
			
			Xbuilder.setAttr(attributeElement, "key", sat.getKey());
			Xbuilder.setAttr(attributeElement, "value", value);
			Xbuilder.setAttr(attributeElement, "required", sat.isRequired());
			attributesEl.appendChild(attributeElement);
		}
    	
    	Element groupsEl = Xbuilder.addEl(this.getModel(), "newsletter-groups", null);
        //TODO: replace with service usage
        List<SubscriberGroup> groups = getHiberSession().
                createQuery("from SubscriberGroup sg where sg.internal = false").list();
		I18nUtil.setCurrentLanguageForSubscriberGroups(groups, language.getId());
		for (SubscriberGroup sg : groups) {
			Element groupElement = Xbuilder.createEl(groupsEl, "newsletter-group", null);
			Xbuilder.setAttr(groupElement, "id", sg.getId());
			Xbuilder.setAttr(groupElement, "title", sg.getTitle());
			Xbuilder.setAttr(groupElement, "description", sg.getDescription());
			groupsEl.appendChild(groupElement);
		}
		
		getModel().appendChild(attributesEl);
		getModel().appendChild(groupsEl);
		getModel().appendChild(Language.getDomItems(this.getModel().getOwnerDocument()));
    }

    private void confirmSubscribeRequest() {
	    Long subscriberId = this.getRequestContext().getLong("subscriberId");
		try {
		    if (subscriberId == null) {
		        throw new CriticalException("BAD_SUBSCRIBER_ID");
		    }
		    if (logger.isInfoEnabled()){
		        logger.info("Confirmation started, subscriber id: " + subscriberId);
		    }
		    
			SubscriberService sr =
				(SubscriberService) getRequestContext().getService(ModuleConstants.NEWSLETTER_MODULE,
						SpringConstants.NEWSLETTER_MODULE_SUBSCRIBER_SERVICE);

			Subscriber subscriber = sr.findById(subscriberId);
			if (subscriber == null)
				throw new CriticalException("SUBSCRIBER_NOT_FOUND");
			
			subscriber.setActivated(true);
            buildResultMessageElement("SUBSCRIPTION_CONFIRMED");
            
        	
			
        }
		catch(CriticalException e){
			buildResultMessageElement(e.getMessage());
		}

	}
	
    private Element buildResultMessageElement(String message) {
    	Xbuilder.setAttr(this.getModel(), "view-type", Configuration.RESULT_VIEW);
		return Xbuilder.addEl(this.getModel(), "newsletter-message", message);
	}

}
