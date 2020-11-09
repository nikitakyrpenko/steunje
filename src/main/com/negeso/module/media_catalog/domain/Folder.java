/*
 * @(#)$Id: Folder.java,v 1.17, 2007-02-19 09:20:04Z, Olexiy Strashko$
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
import java.io.FileFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.QNameCache;
import org.w3c.dom.Document;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.AccessFileFilter;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.dependency.ResourceDependencyLocator;

/**
 *
 * Folder domain object. (Secured)
 * 
 * @version		$Revision: 18$
 * @author		Olexiy Strashko
 * 
 */
public class Folder extends Resource  {
    
	private static Logger logger = Logger.getLogger( Folder.class );

	private static QNameCache qNameCache = new QNameCache();

	private File file = null;
	
	private FolderDomain domain = null;
	
	private boolean containHiddenContent;
	private boolean containViewableContent;

    /**
     * 
     */
    public Folder() {
        super();
    }

    /**
     * 
     */
    public Folder(File file) {
        super();
        this.file = file;
    }

    
	/**
	 * @return Returns the domain.
	 */
	public FolderDomain getDomain() {
		if (domain == null){
			try{
				domain = Repository.get().getCache().getFolderDomain(
					this.getCatalogPath()
				);
			}
			catch(CriticalException e){
				logger.error("-error", e);
			}
		}
		return domain;
	}


    public boolean canView(User user) {
    	FolderDomain domain = this.getDomain();
    	if ( domain == null ) return false;
    	return SecurityGuard.canView(
    		user, domain.getContainerId()
		);
    }
    
    public boolean canEdit(User user) {
    	FolderDomain domain = this.getDomain(); 
    	if ( domain == null ) return false;
    	return SecurityGuard.canEdit(
   			user, domain.getContainerId()
		);
    }

    public boolean canManage(User user) {
    	FolderDomain domain = this.getDomain(); 
    	if ( domain == null ) return false;
    	return SecurityGuard.canManage(
   			user, domain.getContainerId()
		);
    }
    
    /**
     * @return
     */
    public Long getContainerId() {
    	FolderDomain domain = this.getDomain(); 
    	if ( domain == null ) return Repository.DEFAULT_CONTAINER;
    	return domain.getContainerId();
    }

    /**
     * @return
     */
    public String getContainerName() { 
    	FolderDomain domain = this.getDomain(); 
    	if ( domain == null ) return Repository.DEFAULT_CONTAINER_NAME;
    	return domain.getContainerName();
    }
    
    /**
     * Folder id getter
     * 
     * @return
     */
    public Long getId() {
    	FolderDomain domain = this.getDomain(); 
    	if ( domain == null ) return Repository.DEFAULT_FOLDER_ID; 
    	return domain.getId();
    }

    
	/**
	 *  
	 * @return
	 */
	public boolean isEmpty() {
		if ( this.getFile().listFiles().length == 0 ){
			return true;
		}
		return false;
	}
    
	/**
	 * Get size
	 * 
	 * @return
	 */
	public long getSize() {
		return FileUtils.sizeOfDirectory(file);
	}

	/**
	 * Delete Folder from MediaCatalog. 
	 * 
	 * @return
	 * @throws AccessDeniedException
	 */
	public boolean delete(User user) 
		throws CriticalException, RepositoryException, AccessDeniedException 
	{
	    if ( !this.canManage(user) ){
	        throw new AccessDeniedException();
	    }
	    // check if this is a not empty folder,
	    // if so - delete operation is denied
	    if ( !this.isEmpty() ){
			throw new RepositoryException(
			    "Sorry, folder '" +	this.getName() + 
			    "' contain files and cannot be deleted"
			);
		}
	    
//		if ( this.hasDependencies() ){
//			throw new RepositoryException("Sorry, folder '" +
//				this.getName() + "' has external links and cannot be deleted"
//			);
//		}

	    // delete folder domain 
	    Connection con = null;
	    try{
	        con = DBHelper.getConnection();
		    this.getDomain().delete(con);
	    }
	    catch(SQLException e){
	        logger.error("-error", e);
	        throw new CriticalException(e);
	    }
	    finally{
	        DBHelper.close(con);
	    }
	    // clear cache
	    Repository.get().getCache().clearFolderDomainCache();
		return this.getFile().delete();
	}

