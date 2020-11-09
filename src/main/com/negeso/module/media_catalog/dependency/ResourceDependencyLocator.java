/*
 * @(#)$Id: ResourceDependencyLocator.java,v 1.10, 2007-01-09 18:47:50Z, Anatoliy Pererva$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.dependency; 

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.QNameCache;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.XmlHelper;

/**
 * Resource dependency checker and locator. Traverces CMS database for links to
 * resource. Can:
 * <ul>
 * <ui>test resource for dependecies</ui>
 * <ui>find all dependecies for resource, described by <br/>
 * 'dependencies' xml data type</ui>
 * </ul>			  
 * Resource dependency locator is Singleton.
 *
 * @version 	$Revision: 11$
 * @author 		Olexiy.Strashko
 */
public class ResourceDependencyLocator {
	private static Logger logger = Logger.getLogger(ResourceDependencyLocator.class);

	private static ResourceDependencyLocator instance = null; 

	private List<DependencyLocator> locators = null;

	private List<DependencyLocator> folderLocators = null;
	
	/**
	 * 
	 */
	private ResourceDependencyLocator() {
		super();
	}
	
    
	public static ResourceDependencyLocator get(){
	    if ( instance == null ){
	    	instance = new ResourceDependencyLocator();
	    	instance.init();
	    }
		return instance;
	}
	
	private void init(){
	    this.getLocators().add( new PageDependencyLocator() );
	    this.getLocators().add( new NewsDependencyLocator() );
        this.getLocators().add( new DocumentModuleLocator() );
        this.getLocators().add( new PhotoAlbumLocator() );
        this.getLocators().add( new PictureFrameLocator() );
        
        this.getFolderLocators().add(new DocumentModuleFolderLocator());
	}

	private List<DependencyLocator> getLocators(){
		if ( this.locators == null ) {
			this.locators = new ArrayList<DependencyLocator>();
		}
	    return this.locators; 
	}
	
	public void registerLocator (DependencyLocator locator) {
		synchronized (locators) {
			locators.add(locator);
		}
	}
	
	/**
	 * 
	 * @param catalogPath
	 * @return
	 * @throws CriticalException
	 */
	public Element getResourceDependenciesXml(String catalogPath){
		logger.debug("+");
		
		Element dependencies = DocumentHelper.createElement(
		    new QNameCache().get("dependencies",  XmlHelper.getNegesoDom4jNamespace())
		);

	    Connection con = null;
	    try{
	        con = DBHelper.getConnection();
		    for ( DependencyLocator locator: this.getLocators()){
		        try{
			        locator.buildDom4jXml(con, dependencies, catalogPath);
		        }
		        catch( CriticalException e ){
		            logger.error(
		                "-error in Locator:" + locator.getClass().getName(), e
		            );
		        }
		    }
	    }
	    catch (SQLException e){
	        logger.error("-error", e);
	    }
	    finally{
	        DBHelper.close(con);
	    }
		logger.debug("-");
		return dependencies;
	}
    
    
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws CriticalException
	 */
	public boolean hasDependencies(String fileName){
	    Connection con = null;
	    try{
	        con = DBHelper.getConnection();
		    DependencyLocator locator = null;
		    for ( Iterator i = this.getLocators().iterator(); i.hasNext(); ){
		        locator = (DependencyLocator) i.next();
		        try{
			        if ( locator.hasDependencies(con, fileName) ){
			            return true;
			        }
		        }
		        catch( CriticalException e ){
		            logger.error(
		                "-error in Locator:" + locator.getClass().getName(), e
		            );
		        }
		    }
	    }
	    catch (Exception e){
	        logger.error("-error", e);
	    }
	    finally{
	        DBHelper.close(con);
	    }
	    
	    return false;
	}


	public boolean hasFolderDependencies(String catalogPath) {
	    Connection con = null;
	    try{
	        con = DBHelper.getConnection();
		    for (DependencyLocator locator: this.getFolderLocators()){
		        try{
			        if ( locator.hasDependencies(con, catalogPath) ){
			            return true;
			        }
		        }
		        catch( CriticalException e ){
		            logger.error(
		                "-error in Locator:" + locator.getClass().getName(), e
		            );
		        }
		    }
	    }
	    catch (Exception e){
	        logger.error("-error", e);
	    }
	    finally{
	        DBHelper.close(con);
	    }
	    
		
		return false;
	}


	/**
	 * @return Returns the folderLocators.
	 */
	public List<DependencyLocator> getFolderLocators() {
		if ( this.folderLocators == null ){
			this.folderLocators = new ArrayList<DependencyLocator>();
		}
	    return this.folderLocators; 
	}
}
