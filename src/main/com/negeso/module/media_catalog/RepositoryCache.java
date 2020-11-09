/*
 * @(#)$Id: RepositoryCache.java,v 1.17, 2005-10-18 09:37:52Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


import com.negeso.framework.ShutdownNotifier;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.event.AppDestroyListener;
import com.negeso.framework.image.ImageInfo;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.FolderDomain;
import com.negeso.module.media_catalog.domain.ImageInformation;
import com.negeso.module.media_catalog.domain.Resource;

/**
 * File repository cache
 *
 * @version 		$Revision: 18$
 * @author 			Olexiy.Strashko
 */
public class RepositoryCache implements AppDestroyListener {
	private static Logger logger = Logger.getLogger( RepositoryCache.class );

	private Map<String, ImageInformation> imageInfoCache = null;
	private Map<String, FolderDomain> folderDomainCache = null;

	private static final int IMAGE_INFO_CACHE_INIT_SISE = 300;
	private static final int FOLDER_DOMAIN_INIT_SISE = 20;
	
	private static RepositoryCache instance = null;

	private RepositoryCache(){
		super();
	}
	
	/**
	 *	Singleton getter 
	 * @return
	 */
	static public RepositoryCache get() {
		if (instance == null) {
			instance = new RepositoryCache();
			ShutdownNotifier.registerAppDestroyListener(instance);
		}
		return instance;
	}
	
	/**
	 * Prepare file name to <code>valid</code> repository name.
	 * 
	 * @param fileName
	 * @return
	 * @throws RepositoryException
	 */
	synchronized public ImageInformation getImageInfo(String fileName) 
		throws RepositoryException
	{
	    if (fileName == null){
			throw new RepositoryException("File name parameter is null");		
	    }
	    ImageInformation imageInformation = this.getImageInfoCache().get(fileName);
	    if (imageInformation == null){
			Resource res = Repository.get().getResource(fileName);
			FileInputStream fis = null;
			try{
			    ImageInfo imageInfo = new ImageInfo();
			    fis = new FileInputStream(res.getFile());
			    imageInfo.setInput(fis);
				if ( !imageInfo.check() ){
					throw new RepositoryException("Image file <" + 
						res.getName() + "> is not valid image"
					);		
				}
				imageInformation = new ImageInformation(imageInfo.getWidth(), imageInfo.getHeight());
			}
			catch(IOException e){
			    logger.error("Unable to read image file <" + res.getName() + ">");
				throw new RepositoryException("Unable to read image file <"	+ 
					res.getName() + ">"
				);		
			}
			finally{
			    // close file
			    if (fis != null){
			        try{
			            fis.close();
			        }
			        catch(IOException e){
			            logger.error("-error :" + e.getMessage());
			        }
			    }
			}
			this.getImageInfoCache().put(fileName, imageInformation);
	    }
	    else{
	    	if (logger.isDebugEnabled()){
	    		logger.debug("Getting image from cache: " + fileName);
	    	}
	    }
	    return imageInformation;
	}
	
	
	
	/**
	 * @return Returns the imageInfoCache.
	 */
	private Map<String, ImageInformation> getImageInfoCache() {
		if ( this.imageInfoCache == null ){
			this.imageInfoCache = new HashMap<String, ImageInformation>(IMAGE_INFO_CACHE_INIT_SISE);	
		}
		return imageInfoCache;
	}
	
	/* (non-Javadoc)
	 * @see com.negeso.framework.event.AppDestroyListener#destroy(java.util.EventObject)
	 */
	public void destroy(EventObject event) {
		logger.info("Clearing Repository cache...");
		this.clearCache();
		this.imageInfoCache = null;
	}


	/**
	 * Clear cache
	 */
	synchronized private void clearCache() {
		if (this.imageInfoCache != null) {
			this.imageInfoCache.clear();
		}
		this.imageInfoCache = null;
	}

