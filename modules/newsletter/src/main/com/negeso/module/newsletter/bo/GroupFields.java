/*
 * @(#)Id: GroupFields.java, 06.03.2008 14:58:44, Dmitry Fedotov
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
public class GroupFields implements Entity {

	private static final long serialVersionUID = -7536537556799233874L;
	
	private Long id;
	private String title;
	private String description;
	private Long langId;
	
	private SubscriberGroup group;
	
	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public GroupFields() {
	}

    public GroupFields(SubscriberGroup group, Long langId) {
        this.group = group;
        this.langId = langId;
    }


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public SubscriberGroup getGroup() {
		return group;
	}

	public void setGroup(SubscriberGroup group) {
		this.group = group;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
}
