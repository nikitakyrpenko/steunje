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

package com.negeso.module.guestbook.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.module.guestbook.bo.Guestbook;
import com.negeso.module.guestbook.dao.GuestbookDao;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class GuestbookService {

	private static Logger logger = Logger.getLogger(GuestbookService.class);
	
	private GuestbookDao guestbookDao;

	public void setDao(GuestbookDao guestbookDao) {
		this.guestbookDao = guestbookDao;
	}
	
	public List<Guestbook> listGuestbooks() {
		logger.debug("+-");
		return guestbookDao.listGuestbooks(Env.getSiteId());		
	}

}
