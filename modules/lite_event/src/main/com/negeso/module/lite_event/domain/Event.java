/*
* @(#)$Id: Event.java,v 1.7, 2007-02-06 10:12:59Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.negeso.wcms.field_configuration.Field;
import com.negeso.wcms.field_configuration.FieldConfiguration;
import com.negeso.wcms.field_configuration.FieldConfigurationCache;

/**
 *
 * TODO
 * 
 * @version                $Revision: 8$
 * @author                 Svetlana Bondar
 * 
 */
public class Event {

	private static Logger logger = Logger.getLogger(Event.class);
	
	Long 	id;
	Date 	date;
	Date 	publishDate;
	Date 	expiredDate;
	Long 	langId;
	Long 	siteId;
	Long 	eventDetailsId;
	Long 	fieldSetId;
	Long 	categoryId;
	String 	defaultTitle;
	Category 	category;
	Map<Long, Field> fields = new HashMap<Long, Field> ();
	
	public Map getEventDetails() {
		Map eventDetailsMap = new HashMap();
		for (Field field: this.getFields().values()) {
			eventDetailsMap.put(field.getName(), field.getValue());
		}
		eventDetailsMap.put("id", this.getEventDetailsId());
		eventDetailsMap.put("langId", this.getLangId());
		eventDetailsMap.put("eventId", this.getId());
		return eventDetailsMap;
	}

	public void setFieldsFromEventDetails( Map eventDetailsMap, HibernateTemplate hibernateTemplate ) {
		logger.debug("+");
		for (FieldConfiguration fieldConf: FieldConfigurationCache.getInstance().getFieldsConfiguration(this.getFieldSetId(), hibernateTemplate)) {
			logger.debug("fieldConf = " + fieldConf);
			this.getFields().put( fieldConf.getId(), new Field(
					fieldConf.getId(), 
					(String) eventDetailsMap.get(fieldConf.getName()),
					fieldConf.getTitles().get(langId).toString(),
					fieldConf.getName()
			) );
		}
		this.setEventDetailsId((Long)eventDetailsMap.get("id"));
		this.setLangId((Long)eventDetailsMap.get("langId"));
		logger.debug("-");
	}
	
	public String toString () {
		  return ReflectionToStringBuilder.reflectionToString(this);
	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getEventDetailsId() {
		return eventDetailsId;
	}
	public void setEventDetailsId(Long eventDetailsId) {
		this.eventDetailsId = eventDetailsId;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
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
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public Long getFieldSetId() {
		return fieldSetId;
	}

	public void setFieldSetId(Long fieldSetId) {
		this.fieldSetId = fieldSetId;
	}

	public Map<Long, Field> getFields() {
		return fields;
	}

	public void setFields(Map<Long, Field> fields) {
		this.fields = fields;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getDefaultTitle() {
		return defaultTitle;
	}

	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}
	
}
