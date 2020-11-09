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
package com.negeso.module.core.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.core.domain.ArticleRevision;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public interface ArticleRevisionDao extends GenericDao<ArticleRevision, Long>{
	
	public List<ArticleRevision> listByArticleId(Long articleId);
}

