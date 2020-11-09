/*
 * @(#)$Id: ConditionalCommand.java,v 1.0, 2007-03-19 09:31:47Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.condition.commands;

import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.condition.ConditionFailedException;
import com.negeso.framework.condition.Conditionable;
import com.negeso.framework.condition.conditions.Condition;
import com.negeso.framework.condition.conditions.NullCondition;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.ParameterException;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public abstract class ConditionalCommand extends AbstractCommand implements Conditionable {

	Logger logger = Logger.getLogger(ConditionalCommand.class);
	
	private Condition condition;
	private ResponseContext response = new ResponseContext();
	
	public void parseParameters(RequestContext requestContext) {
		RequestContext savedRequestContext = requestContext;
		// inject actual session data
		savedRequestContext.setSessionData(
				   getRequestContext().getSessionData()
				);
		setRequestContext(savedRequestContext);
	}
	
	public void setCondition() {
		this.condition = new NullCondition(this);
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	final public ResponseContext execute() {
		logger.debug("+-");
		try {
			setCondition();
			condition.checkParameters(getRequestContext());
			conditionalExecute();
		} catch (ConditionFailedException e) {
			response.setResultName(condition.getResultName());
			response.getResultMap().put( OUTPUT_XML, condition.getOutputXML());
		} catch (ParameterException e) {
			logger.error("exception -", e);
			response.setResultName(RESULT_FAILURE);
		}
		return this.response;
	}
	
	/** 
	 * Do not create instance of ResponseContext. Use getResponseContext() method
	 * call getCondition().checkCondition() in overrided methods 
	 * @return
	 * @throws ConditionFailedException
	 */
	abstract protected void conditionalExecute() throws ConditionFailedException;

	protected ResponseContext getResponseContext() {
		return this.response;
	}
	
	protected Condition getCondition() {
		return this.condition;
	}

	public boolean whenToSave(RequestContext request) {
		return ALWAYS_SAVE;
	}

}

