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
package com.negeso.framework.menu.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.jaxb.DateAdapter;
import com.negeso.framework.security.SecurityGuard;

/**
 * 
 * @TODO
 * 
 * @author		E.Dzhentemirova
 * @version		$Revision: $
 *
 */

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "menu_item", namespace = Env.NEGESO_NAMESPACE)
public class Menu implements Entity{
		
	private static final long serialVersionUID = -382056497889598992L;

	private static Logger logger = Logger.getLogger(Menu.class);	
	
	private Long id = 0L;	
    private Long parentId = null;	
    private Long pageId;	
    private Long langId;
    private String title = null;
    private String titlePage = null;
    private String link = null;	
    private String linkPage = null;	
    private Long order = null;    
    private Boolean keepMenu = false;	
    private Long siteId;
    private Long containerId;
    private Timestamp publishDate;
    private Timestamp publishDatePage;
    private Timestamp expiredDate;
    private Timestamp expiredDatePage;
    private Boolean visible;
    private Boolean forceVisibility;
	private List<Menu> menuItems = new ArrayList<Menu>();    

	@Override
	@XmlAttribute
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;		
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	@XmlAttribute
	public Long getParentId() {
		return parentId;
	}
	
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
	@XmlAttribute
	public Long getPageId() {
		return pageId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}
	@XmlTransient
	public Long getLangId() {
		return langId;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@XmlTransient
	public String getTitle() {
		return title;
	}
	public void setTitlePage(String titlePage) {
		this.titlePage = titlePage;
	}
	@XmlTransient
	public String getTitlePage() {
		return titlePage;
	}	
	@XmlAttribute(name="title")
	public String getTitleMenu(){
		return this.title != null ? this.title : this.titlePage;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	@XmlTransient
	public String getLink() {
		return link;
	}

	public void setLinkPage(String linkPage) {
		this.linkPage = linkPage;
	}
	@XmlTransient
	public String getLinkPage() {
		return linkPage;
	}
	
	@XmlAttribute(name="href")
	public String getHref(){
		return this.linkPage != null ? this.linkPage : this.link ;	
	}
	
	public void setOrder(Long order) {
		this.order = order;
	}
	@XmlAttribute
	public Long getOrder() {
		return order;
	}
	
	public void setKeepMenu(Boolean keepMenu) {
		this.keepMenu = keepMenu;
	}
	@XmlAttribute
	public Boolean getKeepMenu() {
		return keepMenu;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	@XmlTransient
	public Long getSiteId() {
		return siteId;
	}
	
	public void setMenuItems(List<Menu> menuItems) {
		this.menuItems = menuItems;
	}
	@XmlTransient		
	public List<Menu> getMenuItems() {
		return menuItems;
	}
	
	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}
	
	@XmlAttribute(name="container_id")
	public Long getContainerId() {
		return containerId;
	}

	public void setPublishDate(Timestamp publishDate) {
		this.publishDate = publishDate;
	}
	@XmlAttribute
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getPublishDate() {		
		return this.pageId != null ? this.publishDatePage : this.publishDate;
	}

	public void setPublishDatePage(Timestamp publishDatePage) {
		this.publishDatePage = publishDatePage;
	}
	@XmlTransient
	public Timestamp getPublishDatePage() {
		return publishDatePage;
	}

	public void setExpiredDate(Timestamp expiredDate) {
		this.expiredDate = expiredDate;
	}
	@XmlAttribute
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getExpiredDate() {		
		return this.pageId != null ? this.expiredDatePage : this.expiredDate;
	}

	public void setExpiredDatePage(Timestamp expiredDatePage) {
		this.expiredDatePage = expiredDatePage;
	}
	@XmlTransient
	public Timestamp getExpiredDatePage() {
		return expiredDatePage;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	@XmlAttribute
	public Boolean getVisible() {
		return visible;
	}
	
	public void setForceVisibility(Boolean forceVisibility) {
		this.forceVisibility = forceVisibility;
	}
	@XmlTransient
	public Boolean getForceVisibility() {
		return forceVisibility;
	}

	@XmlAttribute(name="friendly-href")
	public String getFriendlyHref() throws CriticalException, ObjectNotFoundException {	
		if (Env.isUseLangCodeAsUrlStart() && this.pageId != null) {
			String langCode = Language.findById(this.langId).toString();
			return langCode + "/" + getLinkPage().replace("_" + langCode + ".", ".");
		}
		return null;
	}
	
	@XmlAttribute(name="role-id")
	public String getRoleId(){	
		User user = (User) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getAttribute(SessionData.USER_ATTR_NAME);		
		return SecurityGuard.resolveRole(user, this.containerId);		
	}
		
}
