/*
 * @(#)Id: NewsletterCategory.java, 21.02.2008 16:41:21, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.bo;

import java.util.ArrayList;
import java.util.List;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class SubscriptionCategory implements Entity {

	private static final long serialVersionUID = 2505364412675759250L;

	private Long id = 0L;
	private String title;
	private Long orderNumber;
	
	private List<Publication> publications = new ArrayList<Publication>();
	
	public SubscriptionCategory() {}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Publication> getPublications() {
		return publications;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
}
