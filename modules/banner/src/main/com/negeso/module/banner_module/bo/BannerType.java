/*
 * @(#)Id: BannerType.java, 17.12.2007 14:00:32, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.bo;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerType implements Entity {
	
	private static final long serialVersionUID = 1182418754913006140L;
	
	private Long id;
	private String title;
	private int width;
	private int height;
	
	public BannerType(){
		id = -1L;
	}
	
	public BannerType(Long id, String title, int width, int height) {
		this.id = id;
		this.title = title;
		this.width = width;
		this.height = height;
	}

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
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

}
