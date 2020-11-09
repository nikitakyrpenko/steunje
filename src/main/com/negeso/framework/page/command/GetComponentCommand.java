/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page.command;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.page.ComponentManager;
import com.negeso.framework.page.PageComponent;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class GetComponentCommand extends AbstractCommand{

	private static Logger logger = Logger.getLogger(GetComponentCommand.class);
	
	@Override
	public ResponseContext execute() {
		logger.debug("+");
		
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        Element page = null;
        try {
        	page = XmlHelper.createPageElement(request);
        	String componentName = request.getParameter("component_name");
        	String className = ComponentManager.get().resolveComponentClassName(componentName);
    		Validate.notNull(className, "class not found for component: " + componentName);
            PageComponent component = (PageComponent) Class.forName(className).newInstance();
            Map params = request.getParameterMap();
        	page.appendChild(component.getElement(page.getOwnerDocument(), request, params));
        	response.setResultName(RESULT_SUCCESS);
        } catch( CriticalException e ){
            logger.error("-error", e);
            response.setResultName(RESULT_FAILURE);
            this.getErrors().add(e.getMessage());
        } catch( Exception e ){
            logger.error("-error", e);
            response.setResultName(RESULT_FAILURE);
            this.getErrors().add(e.getMessage());
        }
        if ( page != null ){
            response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
        }
        if ( this.hasErrors() ){
        	page.appendChild(
				XmlHelper.createErrorsElement(
						page.getOwnerDocument(), this.getErrors()
				)
			);
		}
        return response;
	}

}

