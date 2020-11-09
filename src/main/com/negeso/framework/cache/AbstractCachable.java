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
package com.negeso.framework.cache;

import java.util.List;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
abstract public class AbstractCachable implements Cachable {
  
	public void init() {
		List<Cachable> cachableList = CacheFacade.getInstance().getCachableList();  
		synchronized (cachableList) {
				cachableList.add(this);
		}	
	}
    
}

