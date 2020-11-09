/*
 * Created on 25.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.media_catalog.generator;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.module.media_catalog.AccessFileFilter;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.Resource;
import com.negeso.module.media_catalog.domain.ResourceComparator;

/**
 * @author lexa
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResourceXmlBuilder {

	private static Logger logger = Logger.getLogger(RepositoryXmlBuilder.class);
    private static ResourceXmlBuilder instance = null;
    
    
	public static ResourceXmlBuilder get(){
	    if ( ResourceXmlBuilder.instance == null ){
	    	ResourceXmlBuilder.instance = new ResourceXmlBuilder();
	    }
	    return ResourceXmlBuilder.instance;
	}
	
	
    private ResourceXmlBuilder(){
        super();
    }
	
    
    public Element buildFolderContentsElement(
    		Folder folder, Document doc, User user, String sortType, String viewMode) 
    	throws RepositoryException, CriticalException
	{
		if ( !folder.isFolder() ){
			throw new RepositoryException(
				"Requested directory is not a directory <" + folder.getName()+ ">"
			);
		} 
		
		Element folderEl = folder.getElement(doc, user);
		
		
		List<Resource> fileList = new ArrayList<Resource>();
		List<Resource> folderList = new ArrayList<Resource>();
		FileFilter filter = null;

		
		Set extFilter = Repository.get().getExtensionsByResourceType(viewMode);
		
		if (extFilter == null){
			filter = new AccessFileFilter(user);
		}
		else{
			filter = new AccessFileFilter(user, extFilter);
		}
		File[] files = folder.getFile().listFiles(filter);

		Resource curr = null;
		for (int i = 0; i < files.length; i++){
			curr = Repository.get().getResource(files[i]); 
			if (curr.isFolder()){
				folderList.add(curr);
			}
			else{
				fileList.add(curr);
			}
		}
		Comparator comparator = new ResourceComparator();
		Collections.sort(folderList, comparator);
		Collections.sort(fileList, comparator);
		folderList.addAll(fileList);
		
		Resource cur = null;
		for (Iterator iter = folderList.iterator(); iter.hasNext();){
			cur = (Resource) iter.next();
			folderEl.appendChild( cur.getElement(doc, user, viewMode));
		}

		return folderEl;
    }
    
}
