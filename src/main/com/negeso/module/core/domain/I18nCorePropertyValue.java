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

import com.negeso.framework.dao.LangualEntity;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class I18nCorePropertyValue implements LangualEntity {

	private static final long serialVersionUID = 5288627219937912216L;
	
	private Long id;
	private String value;
	private Long langId;
	private I18nCoreProperty i18nCoreProperty;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public I18nCoreProperty getI18nCoreProperty() {
		return i18nCoreProperty;
	}

	public void setI18nCoreProperty(I18nCoreProperty i18nCoreProperty) {
		this.i18nCoreProperty = i18nCoreProperty;
	}

}
