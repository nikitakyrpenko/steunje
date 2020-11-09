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
package com.negeso.module.parametrized_link.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.parametrized_link.domain.ParametrizedLink;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class GetParametrizedLinksCommand extends AbstractCommand {
	
	private static Logger logger = Logger.getLogger(GetParametrizedLinksCommand.class);
	
	private Long getLangId() {
		return getRequestContext().getSession().getLanguage().getId();
	}
	
	@SuppressWarnings("unchecked")
	private List<ParametrizedLink> getParametrizedLinks() {
		logger.debug("+-");
		Session session = DBHelper.openSession();
		try {
			return (List<ParametrizedLink>) session.createQuery(
					"FROM ParametrizedLink WHERE siteId = " + Env.getSiteId() +
					" AND langId = " + getLangId()).list();
		} finally {
			DBHelper.close(session);
		}
	}
	
	private void buildParametrizedLinks(Element plListElement, List<ParametrizedLink> plList) {
		logger.debug("+");
		
		for (ParametrizedLink pl:plList) {
			Element pLink = Xbuilder.addEl(plListElement, "plink", "");
			Xbuilder.setAttr(pLink, "id", pl.getId());
			Xbuilder.setAttr(pLink, "url", "/pl_link?pl_id=" + pl.getId());
			Xbuilder.setAttr(pLink, "title", pl.getTitle());
			Xbuilder.setAttr(pLink, "secured", pl.getIsSecured());
		}
		
		logger.debug("-");
	}
	
	public ResponseContext execute() {
		
		logger.debug("-");
		
		ResponseContext response = new ResponseContext();		
		try {
			Element linksList = Xbuilder.createTopEl("linksList");
			buildParametrizedLinks(linksList, getParametrizedLinks());
			response.setResultName(RESULT_SUCCESS);
			response.getResultMap().put( OUTPUT_XML, linksList.getOwnerDocument() );
		}	
		catch (Exception e) {
			logger.error("Can't get parametrized links");
			logger.error("Exception - " + e);
			response.setResultName(RESULT_FAILURE);
			response.getResultMap().put( OUTPUT_XML, getStubElement(e) );
		}
  
		logger.debug("+");
		
		return response;		
	}

	private Object getStubElement(Exception e) {
		Element linksList = Xbuilder.createTopEl("linksList");
		Xbuilder.addEl(linksList, "error", e);
		return linksList.getOwnerDocument();
	}

}

