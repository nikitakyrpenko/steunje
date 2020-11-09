/*
 * @(#)Id: Banner2Group.java, 24.12.2007 13:00:54, Dmitry Fedotov
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
public class Banner2Group implements Entity {
	
	private static final long serialVersionUID = 1935628760181172273L;
	
	private Long id;
	private Long bannerId;
	private Long groupId;

	public Banner2Group() {}
	
	public Banner2Group(Long id, Long bannerId, Long groupId) {
		this.id = id;
		this.bannerId = bannerId;
		this.groupId = groupId;
	}
	
	public Banner2Group(Long bannerId, Long groupId) {
		this(null, bannerId, groupId);
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
}
