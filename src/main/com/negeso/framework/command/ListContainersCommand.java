/*
 * @(#)Id: ListContainersCommand.java, 01.10.2007 15:44:53, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.command;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.module.user.domain.Container;

/**
 * 
 * Command that generates list of containers
 * Used for dialogs
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class ListContainersCommand extends AbstractCommand {
    
    private static Logger logger = Logger.getLogger(SimpleCommand.class);

    public ResponseContext execute() {
        logger.debug("+ -");
        Document xml = XmlHelper.newDocument();
        Container.appendContainers(xml);
        xml.getDocumentElement().appendChild(Language.getDomItems(xml, getRequestContext().getSession().getLanguage().getId()));
        ResponseContext response = new ResponseContext();
        response.getResultMap().put(OUTPUT_XML, xml);
        response.setResultName(RESULT_SUCCESS);
        return response;
    }

}
