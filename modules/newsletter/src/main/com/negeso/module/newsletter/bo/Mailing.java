/*
 * @(#)Id: StatusInfo.java, 27.02.2008 18:17:35, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.bo;

import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class Mailing implements Entity {

	private static final long serialVersionUID = -5394917221352835363L;

	private Long id = -1L;
	
	private Publication publication;
	private Subscriber subscriber;
	private MailingState mailingState;
	private String message;
	private Date createdDate;
	private Long uniqId;
	private Integer retryCount = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Publication getPublication() {
		return publication;
	}

	public void setPublication(Publication publication) {
		this.publication = publication;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public MailingState getMailingState() {
		return mailingState;
	}

	public void setMailingState(MailingState mailingState) {
		this.mailingState = mailingState;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public Long getUniqId() {
		return uniqId;
	}

	
	public void setUniqId(Long uniqId) {
		this.uniqId = uniqId;
	}

	public Integer getRetryCount() {
		if (retryCount == null) {
			retryCount = 0;
		}
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}
}
