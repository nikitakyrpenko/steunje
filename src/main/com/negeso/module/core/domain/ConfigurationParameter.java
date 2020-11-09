/*
 * @(#)$Id: ConfigurationParameter.java,v 1.0, 2007-04-11 06:59:54Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class ConfigurationParameter {
	
	private Long moduleId;
	
    private Long id;
    private String name;
    private String value;
    private String title;
    private String description;
    private boolean visible = true;
    private boolean readonly = false;
    private boolean required = true;
    private boolean resetCache = false;
    private Long siteId;
    private String fieldType;
  

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long module) {
		this.moduleId = module;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean getReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
    
	public boolean getGlobal() {
		return siteId == null;
	}

	public void setGlobal(boolean isGlobal) {
		if (isGlobal) {
			this.siteId = null;
		} else {
			this.siteId = Env.getSiteId();	
		}
	}
	
	public boolean getResetCache() {
		return resetCache;
	}

	public void setResetCache(boolean resetCache) {
		this.resetCache = resetCache;
	}
	
	public void setNewModuleId(Long moduleId) {
		// for binding from request
		this.moduleId = moduleId;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
}

