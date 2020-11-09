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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public abstract class CustomizableEntity<K, V> implements Entity {

	private Map<K, V> customFields;

	public Map<K, V> getCustomFields() {
		if (customFields == null)
			customFields = new LinkedHashMap<K, V>();
		return customFields;
	}

	public void setCustomFields(Map<K, V> customFields) {
		this.customFields = customFields;
	}
	
	public V getField(K key) {
		return customFields.get(key); 
	}
}
