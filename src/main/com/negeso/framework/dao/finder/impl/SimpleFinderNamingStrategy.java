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

import java.lang.reflect.Method;

import com.negeso.framework.dao.finder.FinderNamingStrategy;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class SimpleFinderNamingStrategy implements FinderNamingStrategy {

	public String queryNameFromMethod(Class findTargetType, Method finderMethod) {
		return findTargetType.getSimpleName() + "." + finderMethod.getName();
	}

}
