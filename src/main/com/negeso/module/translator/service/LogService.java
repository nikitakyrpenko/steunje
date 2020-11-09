/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.translator.service;

import com.negeso.module.translator.dao.LogDao;
import com.negeso.module.translator.domain.Log;

import java.util.List;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class LogService {
	
	private LogDao logDao;
	
	public void save(Log log) {
		logDao.create(log);
	}
	
	public List<Log> list() {
		return logDao.listOrderById();
	}

	public LogDao getLogDao() {
		return logDao;
	}

	public void setLogDao(LogDao logDao) {
		this.logDao = logDao;
	}
}