	/**
	 * Rename resource.
	 * 
	 * @return
	 */
	public boolean rename(User user, String newName) 
		throws CriticalException, RepositoryException 
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return
	 */
	public List getContents(User user, Comparator comparator, Set extFilter, String showMode) {
		this.containHiddenContent = false;
		this.containViewableContent = false;
		List<Resource> fileList = new ArrayList<Resource>();
		List<Resource> folderList = new ArrayList<Resource>();
		if (this.isFolder()){
			FileFilter filter = null;
			if (extFilter == null){
				filter = new AccessFileFilter(user);
			}
			else{
				filter = new AccessFileFilter(user, extFilter);
			}
			File[] files = this.getFile().listFiles(filter);

			Resource curr = null;
			for (int i = 0; i < files.length; i++){
				curr = Repository.get().getResource(files[i]);
				if (ResourceFilter.get().isAllow(curr, showMode)){
					if (curr.isFolder()){
						folderList.add(curr);
					}
					else{
						fileList.add(curr);
					}
				}
				if (!ResourceFilter.get().isAllow(curr, "false")){
					this.containHiddenContent = true;
				}else{
					this.containViewableContent = true;
				}
			}
		}
		Collections.sort(folderList, comparator);
		Collections.sort(fileList, comparator);
		folderList.addAll(fileList);
		return folderList;
	}

	/**
	 *  
	 * @return
	 * @throws RepositoryException
	 */
	public Element toDom4jDirectoryElement(User user, String sortType, String viewMode, String showMode) 
		throws RepositoryException
	{
		if (!this.isFolder()){
			throw new RepositoryException(
				"Requested directory is not a directory <" + this.getName()+ ">"
			);
		} 
		
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();
		Element element = this.toDom4jElement(user, viewMode); 
		element.setQName(qNameCache.get("directory", ngNamespace));
		
		List list = this.getContents(
			user, 
			new ResourceComparator(sortType),
			Repository.get().getExtensionsByResourceType(viewMode), 
			showMode
		);
		int filesCount = 0;
		Resource curr = null;
		Element currEl = null;
		MessageFormat formatter = new MessageFormat("icon_{0}.gif");
		Object[] arg = new Object[1];
		for (Iterator iter = list.iterator(); iter.hasNext();){
			curr = (Resource)iter.next();
			if (curr.isFolder()) {
				((Folder)curr).getContents(user, new ResourceComparator(sortType), Repository.get().getExtensionsByResourceType(viewMode), showMode);				
			} else {
				filesCount ++;
			}
			currEl = curr.toDom4jElement(user, viewMode, showMode);
			arg[0] = curr.getType();
			currEl.addAttribute(
				"icon-image", 
				formatter.format(arg)
			);
			element.add( currEl );
		}
		element.addAttribute("filesCount", String.valueOf(filesCount));
		return element;
	}
 
	/**
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Element toDom4jDirectoryElement(User user) throws RepositoryException{
		return toDom4jDirectoryElement(user, Repository.DEFAULT_SORT_MODE, null, "false");
	}

	/**
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public static Element toFakeDom4jDirectoryElement() {
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();
		Element element = DocumentHelper.createElement(
			qNameCache.get("directory", ngNamespace)
		);
		return element;
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.negeso.module.media_catalog.domain.AbstractResource#toDom4jElement()
	 */
	public Element toDom4jElement(User user, String showMode) {
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();
		Element element = DocumentHelper.createElement(
			qNameCache.get("resource", ngNamespace)
		)
			.addAttribute("id", this.getId().toString())
			.addAttribute("name", this.getName())
			.addAttribute("escapedName",  StringEscapeUtils.escapeHtml((this.getName())))
			.addAttribute("title", this.getTitle())
			.addAttribute("extension", this.getExtension())
			.addAttribute("size", StringUtil.formatSizeInKb(this.getSize()))
			.addAttribute("last-modified", this.getLastModified())
			.addAttribute("type", this.getType())
			.addAttribute("can-view", "" + this.canView(user))
			.addAttribute("can-edit", "" + this.canEdit(user))
			.addAttribute("can-manage", "" + this.canManage(user))
			.addAttribute("container-name", this.getContainerName())
			.addAttribute("container-id", this.getContainerId() == null? "": this.getContainerId().toString())
		;
		if (showMode != null && showMode.equals("true") && !ResourceFilter.get().isAllow(this, "false"))
			element.addAttribute("isHidden", "true");
		element.addAttribute("containHiddenContent", isContainHiddenContent()?"true":"false");
		element.addAttribute("containViewableContent", isContainViewableContent()?"true":"false");
		return element;
	}
	
