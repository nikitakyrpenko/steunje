/*
 * @(#)$Id: DatabaseResourceBundleMessageSource.java,v 1.2, 2007-01-09 18:27:11Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 3$
 *
 */
public class DatabaseResourceBundleMessageSource extends ResourceBundleMessageSource {

	private static final String BASE_NAME_DELIMITER = ",";
	
	/*
	 * @see org.springframework.context.support.ResourceBundleMessageSource#getResourceBundle(java.lang.String, java.util.Locale)
	 * @parameter basename The basename contains a list of dictionaries which
	 * will be used. The dictionaries are separated by comma.
	 */
	@Override
	protected ResourceBundle getResourceBundle(String basename, Locale locale) {
		String[] dictionaries = basename.split(BASE_NAME_DELIMITER);
		String[] dictChain = removeBlankChars(dictionaries); 
		return DatabaseResourceBundle.getChainResourceBundle(dictChain, locale.getLanguage());
	}

	private final static String[] removeBlankChars(String[] strings) {
		String[] res = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			res[i] = strings[i].trim();
		}
		return res;
	}

}
