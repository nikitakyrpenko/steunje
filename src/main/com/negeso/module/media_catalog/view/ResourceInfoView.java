/*
 * @(#)ResourceInfoView.java       @version	30.03.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.view;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;


import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.CatalogXmlBuilder;

/**
 * Resource info view
 *
 * @version 	30.03.2004
 * @author 		Olexiy.Strashko
 */
public class ResourceInfoView extends AbstractHttpView {
	private static Logger logger = Logger.getLogger(ResourceInfoView.class);
	
	// Input parameters
	public static final String OUTPUT_RESOURCE = "resource";
	public static final String OUTPUT_ERROR_TEXT = "errorMessage";
	public static final String OUTPUT_USER_TEXT = "userMessage";
	public static final String OUTPUT_PARENT_DIR = "parentDir";
	public static final String OUTPUT_CURRENT_DIR = "currentDir";
	public static final String OUTPUT_VIEW_MODE = "viewMode";
	public static final String OUTPUT_SORT_MODE = "sortMode";
	public static final String OUTPUT_ACTION_MODE = "actionMode";
	public static final String OUTPUT_SHOW_MODE = "showHiddenFiles";
	
	
	public static final String STYLE_SHEET = "RESOURCE_INFO_XSL";


	/* (non-Javadoc)
	 * @see com.negeso.framework.view.View#process(com.negeso.framework.controller.RequestContext, com.negeso.framework.controller.ResponseContext)
	 */
	public void process(RequestContext request, ResponseContext response) {
		logger.debug("+");
		
		Map resultMap = response.getResultMap();
		Element resource = (Element) resultMap.get(OUTPUT_RESOURCE);
		String parentDir = (String) resultMap.get(OUTPUT_PARENT_DIR);
		String currentDir = (String) resultMap.get(OUTPUT_CURRENT_DIR);
		String errorMessage = (String) resultMap.get(OUTPUT_ERROR_TEXT);
		String userMessage = (String) resultMap.get(OUTPUT_USER_TEXT);
		String viewMode = (String) resultMap.get(OUTPUT_VIEW_MODE);
		String sortMode = (String) resultMap.get(OUTPUT_SORT_MODE);
		String actionMode = (String) resultMap.get(OUTPUT_ACTION_MODE);
		String showMode = (String) resultMap.get(OUTPUT_SHOW_MODE);
		if (showMode == null) showMode = Repository.DEFAULT_SHOW_MODE;
		if (viewMode == null) viewMode = Repository.DEFAULT_VIEW_MODE;
		if (sortMode == null) sortMode = Repository.DEFAULT_SORT_MODE;
		if (actionMode == null) actionMode = Repository.DEFAULT_ACTION_MODE;

		Element page = CatalogXmlBuilder.get().getPageDom4j("Media catalog. Resource info");

		// *********************************************************************
		// +++ add repository
		page.add(CatalogXmlBuilder.get().getRepositoryDom4j());

		// *********************************************************************
		// +++ browser config
		page.add(CatalogXmlBuilder.get().getBrowserConfigDom4j(
			parentDir, currentDir, viewMode, sortMode, actionMode, showMode
		));
		// +++ browser config

		page.add(resource);
		
		// Process error message
		if ( (errorMessage != null) && (!errorMessage.equalsIgnoreCase("")) ){
			page.add(CatalogXmlBuilder.get().getErrorMessageDom4j(errorMessage));
		}

		// Process user message
		if ( (userMessage != null) && (!userMessage.equalsIgnoreCase("")) ){
			page.add(CatalogXmlBuilder.get().getUserMessageDom4j(userMessage));
		}

		Document doc = DocumentHelper.createDocument(page);

        // Addition the interface language attribute for localization the XSLT
        doc.getRootElement().addAttribute(
            "interface-language",
            request.getSession().getInterfaceLanguageCode()
        );

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
