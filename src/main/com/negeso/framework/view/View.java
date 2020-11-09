/*
 * @(#)View.java  Created on 19.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.view;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public interface View {
	
	public void process(RequestContext request, ResponseContext response);
	
}
