/*
 * @(#)$Id: Cacheble.java,v 1.0, 2005-04-25 12:16:19Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

/**
 *
 * Cacheble interface   
 * 
 * @version		$Revision: 1$
 * @author		Olexiy Strashko
 * 
 */
public interface Cacheble {
    public boolean isExpired();
    public void setExpired(boolean newExpired);
}
