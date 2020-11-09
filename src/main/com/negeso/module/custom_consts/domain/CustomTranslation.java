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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.negeso.module.core.domain.Reference;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class CustomTranslation {
	
	private static Logger logger = Logger.getLogger(CustomTranslation.class);
	
	private Long id;
	private String translation = "";
	private Reference language;
	private CustomConst customConst;
	
	public String getTranslation() {
		return translation;
	}
	
	public void setTranslation(String translation) {
		logger.debug("+-");
		if (translation != null)
			this.translation = translation.trim();
		else 
			this.translation ="";
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Reference getLanguage() {
		return language;
	}
	public void setLanguage(Reference language) {
		this.language = language;
	} 
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public CustomConst getCustomConst() {
		return customConst;
	}
	public void setCustomConst(CustomConst customConst) {
		this.customConst = customConst;
	}

	
}

