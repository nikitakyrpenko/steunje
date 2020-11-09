/*
 * @(#)$Id: DefaultContainerStrategy1.java,v 1.0, 2007-03-22 15:07:00Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.security;

import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class DefaultContainerStrategy1 extends SecurityCache {

	Logger logger = Logger.getLogger(DefaultContainerStrategy1.class);
	
	public DefaultContainerStrategy1(Long siteId) {
		super(siteId);
	}

	@Override
	synchronized protected String doResolveRole(Long uid, Long cid) {
	    logger.debug("+-");
	        String role = templateResolveRole(uid, cid);
	        
        	if (role == null && isDefaultContainer(cid)) {
        		return SecurityGuard.VISITOR;
        	} else {
        		return role;
        	}
	}

}

