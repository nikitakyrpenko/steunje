/*
 * @(#)AccessFileFilter.java       @version	15.03.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;

import com.negeso.framework.domain.User;
import com.negeso.framework.util.FileUtils;


/**
 * Access file filter. Filters files listed in directory. By default all files
 * are included. 
 *
 * @version 	15.03.2004
 * @author 		Olexiy.Strashko
 */
public class AccessFileFilter extends Object implements FileFilter {

	private User user = null;
	
	private Boolean canViewParent = null; 

    private Set filteredExtsSet = null; 

    /**
	 * 
	 */
	public AccessFileFilter(User user) {
		super();
		this.user = user;
	}

	public AccessFileFilter(User user, Set filteredExtsSet) {
		super();
		this.user = user;
		this.filteredExtsSet = filteredExtsSet;
	}

	private boolean canViewParent(User user, File file){
		if ( canViewParent == null ){
			// check if user can view parent folder
			if ( Repository.get().getFolder(file.getParentFile()).canView(user) ){
				canViewParent = Boolean.TRUE;
			}
			else{
				canViewParent = Boolean.FALSE;
			}
		}
		return canViewParent.booleanValue();
	}
	
	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File file) {
		if ( !this.canViewParent(user, file) ){
			return false;
		}
		
		// if file is folder - check if user can view it
		if (file.isDirectory()){
			if ( Repository.get().getFolder(file).canView(user) ){ 
				return true;
			}
			return false;
		}
		
		// filter is null
		if ( this.filteredExtsSet == null ){
			// no filter, pass all throught
			return true;
		}

		// filter files not allowed by extensions
		if ( this.filteredExtsSet.isEmpty() ){
			// empty filter, pass all throught
			return true;
		}
		// check extension
		if ( this.filteredExtsSet.contains(FileUtils.getExtension(file)) )
		{
			// allow file
			return true;
		}
		// restruct file
		return false;
	}
}
