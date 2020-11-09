/*
 * @(#)Id: Group.java, 25.02.2008 12:18:26, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.bo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.Language;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriberGroup implements Entity {
	
	private static final long serialVersionUID = 4459858387850222519L;
	
	private Long id = 0L;
	private boolean internal = false;
	private boolean i18n = false;
	private Long siteId = Env.getSiteId();
	private Long orderNumber;
	private Long lang_id;

	private List<Subscriber> subscribers;
	private List<Publication> publications;
	private Map<Long, GroupFields> customFields;
	
	public SubscriberGroup(){
		customFields = new HashMap<Long, GroupFields>();
		for (Language l :Language.getItems()){
			customFields.put(l.getId(), new GroupFields(this, l.getId()));
		}
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return getCustomFields().get(getLang_id()).getTitle();
	}
	public void copyTitleToAllLanguages(){
		for (GroupFields fields : getCustomFields().values()){
			fields.setTitle(getTitle());
		}
	}
	public void copyDescriptionToAllLanguages(){
		for (GroupFields fields : getCustomFields().values()){
			fields.setDescription(getDescription());
		}
	}
	public void setTitle(String title) {
		getCustomFields().get(getLang_id()).setTitle(title);
	}
	public String getDescription() {
		return getCustomFields().get(getLang_id()).getDescription();
	}
	public void setDescription(String description) {
		getCustomFields().get(getLang_id()).setDescription(description);
	}
	public boolean isInternal() {
		return internal;
	}
	public void setInternal(boolean internal) {
		this.internal = internal;
	}
	public boolean isI18n() {
		return i18n;
	}
	public void setI18n(boolean i18n) {
		this.i18n = i18n;
	}
	public void setCustomFields(Map<Long, GroupFields> customFields) {
		this.customFields = customFields;
	}
	public Map<Long, GroupFields> getCustomFields() {
		return customFields;
	}
	public Long getLang_id() {
		return (lang_id == null? 
					Language.getDefaultLanguage().getId():
						lang_id);
	}
	public void setLang_id(Long lang_id) {
		this.lang_id = lang_id;
	}
	public List<Subscriber> getSubscribers() {
		return subscribers;
	}
	public void setSubscribers(List<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public List<Publication> getPublications() {
		return publications;
	}
	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<Subscriber> getActivatedSubscribers() {
		List<Subscriber> list = new LinkedList<Subscriber>();
		for (Subscriber s : subscribers) {
			if (s.isActivated())
				list.add(s);
		}
		return list;
	}

}
