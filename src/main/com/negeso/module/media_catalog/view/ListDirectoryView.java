/*
 * @(#)$Id: ListDirectoryView.java,v 1.18, 2006-06-26 15:47:29Z, Stanislav Demchenko$
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
import com.negeso.framework.util.Timer;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.CatalogXmlBuilder;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * Directory contents representation view.
 *
 * @version 	$Revision: 19$
 * @author 		Olexiy.Strashko
 */
public class ListDirectoryView extends AbstractHttpView {
	
	private static Logger logger = Logger.getLogger(ListDirectoryView.class);
	
    private static Templates CACHED_TEMPLATES = null;
	
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
	
	public static final String STYLE_SHEET = "LIST_DIRECTORY_XSL";
	
	
	public void process(RequestContext request, ResponseContext response) {
		logger.debug("+");
		
		Timer timer = null;
		if (logger.isInfoEnabled()){
		    timer = new Timer();
		}
		
		Map resultMap = response.getResultMap();
		Folder folder = (Folder) resultMap.get(OUTPUT_RESOURCE);
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
		logger.debug("get params -");

		Element page = CatalogXmlBuilder.get().getPageDom4j("Media catalog");
		// *********************************************************************
		// +++ generate resource sort modes
		page.add(CatalogXmlBuilder.get().getSortModeOptionDom4j());
		// - generate resource sort modes
		// *********************************************************************
		// +++ generate resource view modes
		page.add(CatalogXmlBuilder.get().getViewModeOptionDom4j());
		// - generate resource view modes
		
		
		page.add(CatalogXmlBuilder.get().getBrowserConfigDom4j(
				parentDir, currentDir, viewMode, sortMode, actionMode, showMode
		));
		
		page.addAttribute("userId", request.getSession().getUserId()+"");
		page.addAttribute("sessionId", request.getHttpRequest().getSession().getId());

		if (logger.isInfoEnabled()){
		    logger.info("first part " + timer);
		}

		page.add(CatalogXmlBuilder.get().getRepositoryDom4j());

		if (logger.isInfoEnabled()){
		    logger.info("repository xml:" + timer);
		}
		
		try{
		    
			Element directory = Folder.toFakeDom4jDirectoryElement(); 
			directory = folder.toDom4jDirectoryElement(
				request.getSession().getUser(), sortMode, viewMode, showMode
			)
				.addAttribute("parent-dir", parentDir)
				.addAttribute("current-dir", currentDir)
			;
			if (logger.isInfoEnabled()){
			    logger.info("folder xml building " + timer);
			}

			page.add(directory);

		}
		catch (RepositoryException e){
			if (errorMessage != null){
				errorMessage = errorMessage + ". " + e.getMessage();
			}
			else {
				errorMessage = e.getMessage(); 
			}
		}	

		// Process error message
		if ( (errorMessage != null) && (!errorMessage.equalsIgnoreCase("")) ){
			page.add(CatalogXmlBuilder.get().getErrorMessageDom4j(errorMessage));
		}

		// Process user message
		if ( (userMessage != null) && (!userMessage.equalsIgnoreCase("")) ){
			page.add(CatalogXmlBuilder.get().getUserMessageDom4j(userMessage));
		}

		Document doc = DocumentHelper.createDocument(page);

        doc.getRootElement().addAttribute(
            "interface-language",
            request.getSession().getInterfaceLanguageCode()
        );

		try {
			
			Templates templates = null;
			if ( Repository.get().getConfig().isXmlCached() ){
				if ( CACHED_TEMPLATES == null ){
                    CACHED_TEMPLATES = ResourceMap.getTemplates(STYLE_SHEET);
				}
				templates = CACHED_TEMPLATES;
			}
			else{
				CACHED_TEMPLATES = null;
				templates = ResourceMap.getTemplates(STYLE_SHEET);
			}
			
			
			if (logger.isInfoEnabled()){
			    logger.info("get templates: " + timer);
			}
			HttpServletResponse httpResponse = getServletResponse(request);
			httpResponse.setContentType(HTML_MIME_TYPE);
			
			templates.newTransformer().transform(new DocumentSource(doc), 
				new StreamResult(httpResponse.getOutputStream())
			);
			
			//XmlHelper.serializeXml(doc, httpResponse.getOutputStream());
			if (logger.isInfoEnabled()){
			    logger.info("xslt + serialize " + timer);
			}
		} catch (Exception e) {
			handleException(e);
		}
		if (logger.isInfoEnabled()){
		    logger.info("execution time: " + timer.formatTotal());
		}
		logger.debug("-");
	}
}
