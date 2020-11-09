/*
 * @(#)UpdatePageInfoCommand.java  Created on 21.04.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page.command;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.page.PageConstants;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */
@Deprecated
public class UpdatePageInfoCommand extends AbstractCommand implements PageConstants {
    
    
    /** Title of new menu item (and of its page, if any) */
    public static final String INPUT_ID = "id";
    
    /** Filename of new menu item (and of its page, if any) */
    public static final String INPUT_FILENAME= "filename";
    
    /** Title of new menu item (and of its page, if any) */
    public static final String INPUT_TITLE = "title";
    
    /** Title of new menu item (and of its page, if any) */
    public static final String INPUT_META_TITLE = "meta_title";
    
    /** Title of new menu item (and of its page, if any) */
    public static final String INPUT_META_DESCRIPTION = "meta_description";
    
    /** Title of new menu item (and of its page, if any) */
    public static final String INPUT_META_KEYWORDS = "meta_keywords";
    
    /** Title of new menu item (and of its page, if any) */
    public static final String INPUT_PROPERTY_VALUE = "property_value";
    
    
    /** Title of new menu item (and of its page, if any) */
    public static final String GOOGLE_SCRIPT = "google_script";
    
    /** Page (or popup) publication date */
    public static final String INPUT_PUBLISH = "publish";
    
    
    /** Page (or popup) expiration date */
    public static final String INPUT_EXPIRE = "expire";
    
    
    /** Page container */
    public static final String INPUT_CONTAINER_ID = "container_id";
    
    
    /** Page visibility enforcement */
    public static final String INPUT_VISIBILITY = "force_visibility";
    
    
    /** New publication date of the page */
    private Timestamp publish;
    
    
    /** New expiration date of the page */
    private Timestamp expire;
    
    
    /** Container of the new page */
    private Long containerId;
    
    
    /** Should the page be visible in menu if a user has no rights for it. */
    private String forceVisibility = "false";
    
    private String isSearch = "true";
    
    
    /** New title of the page  */
    private String title;
    
    private String metaTitle;
    
    /** New filename of the page  */
    private String filename;

    /** meta description of the Page, stored encoded */
    private String meta_description = null;
    
    /** meta keywords of the Page, stored encoded */
    private String meta_keywords = null;
    
    /** value for additional property of the Page, stored encoded */
    private String property_value = null;
    
    /** google script of the Page, stored encoded */
    private String google_script = null;
        
    /** Id of the page  */
    private Long id;
    
    
    /** Negative result of the command execution */
    private String error = null;
    
    
    private static Logger logger = Logger.getLogger(UpdatePageInfoCommand.class);
    
    
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_FAILURE);
        Map responseMap = response.getResultMap();
        SessionData session = getRequestContext().getSession();
        error = "Only administrators can execute this command";
        if(!readParameters()){
            logger.warn("- Error while processing parameters");
            responseMap.put(OUTPUT_ERROR, error);
            return response;
        }
        try {
            if(session.getUser() == null){
                throw new Exception("User is not contributor");
            }
            PageH page = PageService.getInstance().findById(id);
            try {
                if(!SecurityGuard.canEdit(session.getUser(), page.getContainerId())){
                    throw new Exception("User is not contributor");
                }
            } catch (Exception e) {
                logger.warn("- User is not contributor", e);
                responseMap.put(OUTPUT_ERROR, "You are not a valid editor");
                return response;
            }
            
            String filenameOld = page.getFilename();
            page.setTitle(title);
            page.setMetaTitle(metaTitle);
            page.setFilename(filename);            
            page.setLastModified(new Timestamp(System.currentTimeMillis()));
            if(Boolean.parseBoolean(Env.getProperty("page.metatags"))) {
	            page.setMetaDescription(meta_description);
	            page.setMetaKeywords(meta_keywords);
	            page.setPropertyValue(property_value);
            }
            if(Boolean.parseBoolean(Env.getProperty("page.googleScript"))) {
            	page.setGoogleScript(google_script);
            }
            page.setPublishDate(publish);
            page.setExpiredDate(expire);
            page.setContainerId(containerId);
            page.setVisible(Boolean.valueOf(forceVisibility).booleanValue());
            page.setSearch(Boolean.valueOf(this.isSearch).booleanValue());
            page.setEditUser(session.getUser().getLogin());
            PageService.getInstance().save(page);
        } catch (Exception e) {
            logger.error("- Error while updating Page");
            responseMap.put(OUTPUT_ERROR, "Cannot save changes");
            return response;
        }
        response.setResultName(RESULT_SUCCESS);
        return response;
    }


    /** Checks validity of parameters in request context. */
    private boolean readParameters() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        id = request.getLong(INPUT_ID);
        if(id == null){
            logger.error("- Invalid page id");
            error = "Invalid page id";
            return false;
        }
        filename = request.getString(INPUT_FILENAME, "untitled");
        title = request.getString(INPUT_TITLE, "[Untitled]");
        metaTitle = request.getString(INPUT_META_TITLE, "[Untitled]");
        if(Boolean.parseBoolean(Env.getProperty("page.metatags"))) {
	        meta_description = refine(request.getParameter(INPUT_META_DESCRIPTION));
	        meta_keywords = refine(request.getParameter(INPUT_META_KEYWORDS));
	        property_value = refine(request.getParameter(INPUT_PROPERTY_VALUE));
        }
        if(Boolean.parseBoolean(Env.getProperty("page.googleScript"))) {
        	google_script = refine(request.getParameter(GOOGLE_SCRIPT));
        }
        publish = request.getTimestamp(INPUT_PUBLISH);
        expire = request.getTimestamp(INPUT_EXPIRE);
        containerId = request.getLong(INPUT_CONTAINER_ID);
        forceVisibility = request.getString(INPUT_VISIBILITY, null);
        isSearch = request.getString(PageConstants.IS_SEARCH_PARAMETER, null);
        if (isSearch != null && isSearch.equals("false")) {
        	logger.debug("IS_SEARCH disabled"+isSearch);
        } else {
        	isSearch = "true";
        	logger.debug("IS_SEARCH enabled"+isSearch);
        }
        logger.debug("-");
        return true;
    }

    public static String refine(String param) {
    	if(!( (param == null) || ("".equals(param)) )) {
    		param = param.replace("'","\'").replace("\n"," ").replace("\r"," ");
    	};
    	return param;
    }

}
