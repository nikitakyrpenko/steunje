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
package com.negeso.framework.list.service;

import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.list.dao.ListItemDao;
import com.negeso.framework.list.domain.ListItem;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ListItemService {
	
	private ListItemDao listItemDao = null;
	
	private static ListItemService instance = null;
	
	public static ListItemService getInstance() {
		if (instance == null) {
			instance = (ListItemService)DispatchersContainer.getInstance().getBean("core", "listItemService");
		}
		return instance;
	}
	
	public ListItem findById(Long id) {
		return listItemDao.read(id);
	}
	
	public void createOrUpdate(ListItem listItem) {
		listItemDao.createOrUpdate(listItem);
	}

	public ListItemDao getListItemDao() {
		return listItemDao;
	}

	public void setListItemDao(ListItemDao listItemDao) {
		this.listItemDao = listItemDao;
	}
	
	
}

