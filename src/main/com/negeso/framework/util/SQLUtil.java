/*
 * @(#)$Id: SQLUtil.java,v 1.1, 2005-10-13 09:39:50Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 
 * Search command
 * 
 * @version $Revision: 2$
 * @author Alexander G. Shkabarnya
 * @author Olexiy V. Strashko
 * 
 */
public class SQLUtil {

	/* Doesn't add first and last AND */
	private static String createLike(String[] fields, String searchWord) {
		String like = "";
		for (int i = 0; i < fields.length; i++) {
			like += fields[i] + " iLIKE '%" + searchWord + "%' ";
			if (i != fields.length - 1) {
				like += " OR ";
			}
		}
		return like;
	}

	public static String createLike(String[] fields, ArrayList searchWordList) {
		String like = "";
		for (int i = 0; i < searchWordList.size(); i++) {
			if (i == 0) {
				like += " ( ";
			}
			like += createLike(fields, (String) searchWordList.get(i));
			if (i != searchWordList.size() - 1) {
				like += " OR ";
			} else {
				like += " ) ";
			}
		}
		return like;
	}

	public static ArrayList generateSearchWordList(String searchString) {
		ArrayList list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(searchString, " ",
				false);
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}
		return list;
	}

	public static String createSearchCondition(String[] searchFields,
			String searchWords) {
		return createLike(searchFields, SQLUtil
				.generateSearchWordList(searchWords));
	}
}
