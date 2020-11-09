/*
 * @(#)CommandMapping.java  Created on 25.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.negeso.framework.domain.Language;



/**
 * Each CommandMapping object is caching one mapping (<b>commnand</b> or
 * <b>matcher</b>) defined in application.xconf. Such caching can save
 * perfomance (especially when 1 request is matched against many matchers).
 * In the other case, we would have to run many XPath queries against DOM tree
 * of application.xconf with each request.
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class CommandMapping {

    private static final Logger logger = Logger.getLogger(CommandMapping.class);

    private String commandName;

    /** The pattern is null for explicit commands and not null for matchers */
    private Pattern pattern;

    /** [result name] => [view name] */
    private Map<String, String> binds = new HashMap<String, String>(3);

    /** Command's parameters: [parameter name] => [parameter value] */
    private final Map<String, String> commandParameters =
    		new HashMap<String, String>(3);

    /** Command's bindings (including parameters of bindings)
     * [result name] => [ [parameter name] => [parameter value] ]
     */
    private final Map<String, Map<String, String>> binds2params =
    		new HashMap<String, Map<String, String>>(1);

    private static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap(0));
    
    public String getCommandName() { return commandName; }
    public void setCommandName(String name) { commandName = name; }
    
    public Map<String, String> getBinds() { return binds; }

    public Map<String, String> getCommandParameters() { return commandParameters; }

    public Map getBindParameters(String resultName) {
		return MapUtils.getMap(binds2params, resultName, EMPTY_MAP);
    }

    public void addBindingParameters(String resultName, Map<String, String> map) {
        binds2params.put(resultName, map);
    }

    /**
     * Converts wildcard expression of URI of the command matcher
     * to regular expression (to match it against future http requests).<br>
     */
    public void setPattern(String commandUri) {
        logger.debug("+");
        if(commandUri == null){
            logger.debug("- URI is not set");
            return;
        }
        String fixedExpr = null;
        if(commandUri.equals("/")){
            fixedExpr = "^/$";
        }else if(commandUri.equals(StringUtils.EMPTY)){
            fixedExpr = "^$";
        }else{
            fixedExpr = commandUri.replaceFirst("^/*", StringUtils.EMPTY);
            fixedExpr = wildcardToRegexp(fixedExpr);
        }
        if(fixedExpr.contains("@lang")){
        	fixedExpr = fixedExpr.replace("@lang", getLangPattern());
        }
        pattern = Pattern.compile(fixedExpr);
        logger.debug("-");
    }
    
    
    private String getLangPattern(){
    	String pattern = "(";
    	for(Language lang :Language.getItems() ){
    		pattern += "|" + lang.getCode().replace("-", "\\-");
    	}
    	pattern = pattern.replaceFirst("\\|", StringUtils.EMPTY);
    	return pattern + ")";
    	//return "(nl|en|de|ru|pl|es|fr)";
    	
    }
    
    
    /**
     * Escape all characters which have special meaning in Regexp
     * 
     * @param expression
     * @return
     */
    private static String wildcardToRegexp(String expression) {
        logger.debug("+");
        StringBuffer fixed = new StringBuffer();
        fixed.append("^/?");
        final String SPRECIAL_CHARS = ".{}[]()&^$?+|\\";
        for(int i = 0, len = expression.length(); i < len; i++){
            char c = expression.charAt(i);
            if(SPRECIAL_CHARS.indexOf(c) != -1){
                fixed.append('\\');
                fixed.append(c);
            }else if(c == '*'){
                // Is it a doubled asterisk (**) or single?
                if( (i + 1 < len) && (expression.charAt(i + 1) == '*') ){
                    fixed.append("(.*)");
                    // skipping the second asterisk
                    i++;
                }else{
                    // expression for any symbols except forward slash
                    fixed.append("([^/]*)");
                }
            }else{
                // If not a special character, append it without changes
                fixed.append(c);
            }
        }
        fixed.append('$');
        logger.debug("-");
        return fixed.toString();
    }
    
    
    /**
     * Matches an URI against the pattern contained in field
     * <code>pattern</code>
     * 
     * @param uri URI to match against the pattern
     * @return true, if the uri matches the pattern;
     *         <code>false</code> otherwise
     */
    public boolean matches(String uri) {
        if (logger.isDebugEnabled()){
            logger.debug("+ pattern: " + pattern.pattern() + ", uri: " + uri);
        }
        if( (this.pattern == null) || (uri == null) ){
            logger.error("- pattern or uri is not defined");
            return false;
        }
        logger.debug("-");
        return this.pattern.matcher(uri).matches();
    }

    /**
     * Returns a map of extra parameters defined in application.xconf
     * for this mapping.<br>
     * Configuration parameters are evaluated for every HTTP request:
     * all occurences of keys ({1} to {9}) are replaced with matching
     * groups of the URI. Matching groups are defined by single or
     * double asterisks in the configuration pattern.
     * 
     * @param uri URI to match against the pattern
     * @return map of evaluated parameters for the command (or for its binding).
     */
    public Map<String, String> evaluateParameters(
    		Map<String, String> params, String uri)
	{
        logger.debug("+");
        if(uri == null){
            logger.error("- Illegal invocation: URI must not be null");
            return params;
        }
        Matcher matcher = (pattern == null)
        	? matcher = Pattern.compile("^.*$").matcher(uri)
			: pattern.matcher(uri);
        Validate.isTrue(matcher.matches());
        Map<String, String> evaluatedParams = new HashMap<String, String>(2);
        int numGroups = matcher.groupCount();
        for(String pname : params.keySet()) {
            String pvalue = params.get(pname);
            for(int i = 1; i <= numGroups; i++){
                pvalue = pvalue.replaceAll("\\{" + i + "\\}", matcher.group(i));
            }
            evaluatedParams.put(pname, pvalue);
        }
        logger.debug("-");
        return evaluatedParams;
    }
    
}