	public Element toDom4jElement(User user, String modeType, String showMode) {
		return this.toDom4jElement(user, showMode);
	} 
	
	public Element toDom4jElement(User user) {
		return this.toDom4jElement(user, "true");
	} 

	/* (non-Javadoc)
	 * @see com.negeso.module.media_catalog.domain.AbstractResource#getFile()
	 */
	public File getFile() {
		return this.file;
	}

	/* (non-Javadoc)
	 * @see com.negeso.module.media_catalog.domain.AbstractResource#getParentFolder()
	 */
	public Folder getParentFolder() {
		return new Folder(this.getFile().getParentFile());
	}

    /* (non-Javadoc)
     * @see com.negeso.module.media_catalog.domain.Resource#getDependenciesDom4j()
     */
    public Element getDependenciesDom4j() throws CriticalException {
        return null;
    }

    /* (non-Javadoc)
     * @see com.negeso.module.media_catalog.domain.Resource#hasDependencies()
     */
    public boolean hasDependencies() throws CriticalException {
		return ResourceDependencyLocator.get().hasFolderDependencies(this.getCatalogPath());
    }

    /**
     * @param name
     * @return
     */
    public FileResource newFileResource(String name) {
        if (name == null){
            logger.error("file name is null");
        }
        return Repository.get().getFileResource(new File(this.getFile(), name));
    }


	
	public org.w3c.dom.Element getElement(org.w3c.dom.Document doc, User user){
		org.w3c.dom.Element folderEl = Xbuilder.createEl(doc, "resource", null);

		Xbuilder.setAttr(folderEl, "name", this.getName());
		Xbuilder.setAttr(folderEl, "title", this.getTitle());
		Xbuilder.setAttr(folderEl, "extension", this.getExtension());
		Xbuilder.setAttr(folderEl, "size", "" + this.getSize());
		Xbuilder.setAttr(folderEl, "last-modified", this.getLastModified());
		Xbuilder.setAttr(folderEl, "type", this.getType());
		Xbuilder.setAttr(folderEl, "can-view", "" + this.canView(user));
		Xbuilder.setAttr(folderEl, "can-edit", "" + this.canEdit(user));
		Xbuilder.setAttr(folderEl, "can-manage", "" + this.canManage(user));
		Xbuilder.setAttr(folderEl, "container-name", this.getContainerName());
		Xbuilder.setAttr(folderEl, "container-id", 
			this.getContainerId() == null? "": this.getContainerId().toString()
		);
		
		return folderEl;
	}

	/* (non-Javadoc)
	 * @see com.negeso.module.media_catalog.domain.Resource#getElement(org.w3c.dom.Document, com.negeso.framework.domain.User, java.lang.String)
	 */
	public org.w3c.dom.Element getElement(Document doc, User user, String modeType) throws CriticalException {
		return this.getElement(doc, user);
	}

	/**
	 * @throws CriticalException
	 * 
	 *
	 */
	public void setContainerId(Long containerId) throws CriticalException{
		FolderDomain domin = this.getDomain();
		
		if ( domain != null ){
			Connection con = null;
			try{
				con = DBHelper.getConnection();
				this.getDomain().setContainerId(containerId);
				this.getDomain().update(con);
				Repository.get().getCache().clearFolderDomainCache();
			}
			catch(SQLException e){
				logger.error(
					"Error setContainerId, Folder:" + this.getName() + " cid" + containerId, 
					e
				);
			}
		}
	}
	
	private boolean isContainHiddenContent(){
		return this.containHiddenContent;
	}
	
	private boolean isContainViewableContent(){
		return this.containViewableContent;
	}
	
}
