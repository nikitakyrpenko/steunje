/*
* @(#)$Id: Field.java,v 1.1, 2007-02-01 18:12:02Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.wcms.field_configuration;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 *
 * TODO
 * 
 * @version                $Revision: 2$
 * @author                 sbondar
 * 
 */
public class Field {
	
	Long 	configurationId;
	String 	value;
	String 	title;
	String 	name;

	public String toString () {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	public Long getConfigurationId() {
		return configurationId;
	}
	public void setConfigurationId(Long configurationId) {
		this.configurationId = configurationId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Field(Long configurationId, String value, String title, String name) {
		super();
		this.configurationId = configurationId;
		this.value = value;
		this.title = title;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
