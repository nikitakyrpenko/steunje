/*
 * @(#)$Id: ConditionalPreparedPageComponent.java,v 1.0, 2007-03-20 10:14:24Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.condition.components;

import org.apache.log4j.Logger;

import com.negeso.framework.condition.Conditionable;
import com.negeso.framework.condition.conditions.Condition;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.page.PreparedPageComponent;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public abstract class ConditionalPreparedPageComponent extends PreparedPageComponent implements
		Conditionable {

	Logger logger = Logger.getLogger(ConditionalPreparedPageComponent.class);
	
	private Condition condition;
	
	public void parseParameters(RequestContext requestContext) {
		setRequest(requestContext);
	}
	
	@Override
	public void checkParameters() {
		try {
			setCondition();
			getCondition().checkParameters(getRequestContext());
		} catch (Exception e) {
			logger.error("Error while parsing parameters");
			logger.error("Exception -", e);
		} 
	}
	
	/**
	 * Inject condition instance
	 * Example setCondition() {
	 * setCondition(new SecuredCondition());
	 * }
	 */
	protected abstract void setCondition(); 
	
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public boolean whenToSave(RequestContext request) {
		return ALWAYS_SAVE;
	}

	protected Condition getCondition() {
		return condition;
	}

}

