/*
 * @(#)Id: PageDescriptior.java, 26.12.2007 14:44:05, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.site_map;

import java.util.List;

import com.negeso.framework.friendly_url.UrlEntityType;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class PageDescriptor {

	private Long id;
	private String title;
	private boolean isLeaf;
	private boolean isExpired;
	private String href;
	private Long pageId;
	private List<PageDescriptor> content;
	private String contentStatus;
	private String friendlyHref;
	private UrlEntityType type;
	
	public PageDescriptor(){}
	
	public PageDescriptor(Long id, String title, boolean isLeaf,
			boolean isExpired, Long pageId, List<PageDescriptor> content, String href, String contentStatus) {
		this.id = id;
		this.title = title;
		this.href = href;
		this.isLeaf = isLeaf;
		this.isExpired = isExpired;
		this.pageId = pageId;
		this.content = content;
		this.contentStatus = contentStatus;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	public List<PageDescriptor> getContent() {
		return content;
	}

	public void setContent(List<PageDescriptor> content) {
		this.content = content;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getContentStatus() {
		return contentStatus;
	}

	public void setContentStatus(String contentStatus) {
		this.contentStatus = contentStatus;
	}

	public String getFriendlyHref() {
		return friendlyHref;
	}

	public void setFriendlyHref(String friendlyHref) {
		this.friendlyHref = friendlyHref;
	}

	public UrlEntityType getType() {
		return type;
	}

	public void setType(UrlEntityType type) {
		this.type = type;
	}
}
