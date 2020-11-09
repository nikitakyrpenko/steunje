/*
 * @(#)$Id: AppDestroyListener.java,v 1.0, 2004-08-19 15:14:07Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 *
 * Public interface for application destroy listeners. 
 * 
 * @version		$Revision: 1$
 * @author		Olexiy Strashko
 * 
 */
public interface AppDestroyListener extends EventListener {
    void destroy(EventObject event);
}
