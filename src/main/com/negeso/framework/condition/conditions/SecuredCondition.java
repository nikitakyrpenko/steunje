/*
 * @(#)$Id: SecuredCondition.java,v 1.1, 2007-03-19 17:03:26Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.condition.conditions;


import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.condition.Conditionable;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.page.command.GetPageCommand;


/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class SecuredCondition extends Condition {

	private static final Logger logger = Logger.getLogger(SecuredCondition.class);
	
	public SecuredCondition(Conditionable context) {
		super(context);
	}
	
	@Override
	protected boolean doCheckCondition() {
		logger.debug("+");
		if (!isConditionTrue()) {
			GetPageCommand pageCommand = new GetPageCommand();
			getRequest().setParameter(GetPageCommand.INPUT_FILENAME, "login.html");
			pageCommand.setRequestContext(getRequest());
			
	        ResponseContext pageCommandResponse = pageCommand.execute();
	        if (!isConditionTrue()) {
	        	setResultName(AbstractCommand.RESULT_ACCESS_DENIED);
	        	setOutputXML((Document)pageCommandResponse.getResultMap().get(AbstractCommand.OUTPUT_XML));
                logger.debug("-");
                return false;
	        }	
		}
        logger.debug("-");
        return true;
	}

	protected boolean isConditionTrue() {
		return getRequest().getSession().isAuthorizedUser();
	}
	
	@Override
	protected boolean isConditionParametersPresent() {
        return getRequest().getParameter(GetPageCommand.INPUT_LOGIN) != null;
    }

}

