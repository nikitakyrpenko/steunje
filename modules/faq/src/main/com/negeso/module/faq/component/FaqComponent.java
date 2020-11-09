/*
 * @(#)$Id: FaqComponent.java,v 1.1, 2007-01-10 11:27:35Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.faq.component;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.list.component.ListComponent;


/**
 * 
 * @TODO
 * 
 * @author		Vyacheslav Zapadnyuk
 * @version		$Revision: 2$
 *
 */
public class FaqComponent extends ListComponent {
    
    private static Logger logger = Logger.getLogger(FaqComponent.class);
    
    public static final String LIST_ID = "listId";
    public static final String LIST_PATH = "listPath";
    
    public Element getElement(
            Document document,
            RequestContext request,
            Map parameters )
    {
        logger.debug("+");
        try {
            parameters.put(LIST_PATH, 
         		  (null == request.getParameter(LIST_PATH)?"0":request.getParameter(LIST_PATH)));
            Element element = super.getElement(document, request, parameters);
            return element;
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
        }
        logger.debug("-");
        return getStubElement(document);
    }
}
