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
package com.negeso.module.imp.extension;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

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

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class ImportObject {
	
	private Long id;

	public Long getId() {
		Validate.notNull(id, "id should not be null at this moment");
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Map<String, Object> row;

	public Map<String, Object> getRow() {
		if (row == null) {
			row = new LinkedHashMap<String, Object>();
		} 
		return row;
	}

	public void setRow(Map<String, Object> row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public void putProperty(String propertyName, Object propertyValue) {
		getRow().put(propertyName, propertyValue);
	}

	public String getTitle() {
		return "";
		//throw new IllegalStateException("This method should not be called");
	}
}

