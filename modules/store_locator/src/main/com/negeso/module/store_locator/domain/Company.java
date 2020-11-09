/*
* @(#)$Id: Company.java,v 1.2, 2006-06-20 13:58:24Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.store_locator.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 *
 * TODO
 * 
 * @version                $Revision: 3$
 * @author                 Svetlana Bondar
 * 
 */
public class Company {

	
    private static Logger logger = Logger.getLogger(Company.class);
    
    private Long  id;
    private Long  regionId;
    private String  image 	= "";
    private String  title 	= "";
    private String  link	= "";
    List<Shop> shops = new ArrayList<Shop> ();
    
	public Company() {
		
	}
	public Company(Long id, Long regionId, String title) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.regionId = regionId;
		this.title = title;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Long getRegionId() {
		return regionId;
	}
	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    
	public String toString () {
		   return ReflectionToStringBuilder.reflectionToString(this);
	}
	public List<Shop> getShops() {
		return shops;
	}
	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}

}
