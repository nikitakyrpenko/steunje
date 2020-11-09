/*                 
 * @(#)$Id: CreateMenuItemDialogCommand.java,v 1.5, 2007-01-10 11:27:35Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.menu.command;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.ResourceMap;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.user.domain.Container;

/**
 * Page Properties Dialog command
 * 
 * @version		$Revision: 6$
 * @author		Olexiy Strashko
 */
public class CreateMenuItemDialogCommand extends AbstractCommand {
    
    
	private final static Logger logger =
        Logger.getLogger(CreateMenuItemDialogCommand.class);
	
	
    public ResponseContext execute()
    {
        logger.debug("+");
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		Map resultMap = response.getResultMap();
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
		    response.setResultName(RESULT_ACCESS_DENIED);
		    return response;
		}
		try{
			Element page = XmlHelper.createPageElement( request );
			Xbuilder.setAttr(page, "parentId", request.getParameter("parentId"));
			Xbuilder.setAttr(page, "isSubMenu", request.getParameter("isSubMenu"));			
			Container.appendContainers(page);
            setDefaultTemplateInfo(page);
			resultMap.put( OUTPUT_XML, page.getOwnerDocument() );
			response.setResultName(RESULT_SUCCESS);
		}
		catch(Exception e){
		    logger.error("error", e);
		    response.setResultName(RESULT_FAILURE);
		} 
		logger.debug("-");
        return response;
    }
    
    
    private void setDefaultTemplateInfo(Element page)
    {
        logger.debug("+");
        try {
            DOMXPath path = new DOMXPath("//negeso:template[@default='true']");
            path.addNamespace(Env.NEGESO_PREFIX, Env.NEGESO_NAMESPACE);
            Element templateInfo = (Element) 
                path.selectSingleNode(ResourceMap.getDom("TEMPLATE_NEW_PAGE"));
            if(templateInfo == null) {
                logger.warn("- default template not found");
                return;
            }
            page.setAttribute(
                "default-template-name", templateInfo.getAttribute("name"));
            page.setAttribute(
                "default-template-title", templateInfo.getAttribute("title"));
            logger.debug("-");
            return;
        } catch (JaxenException e) {
            logger.error("- error", e);
            return;
        }
    }
    
    
}
