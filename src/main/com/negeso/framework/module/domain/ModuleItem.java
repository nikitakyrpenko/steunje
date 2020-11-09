/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.module.domain;

import com.negeso.framework.dao.Entity;
import com.negeso.framework.module.Module;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ModuleItem implements Entity{
	
	private static final long serialVersionUID = 6022809255790462345L;
	
	private Long id = -1L;
	private String dictionaryKey;
	private String url;
	private boolean hideFromUser;
	private Long orderNumber;
	private String helpUrl;
	
	private Module module;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDictionaryKey() {
		return dictionaryKey;
	}

	public void setDictionaryKey(String dictionaryKey) {
		this.dictionaryKey = dictionaryKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isHideFromUser() {
		return hideFromUser;
	}

	public void setHideFromUser(boolean hideFromUser) {
		this.hideFromUser = hideFromUser;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}

}

