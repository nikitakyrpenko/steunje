/*
 * @(#)Id: Banner2Page.java, 28.12.2007 17:34:31, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.bo;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class Banner2Page implements Entity {
	
	private static final long serialVersionUID = -3734895855737691689L;
	
	private Long id;
	private Long bannerId;
	private Long pageId;
	private Long pmProductId;
	private Long pmCategoryId;
	
	/*
	 * possible values: menuitem, unlinked, product
	 * */
	private String type;
	
	public Banner2Page() {}

	public Banner2Page(Long bannerId, Long pageId, Long pmProductId, Long pmCategoryId, String type) {
		this.bannerId = bannerId;
		this.pageId = pageId;
		this.pmProductId = pmProductId;
		this.pmCategoryId = pmCategoryId;
		this.type = type;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getBannerId() {
		return bannerId;
	}
	
	public void setBannerId(Long bannerId) {
		this.bannerId = bannerId;
	}
	
	public Long getPageId() {
		return pageId;
	}
	
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public Long getPmProductId() {
		return pmProductId;
	}

	public void setPmProductId(Long pmProductId) {
		this.pmProductId = pmProductId;
	}

	public Long getPmCategoryId() {
		return pmCategoryId;
	}

	public void setPmCategoryId(Long pmCategoryId) {
		this.pmCategoryId = pmCategoryId;
	}
}
