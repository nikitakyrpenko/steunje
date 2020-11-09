/*
* @(#)$Id: Region.java,v 1.1, 2006-06-20 13:05:35Z, Svetlana Bondar$
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
 * @version                $Revision: 2$
 * @author                 Svetlana Bondar
 * 
 */
public class Region {
	
    private static Logger logger = Logger.getLogger(Region.class);
    
    private Long  id;
    private String  title = "";
    List<Company> companies = new ArrayList<Company> ();
    
    
	public Region() {
		
	}
	public Region(Long id, String title) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.title = title;
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

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public String toString () {
		   return ReflectionToStringBuilder.reflectionToString(this);
	}

}
