/*
 * @(#)Id: Attachment.java, 13.03.2008 18:28:27, Dmitry Fedotov
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
public class Attachment implements Entity {

	private static final long serialVersionUID = 6567757113901714724L;

	private Long id;
	
	private String link;
	
	private boolean embedded = false;
	
	private Publication publication;
	
	public Long getId() {
		return id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isEmbedded() {
		return embedded;
	}

	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
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
	
	public String toString(){
		return link;
	}
}
