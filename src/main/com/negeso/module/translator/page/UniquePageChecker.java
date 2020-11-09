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
package com.negeso.module.translator.page;

import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class UniquePageChecker {
	
	public static String getUniquePageName(String translatedName, Connection con, String toLangCode) throws SQLException {
		String tempPageName = translatedName;
		int i = 1;
		PageH duplicatedPage = PageService.getInstance().findByFileName(translatedName, con);
		while (duplicatedPage != null ) {
			tempPageName = translatedName.replace("_" + toLangCode + ".", "_" + i + "_" +toLangCode + ".");
			duplicatedPage = PageService.getInstance().findByFileName(tempPageName, con);
			i++;
		}
		return tempPageName;
	}
	
	public static String getUniquePageName(String value, Map<String, String> pages, String toLangCode) throws SQLException {
		String tempPageName = value;
		int i = 1;
		boolean flag = true;
		while (flag) {
			flag = false;
			for (Entry<String, String> entry : pages.entrySet()) {
				if (entry.getValue().equals(tempPageName)) {
					tempPageName = value.replace("_" + toLangCode + ".", "_" + i + "_" +toLangCode + ".");
					flag = true;
					break;
				}
			}
			i++;
		}
		return tempPageName;
	}
}

