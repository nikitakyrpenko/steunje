/*
* @(#)$$Id: Link.java,v 1.2, 2006-06-21 14:38:13Z, Svetlana Bondar$$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.framework.navigation;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * The <code>Link</code> class represents a URL with its title and <code>doI18n</code> flag.
 * This flag is true if title will internationalized (replaced with constant in properties file),
 * and false if title will shown in page as is.
 *
 * @author  sbondar
 * @version $Revision: 3$ 
 */

public class Link implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	private String title;
    private String url;
    //true - title will internationalized (replaced with properties constant)
    //false - title will shown in page as is
    private boolean doI18n = true;
    private int level = 1;

    /**
     * Constructs and initializes a link on the specified url with title and i18n flag. 
     * 
     * @param title  url title   
     * @param url    link url
     * @param doI18n i18n flag  
     * @param level	 level in history stack
     */    
    public Link(String title, String url, Boolean doI18n, int level) {
    	this.title = title;
    	this.url   = url;	
    	this.doI18n = doI18n.booleanValue();		
    	this.level = level;		
    }

    /**
     * Constructs and initializes a link on the specified url with title 
     * and true i18n flag. 
     * 
     * @param title  url title   
     * @param url    link url  
     * @param level  level in history stack
     */    
    public Link(String title, String url, int level) {
		this.title = title;
		this.url   = url;	
		this.doI18n = true;
		this.level = level;		
	}
	
    /**
     * Constructs and initializes a link on the specified url with title and i18n flag. 
     * 
     * @param title  url title   
     * @param url    link url
     * @param doI18n i18n flag  
     */    
    public Link(String title, String url, Boolean doI18n) {
    	this.title = title;
    	this.url   = url;	
    	this.doI18n = doI18n.booleanValue();
    	this.level = 1;		
    }

    /**
     * Constructs and initializes a link on the specified url with title 
     * and true i18n flag. 
     * 
     * @param title  url title   
     * @param url    link url  
     */    
    public Link(String title, String url) {
		this.title = title;
		this.url   = url;	
		this.doI18n = true;
		this.level = 1;		
	}
    
	/**
	 * Gets the url title 
	 * 
	 * @return url title 
	 */    
	public String getTitle() {
		return title;
	}
    
	/**
	 * Sets the url title 
	 *    
     * @param title  url title 
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the link url
	 * 
	 * @return link url 
	 */    
	public String getUrl() {
		return url;
	}
	
	/**
	 * Sets the link url
	 *    
     * @param url link url 
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the the i18n flag.
	 * This flag is true if title will internationalized (replaced with constant in properties file),
	 * and false if title will shown in page as is.
	 * 
	 * @return i18n flag
	 */    
	public boolean isDoI18n() {
		return doI18n;
	}

	/**
	 * Sets the i18n flag. 
	 * This flag is true if title will internationalized (replaced with constant in properties file),
	 * and false if title will shown in page as is.
 	 *    
     * @param doI18n i18n flag 
	 */
	public void setDoI18n(boolean doI18n) {
		this.doI18n = doI18n;
	}

	public String toString () {
	    return ReflectionToStringBuilder.reflectionToString(this);
	}

	public int getLevel() {
		return level;
	}

	/**
	 * Sets the link level in history stack.
	 * While link will be pushed, other link with the same level will be removed. 
 	 *    
 	 * @param level link level in history stack  
	 */
	public void setLevel(int level) {
		this.level = level;
	}

}
