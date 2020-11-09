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

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.w3c.dom.Element;

import com.negeso.framework.condition.ConditionFailedException;
import com.negeso.framework.condition.commands.ConditionalCommand;
import com.negeso.framework.condition.conditions.LoginCondition;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.ParameterException;
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
public class DecodeParametrizedLinkCommand extends ConditionalCommand {

	private static Logger logger = Logger.getLogger(DecodeParametrizedLinkCommand.class);

	private static final String INPUT_PARAMETRIZED_LINK_ID = "pl_id";
	
	@Override
	protected void conditionalExecute() throws ConditionFailedException {
		logger.debug("+");
		try {
			if (getParametrizedLink().getIsSecured()) {
				getCondition().checkCondition();
			}	
			getResponseContext().setResultName(RESULT_SUCCESS);
			getResponseContext().getResultMap().put( OUTPUT_XML, buildLinkElement(getParametrizedLink().getUrl()) );
		} catch (ParameterException e) {
			getResponseContext().setResultName(RESULT_FAILURE);
			
		} catch (DataAccessException e) {
			
			logger.error("Can't get parametrized link from DB");
			logger.error("Exception - " + e);
			getResponseContext().setResultName(RESULT_FAILURE);
			
		}	
		logger.debug("-");
	}
	
	private ParametrizedLink getParametrizedLink() throws DataAccessException, ParameterException {
		if (getRequestContext().getParameter(INPUT_PARAMETRIZED_LINK_ID) == null) 
			throw ParameterException.createFormatted(INPUT_PARAMETRIZED_LINK_ID, "Parametrized link module");
		return (ParametrizedLink) DBHelper.getHibernateTemplate().
			load(ParametrizedLink.class, getRequestContext().getLong(INPUT_PARAMETRIZED_LINK_ID));	
	}
	

	private Object buildLinkElement(String url) {
		logger.debug("+-");
		Element link = Xbuilder.createTopEl("link");
		Xbuilder.addText(link, url);
		return link.getOwnerDocument();
	}

    @Override
	public void setCondition() {
		setCondition(new LoginCondition(this));
	}
	
}

