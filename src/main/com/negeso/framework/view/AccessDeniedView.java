/*
 * @(#)$Id: AccessDeniedView.java,v 1.3, 2005-11-22 10:06:59Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.view;


import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;

/**
 * @version $Revision: 4$
 * @author Stanislav Demchenko
 */
public class AccessDeniedView extends AbstractHttpView {
    
    private static Logger logger = Logger.getLogger(AccessDeniedView.class);
    
    public void process(RequestContext request, ResponseContext response) {
        logger.debug("+");
        response.getResultMap().put("xsl", "ACCESS_DENIED_XSL");
        new HtmlView().process(request, response);
        logger.debug("-");
    }
}

