/*
 * @(#)Id: Banner2GroupDao.java, 03.01.2008 13:17:10, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.banner_module.bo.Banner2Group;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public interface Banner2GroupDao extends GenericDao<Banner2Group, Long> {

	List<Banner2Group> listBannerGroups(Long bannerId);
	List<Long> listBanner2GroupIds(Long bannerId);
	Banner2Group findByBannerIdByGroupId(Long bannerId, Long groupId);

}
