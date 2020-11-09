/*
 * @(#)Id: Publication2Article.java, 09.04.2008 17:10:54, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.dao;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.newsletter.bo.Publ2Article;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface Publ2ArticleDao extends GenericDao<Publ2Article, Long>{
	
	public Publ2Article findByPublicationIdLangId(Long publicationId, Long langId);

}
