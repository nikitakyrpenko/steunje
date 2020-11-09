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
package com.negeso.module.imp.extension;

import java.util.List;
import java.util.Map;

import com.negeso.module.imp.log.EventLogger;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public interface IImportValidator {
	void validateImportObjects(Map<String, List<ImportObject>> importedData, EventLogger eventLogger);
}

