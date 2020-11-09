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
package com.negeso.framework.util.replacing;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class MapTokenResolver implements ITokenResolver {

	protected Map<String, String> tokenMap = new HashMap<String, String>();
	private String[] keys = {};
	private String[] values = {};

	public MapTokenResolver(Map<String, String> tokenMap) {
		this.tokenMap = tokenMap;
		keys = tokenMap.keySet().toArray(keys);
		values = tokenMap.values().toArray(values);
	}

	public String resolveToken(String tokenName) {
		return this.tokenMap.get(tokenName);
	}

	public String[] getKeys() {
		return keys;
	}

	public String[] getValues() {
		return values;
	}

}
