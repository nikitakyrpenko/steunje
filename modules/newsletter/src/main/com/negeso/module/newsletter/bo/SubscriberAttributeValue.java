/*
 * @(#)Id: SubscriberAttributeValue.java, 05.03.2008 10:22:05, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.bo;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriberAttributeValue implements Entity {

    private static final long serialVersionUID = -5746740884431860024L;
    
    private Long id;
	private String value;
	private Subscriber subscriber;
	private SubscriberAttributeType subscriberAttributeType;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public SubscriberAttributeType getSubscriberAttributeType() {
		return subscriberAttributeType;
	}

	public void setSubscriberAttributeType(SubscriberAttributeType attributeType) {
		this.subscriberAttributeType = attributeType;
	}
}
