/*
 * @(#)$Id: FileResource.java,v 1.7, 2007-02-19 08:15:48Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.domain;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.QNameCache;
import org.w3c.dom.Document;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.dependency.ResourceDependencyLocator;

/**
 *
 * File resource implamentation
 * 
 * @version		$Revision: 8$
 * @author		Olexiy Strashko
 * 
 */
public class FileResource extends Resource {
	private static Logger logger = Logger.getLogger( FileResource.class );

	private File file = null;
	
	private static QNameCache qNameCache = new QNameCache();
	
	/**
	 * 
	 */
	private FileResource() { 
		super();
	}

	public FileResource(File file) {
		super();
		this.file = file;
	}

	/**
	 * 
	 * @return
	 */
	public Element toDom4jElement(User user, String modeType, String showMode) {
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();
		Element element = DocumentHelper.createElement(
			qNameCache.get("resource", ngNamespace)
		)
			.addAttribute("name", this.getName())
			.addAttribute("escapedName",  StringEscapeUtils.escapeHtml((this.getName())))
			.addAttribute("title", this.getTitle())
			.addAttribute("extension", this.getExtension())
			.addAttribute("size", StringUtil.formatSizeInKb(this.getSize()))
			.addAttribute("last-modified", this.getLastModified())
			.addAttribute("type", this.getType())
		;
		if (showMode != null && showMode.equals("true") && !ResourceFilter.get().isAllow(this, "false"))
			element.addAttribute("isHidden", "true");
		return element;
	}

	/**
	 * 
	 * @return
	 */
	/*public Element toDom4jElement(User user, String showMode) {
		return this.toDom4jElement(user, null, showMode);
	}*/

	/**
	 * 
	 * @return
	 */
	public Element toDom4jElement(User user) {
		return this.toDom4jElement(user, null,"true");
	}

// **************** BASE METHODS ********************************//	

	/**
	 * 
	 * @return
	 * @throws CriticalException
	 */
	public Element getDependenciesDom4j() throws CriticalException{
	    logger.info("CatalogPath:" + this.getCatalogPath());
		return ResourceDependencyLocator.get()
			.getResourceDependenciesXml(this.getCatalogPath()); 
	}
	
	/**
	 * 
	 * @return
	 */	
	public boolean hasDependencies() throws CriticalException {
		return ResourceDependencyLocator.get().hasDependencies(this.getCatalogPath());
	}
	
// ************ Type dependent ******************************/  	
	/**
	 *  
	 * @return
	 */
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Get size
	 * 
	 * @return
	 */
	public long getSize() {
		return this.getFile().length();
	}


	public String toString(){
		return 
			"File: " + this.getName() + 
			", ext: " + this.getExtension() +
			", dir: " + this.isFolder() +
			", size: " + StringUtil.formatSizeInKb(this.getSize()) +
			", modified: " + this.getLastModified() +
			", type: " + this.getType()
		;
	}
	
	/**
	 * Delete resource from MediaCatalog.
	 * 
	 * @return
	 * @throws AccessDeniedException
	 */
	public boolean delete(User user) 
		throws CriticalException, RepositoryException, AccessDeniedException 
	{
	    if ( !this.getParentFolder().canEdit(user) ) {
	        throw new AccessDeniedException();
	    }
	    // check if this file has dependencies
	    // if so - delete operation is denied
//		if ( this.hasDependencies() ){
//			throw new RepositoryException("Sorry, file '" +
//				this.getName() + "' has external links and cannot be deleted"
//			);
//		}
		return this.getFile().delete();
	}
	
	/**
	 * Rename resource.
	 * 
	 * @return
	 * @throws AccessDeniedException
	 */
	public boolean rename(User user, String newName) 
		throws CriticalException, RepositoryException, AccessDeniedException  
	{
	    if ( !this.getParentFolder().canEdit(user) ) {
	        throw new AccessDeniedException();
	    }
		if (this.hasDependencies()){
			throw new RepositoryException("Sorry, file '" +
				this.getName() + "' has external links and cannot be renamed"
			);
		}
		File newFile = new File(
				FileUtils.getPath(this.getFile().getAbsolutePath()), 
				newName
			);
		return this.getFile().renameTo(newFile);
	}


	/**
	 * Move file to target folder with permissions checked.  
	 * 
	 * @param user
	 * @param target
	 * @return
	 * @throws CriticalException
	 * @throws RepositoryException
	 * @throws AccessDeniedException
	 */
	public boolean move(User user, Folder target) 
		throws CriticalException, RepositoryException, AccessDeniedException  
	{
	    
	    // check access rights
	    if ( (!target.canEdit(user)) || (!this.getParentFolder().canEdit(user)) ){
	        throw new AccessDeniedException();
	    }
	
	    // check dependencies
	    if ( this.hasDependencies() ){
			throw new RepositoryException(
				"File '" + 
				this.getName() +
				"' has external links and cannot be moved"
			);
	    }

	    this.usafeCopyFile(target);
			
		this.delete(user);
	    return true;
	}
	
	/**
	 * Copy file to target folder with permissions checked.  
	 * 
	 * @param user
	 * @param target
	 * @return
	 * @throws CriticalException
	 * @throws RepositoryException
	 * @throws AccessDeniedException
	 */
	public boolean copy(User user, Folder target) 
		throws CriticalException, RepositoryException, AccessDeniedException  
	{
	    // check access rights
	    if ( !target.canEdit(user) ){
	        throw new AccessDeniedException();
	    }
	    this.usafeCopyFile(target);
	    return true;
	}
	

	/**
	 * Copy file without permissions checking. 
	 * 
	 * @param target
	 * @throws RepositoryException
	 * @throws CriticalException
	 */
	public void usafeCopyFile(Folder target) throws RepositoryException, CriticalException{
	    // check source existance
		if ( !this.exists() ){
			throw new RepositoryException("Source file '" + this.getName() +
				"' not exists"
			);
		}
	    
	    FileResource destResource = target.newFileResource( this.getName() );
	    
	    // check destination existance
		if ( destResource.exists() ){
			throw new RepositoryException("File '" + this.getName() +
				"' allready exists in '" + target.getName() + "'"  
			);
		}
		
	    // create and copy file
		try{
			if ( !destResource.getFile().createNewFile() ){
				throw new RepositoryException("Unable to create a file '" + 
					this.getName() + "' in '" + target.getName() + "'"  
				);
			}
            org.apache.commons.io.FileUtils
                .copyFile( this.getFile(), destResource.getFile() );
		}
		catch(IOException e){
		    logger.error("error", e);
		    throw new CriticalException(e);
		}
	}
	
	public File getFile() {
		return this.file;
	}

	public org.w3c.dom.Element getElement(Document doc, User user, String modeType) 
		throws CriticalException 
	{
		return this.getElement(doc, user);
	}

	public org.w3c.dom.Element getElement(Document doc, User user) throws CriticalException {
		org.w3c.dom.Element resourceEl = Xbuilder.createEl(doc, "resource", null);
		Xbuilder.setAttr(resourceEl, "name", this.getName());
		Xbuilder.setAttr(resourceEl, "title", this.getTitle());
		Xbuilder.setAttr(resourceEl, "extension", this.getExtension());
		Xbuilder.setAttr(resourceEl, "size", StringUtil.formatSizeInKb(this.getSize()));
		Xbuilder.setAttr(resourceEl, "last-modified", this.getLastModified());
		Xbuilder.setAttr(resourceEl, "type", this.getType());
		
		return resourceEl;
	}

}
