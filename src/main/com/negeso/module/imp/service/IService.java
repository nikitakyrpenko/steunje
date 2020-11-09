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
package com.negeso.module.imp.service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.negeso.module.imp.extension.ImportObject;
import com.negeso.module.imp.log.EventLogger;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public interface IService {

	void doImport(Connection conn, Map<String, List<ImportObject>> rows,
			Map<String, Object> params, EventLogger eventLogger, String moduleId, String importerId);

}
