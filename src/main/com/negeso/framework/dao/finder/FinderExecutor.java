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
package com.negeso.framework.dao.finder;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public interface FinderExecutor<T> {

	List<T> list(Method method, Object[] queryArgs);
	List<T> list(Method method, Object[] queryArgs, int limit, int offset);
	T find(Method method, Object[] queryArgs);
	int count(Method method, Object[] queryArgs);
	
}
