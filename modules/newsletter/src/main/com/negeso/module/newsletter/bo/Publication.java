/*
 * @(#)Id: Publication.java, 21.02.2008 15:51:09, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.Language;
import com.negeso.module.newsletter.Configuration;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class Publication implements Entity {
	
    private static final long serialVersionUID = -5753861055492334628L;

    private Long id = 0L;

	private String feedbackEmail;
	private String feedbackName;
	private String status = Configuration.PUBLICATION_STATUS_CREATED;
	private Long langId;
	
	private SubscriptionCategory subscriptionCategory;
	private List<SubscriberGroup> subscriberGroups = new ArrayList<SubscriberGroup>();
	private MailTemplate mailTemplate;
	
	private List<Attachment> attachments = new ArrayList<Attachment>();

	private Long subscribersNumber;
	
	private PublicationState publicationState;
	
	private boolean i18n = true;
	
	private Timestamp publishDate = new Timestamp(System.currentTimeMillis());
	
	private String accessCode;
	private Long pageId;
	private String pageLink;
	
	public String getFormattedPublishDate(){
		if (publishDate == null )
			return "";
		return Env.formatSimpleRoundFullFormatter(publishDate);		
	}

	private Map<Long, PublicationFields> customFields;

	public boolean isI18n() {
		return i18n;
	}
	
	public void setI18n(boolean i18n) {
		this.i18n = i18n;
	}

	public Map<Long, PublicationFields> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<Long, PublicationFields> customFields) {
		this.customFields = customFields;
	}

	public Publication() {
		
		customFields = new HashMap<Long, PublicationFields>();
		for (Language l :Language.getItems()){
			PublicationFields pFields = new PublicationFields();
			pFields.setPublication(this);
			pFields.setLangId(l.getId());
			customFields.put(l.getId(), pFields);
		}
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return getCustomFields().get(getLangId()).getTitle();
	}
	
	public String getTitle(Long langId) {
		return getCustomFields().get(langId).getTitle();
	}

	public void setTitle(String title) {
		getCustomFields().get(getLangId()).setTitle(title);
	}
	
	public void copyTitleToAllLanguages() {
		for (PublicationFields fields : getCustomFields().values()){
			fields.setTitle(getTitle());
		}
	}
	
    public void copyToAllLanguages() {
		for (PublicationFields fields : getCustomFields().values()){
            fields.setTitle(getTitle());
            fields.setDescription(getDescription());
            
            fields.getArticle().setLangId(fields.getLangId());
			fields.getArticle().setClass_("newsletter");
			fields.getArticle().setText(getText());            
        }
	}

    public String getText(){
		return getCustomFields().get(getLangId()).getArticle().getText();
	}
	
	public String getText(Long langId) {
		return getCustomFields().get(langId).getArticle().getText();
	}
	
	public void setText(String text){
		getCustomFields().get(getLangId()).getArticle().setText(text);
	}
	
	public Article getArticle(){
		return getCustomFields().get(getLangId()).getArticle();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPlainText(){
		return getCustomFields().get(getLangId()).getPlainText();
	}
	
	public String getPlainText(Long langId) {
		return getCustomFields().get(langId).getPlainText();
	}
	
	public void setPlainText(String text){
		getCustomFields().get(getLangId()).setPlainText(text);
	}

	public List<SubscriberGroup> getSubscriberGroups() {
		return subscriberGroups;
	}

	public void setSubscriberGroups(List<SubscriberGroup> subscriberGroups) {
		this.subscriberGroups = subscriberGroups;
	}

	public SubscriptionCategory getSubscriptionCategory() {
		return subscriptionCategory;
	}

	public void setSubscriptionCategory(SubscriptionCategory subscriptionCategory) {
		this.subscriptionCategory = subscriptionCategory;
	}

	public Timestamp getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Timestamp publishDate) {
		this.publishDate = publishDate;
	}

	public String getDescription() {
		return getCustomFields().get(getLangId()).getDescription();
	}

	public void setDescription(String description) {
		if (isI18n()){
			getCustomFields().get(getLangId()).setDescription(description);
		}else{
			for (PublicationFields fields : getCustomFields().values()){
				fields.setDescription(description);
			}
		}
	}

	public MailTemplate getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	public String getFeedbackEmail() {
		return feedbackEmail;
	}

	public void setFeedbackEmail(String feedbackEmail) {
		this.feedbackEmail = feedbackEmail;
	}

	public void setFeedbackName(String feedbackName) {
		this.feedbackName = feedbackName;
	}

	public String getFeedbackName() {
		return feedbackName;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Long getLangId() {
		return (langId == null ? 
				Language.getByCode(Env.getDefaultInterfaceLanguageCode()).getId() :
				langId);
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public String getLangCode() {
		// TODO remove it (for scheduled page is used as fake)
		return "en";
	}
	
	public SubscriberGroup getGroupById(Long groupId){
		for (SubscriberGroup group : subscriberGroups){
			if (group.getId().equals(groupId))
				return group;
		}
		return null;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Long getSubscribersNumber() {
		return subscribersNumber;
	}

	public void setSubscribersNumber(Long subscribersNumber) {
		this.subscribersNumber = subscribersNumber;
	}

	public PublicationState getPublicationState() {
		return publicationState;
	}

	public void setPublicationState(PublicationState publicationState) {
		this.publicationState = publicationState;
	}

	public String getPageLink() {
		return pageLink;
	}

	public void setPageLink(String pageLink) {
		this.pageLink = pageLink;
	}

}
