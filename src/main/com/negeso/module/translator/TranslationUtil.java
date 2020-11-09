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
package com.negeso.module.translator;

import org.apache.commons.lang.StringUtils;

;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class TranslationUtil {
	
	public static final String  CHAR = "_",
								DOUBLE_CHAR_REPLACER = "?^",
								LESS_THAN = "&lt;",
								GREATE_THAN = "&gt;";
	
	
	public static String prepareUntranslatableWord(String str) {
		if (StringUtils.isNotBlank(str)) {
			StringBuilder sb = new StringBuilder(str.length() * 2 - 1);
			sb.append(LESS_THAN);
			for (int i = 0; i < str.length(); i++) {
				sb.append(str.charAt(i));
				if (i < str.length() - 1) {
					sb.append(CHAR);
				}
			}
			sb.append(GREATE_THAN);
			str = sb.toString();
		}
		return str;
	}
	
	public static String prepareUntranslatableWordBack(String str) {
		if (StringUtils.isNotBlank(str)) {
			str =  StringUtils.replace(str, CHAR + CHAR, DOUBLE_CHAR_REPLACER);
			str = StringUtils.replaceChars(str, CHAR, StringUtils.EMPTY);
			str = StringUtils.replace(str, LESS_THAN, StringUtils.EMPTY);
			str = StringUtils.replace(str, GREATE_THAN, StringUtils.EMPTY);
			str = StringUtils.replace(str, DOUBLE_CHAR_REPLACER, CHAR);
		}
		return str;
	}
	
}

