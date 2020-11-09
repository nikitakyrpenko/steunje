/*
 * @(#)$Id: RepositoryXmlBuilder.java,v 1.8, 2007-02-19 09:20:07Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.generator;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.util.Timer;
import com.negeso.module.media_catalog.FileKeeper;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 *
 * Repository xml builder
 * 
 * @version		$Revision: 9$
 * @author		Olexiy Strashko
 * 
 */
public class RepositoryXmlBuilder {
	private static Logger logger = Logger.getLogger(RepositoryXmlBuilder.class);
    private static RepositoryXmlBuilder instance = null;
    
    
	public static RepositoryXmlBuilder get(){
	    if ( RepositoryXmlBuilder.instance == null ){
	        RepositoryXmlBuilder.instance = new RepositoryXmlBuilder();
	    }
	    return RepositoryXmlBuilder.instance;
	}
	
	
    private RepositoryXmlBuilder(){
        super();
    }
    
    
	/**
	 * Return ResourseSet Element with files gathered from Repository.
	 * Files are filtered by :
	 *  - list of extensions, if empty - all extension are gathered (null means 
	 * 	  empty list) 
	 *  - root folder strcits file scope for gathering. 
	 * 
	 * Security rights are applied.
	 * 
	 * @param doc
	 * @param user
	 * @param catalogPath
	 * @param exts
	 * @return
	 */
    public Element getAvailableFilesElement(
            Element parent, 
			User user, 
			String catalogPath, 
			String[] exts, 
			boolean isOptimized)
    {
    	Timer timer = null;
    	if ( logger.isInfoEnabled() ){
    		timer = new Timer();
    	}

	    if (exts == null){
			exts = new String[0]; 
		}

		Folder folder = Repository.get().getFolder(catalogPath);

		Collection<File> fileList = new TreeSet<File> ();
        if ( folder.exists() && (folder.isFolder()) ){
			fileList = FileKeeper.listFiles(
			    user,  
			    folder.getFile(), 
			    fileList, 
			    exts
			);
		}
        
		Repository repository = Repository.get();
		File currFile = null;
		/* Create root element */
		Element resourceSet = null;
        if ( isOptimized ){
    		/* generate html */
        	StringBuffer buffer = new StringBuffer();
			//buffer.append("<![CDATA[");
        	
			String path = null;
			MessageFormat formatter = new MessageFormat(
				"<option value=\"{0}\">{1}</option>"
			);
			buffer.append("<option value=\"\"></option>");
			Object[] args = new Object[2];
			for (Iterator iter = fileList.iterator(); iter.hasNext();){
				currFile = (File)iter.next();
				if (currFile.isFile()){
					path = repository.getCatalogPath(currFile);
					args[0] = path;
					args[1] = path;
					buffer.append(formatter.format(args));
				}
			}
			//buffer.append("]]>");
			resourceSet = Xbuilder.addEl(parent, "resource-set", buffer.toString());
			Xbuilder.setAttr(resourceSet, "is-optimized", "true");
        }
        else{
			/* generate xml */
			resourceSet = Xbuilder.addEl(parent, "resource-set", null);
			Element currEl = null;
			
			for (Iterator iter = fileList.iterator(); iter.hasNext();){
				currFile = (File)iter.next();
				if (currFile.isFile()){
					currEl = Xbuilder.addEl(resourceSet, "resource", null);
					Xbuilder.setAttr(currEl, "name", currFile.getName());
					Xbuilder.setAttr(currEl, "path", repository.getCatalogPath(currFile));
				}
			}
        }

        if ( logger.isInfoEnabled() ){
        	logger.info("View building time:" + timer.stop());
        }
		return resourceSet;
    }

    public Element getAvailableFilesElement(
            Element parent, User user, String[] exts, boolean isOptimized )
    {
        return this.getAvailableFilesElement(
        	parent, user, Repository.get().getRootPath(), exts, isOptimized
		);
    }
    

    
    public Element getAvailableFoldersElement(
            Element parent, User user, String catalogPath )
    {
		/* Create root element */
		Element folderSet = Xbuilder.addEl(parent, "folder-set", null);

		/* Build list of folders */
		Folder folder = Repository.get().getFolder(catalogPath);

		Collection<File> fileList = new ArrayList<File>();
		fileList.add( folder.getFile() );
		if ( folder.isFolder() && folder.exists() ){
			fileList = FileKeeper.listFolders(user, folder.getFile(), fileList);
		}
		
		
		/* generate xml */
		Element currEl = null;
		File currFile = null;

		Repository repository = Repository.get();
		for (Iterator iter = fileList.iterator(); iter.hasNext();){
			currFile = (File)iter.next();
			if (currFile.isDirectory()){
				currEl = Xbuilder.addEl( folderSet, "folder", null );
				Xbuilder.setAttr( currEl, "name", currFile.getName() );
				Xbuilder.setAttr( currEl, "path", repository.getCatalogPath(currFile) );
			}
		}
		
		return folderSet;
    }

    public Element getAvailableFoldersElement( Element parent, User user ){
        return this.getAvailableFoldersElement(
                parent, user, Repository.get().getRootPath()
        );
    }
    
	public Element getFileUploadOption(Element parent, String workingFolder, String mode) {
		Element option = Xbuilder.addEl(parent, "upload-option", null);
		Xbuilder.setAttr(option, "working-folder", workingFolder);
		Xbuilder.setAttr(option, "mode", mode);
		return option;
	}

	public Element getFlashUploadOption(Element parent, String mode, RequestContext request) {
		Element option = Xbuilder.addEl(parent, "upload-option", null);
		Xbuilder.setAttr(option, "mode", mode);
		Xbuilder.setAttr(option, "width", request.getNonblankParameter("width"));
		Xbuilder.setAttr(option, "height", request.getNonblankParameter("height"));
		return option;
	}

	public Element getErrorMessage(Element parent, String message) {
		Element errors = Xbuilder.addEl(parent, "error-messages", null);
		Element error = Xbuilder.addEl(errors, "message", null);
		Xbuilder.setAttr(error, "text", message);
		return errors;
	}
}
