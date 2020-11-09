/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.domain;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAttribute;

import com.negeso.framework.page.PageH.Changefreq;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class BriefPage {
	
	private String name;
	private String editUser;
	private Timestamp editDate;
	private Timestamp publishDate;
	private Timestamp expiredDate;
	private String template;
	private String pageGroup;
	
	private boolean sitemap;
	private String sitemapPrior;
	private String sitemapFreq;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEditUser() {
		return editUser;
	}

	public void setEditUser(String editUser) {
		this.editUser = editUser;
	}

	public Timestamp getEditDate() {
		return editDate;
	}

	public void setEditDate(Timestamp editDate) {
		this.editDate = editDate;
	}

	public Timestamp getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Timestamp publishDate) {
		this.publishDate = publishDate;
	}

	public Timestamp getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Timestamp expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getPageGroup() {
		return pageGroup;
	}

	public void setPageGroup(String pageGroup) {
		this.pageGroup = pageGroup;
	}

	public boolean isSitemap() {
		return sitemap;
	}

	public void setSitemap(boolean sitemap) {
		this.sitemap = sitemap;
	}

	public String getSitemapPrior() {
		return sitemapPrior;
	}

	public void setSitemapPrior(String sitemapPrior) {
		this.sitemapPrior = sitemapPrior;
	}

	public String getSitemapFreq() {
		return sitemapFreq;
	}

	public void setSitemapFreq(String sitemapFreq) {
		this.sitemapFreq = sitemapFreq;
	}
	
	
}
