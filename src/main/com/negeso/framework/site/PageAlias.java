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
package com.negeso.framework.site;

import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.friendly_url.UrlEntityType;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class PageAlias implements Entity {

	private static final long serialVersionUID = -7892772669497932454L;

	private Long id = -1L;
	private Long langId;
	private String fileName;
	private Long pageId;
	private Long entityId;
	private Long entityTypeId;
	private String link;
	private Long siteId;
	private Boolean isInSiteMap = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getEntityTypeId() {
		return entityTypeId;
	}

	public void setEntityTypeId(Long entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public void setEntityType (String entityType) {
		if (entityType != null && entityType.trim().length() != 0) {
			this.entityTypeId = Long.valueOf(UrlEntityType.valueOf(entityType).ordinal());
			
		}
	}
	
	public String getEntityType () {
		if (this.entityTypeId == null) {
			return null;
		} 
		return UrlEntityType.values()[this.entityTypeId.intValue()].toString() ;
	}
	
	public UrlEntityType getEntityTypeEnum () {
		if (this.entityTypeId == null) {
			return null;
		} 
		return UrlEntityType.values()[this.entityTypeId.intValue()];
	}
	
	public String getLangCode() throws CriticalException, ObjectNotFoundException {
		return Language.findById(langId).getCode();
	}

	public Boolean isInSiteMap() {
		return isInSiteMap;
	}
	
	public Boolean getInSiteMap() {
		return isInSiteMap;
	}

	public void setInSiteMap(Boolean isInSiteMap) {
		this.isInSiteMap = isInSiteMap;
	}

}
