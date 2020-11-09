/*
 * @(#)ContextResponse.java  Created on 13.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * Request context is holds settings that an application controller
 * needs to output the response. It can hold an output stream, encoding,
 * and other parameters.<br>
 * Application controller does not have direct means to output the response,
 * therefore all settings that are needed to configure the output,
 * must be providedin the request context. 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class ResponseContext {
	
    private static Logger logger = Logger.getLogger(ResponseContext.class);

	private String resultName;


    private Map<String, Object> resultMap = new HashMap<String, Object>();
    
    
    /**
     * Returns map containing result details. If map has not been set,
     * a new (empty) one is created.
     * 
     * @return results as map; never null
     */
	public Map<String, Object> getResultMap(){
		return this.resultMap;
	}
    
    
    /**
     * Finds a result value by key. It is shortcut
     * <code>for getResultMap().get(key)</code>
     * 
     * @return a value object or null
     */
    public Object get(String key){
        logger.debug("key = " + key);
        return getResultMap().get(key);
    }
    

    /**
     * Returns result name, which was set by the current command
     */
    public String getResultName() {
        return this.resultName;
    }
    
    
    /**
     * Sets result name; this method is normally used by Command
     */
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }


}
