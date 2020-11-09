/*
 * @(#)$Id: Conditionable.java,v 1.2, 2007-03-19 17:07:18Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.condition;

import com.negeso.framework.condition.conditions.Condition;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.ParameterException;

/** Interface for Originator in pattern Memento  
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 3$
 *
 */
public interface Conditionable {
	/**
	 * @return request for saving in HttpSession
	 */	
	RequestContext getRequestContext();
	
	/** Throw ParameterException for retrieving Memento from HttpSession 
	 * 
	 * @throws ParameterException
	 */
	void parseParameters(RequestContext requestContext) throws ParameterException;
	
	void setCondition(Condition condition);
	
	final static boolean ALWAYS_SAVE = true;
	final static boolean SAVE_IN_THIS_CASE = true;
	final static boolean DONT_SAVE = false;
	
	/** Override this function if context has 
	 * many branches of logic. And condition functionality should be used only
	 * in special branches. 
	 * whenToSave is function from request parameters. When it returns true
	 * requestContext will be saved in HttpSession. 
	 * default is simple logic context, thus function should always return true  
	 * @return 
	 */
	boolean whenToSave(RequestContext request);

}

