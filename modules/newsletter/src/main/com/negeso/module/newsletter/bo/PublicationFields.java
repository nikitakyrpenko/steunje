/*
 * @(#)Id: CustomFields.java, 04.03.2008 10:53:43, Dmitry Fedotov
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
import com.negeso.framework.domain.Article;


/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class PublicationFields implements Entity{
	
	private static final long serialVersionUID = 1412741464787410314L;

	private Long id;
	private String title;
	private String description;
	private String plainText = "";
	private Article article = new Article();
	
	private Publication publication;
	private Long langId;

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public Publication getPublication() {
		return publication;
	}

	public void setPublication(Publication publication) {
		this.publication = publication;
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
	
	public String getPlainText() {
		return plainText;
	}
	
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Article getArticle() {
		return article;
	}
	
}
