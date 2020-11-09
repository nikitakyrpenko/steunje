/*
 * @(#)$Id: DefaultContainerStrategy0.java,v 1.0, 2007-03-22 15:06:59Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.security;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class DefaultContainerStrategy0 extends SecurityCache {
	
	public DefaultContainerStrategy0(Long siteId) {
		super(siteId);
	}
	
	@Override
	synchronized protected String doResolveRole(Long uid, Long cid) {
	    Long containerId = (cid == null ? null : new Long(cid));
	    if (isDefaultContainer(containerId)) { 
	    		containerId = null; 
	   	}
   		return templateResolveRole(uid, containerId);
	}
	
}
