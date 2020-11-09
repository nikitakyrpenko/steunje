/*
 * @(#)$Id: CatalogXmlBuilder.java,v 1.14, 2005-06-06 13:04:11Z, Stanislav Demchenko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog;


import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.QNameCache;

import com.negeso.framework.domain.User;
import com.negeso.framework.generators.StandardXmlBulder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.domain.Resource;


/**
 * Media Catalog xml hepler methods
 *
 * @version 	$Revision: 15$
 * @author 		OStrashko
 */
public class CatalogXmlBuilder extends StandardXmlBulder{
    
    private static Logger logger = Logger.getLogger(CatalogXmlBuilder.class);
	
	private QNameCache qNameCache = new QNameCache();
	private Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();

	/**
	 * 
	 */
	
	private static CatalogXmlBuilder instance = new CatalogXmlBuilder();
	
	private Element repositoryElement = null;
	
	public CatalogXmlBuilder() {
		super();
	}
	
	public static CatalogXmlBuilder get(){
		return instance;
	}
	
	/**
	 * 
	 * @return
	 */	
	public Element getRepositoryDom4j(){
	    logger.debug("+");
	    
	    if ( this.repositoryElement == null ){
		    this.repositoryElement = DocumentHelper.createElement(
				qNameCache.get("repository", ngNamespace)
			)
				.addAttribute(
				        "total-space", 
				        StringUtil.formatSizeInKb( Repository.get().getTotalSpace() )
				 )
				.addAttribute(
				        "max-file-size", 
				        StringUtil.formatSizeInKb( Repository.get().getMaxFileSizeString() )
				 )
			;
	    }
	    long usedSpace = Repository.get().getUsedSpace();
	    this.repositoryElement
			.addAttribute(
			        "used-space", 
			        StringUtil.formatSizeInKb( usedSpace )
			 )
			.addAttribute(
			        "free-space",	
			        StringUtil.formatSizeInKb( Repository.get().getTotalSpace() - usedSpace )
			 )
	    ;
	    logger.debug("-");
		return (Element) this.repositoryElement.detach();
	}

	/**
	 * 
	 * @return
	 */	
	public Element getBrowserConfigDom4j(String parentDir, String currentDir, 
		String viewMode, String sortMode, String actionMode, String showMode)
	{
		Element repository = DocumentHelper.createElement(
			qNameCache.get("browser-config", ngNamespace)
		)
			.addAttribute("view-mode", viewMode)
			.addAttribute("sort-mode", sortMode)
			.addAttribute("action-mode", actionMode)
			.addAttribute("parent-dir", parentDir)
			.addAttribute("current-dir", currentDir)
			.addAttribute("show-hidden-files", showMode)
		;
		return repository;
	}

	/**
	 * 
	 * @return
	 */
	public Element getViewModeOptionDom4j(){
		Element option = DocumentHelper.createElement(
			qNameCache.get("resource-view-modes-option", ngNamespace)
		)
			.addAttribute("submit-name", "+")
		;
		
		// node
		option.addElement(
			qNameCache.get("view-mode", ngNamespace)
		) 
			.addAttribute("value", Repository.FILE_MANAGER_VIEW_MODE)
			.addAttribute("text", "File list")
		;

		// node
		option.addElement(
			qNameCache.get("view-mode", ngNamespace)
		) 
			.addAttribute("value", Repository.IMAGE_GALLERY_VIEW_MODE)
			.addAttribute("text", "Image gallery")
		;

		// node
		option.addElement(
			qNameCache.get("view-mode", ngNamespace)
		) 
			.addAttribute("value", Repository.IMAGE_LIST_VIEW_MODE)
			.addAttribute("text", "Image list")
		;

		return option;
	}
	
	/**
	 * 
	 * @return
	 */
	public Element getSortModeOptionDom4j(){
		Element option = DocumentHelper.createElement(
			qNameCache.get("resource-sort-modes-option", ngNamespace)
		)
			.addAttribute("submit-name", "+");
		// node
		option.addElement(
			qNameCache.get("sort-mode", ngNamespace)
		) 
			.addAttribute("value", Repository.SORT_MODE_NAME)
			.addAttribute("text", "Sort by name")
		;
		// node
		option.addElement(
			qNameCache.get("sort-mode", ngNamespace)
		) 
			.addAttribute("value", Repository.SORT_MODE_TYPE)
			.addAttribute("text", "Sort by type")
		;
		// node
		option.addElement(
			qNameCache.get("sort-mode", ngNamespace)
		) 
			.addAttribute("value", Repository.SORT_MODE_SIZE)
			.addAttribute("text", "Sort by size")
		;
		return option;
	}


	/**
	 * @return
	 */
	public Namespace getNgNamespace() {
		return ngNamespace;
	}


	/**
	 * 
	 * @return
	 */
	public Element getRenameResourceViewOptionDom4j(String name, Resource resource){
		Element option = DocumentHelper.createElement(
			qNameCache.get("rename-resource-option", 
			CatalogXmlBuilder.get().getNgNamespace())
		);
		option.addAttribute("submit-name", "Rename File");
		option.addAttribute("old-name", resource.getNameNoExt());
		option.addAttribute("extension", resource.getExtension());

		return option;
	}


	/**
	 * 
	 * @return
	 */
	public Element getMoveResourceViewOptionDom4j(User user, String name, Resource resource, String showMode){
		Element option = DocumentHelper.createElement(
			qNameCache.get("move-resource-option", CatalogXmlBuilder.get().getNgNamespace())
		);
		
		option.addAttribute("submit-name", name);
		option.addAttribute("resourceName", resource.getName());
		option.add(Repository.get().getAvailableFoldersElement( user, showMode ));

		return option;
	}
	
	/**
	 * Build resource dependency xml. 
	 * 
	 * @param domain 		The domain of dependency, String
	 * @param id			The id of dependency, Long
	 * @param href			The href of dependency, String (relative)
	 * @param title			The title of dependency, String
	 * @return				The produced dom4j Element Object
	 */
	public Element buildDependencyDom4j(String domain, Long id,
		String href, String title)
	{
		Element dependency = DocumentHelper.createElement(
			qNameCache.get("dependency", 
			CatalogXmlBuilder.get().getNgNamespace())
		);
		
		dependency.addAttribute("category", domain);
		dependency.addAttribute("id", id.toString());
		dependency.addAttribute("href", href);
		dependency.addAttribute("title", title);

		return dependency;
	}

}
