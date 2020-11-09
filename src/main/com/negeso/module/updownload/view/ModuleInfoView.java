/*
 * @(#)FileChooserView.java       @version	02.04.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.view;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.DocumentSource;

import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.view.AbstractHttpView;

/**
 * Class description goes here
 *
 * @version 	02.04.2004
 * @author 	OStrashko
 */
public class ModuleInfoView extends AbstractHttpView {
	private static Logger logger = Logger.getLogger(ModuleInfoView.class);

	public static final String STYLE_SHEET = "UPDOWNLOAD_MODULE_INFO_XSL";
	
	public static final String OUTPUT_XML = "xml";
	
	public void process(RequestContext request, ResponseContext response) {
		logger.debug("+");
		
		Map resultMap = response.getResultMap();
		Document doc = (Document) resultMap.get(OUTPUT_XML);
		
        String interfaceLanguage = 
            (String)request.getSession().getAttribute("interface-language");         
        if (interfaceLanguage == null)
            interfaceLanguage = "en";
        doc.getRootElement().addAttribute("interface-language",
            interfaceLanguage);

		try{
			Templates xslTempl = ResourceMap.getTemplates(STYLE_SHEET);
			HttpServletResponse httpResponse = getServletResponse(request);
			httpResponse.setContentType(HTML_MIME_TYPE);
			xslTempl.newTransformer().transform(new DocumentSource(doc), 
				new StreamResult(httpResponse.getOutputStream())
			);
		} catch (Exception e) {
			logger.error("-", e);
			throw new RuntimeException(e);
		}
	}
}