	/**
	 * Get FolderDomain from cache
	 * 
	 * @param catalogPath
	 * @return
	 * @throws CriticalException
	 */
	synchronized public FolderDomain getFolderDomain(String catalogPath) 
		throws CriticalException 
	{
	    if ( !this.checkCatalogPath(catalogPath) ){
	        return null;
	    }
		FolderDomain domain = this.getFolderDomainCache().get(catalogPath);
		if ( domain == null ){
			Connection con = null;
			try{
				logger.debug("+ -");
                con = DBHelper.getConnection();
				domain = this.getFolderDomain(con, catalogPath);
			}
			catch(SQLException e){
				logger.error("-error", e);
				throw new CriticalException(e);
			} 
			finally{
				DBHelper.close(con); 
			}
			this.getFolderDomainCache().put(catalogPath, domain);
		}
		else{
		    //if ( logger.isInfoEnabled() ){
		    //    logger.info("Getting FolderDomain forom cache: " + catalogPath);
		    //}
		}
		return domain;
	}
	
	/**
	 * Validate CatalogPath
	 * 
	 * @param catalogPath
	 * @return
	 */
	private boolean checkCatalogPath(String catalogPath){
		if ( catalogPath ==null ){
	        logger.error("-error catalogPath is null");
	        return false;
	    }
	    File file = Repository.get().getFileByCatalogPath(catalogPath);
		if (file == null){
	        logger.error("-error file is null :" + catalogPath);
	        return false;
	    }
	    if ( !file.exists() ){
	        logger.error("-error file not exist:" + catalogPath);
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Get folder domain. Create if not exists. 
	 * 
	 * @param con
	 * @param catalogPath
	 * @return
	 * @throws CriticalException
	 */
	private FolderDomain getFolderDomain(Connection con, String catalogPath) 
		throws CriticalException
	{
		FolderDomain domain = FolderDomain.findByPath(con, catalogPath);
		if ( domain == null ){
			domain = new FolderDomain();
			domain.setPath(catalogPath);
			domain.setContainerId(this.geParentContainerId(con, catalogPath));
		    if ( logger.isInfoEnabled() ){
		        logger.info(
	                "Inserting new FolderDomain to DB: " + 
	                catalogPath + 
	                " containerId:" + domain.getContainerId()
		        );
		    }
			domain.insert(con);
		    // Load domain again to setup container name (!bad perfomance!)
			domain = FolderDomain.findByPath(con, catalogPath);
		}
		else{
		    if ( logger.isDebugEnabled() ){
		        logger.debug("Loading FolderDomain to Cache: " + catalogPath);
		    }
		}
	    return domain; 
	}
	
	/**
	 * 
	 * @param con
	 * @param catalogPath
	 * @return
	 * @throws CriticalException
	 */
	private Long geParentContainerId(Connection con, String catalogPath) 
		throws CriticalException
	{
	    Folder folder = Repository.get().getFolder(catalogPath);

	    if ( catalogPath.equalsIgnoreCase(Repository.get().getRootFolder().getCatalogPath())){
	        return Repository.DEFAULT_CONTAINER;
	    }
	    
	    FolderDomain parentDomain = FolderDomain.findByPath(
	            con, folder.getParentFolder().getCatalogPath()
	    );
	    
	    if ( parentDomain != null ){
	        return parentDomain.getContainerId();  
	    }
	    
	    return Repository.DEFAULT_CONTAINER;  
	}

	/**
	 * 
	 *
	 */
	public void clearFolderDomainCache(){
	    this.folderDomainCache = null;
	}
	
	/**
	 * 
	 *
	 */
	public void clearImageInfoCache(){
	    this.getImageInfoCache().clear();
	}

	/**
	 * @return Returns the folderDomainCache.
	 */
	private Map<String, FolderDomain> getFolderDomainCache() {
		if ( this.folderDomainCache == null ){
			this.folderDomainCache = new HashMap<String, FolderDomain>(FOLDER_DOMAIN_INIT_SISE);	
		}
		return folderDomainCache;
	}
}
