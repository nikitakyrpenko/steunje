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
package com.negeso.module.social.bo;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.negeso.framework.dao.LangualEntity;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SocialNetworkI18nValue implements LangualEntity{

	private static final long serialVersionUID = 8444536903894777430L;
	
	private Long id;
	private String value;
	private Long langId ;
	
	private SocialNetworkParam socialNetworkParam;		

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SocialNetworkParam getSocialNetworkParam() {
		return socialNetworkParam;
	}

	public void setSocialNetworkParam(SocialNetworkParam socialNetworkParam) {
		this.socialNetworkParam = socialNetworkParam;
	}
}

