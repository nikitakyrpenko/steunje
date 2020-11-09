/*
 * @(#)$Id: DependencyLocator.java,v 1.2, 2005-03-16 10:22:16Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.dependency;

import java.sql.Connection;

import org.dom4j.Element;

import com.negeso.framework.domain.CriticalException;

/**
 *
 * Interface class for dependency location. All file containers (content) 
 * should implement this interface and register to <code>DependencyController</code>.
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 3$
 */
public interface DependencyLocator {
	public void buildDom4jXml(Connection con, Element parent, String catalogPath) throws CriticalException;
	public boolean hasDependencies(Connection con, String catalogPath) throws CriticalException;
}
