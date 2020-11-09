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
package com.negeso.module.custom_consts.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class CustomConst {

	private static Logger logger = Logger.getLogger(CustomConst.class);
	
	private Long id;
	private Long moduleId;
	private String key;
	private Set<CustomTranslation> translations = new HashSet<CustomTranslation>(); 
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key.toUpperCase();
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public Set<CustomTranslation> getTranslations() {
		return translations;
	}
	public void setTranslations(Set<CustomTranslation> translations) {
		this.translations = translations;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
	public String getEnglishTranslation() {
		logger.debug("+-");
		return getTranslation("en");
	}
	public String getTranslation(String langCode) {
		Validate.isTrue(StringUtils.isNotBlank(langCode));
		Iterator iterator = this.translations.iterator();
		while (iterator.hasNext()) {
			CustomTranslation translation = (CustomTranslation) iterator.next();
			if (langCode.equals(translation.getLanguage().getCode())) {
				return translation.getTranslation();
			}
		}
		return "";
	}
	
}

