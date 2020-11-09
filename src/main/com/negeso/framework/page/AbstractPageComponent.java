/*
 * @(#)$Id: AbstractPageComponent.java,v 1.6, 2007-02-05 11:22:04Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


/**
 *
 * Abstract PageComponent with some helper methods:
 * 	- parameter dispatching (getLongParameter, getStringParameter)
 * 
 * @version		$Revision: 7$
 * @author		Olexiy Strashko
 * 
 */
public abstract class AbstractPageComponent implements PageComponent {
	private static Logger logger = Logger.getLogger( AbstractPageComponent.class );
	
	private Collection<String> errors = null;

	/**
	 * Read 1'st parameter from parameters <code>Map</code> into   
	 * 
	 * @param parameters
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public String getStringParameter(
		Map parameters, String paramName, String defaultValue)
	{	
	    if ( paramName == null ){
	        logger.error("Parameter (parameName or defaultValue) is null, returning defaultValue");
	        return defaultValue;
	    }
	    if (parameters == null) {
	    	return defaultValue;
	    }
	    
	    Object value = parameters.get(paramName); 
		if ( value != null ){
			if (value instanceof Object[]) {
				Object[] values = (Object[]) value;
				if (values.length > 0){
					return (String) values[0]; 
				}
			} else {
				return (String) value;
			}
		}
		return defaultValue;
	}
    
	/**
	 * Get <code>Long</code> parameter from map   
	 * 
	 * @param parameters
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public Long getLongParameter(
		Map parameters, String paramName, Long defaultValue)
	{
		String value = this.getStringParameter(parameters, paramName, null);
		if (value != null) {
			 try {
				 Long longValue = new Long(value);
				 return longValue;
			 } catch (Exception e) {
				 logger.error("-error", e);
			 }
		}
		return defaultValue;
	}
    
    public Long getLongParameter(Map parameters, String paramName) {
    	return this.getLongParameter(parameters, paramName, null);
    }

    public String getStringParameter(Map parameters, String paramName) {
    	return this.getStringParameter(parameters, paramName, null);
    }
    
    public boolean isVisitorMode(Map parameters) {
    	return Boolean.valueOf(
    		this.getStringParameter(parameters, ComponentManager.RUNTIME_PARAM_VISITOR_MODE)
    	).booleanValue();
    }

    /**
	 * Errors collection to support user errors
	 * 
	 * @return  The errors collection
	 */
	public Collection<String> getErrors() {
		if ( this.errors == null ){
			this.errors = new ArrayList<String>();
		}
		return this.errors;
	}

	/**
	 * Tests component for errors
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		if ( this.errors == null ){
			return false;
		}
		if ( this.errors.isEmpty() ){
			return false;
		}
		return true;
	}
	
	private static Object getFirstParameter(Map parameters, String parameterName) {
		Object[] params = (Object[]) parameters.get(parameterName);
		Validate.notNull(params[0].toString(),
				"Can't find in page component params parameter "
						+ parameterName);
		return params[0];
	}

	public static Integer getIntParameter(Map parameters, String parameterName,
			Integer defaultValue) {
		try {
		return Integer.parseInt(getFirstParameter(parameters, parameterName)
				.toString());
		} catch (NullPointerException e) {
			logger.debug("Can't find in page component params parameter "
					+ parameterName);
			return defaultValue;
		}
	}

	public static String getStringPageComponentParam(Map parameters,
			String parameterName, String defaultValue) {
		try {
			return getFirstParameter(parameters, parameterName).toString();
		} catch (NullPointerException e) {
			logger.debug("Can't find in page component params parameter "
					+ parameterName);
			return defaultValue;
		}
	}
}
