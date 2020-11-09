/*
* @(#)$Id: Shop.java,v 1.1, 2006-06-20 13:05:35Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.store_locator.domain;

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
public class Shop {

	private static Logger logger = Logger.getLogger(Shop.class);
    
    private Long  id;
    private Long  companyId;
    private String  country	= "";  
    private String  link	= "";
    
    public Shop() {    	
    }
	public Shop(Long id, String country) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.country = country;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
    
	public String toString () {
	   return ReflectionToStringBuilder.reflectionToString(this);
	}

}
