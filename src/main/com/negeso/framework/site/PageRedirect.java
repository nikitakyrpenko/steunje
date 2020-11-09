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
package com.negeso.framework.site;

import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;

/**
 * 
 * @TODO
 * 
 * @author Andriy Zagorulko
 * @version $Revision: $
 * 
 */
public class PageRedirect implements Entity {

	private static final long serialVersionUID = -1;

	private Long id = -1L;
	private Long langId;
	private String countryCode;
	private Long siteId = 1L;
	private Long order;
	private String maskUrlFrom;
	private String redirectUrl;
	
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
	public String getMaskUrlFrom() {
		return maskUrlFrom;
	}
	public void setMaskUrlFrom(String maskUrlFrom) {
		this.maskUrlFrom = maskUrlFrom;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public Long getOrder() {
		return order;
	}
	public void setOrder(Long order) {
		this.order = order;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getLangCode() throws CriticalException, ObjectNotFoundException {
		if (langId == null)
			return "";
		if(langId == 0){
			return "*";
		}
		return Language.findById(langId).getCode();
	}
	
}
