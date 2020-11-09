/*
 * @(#)$Id: SortOrder.java,v 1.0, 2006-05-30 09:33:04Z, Dmitry Dzifuta$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.document.domain;

import java.io.Serializable;

import com.negeso.framework.controller.RequestContext;

/**
 * 
 * @TODO Keeps data about sorting order in the document module
 * 
 * @author		Dmitry S. Dzifuta
 * @version		$Revision: 1$
 *
 */
public class SortOrder implements Serializable{
	
	private String column;
	
	private String order;

	public SortOrder() {
		this.column = "name";
		this.order = "ASC";
	}	
	
	public SortOrder(String column, String order) {
		this.column = column;
		this.order = order;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	public static SortOrder getSortOrder(RequestContext request) {
		SortOrder sortOrder = null;
		String sortColumnRequest = (String)request.getParameter("sort_column");
		String sortOrderRequest = (String)request.getParameter("sort_order");
		if (sortColumnRequest == null || sortColumnRequest.length() == 0) {
			sortOrder = (SortOrder)request.getSession().getAttribute("sort_order");
			if (sortOrder == null) {
				sortOrder = new SortOrder();
			}
		}
		else {
			sortOrder = new SortOrder(sortColumnRequest, sortOrderRequest);
			request.getSession().setAttribute("sort_order", sortOrder);
		}
		return sortOrder;
	}
}

