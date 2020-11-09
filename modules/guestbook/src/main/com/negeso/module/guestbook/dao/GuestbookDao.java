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
package com.negeso.module.guestbook.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.guestbook.bo.Guestbook;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public interface GuestbookDao extends GenericDao<Guestbook, Long> {

	public List<Guestbook> listGuestbooks(Long siteId);

}
