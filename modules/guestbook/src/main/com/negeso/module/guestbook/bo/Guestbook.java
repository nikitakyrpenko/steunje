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

import com.negeso.framework.dao.Entity;


/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class Guestbook implements Entity {

	private static final long serialVersionUID = 4548594876617877815L;
	
	private Long id;
	private Long siteId; 
	private String name;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long newId) {
		id = newId;
	}
	
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	public Long getSiteId() {
		return siteId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
