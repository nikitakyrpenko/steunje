/*
 * Created on 24.01.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.negeso.framework.view;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.WebFrontController;
/**
 * @author Stas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ResourceNotFoundView implements View {
    
    private static Logger logger = Logger.getLogger(ResourceNotFoundView.class);
    
    public void process(
        RequestContext request,
        ResponseContext response)
    {
        logger.debug("+");
        HttpServletRequest httpRequest =
            (HttpServletRequest) request.getIOParameters()
                .get(WebFrontController.HTTP_SERVLET_REQUEST);

        logger.error("Cannot find resource: " + httpRequest.getRequestURI());
        HttpServletResponse httpResponse = (HttpServletResponse) request.getIOParameters().get(
        	WebFrontController.HTTP_SERVLET_RESPONSE
        );
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        logger.debug("-");
    }
}
