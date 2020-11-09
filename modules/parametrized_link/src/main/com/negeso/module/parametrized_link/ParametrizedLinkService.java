/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.parametrized_link;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.framework.Env;
import com.negeso.module.parametrized_link.domain.ParametrizedLink;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
@Transactional
public class ParametrizedLinkService extends HibernateDaoSupport implements IParametrizedLinkService{
	
    private static Logger logger = Logger.getLogger(ParametrizedLinkService.class);

	@SuppressWarnings("unchecked")
	public List<ParametrizedLink> getParametrizedLinks(long langId) {
    	logger.debug("+-");
    	List<ParametrizedLink> tmp =  (List<ParametrizedLink>) this.getHibernateTemplate().find( 
    		"FROM ParametrizedLink pl WHERE pl.siteId = " + Env.getSiteId() +
    		" AND pl.langId = " + langId
    	); 
    	logger.debug("tmp = " + tmp.size());
    	return tmp;
	}

	public void deleteLink(String linkId) {
		logger.debug("+-");
		this.getHibernateTemplate().delete(
				this.getHibernateTemplate().load(ParametrizedLink.class, new Long(linkId)));

	}

	public void addParametrizedLink(ParametrizedLink parametrizedLink) {
		getHibernateTemplate().save(parametrizedLink);
	}

	public void updateParametrizedLink(ParametrizedLink parametrizedLink) {
		getHibernateTemplate().update(parametrizedLink);
	}

	public ParametrizedLink getParametrizedLink(long linkId) {
		return (ParametrizedLink) getHibernateTemplate().load(ParametrizedLink.class, linkId);
	}
}

