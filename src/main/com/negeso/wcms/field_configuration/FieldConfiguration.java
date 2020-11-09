/*
* @(#)$Id: FieldConfiguration.java,v 1.2, 2007-02-01 18:12:04Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.wcms.field_configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * TODO
 * 
 * @version                $Revision: 3$
 * @author                 sbondar
 * 
 */
public class FieldConfiguration {

	public static final String STRING_TYPE 		= "String";
	public static final String RTE_ARTICLE_TYPE = "RteArticle";
	
	Long 	id;
	String 	name;
	String 	type;
	int 	orderNumber;
	boolean requiredField = false;
	Map	titles = new HashMap<Long, String> ();
	
//	public static FieldConfiguration getFake() {
//		FieldConfiguration fieldConfig = new FieldConfiguration();
//		fieldConfig.id = new Long(1);
//		fieldConfig.name = "field";
//		fieldConfig.type = STRING_TYPE;
//		fieldConfig.orderNumber = 0;
//		fieldConfig.required = true;
//		fieldConfig.titles = new HashMap<Long, String> ();
//		fieldConfig.titles.put(new Long(1), "Field");
//		fieldConfig.titles.put(new Long(2), "Fieeeeld");
//		return fieldConfig;
//	}
	
	public String toString () {
		  return ReflectionToStringBuilder.reflectionToString(this);
	}	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Map getTitles() {
		return titles;
	}
	public void setTitles(Map titles) {
		this.titles = titles;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public boolean isRequiredField() {
		return requiredField;
	}

	public void setRequiredField(boolean requiredField) {
		this.requiredField = requiredField;
	}
	

}
