/*
 * @(#)$Id: PropertiesHelper.java,v 1.3, 2007-02-02 13:22:30Z, Alexander Serbin$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.util;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * Properties helper. Provide type dependend methods for property retrieving, 
 * like: getInt, getBoolean, ...  
 * 
 * @version		$Revision: 4$
 * @author		Olexiy Strashko
 * 
 */
public class PropertiesHelper {

    private static final Logger logger = Logger.getLogger(PropertiesHelper.class);

    private Properties properties = null;

    /**
     * Retrieves property value by name. If property by this name
     * not exists - null is returned.
     *
     * @param name         The parameter name.
     * @return                  The property value (null if not found).
     *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      property not exists.
     */
    public String getString(String name){
        return getString(name, null);
    }
    
    
    /**
     * Retrieves property value by name. If property not exists, or
     * properties source is not set - default value is returned.
     *
     * @param name         The parameter name.
     * @param defaultValue      The default value to get.
     * @return                  The property value.
     */
    public String getString(String name, String defaultValue) {
        logger.debug("+");
        if( this.properties == null ){
            logger.warn("- (properties has not been set)");
            return defaultValue;
        }
        String value = properties.getProperty( name );
        if ( value == null ){
            logger.warn("- parameter " + name + " not found");
            return defaultValue;
        }
        logger.debug("-");
        return value;
    }
    
    
    /**
     * Retrieves <code>int</code> property value by name.
     * If property does not exist or properties source is not set,
     * default value is returned.
     *
     * @param paramName         The parameter name.
     * @param defaultValue      The default value to get.
     * @return                  The property value.
     */
    public Integer getInt(String paramName, Integer defaultValue) {
        logger.debug("+");
        if(properties == null){
            logger.warn("- (properties has not been set)");
            return defaultValue;
        }
        logger.debug("- (searching a property)");
        String value = StringUtils.trim(properties.getProperty(paramName));
        if ( value == null ){
            logger.warn("- parameter " + paramName + " not found");
            return defaultValue;
        }
        try{
            int intval = Integer.parseInt( value );
            logger.debug("-");
            return intval;
        } catch(NumberFormatException e){
            logger.error("- invalid number format", e);
            return defaultValue;
        }
    }

    public Properties getProperties() { return properties; }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean hasProperty(String name) {
        return this.getProperties().containsKey(name);
    }
    
}
