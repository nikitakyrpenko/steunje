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
package com.negeso.module.imp.extension;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.negeso.module.imp.log.EventLogger;
import com.negeso.module.imp.service.IService;

/**
 * 
 * @TODO
 * 
 * @author Anatoliy Pererva
 * @version $Revision: $
 * 
 */
public interface Importer {

	/**
	 * Parses data from import file and returns data model representation.
	 * @param is input stream of import file in XML format.
	 * @param eventLogger event logger which gathers events during import.
	 * @return the data model. The key of map is a category key, the value is
	 * the rows of import objects. Each import object represents a row of
	 * table data. 
	 * @throws ImportException
	 */
	public Map<String, List<ImportObject>> parseImportStream(
			InputStream is, EventLogger eventLogger, Map<String, Object> attributes) throws ImportException;
	
	public IService getImportService();

}
