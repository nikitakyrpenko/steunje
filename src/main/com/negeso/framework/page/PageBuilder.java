/*
 * @(#)PageBuilder.java  Created on 10.03.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.module.rich_snippet.service.RichSnippetXmlBuilder;
import com.negeso.module.user.domain.Container;
import com.negeso.module.wcmsattributes.WcmsAttributesXmlBuilder;


public class PageBuilder {
	
	/** User who requestedthe page. <b>null</b> means anonimous user (guest).  */
    private User user;
    
    /** Is XML being built for end-user part or admin-part? */
    private boolean isVisitorMode;
    
    public PageBuilder( User user ) { this.user = user; }
    
    public static final String CANONICAL = "canonical";
    
    private static final Logger logger = Logger.getLogger(PageBuilder.class);
    
    public Document buildPage(PageH page, RequestContext request, boolean forVisitors) {
        logger.debug( "+" );
        isVisitorMode = forVisitors;
        SessionData session = request.getSessionData();
        Long cid = page.getContainerId();
        Element elPage = Xbuilder.createTopEl("page");
        addRequestParameters(elPage, request);
        addGoogleParameters(elPage);
        addBehavior(elPage, request);
        addBrowser(elPage, request);
        Document doc = elPage.getOwnerDocument();
        Xbuilder.setAttr(elPage, "id", page.getId());
        Xbuilder.setAttr(elPage, "role-id-max",
                SecurityGuard.resolveRole(user, null));
	    String role = SecurityGuard.resolveRole(user, cid);
	    if (role.equals("guest") && user != null)
	    	role = "visitor";
	    Xbuilder.setAttr(elPage, "role-id", role);
        Xbuilder.setAttr(elPage, "lang", session.getLanguageCode());
        Timestamp modified = page.getLastModified();
        Xbuilder.setAttr(elPage, "last_modified",
            modified == null ? null : Env.formatDate(modified));
        Xbuilder.setAttr(elPage, "class", page.getClass_());        
        Xbuilder.setAttr(elPage, "category", page.getCategory());
        Xbuilder.setAttr(elPage, "admin-path", request.getParameter(RequestContext.REQUEST_URI).contains("/admin"));
        if (page.getProtected_()!=null)
        	Xbuilder.setAttr(elPage, "protected", page.getProtected_());
        
        Xbuilder.addEl(elPage, "filename", page.getFilename());
        Xbuilder.addEl(elPage, "title", page.getTitle());        
        Xbuilder.addEl(elPage, "title_prefix", Env.getProperty("SS.BROWSER_PREFIX")!=null ? Env.getProperty("SS.BROWSER_PREFIX") : StringUtils.EMPTY);
       	Xbuilder.addEl(elPage, "title_suffix", Env.getProperty("SS.BROWSER_SUFFIX")!=null ? Env.getProperty("SS.BROWSER_SUFFIX") : StringUtils.EMPTY);
       	
       
       	
       	Xbuilder.addEl(elPage, "meta_title", page.getMetaTitle()!=null ? page.getMetaTitle() : page.getTitle());
        if (Env.getProperty("SS.FAVICON_PATH")!=null)
        	Xbuilder.addEl(elPage, "favicon", Env.getProperty("SS.FAVICON_PATH"));
        if(Boolean.parseBoolean(Env.getProperty("page.metatags"))) {
        	Xbuilder.setAttr(elPage, "metatags", Boolean.parseBoolean(Env.getProperty("page.metatags")));
	        Element metaEl;
	        
	       String metaDescription = page.getMetaDescription();
	       String metaKeywords = page.getMetaKeywords();
	       if(StringUtils.isBlank(metaDescription) || StringUtils.isBlank(metaKeywords)){
	    	   if(Env.getFrontPageName(session.getLanguageCode()) != null){
		    	   PageH frontPage = PageService.getInstance().findByFileName(Env.getFrontPageName(session.getLanguageCode())); 
		    	   
		    	   if(StringUtils.isBlank(metaDescription)) {
		    		   metaDescription = frontPage != null ? frontPage.getMetaDescription() : StringUtils.EMPTY;
		    	   }
		    	   
		    	   if(StringUtils.isBlank(metaKeywords)) {
		    		   metaKeywords = frontPage != null ? frontPage.getMetaKeywords(): StringUtils.EMPTY;
		    	   }
	    	   }
	    	   
	       }
	        metaEl = Xbuilder.addEl(elPage, "meta", metaDescription);
	        metaEl.setAttribute("name","description");
	        
	        metaEl = Xbuilder.addEl(elPage, "meta", metaKeywords);
	        metaEl.setAttribute("name","keywords");
	        
	        String author = Env.getProperty("SS.AUTHOR")!=null ? Env.getProperty("SS.AUTHOR") : StringUtils.EMPTY;
	        if (StringUtils.isNotBlank(page.getMetaAuthor())) {
	        	author = page.getMetaAuthor();
	        }
	        
	    	Xbuilder.addEl(elPage, "copyright", Env.getProperty("SS.COPYRIGHT")!=null ? Env.getProperty("SS.COPYRIGHT") : StringUtils.EMPTY);
	       	Xbuilder.addEl(elPage, "author", author);
	       	Xbuilder.addEl(elPage, "generator", Env.getProperty("SS.GENERATOR")!=null ? Env.getProperty("SS.GENERATOR") : StringUtils.EMPTY);
	       	
	       	if(Boolean.parseBoolean(Env.getProperty("SS.NOODP"))){
	       		Xbuilder.addEl(elPage, "robots",  "NOODP");
	       	}
	       	
	       		buildPathToCanonicalPage(elPage, request, page);
	       	
	        
        }
        if(Boolean.parseBoolean(Env.getProperty("page.googleScript"))) {
        	Xbuilder.setAttr(elPage, "google_script_enabled", Boolean.parseBoolean(Env.getProperty("page.googleScript")));
        	Element metaEl;
        	metaEl = Xbuilder.addEl(elPage, "meta", page.getGoogleScript());
        	metaEl.setAttribute("name","google_script");
        }
        if("true".equalsIgnoreCase(Env.getProperty("ss.isParticipleOfRankBooster"))){
        	Xbuilder.setAttr(elPage, "isParticipleOfRankBooster", "true");
        }
        
        
        elPage.appendChild(Language.getDomItems(doc, request.getSession().getLanguage().getId()));
        

		Xbuilder.setAttr(elPage, "isMentrix", Env.isMultisite());
        Element siteConfElement = Env.getSite().getConfElement();
        if (siteConfElement != null){
            siteConfElement = (Element) doc.importNode(siteConfElement, true);
            elPage.appendChild(siteConfElement);
        }
        if (page.getAttributeSetId() != null && page.getAttributeSetId() > 0)
        	Xbuilder.setAttr(elPage, "wcms_attributes", page.getAttributeSetId());
        appendAttibuteSet(elPage, page);
        if(!forVisitors) {
            Timestamp publish = page.getPublishDate();
            Xbuilder.setAttrForce( elPage, "publish_date",
                    publish == null ? null : Env.formatDate(publish));
            Timestamp expire = page.getExpiredDate();
            Xbuilder.setAttrForce( elPage, "expired_date",
                    expire == null ? null : Env.formatDate(expire));
            Timestamp editDate = page.getEditDate();
            Xbuilder.setAttrForce( elPage, "edit_date",
            		editDate == null ? null : Env.friendlyFormatDate(editDate));
            
            if (page.getEditUser() != null )
            	Xbuilder.setAttr(elPage, "edit_user", page.getEditUser());
            
            if (page.getClass_() != null )
            	Xbuilder.setAttr(elPage, "page_class",page.getClass_() );
            
            Xbuilder.setAttr(elPage, "force_visibility", StringUtils.EMPTY + page.isVisible());            
            //addIsSpecial(elPage, request, page);
            Xbuilder.setAttr(elPage, "isInMenu", StringUtils.EMPTY + page.isInMenu());
			Xbuilder.setAttrForce(elPage, "isSearch", StringUtils.EMPTY + page.isSearch());
			if (Env.isMultisite()){
				setCustomerLoginPassword(elPage);
			}
			Xbuilder.setAttrForce(elPage, "container_id", cid);
            String cname = StringUtils.EMPTY;
            if(cid != null) {
                try {
                    Container container = new Container();        
                    container.load(cid);
                    cname = container.getName();
                } catch (Exception e) {
                    logger.error("Requested cid: " + cid, e);
                }
            }
            Xbuilder.setAttrForce(elPage, "container_title", cname);
        }
        if (!forVisitors){
            Container.appendContainers(elPage);
        }
        RichSnippetXmlBuilder.buildXml(elPage, page);
        ComponentManager.get().addPageComponents(page, request, elPage, isVisitorMode);
        logger.debug( "-" );
        return doc;
    }
    
    
	private void buildPathToCanonicalPage(Element elPage, RequestContext request, PageH page) {
		String hostName = buildSimpleHttpUrl(Env.getServerName());
		SiteUrl siteUrlForLang = SiteUrlCache.getLangIdToSiteUrlMap().get(request.getSession().getLanguage().getId());
		if (siteUrlForLang != null &&  !Env.getServerName().equals(siteUrlForLang.getUrl())) {
			hostName = buildSimpleHttpUrl(siteUrlForLang.getUrl());
			request.setCanonicalAttribute();
		}
		if (Boolean.parseBoolean((String)request.getHttpRequest().getAttribute(CANONICAL))) {
			if (!"frontpage".equals(page.getCategory())) {
				hostName = hostName + page.getFilename();
			}
			elPage.setAttribute(CANONICAL, hostName);
		}
	}
	
	private static final String SIMPLE_HTTP_URL = "http://%s/"; 
	public static String buildSimpleHttpUrl(String serverName) {
		return String.format(SIMPLE_HTTP_URL, serverName);
	}


	/** Adds attribute set to XML of the page if it has linked attributes. */
    private void appendAttibuteSet(Element elPage, PageH page) {
        logger.debug("+");
        if (page.getAttributeSetId() == null) {
            logger.debug("- no attribute set");
            return;
        }
        Connection con = null;
        try{
            con = DBHelper.getConnection();
            
            Element attrElement = WcmsAttributesXmlBuilder.getAttributes(
            	elPage.getOwnerDocument(), 
                con,
                page.getAttributeSetId(),
                true
            );
            elPage.appendChild(attrElement);
             
            logger.debug("-");
        } catch(Exception e){
            logger.error(StringUtils.EMPTY, e);
        } finally {
            DBHelper.close(con);
        }
    }
    
    /**
     * Adds request parameters to the root element, e.g.:
     * [negeso:page [negeso:request [negeso:parameter @name [value], ...] ] ]
     */
    private void addRequestParameters(Element elPage, RequestContext request) {
    	Element elRequest = Xbuilder.addEl(elPage, "request", null);
    	for (Object name : request.getParameterMap().keySet()) {
    		Element elParameter = Xbuilder.addEl(elRequest, "parameter", null);
    		Xbuilder.setAttr(elParameter, "name", name);
    		
    		if (request.getParameters((String) name) != null) {
	    		for (String value : request.getParameters((String) name)){
	    			Xbuilder.addEl(elParameter, "value", value);
	    		}
    		}
		}
    }
    
    private void addGoogleParameters(Element elPage) {
    	Element elRequest = Xbuilder.addEl(elPage, "google", null);
    	SiteUrl siteUrl = SiteUrlCache.getSiteUrlMap().get(Env.getServerName());
    	if (siteUrl == null) {
    		siteUrl = new SiteUrl();
    	}
    	boolean showSiteUrlGoogleCodes = StringUtils.isNotBlank(siteUrl.getAnalytic()) 
									  || StringUtils.isNotBlank(siteUrl.getVerification()) 
									  || StringUtils.isNotBlank(siteUrl.getBing());
    	buildCode(siteUrl.getAnalytic(), "page.googleAnalyticScript", showSiteUrlGoogleCodes, elRequest, "analytic_script");
    	buildCode(siteUrl.getVerification(), "page.googleVerificationCode", showSiteUrlGoogleCodes, elRequest, "verification_code");
    	buildCode(siteUrl.getBing(), "page.googleABingCode", showSiteUrlGoogleCodes, elRequest, "bing_code");
		buildCode(siteUrl.getBing(), "page.googleTagManager", showSiteUrlGoogleCodes, elRequest, "tag_manager_code");
    }
    
    private void buildCode(String siteUrlCodeValue, String defaultCodeName, boolean showSiteUrlSpecificCodes, Element elRequest, String xmlAttrName) {
    	String codeValue = showSiteUrlSpecificCodes ? siteUrlCodeValue : Env.getProperty(defaultCodeName);
    	if (codeValue != null) {
    		Element elParameter = Xbuilder.addEl(elRequest, "parameter", null);
   			Xbuilder.setAttr(elParameter, "name", xmlAttrName);   		
   			Xbuilder.addEl(elParameter, "value", codeValue);
    	}
    }
    
    /**
     * Saves browser type in attribute "browser" and browser version 
     * in attribute "browserVersion" of the root element
     * ("Firefox"/"Opera"/etc). 
     */
    private void addBrowser(Element elPage, RequestContext request) {
    	logger.debug("+ -");
    	HttpServletRequest httpreq =
    		(HttpServletRequest) request.getIOParameters()
    			.get(WebFrontController.HTTP_SERVLET_REQUEST);
    	String browserInfo = httpreq.getHeader("User-Agent");
    	String browser = browserInfo;
    	
    	if (browserInfo != null) {
    		if (browserInfo.indexOf("Firefox") != -1) browser = "Firefox";
    		if (browserInfo.indexOf("Opera") != -1) browser = "Opera";
    		if (browserInfo.indexOf("Safari") != -1) browser = "Safari";
    		if (browserInfo.indexOf("Konqueror") != -1) browser = "Konqueror";
    		if (browserInfo.indexOf("MSIE") != -1) browser = "MSIE";
    		
			StringTokenizer tokenizer = new StringTokenizer(browserInfo, "/ ;()");
			while(tokenizer.hasMoreTokens()) {
				if (browser.equals(tokenizer.nextToken()) && tokenizer.hasMoreTokens()) {
					Xbuilder.setAttr(elPage, "browserVersion", tokenizer.nextToken());    		
				}
			}
    	}
    	
    	Xbuilder.setAttr(elPage, "browser", browser);
    	
    }
    
    /**
     * This facility method is for the client team. It adds session-scoped
     * attribute "long-time-behavior" to the root element. To update the
     * attribute in session, use request parameter "long-time-behavior".
     */
    private void addBehavior(Element elPage, RequestContext request) {
    	SessionData session = request.getSession();
		if (request.getParameter("long-time-behavior") != null) {
    		String s = request.getParameter("long-time-behavior");
			session.setAttribute("long-time-behavior", StringUtils.EMPTY.equals(s) ? null : s);
    	}
    	Xbuilder.setAttr(elPage, "long-time-behavior",
    			session.getAttribute("long-time-behavior"));
    }
    
   /* private void addIsSpecial(Element elPage, RequestContext request, Page page) {
    	boolean isSpecialFlag = false;
    	long langId = request.getSession().getLanguage().getId();
    	String filename = page.getFilename();
    	if(page.isSpecial(langId, filename)>0) {
    		isSpecialFlag = true;
    	}
    	Xbuilder.setAttr(elPage, "isSpecial", isSpecialFlag);
    }*/
	
    /** Sets login and password attributes for Mentrix link */
	private void setCustomerLoginPassword(Element parent) {
		Connection conn = null;
		PreparedStatement stat = null;
		try{
			conn = DBHelper.getConnection();
			stat = conn.prepareStatement(
					" SELECT login, md5(password) AS pwd FROM user_list WHERE site_id=? ");
			stat.setLong(1, Env.getSiteId().longValue());
			ResultSet res = stat.executeQuery();
			if(res.next()){
				Xbuilder.setAttr(parent, "adminLogin", res.getString("login"));
				Xbuilder.setAttr(parent, "adminPassword", res.getString("pwd"));
			} else{
				logger.error("No CUSTOMER with siteId: " + Env.getSiteId());
			}
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			logger.error("Can't set Customer Login & Password");
		} finally{
			DBHelper.close(stat);
			DBHelper.close(conn);
		}
	}
	
}