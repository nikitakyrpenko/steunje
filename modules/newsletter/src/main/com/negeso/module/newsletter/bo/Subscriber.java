/*
 * @(#)Id: Subscriber.java, 25.02.2008 17:40:54, Dmitry Fedotov
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * 
 * @TODO
 * 
 * @version Revision:
 * @author Dmitry Fedotov
 * 
 */
public class Subscriber implements Entity {

	private static final long serialVersionUID = 2408092456856379752L;
	
	public static final String ATTR_FIRST_NAME = "FIRST_NAME";
	public static final String ATTR_LAST_NAME = "LAST_NAME";
	public static final String ATTR_EMAIL = "EMAIL";
	
	private Long id = 0L;
	private boolean activated = false;
	private Timestamp createdTime;
	private Long siteId = Env.getSiteId();
	private Long subscriptionLangId;

	private boolean html = true;
	
	private Set<SubscriberAttributeValue> attributes = 
		new HashSet<SubscriberAttributeValue>();
	
	private List<SubscriberGroup> subscriberGroups = new ArrayList<SubscriberGroup>();

	public Subscriber(Long id, List<SubscriberGroup> subscriberGroups) {
		this.id = id;
		this.subscriberGroups = subscriberGroups;
	}
	
	public Subscriber() {
		createdTime = new Timestamp(new Date().getTime());
		subscriptionLangId = 1L;
	}
	
	public void addAttribute(String attrName, String v){
		
		SubscriberAttributeType type = new SubscriberAttributeType();
		type.setKey(attrName);
		
		SubscriberAttributeValue value = new SubscriberAttributeValue();
		value.setSubscriber(this);
		value.setSubscriberAttributeType(type);
		value.setValue(v);
		
		attributes.add(value);
	}

	public Long getId() {
		return id;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public Set<SubscriberAttributeValue> getAttributes() {
		return attributes;
	}

	public void setAttributes(
			Set<SubscriberAttributeValue> attributes) {
		this.attributes = attributes;
	}

	public List<SubscriberGroup> getSubscriberGroups() {
		return subscriberGroups;
	}

	public void setSubscriberGroups(List<SubscriberGroup> subscriberGroups) {
		this.subscriberGroups = subscriberGroups;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	private String getAttribute(String key){
		for (SubscriberAttributeValue attrValue: attributes){
			if (attrValue.getSubscriberAttributeType().getKey().equals(key)){
				return attrValue.getValue();
			}
		}
		return "";
	}
	
	public String getFirstName(){
		return getAttribute(ATTR_FIRST_NAME);
	}

	public String getLastName(){
		return getAttribute(ATTR_LAST_NAME);
	}
	
	public String getEmail(){
		return getAttribute(ATTR_EMAIL);
	}
	
	public boolean setAttribute(String key, String value) {
		if (value == null)
			return false;
		for (SubscriberAttributeValue attrValue: attributes){
			if (attrValue.getSubscriberAttributeType().getKey().equals(key)){
				attrValue.setValue(value);
				return true;
			}
		}
		return false;
	}
	
	public SubscriberGroup getGroupById(Long groupId){
		for (SubscriberGroup group : subscriberGroups){
			if (group.getId().equals(groupId))
				return group;
		}
		return null;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getSubscriptionLangId() {
		return subscriptionLangId;
	}

	public void setSubscriptionLangId(Long subscriptionLangId) {
		this.subscriptionLangId = subscriptionLangId;
	}

	public boolean isHtml() {
		return html;
	}
	
	public void setHtml(boolean html) {
		this.html = html;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
