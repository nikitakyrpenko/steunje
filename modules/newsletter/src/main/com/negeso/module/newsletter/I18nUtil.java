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
package com.negeso.module.newsletter;

import java.util.List;

import com.negeso.framework.dao.I18NEntity;
import com.negeso.module.newsletter.bo.SubscriberGroup;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class I18nUtil {

	public static void setCurrentLanguageForList(List<? extends I18NEntity<?>> entities, Long langId) {
		for (I18NEntity<?> entity : entities) {
			entity.setCurrentLangId(langId);
		}
	}
	
	// TODO refactor SubscriberGroup to i18n entity and remove this method
	public static void setCurrentLanguageForSubscriberGroups(List<SubscriberGroup> list, Long langId) {
		for (SubscriberGroup sg : list) {
			sg.setLang_id(langId);
		}
	}
	
}
