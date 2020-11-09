/*
 * @(#)$Id: ParametrizedLinkCommand.java,v 1.3, 2005-06-06 13:04:55Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.command;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;

/**
 *
 * AuthorizerdLink Command
 * 
 * @version		$Revision: 4$
 * @author		Olexiy Strashko
 * 
 */
public class ParametrizedLinkCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(ParametrizedLinkCommand.class);

	private static final String DEFAULT_HTTP_METHOD = "GET";

    
	public static final String INPUT_LINK_URL = "link_url";


    public ResponseContext execute() {
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		Map resultMap = response.getResultMap();
		
		try{
			Element page = XmlHelper.createPageElement( request );
			
			String linkUrl = request.getString( INPUT_LINK_URL, null );
			if ( linkUrl == null ){
				logger.error("Link is not set");
	            response.setResultName( RESULT_FAILURE );
	            return response;
			}

			User user = request.getSession().getUser();
			if ( user == null ){
			    // unauthorized user
	            response.setResultName( RESULT_ACCESS_DENIED );
	            return response;
			}
			
			Element linkInfo = Xbuilder.addEl(page, "link-info", null);
			Xbuilder.setAttr(linkInfo, "url", linkUrl);
			Xbuilder.setAttr(linkInfo, "http-method", this.getHttpMethod() );

			Element param = null;
			// add user id parameter 
			param = Xbuilder.addEl(linkInfo, "parameter", null);
			Xbuilder.setAttr(param, "name", "user-id");
			Xbuilder.setAttr(param, "value", user.getId());

			// add app id parameter 
			param = Xbuilder.addEl(linkInfo, "parameter", null);
			Xbuilder.setAttr(param, "name", "app-id");
			Xbuilder.setAttr(param, "value", new Long(1));
			
			resultMap.put( "xml", page.getOwnerDocument() );
			response.setResultName(RESULT_SUCCESS);
		}
		catch(CriticalException e){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
        return response;
    }

    /**
     * @return
     */
    private String getHttpMethod() {
        return DEFAULT_HTTP_METHOD;
    }
}
