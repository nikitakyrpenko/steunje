/*
 * @(#)$Id: ParameterComparator.java,v 1.0, 2007-04-11 06:59:55Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core;

import com.negeso.module.core.domain.ConfigurationParameter;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class ParameterComparator implements java.util.Comparator<ConfigurationParameter> {

	public int compare(ConfigurationParameter p1, ConfigurationParameter p2) {
		return p1.getName().compareTo(p2.getName());
	}
	
}

