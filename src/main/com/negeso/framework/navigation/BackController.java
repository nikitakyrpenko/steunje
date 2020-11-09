/*
* @(#)$Id: BackController.java,v 1.2, 2007-01-18 16:53:09Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.framework.navigation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;



/**
 *
 * TODO
 * 
 * @version                $Revision: 3$
 * @author                 Svetlana Bondar
 * 
 */

public class BackController extends AbstractController {
    private static Logger logger = Logger.getLogger(BackController.class);
   
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse arg1) throws Exception {
        logger.debug("+");        
        HistoryStack hSt = RequestUtil.getHistoryStack(request);
        
        
        String goIndex = request.getParameter("go");
          
      	if ( goIndex != null && goIndex.trim().length() > 0 )	{
        	logger.debug("- go");   
        	return new ModelAndView("redirect:" + hSt.go(Integer.parseInt(goIndex)));        	
        }
        logger.debug("- goBack");
        return new ModelAndView("redirect:" + hSt.goBack().getUrl());
    }

	
	

}
