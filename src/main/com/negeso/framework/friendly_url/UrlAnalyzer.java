/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
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
public class UrlAnalyzer {
	
	public static String parse (String str) {
		return str == null ? "" : str.replaceAll("\\s|\\t|\\W","_")
					        		 .replaceAll("(__*)|(_$)","_")
					        		 .replaceAll("_$","")
					        		 .toLowerCase();
	}
}

