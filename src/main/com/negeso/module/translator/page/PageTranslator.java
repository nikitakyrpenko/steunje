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

import com.negeso.framework.domain.Language;
import com.negeso.module.translator.service.AbstractTranslator;
import com.negeso.module.translator.service.ITranslateService;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.util.Map;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PageTranslator extends AbstractTranslator {
	
	public PageTranslator(ITranslateService translateService) {
		super(translateService);
	}
	
	PageTranslationStrategy translationStrategy = null;
	
	public PageTranslator(ITranslateService translateService, TranslationStrategyType translationStrategyType) {
		super(translateService);
		if (TranslationStrategyType.all.equals(translationStrategyType)) {
			translationStrategy = new PageTranslationStrategy(translateService, this);
		} else if(TranslationStrategyType.allExceptSpecial.equals(translationStrategyType)) {
			translationStrategy = new AllExceptSpecialPageTranslationStrategy(translateService, this);
		} else if (TranslationStrategyType.newAdded.equals(translationStrategyType)) {
			translationStrategy = new NewAddedPageTranslationStrategy(translateService, this);
		}
	}
	
	public void setListIdsMap(Map<Long, Long> listIdsMap) {
		if (translationStrategy != null) {
			translationStrategy.setListIdsMap(listIdsMap);
		}
	}

	@Override
	public void clean(Connection con, Language to) {
		if (translationStrategy != null) {
			translationStrategy.clean(con, to);
		}
	}

	@Override
	public void clean(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyAndTranslate(Connection con, Language from, Language to) {
		if (translationStrategy != null) {
			translationStrategy.copyAndTranslate(con, from, to);
		}
	}

	@Override
	public void copyAndTranslate(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}
	
	public Map<Long, Long> getPageIdsMap() {
		return translationStrategy.getPageIdsMap();
	}

	public PageTranslationStrategy getTranslationStrategy() {
		return translationStrategy;
	}
	
	
	
	

}

