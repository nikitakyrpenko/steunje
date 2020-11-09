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
package com.negeso.module.translator.service;

import com.negeso.framework.domain.Language;
import com.negeso.module.translator.service.replace.IReplacer;

import java.util.List;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public interface ITranslateService {
	
	String translate(String text, String fromLangCode, String toLangCode);
	
	String[] translate(String text[], String fromLangCode, String toLangCode);
	
	String translateHtml(String html, String fromLangCode, String toLangCode);
	
	String translatePageName(String pageName, String fromLangCode, String toLangCode);
	
	Language[] getSupportedLaguages();
	
	void setPageNameTranslatable(boolean isPageNameTranslatable);
	int getCharsCounter();

	void setWithoutHtmlTags(boolean isWithoutHtmlTags);
	public List<IReplacer> getReplacers() ;
}

