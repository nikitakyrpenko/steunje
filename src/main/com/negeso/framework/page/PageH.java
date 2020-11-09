/*
 * @(#)Page.java       @version 12.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.jaxb.DateAdapter;
import com.negeso.framework.jaxb.TimestampAdapter;
import com.negeso.module.rich_snippet.IRichSnippetContainer;
import com.negeso.module.rich_snippet.bo.RichSnippet;
import com.negeso.module.user.domain.DefaultContainer;


/**
 * Page entity encapsulation. ActiveRecord pattern.
 *
 * @version     $Revision$ $Date: 28.03.2011 14:55:54$
 * @author      Andriy Zagorulko
 */
/**
 * @author andriyz
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "page", namespace = Env.NEGESO_NAMESPACE)
public class PageH implements Entity, Cloneable, IRichSnippetContainer {
   
	public static enum Changefreq {ALWAYS,
									HOURLY,
									DAILY,
									WEEKLY,
									MONTHLY,
									YEARLY,
									NEVER
} 
	
	
	@XmlTransient
	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private Long id = 0L;
	@XmlAttribute
    private Long langId = 0L;
	@XmlAttribute
	private Long siteId = 1L;
	@XmlAttribute
	private String filename = null;
	@XmlAttribute
	private String title = null;
	@XmlAttribute
	private String metaTitle = null;
	@XmlAttribute
	private String metaDescription = null;
	@XmlAttribute
	private String metaKeywords = null;
	@XmlAttribute
	private String propertyValue = null;
	@XmlAttribute
	private String googleScript = null;
	@XmlAttribute
	private String contents = null;
	@XmlAttribute
    private String class_ = "";
	@XmlAttribute
	private String category = "page";
	@XmlAttribute
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	private Timestamp lastModified = new Timestamp(System.currentTimeMillis());
	@XmlAttribute
    private String protected_ = null;
	@XmlAttribute
	@XmlJavaTypeAdapter(DateAdapter.class)
    private Timestamp publishDate = null;
	@XmlAttribute
	@XmlJavaTypeAdapter(DateAdapter.class)
    private Timestamp expiredDate = null;
	@XmlAttribute
	@XmlJavaTypeAdapter(TimestampAdapter.class)
    private Timestamp editDate = null;
	@XmlAttribute
    private String editUser = null;
	@XmlAttribute
    private boolean visible = true;
	@XmlAttribute
    private Long containerId = DefaultContainer.getInstance().getId();
	@XmlAttribute
    private Long attributeSetId = null;
	@XmlAttribute
    private boolean inMenu;
	@XmlAttribute
	private boolean search = true;
	
	private boolean multilanguage = true;
	
	@XmlAttribute
	private boolean sitemap = true;
	@XmlAttribute
	private String sitemapPrior = "0.5";
	@XmlAttribute
	private Changefreq sitemapFreq = Changefreq.DAILY;
	@XmlAttribute
	private String metaAuthor;
	
	private Set<RichSnippet> richSnippets = new HashSet<RichSnippet>();
    
    public void setInMenu(boolean inMenu) {
		this.inMenu = inMenu;
	}

    public PageH(){}
    
    protected PageH(
            Long id,
            Long langId,
            String filename,
            String title,
            String metaTitle,
            String contents,
            String class_,
            String category,
            Timestamp last_modified,
            String protected_,
            Timestamp publish_date,
            Timestamp expired_date,
            Timestamp edit_date,
            String edit_user,
            boolean visible,
            Long containerId,
            Long attributeSetId,
            String meta_description,
            String meta_keywords,
    		String property_value,
    		String google_script,
    		boolean is_search,
    		boolean is_sitemap,
    		String sitemapMapPrior,
    		String sitemapMapFreq
    		)
    {
        
        this.id = id;
        this.langId = langId;
        this.filename = filename;
        this.title = title;
        this.metaTitle = metaTitle;
        this.contents = contents;

        this.class_ = class_;
        this.category = category;
        this.lastModified = last_modified;
        this.protected_ = protected_;
        this.publishDate = publish_date;
        this.expiredDate = expired_date;
        this.editDate = edit_date;
        this.editUser = edit_user;
        this.visible = visible;
        this.containerId = containerId;
        this.attributeSetId = attributeSetId;

        this.metaDescription = meta_description;
        this.metaKeywords = meta_keywords;
        this.propertyValue = property_value;
        this.googleScript = google_script;
        this.search = is_search;

        this.sitemap = is_sitemap;
        this.sitemapPrior = sitemapMapPrior;
        this.setSitemapFreq(sitemapMapFreq);
    }

    public static PageH load( ResultSet rs ) {
        try {
            PageH result = new PageH(
                new Long( rs.getLong( "id" ) ),
                rs.getLong( "lang_id" ) != 0 ? 
                    new Long( rs.getLong( "lang_id" )) : null,
                rs.getString( "filename" ),
                rs.getString( "title" ),
                rs.getString( "meta_title" ),
                rs.getString( "contents" ),
                rs.getString( "class" ),
                rs.getString( "category" ),
                rs.getTimestamp( "last_modified" ),
                rs.getString( "protected" ),
                rs.getTimestamp( "publish_date" ),
                rs.getTimestamp( "expired_date" ),
                rs.getTimestamp( "edit_date" ),
                rs.getString( "edit_user" ),
                rs.getBoolean( "visible" ),
                makeLong(rs.getLong( "container_id" )),
                makeLong(rs.getLong( "attribute_set_id" )),
        		rs.getString( "meta_description" ),
        		rs.getString( "meta_keywords" ),
        		rs.getString( "property_value" ),
        		rs.getString( "google_script" ),
        		rs.getBoolean( "is_search" ),
        		rs.getBoolean( "is_sitemap" ),
        		rs.getString( "sitemap_prior" ),
        		rs.getString( "sitemap_freq" )
        		
            );
            return result;
        } catch ( SQLException ex ) {
            throw new CriticalException( ex );
        }
    }
    
    private static Long makeLong(long value) {
        return value > 0 ? new Long(value) : null;
    }

	public boolean isDateOk() {
        long curTime = System.currentTimeMillis();
        Timestamp publish = getPublishDate();
        Timestamp expire = getExpiredDate();
        boolean valid = true;
        if ( publish != null && curTime < publish.getTime() ) {
            valid = false;
        }
        if ( expire != null && curTime > expire.getTime() ) {
            valid = false;
        }
        return valid;
    }
    

    public void setContainerId(Long containerId) {
    	//Validate.notNull(containerId, "Null container id");
    	if (containerId != null) {
    		this.containerId = containerId;
    	} else {
    		this.containerId = DefaultContainer.getInstance().getId();
    	}
    }


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

	
	public Long getSiteId() {
		return siteId;
	}


	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getMetaTitle() {
		return metaTitle;
	}


	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}


	public String getMetaDescription() {
		return metaDescription;
	}


	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}


	public String getMetaKeywords() {
		return metaKeywords;
	}


	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}


	public String getPropertyValue() {
		return propertyValue;
	}


	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}


	public String getGoogleScript() {
		return googleScript;
	}


	public void setGoogleScript(String googleScript) {
		this.googleScript = googleScript;
	}


	public String getContents() {
		return contents;
	}


	public void setContents(String contents) {
		this.contents = contents;
	}


	public String getClass_() {
		return class_;
	}


	public void setClass_(String class1) {
		class_ = class1;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public Timestamp getLastModified() {
		return lastModified;
	}


	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}


	public String getProtected_() {
		return protected_;
	}


	public void setProtected_(String protected1) {
		protected_ = protected1;
	}


	public Timestamp getPublishDate() {
		return publishDate;
	}


	public void setPublishDate(Timestamp publishDate) {
		this.publishDate = publishDate;
	}


	public Timestamp getExpiredDate() {
		return expiredDate;
	}


	public void setExpiredDate(Timestamp expiredDdate) {
		this.expiredDate = expiredDdate;
	}


	public Timestamp getEditDate() {
		return editDate;
	}


	public void setEditDate(Timestamp editDdate) {
		this.editDate = editDdate;
	}


	public String getEditUser() {
		return editUser;
	}


	public void setEditUser(String editUser) {
		this.editUser = editUser;
	}

	public void updatedByUser(String editUser) {
		this.editUser = editUser;
		this.editDate = new Timestamp(System.currentTimeMillis());
	}
	

	public boolean isVisible() {
		return visible;
	}


	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	public Long getAttributeSetId() {
		return attributeSetId;
	}


	public void setAttributeSetId(Long attributeSetId) {
		this.attributeSetId = attributeSetId;
	}


	public boolean isSearch() {
		return search;
	}


	public void setSearch(boolean search) {
		this.search = search;
	}


	public Long getContainerId() {
		return containerId;
	}


	public boolean isInMenu() {
		return inMenu;
	}

	
	
	public boolean isSitemap() {
		return sitemap;
	}

	public void setSitemap(boolean inSitemap) {
		this.sitemap = inSitemap;
	}

	public String getSitemapPrior() {
		return sitemapPrior;
	}

	public void setSitemapPrior(String sitemapMapPrior) {
		this.sitemapPrior = sitemapMapPrior;
	}

	public String getSitemapFreq() {
		return sitemapFreq.toString();
	}

	public void setSitemapFreq(String sitemapMapFreq) {
		try{
			this.sitemapFreq = Changefreq.valueOf(sitemapMapFreq);
		}catch (IllegalArgumentException  e) {
			this.sitemapFreq = Changefreq.DAILY;
		}	
	}

	public PageH clone(){
		try {
			return (PageH)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isMultilanguage() {
		return multilanguage;
	}

	public void setMultilanguage(boolean multilanguage) {
		this.multilanguage = multilanguage;
	}

	public String getMetaAuthor() {
		return metaAuthor;
	}

	public void setMetaAuthor(String metaAuthor) {
		this.metaAuthor = metaAuthor;
	}

	public Set<RichSnippet> getRichSnippets() {
		return richSnippets;
	}

	public void setRichSnippets(Set<RichSnippet> richSnippets) {
		this.richSnippets = richSnippets;
	}

	
	
}
