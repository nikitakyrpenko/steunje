/*
 * @(#)Id: Category.java, 13.12.2007 19:21:32, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.bo;

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
public class BannerCategory implements Entity {

	private static final long serialVersionUID = -4300882450324166371L;
	
	private Long id;
	private String title;
	private Long orderNumber;
	private boolean isLeaf;
	private Long parentId;
	private Long siteId;
	
	public BannerCategory(){
		id = -1L;
	}
	
	public BannerCategory(Long id, Long parentId, boolean isLeaf, String title, Long orderNumber, Long siteId) {
		this.id = id;
		this.parentId = parentId;
		this.isLeaf = isLeaf;
		this.title = title;
		this.orderNumber = orderNumber;
		this.siteId = siteId;
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

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
