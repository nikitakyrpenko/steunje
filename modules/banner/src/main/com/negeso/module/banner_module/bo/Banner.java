/*
 * @(#)Id: Banner.java, 13.12.2007 19:25:33, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.bo;

import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class Banner implements Entity {

	private static final long serialVersionUID = -4216863918494199188L;
	
	public static final String IMAGE_TYPE_IMAGE = "image"; //$NON-NLS-N$
	public static final String IMAGE_TYPE_FLASH = "flash"; ////$NON-NLS-N$
	
	private Long id = -1L;
	private String title;
	private Long categoryId;
	private Long bannerTypeId;
	private String imageUrl;
	private Long priority;
	private String url;
	private Long imageType = 1L; /*default - image*/
	private Long maxClicks = 0L;
	private Long maxViews = 0L;
	
	private boolean inNewWindow;
	private boolean activated;
	
	private Date publishDate;
	private Date expiredDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getBannerTypeId() {
		return bannerTypeId;
	}

	public void setBannerTypeId(Long bannerTypeId) {
		this.bannerTypeId = bannerTypeId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getMaxClicks() {
		return maxClicks;
	}

	public void setMaxClicks(Long maxClicks) {
		this.maxClicks = maxClicks;
	}

	public Long getMaxViews() {
		return maxViews;
	}

	public void setMaxViews(Long maxViews) {
		this.maxViews = maxViews;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public void setActivated(boolean activated){
		this.activated = activated;
	}
	
	public boolean getActivated(){
		return this.activated;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public boolean getInNewWindow() {
		return inNewWindow;
	}

	public void setInNewWindow(boolean inNewWindow) {
		this.inNewWindow = inNewWindow;
	}
	
	public void setImageType(Long imageType) {
		this.imageType = imageType;
	}
	
	public Long getImageType() {
		return imageType;
	}

}
