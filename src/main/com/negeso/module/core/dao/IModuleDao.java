/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.dao;

import java.util.List;

import com.negeso.framework.module.Module;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public interface IModuleDao {
	
	Module getModuleById(Long moduleId);
	List<Module> getAllModules();
	Long getModuleByName(String moduleName);
	List<Module> readAll();
	String getHelpLink(String moduleLink);
}

