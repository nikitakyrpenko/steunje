/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.component;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.negeso.framework.controller.RequestContext;
import com.negeso.module.newsletter.web.controller.SubscriberController;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class SubscriberAttributeParameters {

	private Map<Long, String> attributeValues = new LinkedHashMap<Long, String>();
	private List<Long> groups = new LinkedList<Long>();

	public void bind(RequestContext requestContext) {
		for (Iterator i = requestContext.getParameterMap().keySet().iterator(); i.hasNext() ;) {
			String param_name = (String) i.next();
			if (param_name.startsWith(SubscriberController.ATTRIBUTE_PREFIX)) {
				String sKeyId = param_name.substring(SubscriberController.ATTRIBUTE_PREFIX.length());
				Long key = Long.valueOf(sKeyId);
				String value = requestContext.getParameter(param_name);
				attributeValues.put(key, value);
			} else if (param_name.startsWith(SubscriberController.GROUP_PREFIX)){
				String sGroupId = param_name.substring(SubscriberController.GROUP_PREFIX.length());
				Long groupId = Long.valueOf(sGroupId);
				groups.add(groupId);
			}
		}
	}
	
	public Map<Long, String> getAttributeValues() {
		return attributeValues;
	}
	
	public List<Long> getGroupIds() {
		return groups;
	}
	
}
