/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.friendly_url;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public enum UrlEntityType {
	PRODUCT_CATEGORY, PRODUCT, LIST_ITEM, JOB_VACANCY;
	
	public String getParam(){
		switch (this.ordinal()) {
		case 0:
			return "pmCatId";
		case 1:
			return "pmProductId";
		case 2:
			return "id";
		default:
			return null;
		}
	}
}

