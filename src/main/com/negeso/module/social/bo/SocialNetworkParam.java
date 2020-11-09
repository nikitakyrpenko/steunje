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

import com.negeso.framework.dao.I18NEntity;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class SocialNetworkParam extends I18NEntity<SocialNetworkI18nValue>{

	private static final long serialVersionUID = 5733471753762807852L;
	
	private Long id = -1L;
	private String code = null;
	private SocialNetwork socialNetwork = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SocialNetwork getSocialNetwork() {
		return socialNetwork;
	}

	public void setSocialNetwork(SocialNetwork socialNetwork) {
		this.socialNetwork = socialNetwork;
	}
	
	public String getValue() {
		return getField().getValue();
	}
	
	public void setValue(String value) {
		getField().setValue(value);
	}
	
	@Override
	public SocialNetworkI18nValue createI18nInstance() {
		SocialNetworkI18nValue value = new SocialNetworkI18nValue();
		value.setSocialNetworkParam(this);
		value.setLangId(getCurrentLangId());
		putField(getCurrentLangId(), value);
		return value;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
