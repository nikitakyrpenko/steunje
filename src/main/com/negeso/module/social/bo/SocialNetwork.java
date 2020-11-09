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
package com.negeso.module.social.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "socialNetwork", namespace = Env.NEGESO_NAMESPACE)
public class SocialNetwork implements Entity {


	private static final long serialVersionUID = -2054618066783224705L;
	
	@XmlAttribute
	private Long id = -1L;
	@XmlAttribute
	private String title = null;
	private Long siteId = Env.getSiteId();
	
	private Map<String, SocialNetworkParam> params = new HashMap<String, SocialNetworkParam>();
	
	public void setCurrentLangId(Long langId) {
		for (Entry<String, SocialNetworkParam> entry : getParams().entrySet()) {
			entry.getValue().setCurrentLangId(langId);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, SocialNetworkParam> getParams() {
		return params;
	}

	public void setParams(Map<String, SocialNetworkParam> params) {
		this.params = params;
	}

}
