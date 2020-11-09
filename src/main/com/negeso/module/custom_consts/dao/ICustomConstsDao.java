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
package com.negeso.module.custom_consts.dao;

import java.util.List;

import com.negeso.module.custom_consts.domain.CustomConst;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public interface ICustomConstsDao {

	List getConstsByModuleId(Long moduleId);
	List getCommonConsts();
	CustomConst loadConst(Long id);
	CustomConst loadConst(String key);
	void deleteConst(Long constId);
	void saveConst(CustomConst customConst);
	List getCommonTranslations(String language);
	List getModuleConstsTranslations(Long moduleId, String language);

}

