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

import com.negeso.framework.dao.I18NEntity;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class I18nCoreProperty extends I18NEntity<I18nCorePropertyValue> {

	private static final long serialVersionUID = 2721340164807227301L;
	private Long id;
	private String name;
	private String title;
	private Long moduleId;
	private Long siteId;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	public String getValue() {
		return getField().getValue();
	}

	public void setValue(String value) {
		getField().setValue(value);
	}

	@Override
	public I18nCorePropertyValue createI18nInstance() {
		I18nCorePropertyValue value = new I18nCorePropertyValue();
		value.setI18nCoreProperty(this);
		if (this.getId() != null && this.getId() > 0) {
			value.setLangId(getCurrentLangId());
			putField(getCurrentLangId(), value);
		}
		return value;
	}
}
