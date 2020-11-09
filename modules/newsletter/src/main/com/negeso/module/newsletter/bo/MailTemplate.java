/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.bo;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.negeso.framework.Env;
import com.negeso.framework.dao.I18NEntity;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class MailTemplate extends I18NEntity<MailTemplateI18nFields> {

	private static final long serialVersionUID = -5884542618125765431L;

	private Long id = -1L;
	
	private boolean confirmationText = false;

	private Long siteId = Env.getSiteId();
	
	public MailTemplate() {
		super();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return getField().getTitle();
	}
	
	public void setTitle(String title) {
		getField().setTitle(title);
	}
	
	public String getText() {
		return getField().getText();
	}
	
	public void setText(String text) {
		getField().setText(text);
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	@Override
	public MailTemplateI18nFields createI18nInstance() {
		MailTemplateI18nFields fields = new MailTemplateI18nFields();
		fields.setMailTemplate(this);
		return fields;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public boolean isConfirmationText() {
		return confirmationText;
	}

	public void setConfirmationText(boolean confirmationText) {
		this.confirmationText = confirmationText;
	}

}
