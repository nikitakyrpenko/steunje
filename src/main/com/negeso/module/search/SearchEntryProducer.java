/*
 * @(#)SearchEntryProducer.java  Created on 11.05.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.search;


/**
 * Make indexes directly
 * 
 * @version 1.0
 * @author Mykola Lykhozhon
 */

public interface SearchEntryProducer {

	public void indexEntries(SearchIndexCallBack callBack);
}
