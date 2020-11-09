/*
 * @(#)$Id: Resource.java,v 1.23, 2007-02-19 09:20:07Z, Olexiy Strashko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.domain;

import org.dom4j.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;

import java.io.File;
import java.util.Date;


/**
 * Resource incapsulation domain. 
 *
 * @version 	$Revision: 24$
 * @author 		Olexiy.Strashko
 */
abstract public class Resource {

	public static String DIRECTORY_TYPE = "dir";
	public static String IMAGE_TYPE = "image";
	public static String HTML_TYPE = "html";
	public static String TEXT_TYPE = "text";
	public static String DOCUMENT_TYPE = "doc";
	public static String UNKNOWN_TYPE = "file";
	

	private String nameNoExt = null;
	private String type = null;
	private String title = "No title here";
	private String catalogPath = null;
	private boolean isHidden = false;
	
	/**
	 * Get resource extensions by resource type. 
	 * 
	 * @param resourceType
	 * @return
	 */
	public static String[] getExtensionsByResourceType(String resourceType){
		if (resourceType == null){
			return Configuration.allExtensions;
		}
		if (resourceType.equalsIgnoreCase(IMAGE_TYPE)){
			// images
			return Configuration.imageTypeExtensions;
		}
		else if (resourceType.equalsIgnoreCase(DOCUMENT_TYPE)){
			// documents
			return Configuration.documentTypeExtensions;
		}
		else{
			// default - all types 
			return Configuration.allExtensions;
		}
	}
	
	
	
	/**
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	
	/**
	 * @return
	 */
	public String getLastModified() {
		return Configuration.get().getDateFormat().format(
			new Date(this.getFile().lastModified())
		); 
	}

	/**
	 * @return
	 */
	public boolean exists() {
		return this.getFile().exists();
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.getFile().getName();
	}

	/**
	 * @return
	 */
	public boolean isFolder() {
		return this.getFile().isDirectory();
	}

	/**
	 * @return
	 */
	public boolean isImage() {
		if (this.getType().equalsIgnoreCase(Resource.IMAGE_TYPE)){
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public String getType(){
		if (this.type == null){
			if (this.isFolder()){
				// DIRECTORY_TYPE
				this.type = Resource.DIRECTORY_TYPE;
			}else if (StringUtil.isInStringArray(
				Configuration.imageTypeExtensions, 
				this.getExtension())
			){
				// IMAGE_TYPE
				this.type = Resource.IMAGE_TYPE;
			} else if (StringUtil.isInStringArray(
				Configuration.textTypeExtensions, 
				this.getExtension())
			){
				// TEXT_TYPE
				this.type = Resource.TEXT_TYPE;
			} else if (StringUtil.isInStringArray(
				Configuration.documentTypeExtensions, 
				this.getExtension())
			){
				// DOCUMENT_TYPE
				this.type = Resource.DOCUMENT_TYPE;
			} else if (StringUtil.isInStringArray(
				Configuration.htmlTypeExtensions, 
				this.getExtension())
			){
				// HTML_TYPE
				this.type = Resource.HTML_TYPE;
			} else 
			{
				// UNKNOWN_TYPE
				this.type = Resource.UNKNOWN_TYPE;
			}
		}
		return this.type;
	}

	/**
	 * @return
	 */
	public String getExtension() {
		return FileUtils.getExtension(this.getFile());
	}
	
	/**
	 * @return
	 */
	public String getNameNoExt() {
		if (this.nameNoExt == null){
			this.nameNoExt = FileUtils.removeExtension(this.getName());
		}
		return this.nameNoExt;
	}
	
	/**
	 * Get Media Catalog path 
	 * 
	 * @return		The String MediaCatalog resource path 
	 */
	public String getCatalogPath() {
	    if ( this.catalogPath == null ){
	        this.catalogPath = Repository.get().getCatalogPath(this.getFile());
	    }
	    return this.catalogPath;
	}

	/**
	 * Get Parent folder
	 * 
	 * @return
	 */
	public Folder getParentFolder(){
		return Repository.get().getFolder(this.getFile().getParentFile());
	}

	/**
	 * File getter
	 * 
	 * @return
	 */
	abstract public File getFile();

	/**
	 * Size getter
	 * 
	 * @return
	 */
	abstract public long getSize();
	
	/**
	 * Tests if resource is empty
	 * 
	 * @return
	 */
	abstract public boolean isEmpty();
	
	/**
	 * Delete resource (secured)
	 * 
	 * @param user
	 * @return
	 * @throws CriticalException
	 * @throws RepositoryException
	 */
	abstract public boolean delete(User user) 
		throws CriticalException, RepositoryException, AccessDeniedException
	; 

	/**
	 * Rename resource (secured)
	 * 
	 * @param user
	 * @param newName
	 * @return
	 * @throws CriticalException
	 * @throws RepositoryException 
	 */
	abstract public boolean rename(User user, String newName) 
		throws CriticalException, RepositoryException, AccessDeniedException
	;

	/**
	 * To xml element
	 * 
	 * @param modeType
	 * @return
	 */
	abstract public Element toDom4jElement(User user);

	/**
	 * To xml element
	 * 
	 * @param modeType
	 * @return
	 */
	abstract public Element toDom4jElement(User user, String modeType, String showMode);

	/**
	 * 
	 * @return
	 * @throws CriticalException
	 */
	abstract public Element getDependenciesDom4j() throws CriticalException;
	
	/**
	 * 
	 * @return
	 * @throws CriticalException
	 */
	abstract public boolean hasDependencies() throws CriticalException;

	/**
	 * 
	 * @return
	 * @throws CriticalException
	 */
	abstract public org.w3c.dom.Element getElement(
			org.w3c.dom.Document doc, User user, String modeType
	) throws CriticalException;
	
	/**
	 * 
	 * @return
	 * @throws CriticalException
	 */
	abstract public org.w3c.dom.Element getElement(
			org.w3c.dom.Document doc, User user
	) throws CriticalException;
	
}