/*
 * @(#)$Id: Condition.java,v 1.2, 2007-03-19 17:09:26Z, Alexander Serbin$
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
import com.negeso.framework.condition.ConditionFailedException;
import com.negeso.framework.condition.Conditionable;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.ParameterException;

/** Pattern Strategy:
 * Condition is a Strategy (pattern Memento)
 * Command or PageComponent is Context 
 * -------------------------------------------------------  
 *  Pattern Memento:
 *  Originator is Condition
 *  state is RequestContext
 *  Memento is HttpSession 
 *  Caretaker is servlets container(TomCat)  
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 3$
 *
 */
public abstract class Condition {
	
	private static Logger logger = Logger.getLogger(Condition.class);
	
	private Conditionable context;
	private RequestContext request;
	private Document outputXML;
	private String resultName = AbstractCommand.RESULT_SUCCESS;
	
	protected Condition(Conditionable context) {
		this.context = context;
	}
	
	private String getAttributeName () {
		return "VO_" + context.getClass().getName();
	}
	
	/**Strongly required method. Have to be performed first in
	 * method execute() of command
	 * getElement() of PageComponent
	 * Caches in HttpSession or retrieving the Memento from HttpSession 
	 * Make sure that context.parseParameters() throws ParameterException when
	 * expected parameters are missing
	 * @param request
	 * @throws ParameterException
	 */
	public void checkParameters(RequestContext request) throws ParameterException {
		logger.debug("+-");
		this.request = request;
		if (!isConditionParametersPresent()) {
			removeSavedRequestFromHttpSession();
		}
		if (isStateAbsentInHttpSession()) {
			context.parseParameters(request);
			if (context.whenToSave(this.request)) {
				cashRequestInHttpSession();
			} 
		} else {
			context.parseParameters(getCachedRequest());
		}
	}
	
	/** This function is for identifying moment
	 *  when client refused to perform condition
	 *  and want to go back or to another link
	 *  Should be overrided if context behavior 
	 *  supposes returning back 
	 */
	protected boolean isConditionParametersPresent() {
		return false;
	}

	private boolean isStateAbsentInHttpSession() {
		return request.getSession().getAttribute(getAttributeName()) == null;
	}

	private RequestContext getCachedRequest() {
		return (RequestContext)request.getSession().getAttribute(getAttributeName());
	}
	
	protected void removeSavedRequestFromHttpSession() {
		request.getSession().removeAttribute(getAttributeName());
	}
	
	private void cashRequestInHttpSession() {
		request.getSession().setAttribute(getAttributeName(), context.getRequestContext()); 
	}

	public void checkCondition() throws ConditionFailedException {
		if (!doCheckCondition()) {
			throw new ConditionFailedException();
		}
	}
	
	abstract protected boolean doCheckCondition();

	public RequestContext getRequest() {
		return request;
	}

	public Document getOutputXML() {
		return outputXML;
	}

	public void setOutputXML(Document outputXML) {
		this.outputXML = outputXML;
	}

	public String getResultName() {
		return resultName;
	}

	protected void setResultName(String resultName) {
		this.resultName = resultName;
	}

}

