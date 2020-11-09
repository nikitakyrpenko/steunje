/*
 * @(#)$Id: ArticleService.java,v 1.0, 2007-03-16 09:07:41Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.wcms.service;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.module.translator.service.ITranslateService;

/**
 * 
 * @TODO
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 1$
 *
 */
public class ArticleService extends AbstractJdbcService {
	
	private static Logger logger = Logger.getLogger( ArticleService.class );

	
	public Article createArticleCopy(Long sourceArticleId) {
		Article source = null;

		if (sourceArticleId == null) {
			source = new Article();
			logger.error("Source article is NULL");
		} else {
			try {
				source = Article.findById(this.getConnection(), sourceArticleId);
			} catch (ObjectNotFoundException e) {
				logger.error("error", e);
				source = new Article();
			}
		}
		Article dest = new Article();
		dest.setClass_(source.getClass_());
		dest.setContainerId(source.getContainerId());
		dest.setHead(source.getHead());
		dest.setLang_id(source.getLangId());
		dest.setText(source.getText());
		dest.insert(this.getConnection());
		return dest;
	}
	
	public Article createAndTranslateArticleCopy(Long sourceArticleId, ITranslateService translateService, Language from, Language to) {
		Article source = null;

		if (sourceArticleId == null) {
			source = new Article();
			logger.error("Source article is NULL");
		} else {
			try {
				source = Article.findById(this.getConnection(), sourceArticleId);
			} catch (ObjectNotFoundException e) {
				logger.error("error", e);
				source = new Article();
			}
		}
		Article dest = new Article();
		dest.setClass_(source.getClass_());
		dest.setContainerId(source.getContainerId());
		dest.setHead(source.getHead());
		dest.setLang_id(to.getId());
		dest.setText(translateService.translate(source.getText(), from.getCode(), to.getCode()));
		dest.insert(this.getConnection());
		return dest;
	}


	public void copyArticleText(Long sourceArticleId, Long destArticleId) {
		Article source = null;
		Article dest = null;
		try {
			source = Article.findById(this.getConnection(), sourceArticleId);
			dest = Article.findById(this.getConnection(), destArticleId);
		} catch (ObjectNotFoundException e) {
			logger.error("error", e);
			return;
		}
		
		dest.setText(source.getText());
		dest.update(this.getConnection());
	}
	
	public void copyAndTranslateArticleText(Long sourceArticleId, Long destArticleId, ITranslateService translateService, Language from, Language to) {
		Article source = null;
		Article dest = null;
		try {
			source = Article.findById(this.getConnection(), sourceArticleId);
			dest = Article.findById(this.getConnection(), destArticleId);
		} catch (ObjectNotFoundException e) {
			logger.error("error", e);
			return;
		}
		
		dest.setText(translateService.translate(source.getText(), from.getCode(), to.getCode()));
		dest.update(this.getConnection());
	}


}
