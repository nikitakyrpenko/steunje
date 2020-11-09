/*
 * @(#)$Id: LanghenkelSearch.java,v 1.0, 2007-01-31 11:37:36Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
public class LanghenkelSearch implements ISearchable {

	public Iterator getSearchedIndexes() {
		List<String> list = new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		list.add("test3");
		return list.iterator();
	}

}
