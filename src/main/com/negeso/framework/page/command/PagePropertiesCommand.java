/*
 * @(#)GetExtraPagesCommand.java Created on 29.07.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
 
package com.negeso.framework.page.command;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.menu.service.MenuService;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.user.domain.Container;
import com.negeso.module.user.domain.DefaultContainer;
 
/**
 * Builds a list of pages not included into the menu of the site
 * (popups, messages and unlinked pages); works like GetMenuCommand.  
 * 
 * @author        Stanislav 
 * @version       $Revision: 9$
 */
public class PagePropertiesCommand extends AbstractCommand {
    
    private static Logger logger = Logger.getLogger(PagePropertiesCommand.class);
    
    private static final String ACTION_ADD_PAGE = "add";
    private static final String ACTION_CREATE_PAGE = "create_page";
    private static final String ACTION_AJAX_CHECK_FILENAME = "check_filename";
    private static final String ACTION_EDIT_PAGE = "edit";
    private static final String ACTION_SAVE_PAGE = "save";
    private static final String ACTION_DELETE_PAGE = "delete";
    private static final String ACTION_SHOW_PAGES = "show_pages";
    
    private static final String MULTILANGUAGE = "multilanguage";
    private static final String UNDERLINING = "_";
    private static final String HTML = ".html";
    
    public ResponseContext execute() {
        logger.debug("+");
        
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_ACCESS_DENIED);
        SessionData session = request.getSession();
        User user = session.getUser();
        String role = SecurityGuard.resolveRole(user, null);
        if (!SecurityGuard.ADMINISTRATOR.equals(role) &&
            !SecurityGuard.MANAGER.equals(role))
        {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- Access denied");
            return response;
        }
        
        Long pageId = request.getLong("id");
        String action = request.getParameter("act");        
        if(pageId == null && action == null){
        	action = ACTION_SHOW_PAGES;
        }
        
        Document doc = null;
        
