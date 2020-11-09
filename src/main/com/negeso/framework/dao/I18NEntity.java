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
package com.negeso.framework.dao;

import com.negeso.framework.domain.Language;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public abstract class I18NEntity<V extends LangualEntity> extends CustomizableEntity<Long, V> { 
	
	/**
	 * Identifier of the current language. Not ORM property
	 */
	private Long currentLangId;
	
	private boolean i18n = false;
	
	public I18NEntity() {
		for (Language language : Language.getItems()) {
			V v = createI18nInstance();
			v.setLangId(language.getId());
			putField(language.getId(), v);
		}
	}
	
	public void setI18n(boolean i18n) {
		this.i18n = i18n;
	}
	
	public boolean isI18n() {
		return i18n;
	}

	public void setCurrentLangId(Long currentLangId) {
		this.currentLangId = currentLangId; 
	}
	
	public Long getCurrentLangId() {
		return currentLangId; 
	}

	protected V putField(Long langId, V v) {
		return getCustomFields().put(langId, v);
	}
	
	public V getField() {
		V v = getField(currentLangId);
		if (v == null) {
			v = createI18nInstance();
		}
		return v;
	}

	public abstract V createI18nInstance();

	public boolean canBeCopiedToAllLanguages() {
		return (getId() < 1 || !isI18n());
	}
}
