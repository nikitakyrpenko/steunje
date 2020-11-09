/*
 * @(#)ContextRequest.java  Created on 13.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.negeso.framework.Env;
import com.negeso.framework.page.PageBuilder;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class RequestContext {
    private static final Logger logger = Logger.getLogger(RequestContext.class);

    /**
     * Parameter, holding request uri (if applicable);
     * set by a front controller
     */
    public static final String REQUEST_URI = "REQUEST_URI";
    public static final String REQUEST_URL = "REQUEST_URL";
    
    
    /** Locales preferred by the client (2-letter's comma-separated codes */
    public static final String REQUEST_LOCALES = "REQUEST_LOCALES";


	/** Parameters for a command, associated with this request */
	private Map commandParameters;
	
    
	/**
	 * User specific settings (possibly read from HttpSession)
	 */
	private SessionData sessionData;
    
    
    /**
     * Protocol specific objects (output stream, context and the like)
     */
    private Map currentIO;
    
    

    /**
     * 
     * @param commandParameters ordered map of pairs
     *        <code>name -&gt; value</code>, where each value is array of
     *        strings (String[]) or file items (FileItem[])
     * 
     * @param sessionData instance of SessionData, backed up by 
     * @param currentIO
     */
    public RequestContext(
        Map commandParameters,
        SessionData sessionData,
        Map currentIO)
    {
        logger.debug("+");
        this.commandParameters = commandParameters;
        this.sessionData = sessionData;
        this.currentIO = currentIO;
        logger.debug("-");
    }
	
    
	/**
     * Returns an iterator of parameter names 
	 */
	public Iterator getParameterNames() {
        logger.debug("+");
        Iterator commandNames = commandParameters.keySet().iterator();
        logger.debug("-");
		return commandNames;
	}
    
    
    /**
     * Returns the value of parameter as string. If parameter has multiple
     * String values, the first one is returned.
     *  
     * @param name parameter name (i.e., html-tag name)
     * @return String, if the parameter is set and is of type String;
     *         <code>null</code> otherwise
     */
    public String getParameter(String name) {
        logger.debug("+");
        Object value = commandParameters.get(name);
        if(value instanceof String[]){
            logger.debug("-");
            return ((String[]) value)[0];
        }
        logger.debug("- parameter " + name + " not found");
        return null;
    }
    
    /**
     * Returns the value of parameter as string. If parameter is a blank 
     * string - <code>null</code> value is returned
     *  
     * @param name parameter name (i.e., html-tag name)
     * @return String, if the parameter is set and is of type String(not blank string);
     *         <code>null</code> otherwise
     */
    public String getNonblankParameter(String name) {
        logger.debug("+");
        String value = this.getParameter(name);
        if (value != null){
	        if (value.trim().equalsIgnoreCase(StringUtils.EMPTY)){
	            return null;
	        }
        }
        return value;
    }

    
    /**
     * Sets string values of a command parameter. If a parameter with this
     * name exists, it is replaced with the new value. Otherwise, it is added.
     * <br>
     * Normally, parameters must not be added to RequestContext. This method
     * is package private so that only CommandFactory or some other
     * entrusted classes could add parameters to request.
     * 
     * @param name name of the command parameter
     * @param value array of string values of the command parameter
     */
    public void setParameter(String name, String[] value){
        this.commandParameters.put(name, value);
    }
    
    
    /**
     * Sets string values of a command parameter. If a parameter with this
     * name exists, it is replaced with the new value. Otherwise, it is added.
     * <br>
     * Normally, parameters must not be added to RequestContext. This method
     * is package private so that only CommandFactory or some other
     * entrusted classes could add parameters to request.
     * 
     * @param name name of the command parameter
     * @param value value of the command parameter
     */
	public void setParameter(String name, String value){
        this.commandParameters.put(name, new String[] { value } );
    }
    
    
    /**
     * Returns values of a parameter (string array).
     *  
     * @param name parameter name (i.e., html-tag name)
     * @return array of strings, if the parameter is set and values are
     *         of type String; <code>null</code> otherwise
     */
    public String[] getParameters(String name) {
        logger.debug("+");
        Object value = commandParameters.get(name);
        if(value instanceof String[]){
            logger.debug("-");
            return (String[]) value;
        }
        logger.debug("- parameter " + name + " is not of type String[]");
        return null;
    }
    
    
    /**
     * Returns one FileItem. If parameter has multiple values, only
     * the first one is returned.
     * 
     * @param name parameter name (i.e., html-tag name)
     * @return FileItem, if the parameter is set and is of type FileItem;
     *         <code>null</code> otherwise
     */
    public FileItem getFile(String name) {
        logger.debug("+");
        Object value = commandParameters.get(name);
        if(value instanceof FileItem[]){
            logger.debug("-");
            return ((FileItem[]) value)[0];
        }
        logger.debug("- parameter is not a file");
        return null;
    }
    
    
    /**
     * Returns arrays of org.apache.commons.fileupload.FileItem objects.
     *  
     * @param name parameter name (i.e., html-tag name)
     * @return array of file items, if the parameter is set and and values are
     *         of type FileItem; <code>null</code> otherwise
     */
    public FileItem[] getFiles(String name) {
        logger.debug("+");
        Object value = commandParameters.get(name);
        if(value instanceof FileItem[]){
            logger.debug("-");
            return (FileItem[]) value;
        }
        logger.debug("- parameter is not an array of file items");
        return null;
	}
	
    
	/**
	 * @return user specific settings or data, obtained from http session
	 *         or elsewhere.
	 */
	public SessionData getSessionData() {
		return this.sessionData;
	}
    
	/**
	 * Return started <code>SessionData</code> object.
	 * 
	 * @return user specific settings or data, obtained from http session
	 *         or elsewhere.
	 */
	public SessionData getSession() {
		if ( !this.sessionData.isSessionStarted() ){
			this.sessionData.startSession();
		}
		return this.sessionData;
	}

    
    /**
     * Returns map with protocol-specific attributes to be used in response
     * (encoding, buffering, output stream etc)
     */
    public Map getIOParameters() {
        return this.currentIO;
    }

    /** Rreturns <b>Long</b> or null */
    public Long getLong(String parameter) {
        logger.debug("+");
        String strval = getParameter(parameter);
        if(StringUtils.isEmpty(strval)){
            logger.debug("- parameter is not set: " + parameter);
            return null;
        }
        Long val = null;
        try {
            val = Long.valueOf(strval);
        } catch (Exception e) {
            logger.error("Exception: " + parameter, e);
        }
        logger.debug("-");
        return val;
    }
    
    /** Returns array of <b>Long</b>s; the array can be empty but not null */
    public Long[] getLongs(String parameter) {
        logger.debug("+");
        String[] strvals = getParameters(parameter);
        if(strvals == null){
            logger.debug("- parameter is not set: " + parameter);
            return new Long[0];
        }
        int len = strvals.length;
        Long[] vals = new Long[len];
        for (int i = 0; i < len; i++) {
            try {
                vals[i] = Long.valueOf(strvals[i]);
            } catch (Exception e) {
                logger.error("Exception: " + parameter, e);
                vals[i] = null;
            }
        }
        logger.debug("-");
        return vals;
    }
    
    /** Returns primitive <b>long</b> or 0 */
    public long getLongValue(String parameter) {
        logger.debug("+");
        String strval = getParameter(parameter);
        if(StringUtils.isEmpty(strval)){
            logger.debug("- parameter is not set: " + parameter);
            return 0;
        }
        long val = 0;
        try {
            val = Long.parseLong(strval);
        } catch (Exception e) {
            logger.error("Exception: " + parameter, e);
        }
        logger.debug("-");
        return val;
    }
    
    /** Returns array of <b>long</b>s; the array can be empty but not null */
    public long[] getLongValues(String parameter) {
        logger.debug("+");
        String[] strvals = getParameters(parameter);
        if(strvals == null){
            logger.debug("- parameter is not set: " + parameter);
            return  new long[0];
        }
        int len = strvals.length;
        long[] vals = new long[len];
        for (int i = 0; i < len; i++) {
            try {
                vals[i] = Long.parseLong(strvals[i]);
            } catch (Exception e) {
                logger.error("Exception: " + parameter, e);
                vals[i] = 0;
            }
        }
        logger.debug("-");
        return vals;
    }
    

    /** Rreturns <b>Integer</b> or null */
    public Integer getInteger(String parameter) {
        logger.debug("+");
        String strval = getParameter(parameter);
        if(StringUtils.isEmpty(strval)){
            logger.debug("- parameter is not set: " + parameter);
            return null;
        }
        Integer val = null;
        try {
            val = Integer.valueOf(strval);
        } catch (Exception e) {
            logger.error("Exception: " + parameter, e);
        }
        logger.debug("-");
        return val;
    }
    
    /** Returns array of <b>Integer</b>s; the array can be empty but not null*/
    public Integer[] getIntegers(String parameter) {
        logger.debug("+");
        String[] strvals = getParameters(parameter);
        if(strvals == null){
            logger.debug("- parameter is not set: " + parameter);
            return new Integer[0];
        }
        int len = strvals.length;
        Integer[] vals = new Integer[len];
        for (int i = 0; i < len; i++) {
            try {
                vals[i] = Integer.valueOf(strvals[i]);
            } catch (Exception e) {
                logger.error("Exception: " + parameter, e);
                vals[i] = null;
            }
        }
        logger.debug("-");
        return vals;
    }
    
    /** Returns primitive <b>int</b> or 0 */
    public int getIntValue(String parameter) {
        logger.debug("+");
        String strval = getParameter(parameter);
        if(StringUtils.isEmpty(strval)){
            logger.debug("- parameter is not set: " + parameter);
            return 0;
        }
        int val = 0;
        try {
            val = Integer.parseInt(strval);
        } catch (Exception e) {
            logger.error("Exception: " + parameter, e);
        }
        logger.debug("-");
        return val;
    }
    
    /** Returns array of <b>int</b>s; the array can be empty but not null */
    public int[] getIntValues(String parameter) {
        logger.debug("+");
        String[] strvals = getParameters(parameter);
        if(strvals == null){
            logger.debug("- parameter is not set: " + parameter);
            return  new int[0];
        }
        int len = strvals.length;
        int[] vals = new int[len];
        for (int i = 0; i < len; i++) {
            try {
                vals[i] = Integer.parseInt(strvals[i]);
            } catch (Exception e) {
                logger.error("Exception: " + parameter, e);
                vals[i] = 0;
            }
        }
        logger.debug("-");
        return vals;
    }
    
    /**
     * Returns <b>parameter</b> value as String. If the null, empty string or absent,
     * returns <b>defaultValue</b>.
     */
    public String getString(String parameter, String defaultValue) {
        logger.debug("+");
        String val = getParameter(parameter);
        if(val == null){
            logger.debug("- parameter is not set: " + parameter);
            return defaultValue;
        }
        logger.debug("-");
        return val;
    }
    
    /**
     * @param parameter
     * @return Timestamp value or null
     */
    public Timestamp getTimestamp(String parameter) {
        logger.debug("+");
        String strval = getParameter(parameter);
        if(StringUtils.isEmpty(strval)){
            logger.debug("- parameter is not set: " + parameter);
            return null;
        }
        Timestamp tstamp = null;
        try {
            tstamp = new Timestamp(Env.parseDate(strval).getTime());
        } catch (ParseException e) {
            logger.error("ParseException", e);
        }
        logger.debug("-");
        return tstamp;
    }
    
    /**
     * @param parameter
     * @return Timestamp value or null
     */
    public Timestamp getRoundTimestamp(String parameter) {
        logger.debug("+");
        String strval = getParameter(parameter);
        if(StringUtils.isEmpty(strval)){
            logger.debug("- parameter is not set: " + parameter);
            return null;
        }
        Timestamp tstamp = null;
        try {
            tstamp = new Timestamp(Env.parseRoundDate(strval).getTime());
        } catch (ParseException e) {
            logger.error("ParseException", e);
        }
        logger.debug("-");
        return tstamp;
    }
    
    /**
     * @param parameter
     * @return Timestamp value or null
     */
    public Timestamp getSimpleRoundTimestamp(String parameter) {
        logger.debug("+");
        String strval = getParameter(parameter);
        if(StringUtils.isEmpty(strval)){
            logger.debug("- parameter is not set: " + parameter);
            return null;
        }
        Timestamp tstamp = null;
        try {
            tstamp = new Timestamp(Env.parseSimpleRoundDate(strval).getTime());
        } catch (ParseException e) {
            logger.error("ParseException", e);
        }
        logger.debug("-");
        return tstamp;
    }

    /**
     * 
     * @return remote ip address
     */
    public String getRemoteAddr() {
        HttpServletRequest httpRequest =
            (HttpServletRequest)getIOParameters()
                .get(WebFrontController.HTTP_SERVLET_REQUEST);
        return httpRequest.getRemoteAddr();
    }
    
    public boolean isEmptyString(String parameter) {
        return getString(parameter, StringUtils.EMPTY).equals(StringUtils.EMPTY);
    }
    
    public boolean hasParameter(String parameter) {
        if (this.isEmptyString(parameter)) {
        	return false;
        }
        return true;
    }

    public Map getParameterMap() {
    	return this.commandParameters;
    }

	public void setSessionData(SessionData sessionData) {
		this.sessionData = sessionData;
	}
	
	public Object getService(String beanName) {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getSessionData().getServletContext());
		return context.getBean(beanName);
	}
	
	public Object getService(String moduleName, String beanName) {
		return DispatchersContainer.getInstance().getBean(moduleName, beanName);
	}

	public int getLangId() {
		return getSessionData().getLanguage().getId().intValue();
	}
	
	public static HttpServletRequest getServletRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			return ((ServletRequestAttributes)requestAttributes).getRequest();
		}
		return null;
	}
	
	public HttpServletRequest getHttpRequest() {
		return ((HttpServletRequest)getIOParameters().get(WebFrontController.HTTP_SERVLET_REQUEST));
	}
	
	public void setCanonicalAttribute(){
		getHttpRequest().setAttribute(PageBuilder.CANONICAL, Boolean.TRUE.toString());
	}
	
}