        if(ACTION_SHOW_PAGES.equals(action)){
            doc = buildShowPagesDoc(request,role);
        
        }else if(ACTION_ADD_PAGE.equals(action)){
        	doc = buildAddPageDoc(request,role,false);
        
        }else if(ACTION_CREATE_PAGE.equals(action)){
        	PageH page = bindPage(request, new PageH());
        	Long currLangId = getRequestContext().getSession().getLanguage().getId();
        	if(page.isMultilanguage()){
    			for(Language lang : Language.getItems()){
    				doc = createPage(request,role,page,lang.getId(),currLangId);    				
    			}
    		} else{			
    			doc = createPage(request,role,page,currLangId, currLangId);	
    		}        
        
        }else if(ACTION_SAVE_PAGE.equals(action)){
        	doc = savePageProperies(request,pageId,role);
        
        }else if(ACTION_EDIT_PAGE.equals(action)){
        	doc = buildEditPageDoc(request,pageId, role); 
        
        }else if(ACTION_DELETE_PAGE.equals(action)){
        	doc = deletePage(request,pageId, role);
        
        }else if(ACTION_AJAX_CHECK_FILENAME.equals(action)){
        	
        	String pageName =  request.getParameter("filename");
        	if(pageName != null){
        		PageH page = PageService.getInstance().findByFileName(pageName);
        		if(page != null){
        			 response.setResultName(RESULT_FAILURE);
        			 return response;
        		}
        		doc = Xbuilder.createTopEl("fileNameCheckStub").getOwnerDocument();
        	}
        }	
       
       
        response.setResultName(RESULT_SUCCESS);
        response.getResultMap().put(OUTPUT_XML, doc);
        logger.debug("-");
        return response;
    }

	private Document createPage(RequestContext request, String role, PageH oldPage, Long langId, Long currLangId) {
		PageH page = oldPage.clone();		
		
		if(langId != currLangId){
			try {
				Language lang = Language.findById(langId);
				Language currLang = Language.findById(currLangId);
				String filename = page.getFilename();			
				filename = filename.replace(UNDERLINING+ currLang.getCode()+HTML, UNDERLINING+lang.getCode()+HTML);
				page.setFilename(filename);
				page.setLangId(langId);
			} catch (CriticalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		
		page.setLangId(langId);
		User user = request.getSession().getUser();
		page.updatedByUser(user.getLogin());
		page.setMetaTitle(page.getTitle());
		String template = request.getParameter("template");
		
		if(template != null){
			if("popup".equals(template)){
				page.setCategory("popup");
			}
			PageService.getInstance().createPage(page, template);
		}		
		return buildAddPageDoc(request, role, true);
	}
	
	private Document buildAddPageDoc(RequestContext request, String role, boolean closeOnLoad) {
		Element pageRootElement = Xbuilder.createTopEl("new-page"); 
        Container.appendContainers(pageRootElement);
       
        Language lang =  getRequestContext().getSession().getLanguage();
        Element langEl = Xbuilder.addEl(pageRootElement, "language", "");
        Xbuilder.setAttr(langEl, "id",  lang.getId().toString());
        Xbuilder.setAttr(langEl, "code",  lang.getCode());
        
        if(closeOnLoad){
        	Xbuilder.addEl(pageRootElement, "closeOnLoad", "");
        }
        
        builtFrontPage(pageRootElement, getRequestContext().getSession().getLanguage());
        return pageRootElement.getOwnerDocument();
	}


	private Document deletePage(RequestContext request, Long pageId, String role) {
		PageService.getInstance().delete(pageId);
		return buildShowPagesDoc(request,role);
	}

	private Document buildShowPagesDoc(RequestContext request, String role) {
			
			Language lang =  getRequestContext().getSession().getLanguage();
			Element pagesRootElement = Xbuilder.createTopEl("pages");
	        pagesRootElement.setAttribute("role-id", role);
	        pagesRootElement.setAttribute("total-site-languages", String.valueOf(Language.getItems().size()));
	        builtFrontPage(pagesRootElement, lang);
	        
	        Element pagesEl = Xbuilder.addEl(pagesRootElement, "unlinkedPages", "");
	        List<PageH> pages = PageService.getInstance().listUnlinkedPages(lang.getId());
			for(PageH page: pages){
				Xbuilder.addBeanJAXB(pagesEl, page);
			}
		return pagesRootElement.getOwnerDocument();
	}

	private Document savePageProperies(RequestContext request, Long pageId,String role) {
		PageH page = PageService.getInstance().findById(pageId);
		bindPage(request, page);
		User user = request.getSession().getUser();
		page.updatedByUser(user.getLogin());
		PageService.getInstance().save(page);
		MenuService.getSpringInstance().flush();
		return buildEditPageDoc(request,pageId,role);
	}

	private PageH bindPage(RequestContext request, PageH page) {
		DefaultMultipartHttpServletRequest mRequest = new DefaultMultipartHttpServletRequest(
 				(HttpServletRequest) request.getIOParameters().get(	WebFrontController.HTTP_SERVLET_REQUEST),
 				new LinkedMultiValueMap<String, MultipartFile>(), request.getParameterMap(), new HashMap<String, String>());

		NegesoRequestUtils.bind(page,mRequest,Env.SIMPLE_DATE_FORMAT);
		if (request.getParameter("search") == null){
			page.setSearch(false);
		}
		
		if (request.getParameter("sitemap") == null){
			page.setSitemap(false);
		}		
		
		
		if (request.getParameter("visible") == null){
			page.setVisible(false);
		}
		
		if (request.getParameter(MULTILANGUAGE) == null){
			page.setMultilanguage(false);
		}
		return page;
	}

	private Document buildEditPageDoc(RequestContext request, Long pageId, String role) {
			
			String filename = request.getParameter("filename");
			Element pageRootElement = Xbuilder.createTopEl("page-properties"); 
	        Container.appendContainers(pageRootElement);
	        pageRootElement.appendChild(Language.getDomItems(pageRootElement.getOwnerDocument(), getRequestContext().getSession().getLanguage().getId()));
	        
	      
	        
	        for(PageH.Changefreq frq : PageH.Changefreq.values()){
	        	Xbuilder.addEl(pageRootElement, "sitemapFrequency", frq.toString());
	        }
	        
	        for(int i= 0; i <=9; i++){
	        	Xbuilder.addEl(pageRootElement, "sitemapPriority", "0." + i);
	        }
	        
	        PageH page = null;
	        if(pageId != null){
	        	page = PageService.getInstance().findById(pageId);
	        }else if(filename != null){
	        	page = PageService.getInstance().findByFileName(filename);
	        }
	        if(page == null){
	        	return null;
	        }
	        Language lang;
			try {
				lang = Language.findById(page.getLangId());
				 Element langEl = Xbuilder.addEl(pageRootElement, "curlanguage", "");
			     Xbuilder.setAttr(langEl, "id",  lang.getId().toString());
			     Xbuilder.setAttr(langEl, "code",  lang.getCode());
			} catch (Exception e) {
				logger.error(e);
			} 
	       
	        boolean closeOnSave = Boolean.valueOf(request.getParameter("closeOnSave"));
	        if(closeOnSave){
	        	Xbuilder.addEl(pageRootElement, "closeOnSave", "");
	        }
	        Element pageEl =  Xbuilder.addBeanJAXB(pageRootElement, page);
	        pageEl.setAttribute("metatags", Env.getProperty("page.metatags"));
	        pageEl.setAttribute("google_script_enabled", Env.getProperty("page.googleScript"));
	        Long menuId = request.getLong("menuId");
	        if(menuId!=null){
	        	pageEl.setAttribute("menuId",menuId.toString());
	        }
	        
	        pageRootElement.setAttribute("role-id", role);
	        pageRootElement.setAttribute("total-site-languages", String.valueOf(Language.getItems().size()));
	        DefaultContainer.getInstance().addMe(pageRootElement);
		return pageRootElement.getOwnerDocument();
	}
    
	private void builtFrontPage(Element pageRootElement, Language lang) {
		Element fpage = Xbuilder.addEl(pageRootElement, "frontpage", null);
		fpage.setAttribute("metatags", Env.getProperty("page.metatags"));
		fpage.setAttribute("google_script_enabled", Env.getProperty("page.googleScript"));
		if(Boolean.parseBoolean(Env.getProperty("page.metatags"))) {
			PageH frontpage = PageService.getInstance().findByClass("frontpage", lang.getId());
			if(frontpage != null){
				fpage.setAttribute("description", frontpage.getMetaDescription());
				fpage.setAttribute("keywords", frontpage.getMetaKeywords());
			}
		}
	}

	
}