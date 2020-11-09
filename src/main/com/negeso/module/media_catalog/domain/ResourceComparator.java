/*
 * @(#)$Id: ResourceComparator.java,v 1.1, 2004-10-01 06:51:29Z, Olexiy Strashko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.domain;

import java.util.Comparator;

import com.negeso.module.media_catalog.Repository;

/**
 * Resource comparator. Supported sort modes:
 * 	- by name (sname)
 * 	- by size (ssize)
 *  - by type (stype)  
 *
 * @version 	$Revision: 2$
 * @author 		Olexiy.Strashko
 */
public class ResourceComparator implements Comparator {

    private final int SORT_BY_NAME = 1;
    private final int SORT_BY_TYPE = 2;
    private final int SORT_BY_SIZE = 3;
    
    int sortType = SORT_BY_NAME;
	
	public ResourceComparator(){
		super();
	}

	public ResourceComparator(String type){
		super();
		if ( Repository.SORT_MODE_TYPE.equalsIgnoreCase(type) ){
		    sortType = SORT_BY_TYPE;
		}
		else if ( Repository.SORT_MODE_SIZE.equalsIgnoreCase(type) ){
		    sortType = SORT_BY_SIZE;
		}
		else{
		    sortType = SORT_BY_NAME;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object left, Object right) {
	    switch ( sortType ){
	    	case SORT_BY_TYPE: 
	    	    return this.compareType(left, right);
	    	case SORT_BY_SIZE:
	    	    return this.compareSize(left, right);
	    	default:
	    	    return this.compareName(left, right);
	    }	    
	}

	/**
	 * Name comparator. Case are ignored
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public int compareName(Object left, Object right) {
		Resource leftRes = (Resource) left;
		Resource rightRes = (Resource) right;
		return leftRes.getNameNoExt().compareToIgnoreCase(
			rightRes.getNameNoExt()
		);
	}

	
	/**
	 * Type comparator
	 */
	public int compareType(Object left, Object right) {
		Resource leftRes = (Resource) left;
		Resource rightRes = (Resource) right;
		return leftRes.getType().compareToIgnoreCase(rightRes.getType());
	}

	/**
	 * Size comparator
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public int compareSize(Object left, Object right) {
		Resource leftRes = (Resource) left;
		Resource rightRes = (Resource) right;
		
		if (leftRes.getSize() > rightRes.getSize()){
			return -1;
		}
		if (leftRes.getSize() < rightRes.getSize()){
			return 1;
		}
		return 0;
	}
}
