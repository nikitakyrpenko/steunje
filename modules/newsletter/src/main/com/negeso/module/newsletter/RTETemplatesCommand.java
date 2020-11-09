/*
 * @(#)Id: RTETemplcatesCommand.java, 31.03.2008 15:53:03, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.SpringConstants;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.i18n.DatabaseResourceBundleMessageSource;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class RTETemplatesCommand extends AbstractCommand{
	         
	private static final Logger logger = Logger.getLogger(RTETemplatesCommand.class);

	public ResponseContext execute() {
		logger.debug("+");
		
		ResponseContext response = new ResponseContext();
		Map<String, Object> resultMap = response.getResultMap();
		
		HttpServletRequest request = (HttpServletRequest)getRequestContext().getIOParameters().
				get(WebFrontController.HTTP_SERVLET_REQUEST); 
		
		SubscriberService subscriberService = (SubscriberService) getRequestContext().getService(
				ModuleConstants.NEWSLETTER_MODULE, SpringConstants.NEWSLETTER_MODULE_SUBSCRIBER_SERVICE); 
		
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		
        Element pmElement = Xbuilder.createTopEl("attributes");
        pmElement.setAttribute("langId", String.valueOf(language.getId()));
        
        Document doc = pmElement.getOwnerDocument();
        
        List<SubscriberAttributeType> types = 
        		subscriberService.listSubscriberAttributesTypes();
        
        DatabaseResourceBundleMessageSource s = new DatabaseResourceBundleMessageSource();
        s.setBasename("dict_newsletter.xsl");
        
        for (SubscriberAttributeType type : types){
        	Element el = Xbuilder.addEl(pmElement, "attribute", null);
        	el.setAttribute("key", type.getKey());
        	el.setAttribute("value", s.getMessage(
        			"NL." + type.getKey(), null, new Locale(language.getCode())));
        }
        
        resultMap.put("xml", doc);
        response.setResultName(RESULT_SUCCESS);
        
		logger.debug("-");
		return response;
	}

}
