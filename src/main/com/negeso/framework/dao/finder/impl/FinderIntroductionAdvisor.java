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

import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

import com.negeso.framework.dao.finder.FinderExecutor;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class FinderIntroductionAdvisor extends DefaultIntroductionAdvisor {

	private static final long serialVersionUID = -2204948796620954491L;

	public FinderIntroductionAdvisor(IntroductionInterceptor interceptor) {
		super(interceptor);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(Class clazz) {
		return FinderExecutor.class.isAssignableFrom(clazz);
	}



}
