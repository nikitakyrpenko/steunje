/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.guestbook.bo;

import java.sql.Timestamp;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class Message implements Entity {
	
	private static final long serialVersionUID = -4157867542956673424L;
	
	private Long id;
	private Integer gbId = 0;
	private String authorName;
	private String authorEmail;
	private String message;
	private Timestamp posted;
	private Timestamp published;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getGbId() {
		return gbId;
	}
	
	public void setGbId(Integer gbId){
		this.gbId = gbId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getPosted() {
		return posted;
	}

	public void setPosted(Timestamp posted) {
		this.posted = posted;
	}

	public Timestamp getPublished() {
		return published;
	}

	public void setPublished(Timestamp published) {
		this.published = published;
	}
	
	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
}
