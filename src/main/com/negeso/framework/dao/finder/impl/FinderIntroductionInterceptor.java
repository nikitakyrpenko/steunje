/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.dao.finder.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.aop.IntroductionInterceptor;

import com.negeso.framework.dao.finder.FinderExecutor;

/**
 * 
 * @TODO
 * 
 * @author Anatoliy Pererva
 * @version $Revision: $
 * 
 */
public class FinderIntroductionInterceptor implements IntroductionInterceptor {

	private static final Logger logger = Logger.getLogger(FinderIntroductionInterceptor.class);

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		logger.debug("+");
		FinderExecutor executor = (FinderExecutor) methodInvocation.getThis();

		String methodName = methodInvocation.getMethod().getName();
		logger.debug("methodName=" + methodName);
	
		if (methodName.startsWith("find") && methodName != "find"){
			Object[] arguments = methodInvocation.getArguments(); 
			return executor.find(methodInvocation.getMethod(), arguments);
		}
		if (methodName.startsWith("list") && methodName != "list") {
			Object[] arguments = methodInvocation.getArguments(); 
			if (methodName.endsWith("InRange")) {
				Object[] args = extractRangedArguments(arguments);
				int limit = extractRangeLimit(arguments);
				int offset = extractRangeOffset(arguments);
				return executor.list(methodInvocation.getMethod(), args, limit, offset);
			}
			return executor.list(methodInvocation.getMethod(),
					methodInvocation.getArguments());
		}
		if (methodName.startsWith("count") && methodName != "count")
			return executor.count(methodInvocation.getMethod(),
					methodInvocation.getArguments()); 
		
		return methodInvocation.proceed();
	}

	private Object[] extractRangedArguments(Object[] arguments) {
		if (arguments.length < 2)
			return new Object[] {};

		// remove last two arguments
		Object[] result = new Object[arguments.length - 2];
		for (int i = 0; i < arguments.length - 2; i++) {
			result[i] = arguments[i];
		}

		return result;
	}

	private int extractRangeLimit(Object[] arguments) {
		if (arguments.length < 2)
			return 0;

		Object limit = arguments[arguments.length - 2];
		if (limit == null || !(limit instanceof Integer))
			return 0;

		return (Integer) limit;
	}

	private int extractRangeOffset(Object[] arguments) {
		if (arguments.length < 2)
			return 0;

		Object start = arguments[arguments.length - 1];
		if (start == null || !(start instanceof Integer))
			return 0;

		return (Integer) start;
	}

	public boolean implementsInterface(Class intf) {
		return intf.isInterface()
				&& FinderExecutor.class.isAssignableFrom(intf);
	}

}
