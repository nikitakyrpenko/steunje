/*
* @(#)$Id: Category.java,v 1.4, 2007-02-07 10:56:14Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;

/**
 *
 * TODO
 * 
 * @version                $Revision: 5$
 * @author                 sbondar
 * 
 */
public class Category {
	private static Logger logger = Logger.getLogger(Category.class);
	
	Long 	id;
	Date 	publishDate;
	Date 	expiredDate;
	Long 	siteId;
	int 	orderNumber;
	boolean leaf = false;
	Map 	titles = new HashMap<Long, String> ();

	Category 	parentCategory;
	List<Category> 		subCategories 	= new ArrayList<Category> ();
	List<Event> 		events 			= new ArrayList<Event> ();
	
	public Category(int orderNumber, Category parentCategory) {
		super();
		this.siteId = Env.getSiteId();
		this.orderNumber = orderNumber;
		this.leaf = true;
		this.parentCategory = parentCategory;
	}
	
	public boolean checkExpired() {
        Timestamp now = new Timestamp( System.currentTimeMillis() );                
        return !( ( this.getPublishDate() == null 
        		|| this.getPublishDate().before(now) || this.getPublishDate().equals(now) ) 
        		&& ( this.getExpiredDate() == null || this.getExpiredDate().after(now) ) );
	}

	public Category() {
	}
	
	public String toString () {
		  return ReflectionToStringBuilder.reflectionToString(this);
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
	public Category getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
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
	public Map getTitles() {
		return titles;
	}
	public void setTitles(Map titles) {
		this.titles = titles;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<Category> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<Category> subCategories) {
		this.subCategories = subCategories;
	}
	
	public String getDefaultTitle() {
		logger.debug("+-");
		return (this.getTitles().get(Env.getSite().getLangId())==null)?
				" ":this.getTitles().get(Env.getSite().getLangId()).toString();
	}

	public boolean areParentCategoriesExpired() {
		Category parentCategory = this;
		while (parentCategory != null) {
			if (parentCategory.checkExpired()) {
				return true;
			} else {
				parentCategory = parentCategory.getParentCategory();
			}
		}
		return false;
	}
	
	public int getLevel() {
		int i = 1;
		Category  tmpCategory = this.parentCategory;
		while ( tmpCategory != null ) {
			i++;
			tmpCategory = tmpCategory.getParentCategory();
		}
		return i;
	}
	
	

}
