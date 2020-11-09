/*
 * @(#)PageComponent.java       @version	11.05.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;

/**
 * Page container interface. Implement it to create page component.  
 *
 * @version 	11.05.2004
 * @author 		Olexiy Strashko
 */
public interface PageComponent {
	
	/**
	 * Generate <code>xml</code> element for page component. Uses 
	 * <code>org.w3c.dom XML</code> document/element 
	 * presentation.   
	 * 
	 * @param document  	The parent <code>document</code>. Used for element 
	 * 						building.
	 * @param request		The request. Contain all request information from 
	 * 						<code>FrontController</code>.
	 * @param parameters	The parameters for component generation. 
	 * @return				Generated <code>org.w3c.dom.Element</code> 	
	 */
	public Element getElement(
		Document document, 
		RequestContext request,
		Map parameters 
	);
}
