/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.friendly_url;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class FriendlyUrlDescriptor {

	private String htmlMatcher;
	private String xmlMatcher;
	private String urlParamName;
	private String pageClass;
	private String pageComponent;
	private UrlEntityType entityType;
	private boolean isFirstParamPresent = false;
	private boolean isSecondParamPresent = false;
	private boolean isSleshNeeded = false;

	public FriendlyUrlDescriptor(String htmlMatcher, String xmlMatcher,
			String urlParamName, String pageClass, String pageComponent,
			UrlEntityType entityType) {
		super();
		this.htmlMatcher = htmlMatcher;
		this.xmlMatcher = xmlMatcher;
		this.urlParamName = urlParamName;
		this.pageClass = pageClass;
		this.pageComponent = pageComponent;
		this.entityType = entityType;
	}
	
	public FriendlyUrlDescriptor(String htmlMatcher, String xmlMatcher,
			String pageClass, String pageComponent) {
		super();
		this.htmlMatcher = htmlMatcher;
		this.xmlMatcher = xmlMatcher;
		this.pageClass = pageClass;
		this.pageComponent = pageComponent;
	}
	
	public FriendlyUrlDescriptor(String htmlMatcher, String xmlMatcher,
			String urlParamName, String pageClass, String pageComponent,
			UrlEntityType entityType, boolean isFirstParamPresent) {
		this(htmlMatcher, xmlMatcher, urlParamName, pageClass, pageComponent, entityType);
		this.isFirstParamPresent = isFirstParamPresent;
	}
	
	public FriendlyUrlDescriptor(String htmlMatcher, String xmlMatcher,
			String urlParamName, String pageClass, String pageComponent,
			UrlEntityType entityType, boolean isFirstParamPresent, boolean isSecondParamPresent) {
		this(htmlMatcher, xmlMatcher, urlParamName, pageClass, pageComponent, entityType, isFirstParamPresent);
		this.isSecondParamPresent = isSecondParamPresent;
	}
	
	public FriendlyUrlDescriptor(String htmlMatcher, String xmlMatcher,
			String urlParamName, String pageClass, String pageComponent,
			UrlEntityType entityType, boolean isFirstParamPresent, boolean isSecondParamPresent,
			boolean isSleshNeeded) {
		this(htmlMatcher, xmlMatcher, urlParamName, pageClass, pageComponent, entityType, isFirstParamPresent, isSecondParamPresent);
		this.isSleshNeeded = isSleshNeeded;
	}

	public String getHtmlMatcher() {
		return htmlMatcher;
	}

	public void setHtmlMatcher(String htmlMatcher) {
		this.htmlMatcher = htmlMatcher;
	}

	public String getXmlMatcher() {
		return xmlMatcher;
	}

	public void setXmlMatcher(String xmlMatcher) {
		this.xmlMatcher = xmlMatcher;
	}

	public String getUrlParamName() {
		return urlParamName;
	}

	public void setUrlParamName(String urlParamName) {
		this.urlParamName = urlParamName;
	}

	public String getPageClass() {
		return pageClass;
	}

	public void setPageClass(String pageClass) {
		this.pageClass = pageClass;
	}

	public String getPageComponent() {
		return pageComponent;
	}

	public void setPageComponent(String pageComponent) {
		this.pageComponent = pageComponent;
	}

	public UrlEntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(UrlEntityType entityType) {
		this.entityType = entityType;
	}

	public boolean isFirstParamPresent() {
		return isFirstParamPresent;
	}

	public void setFirstParamPresent(boolean isFirstParamPresent) {
		this.isFirstParamPresent = isFirstParamPresent;
	}

	public boolean isSecondParamPresent() {
		return isSecondParamPresent;
	}

	public void setSecondParamPresent(boolean isSecondParamPresent) {
		this.isSecondParamPresent = isSecondParamPresent;
	}

	public boolean isSleshNeeded() {
		return isSleshNeeded;
	}

	public void setSleshNeeded(boolean isSleshNeeded) {
		this.isSleshNeeded = isSleshNeeded;
	}

}
