/*
 * @(#)CommandFactory.java  Created on 13.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;

import com.negeso.framework.ResourceMap;
import com.negeso.framework.ShutdownNotifier;
import com.negeso.framework.command.Command;
import com.negeso.framework.event.AppDestroyListener;
import com.negeso.framework.generators.Xquery;
import com.negeso.framework.module.AbstractFriendlyUrlHandler;


/**
 * Resolves commands based on command name or request URI
 * (as configured in application.xconf).
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class CommandFactory implements AppDestroyListener {
    
    private static Logger logger = Logger.getLogger(CommandFactory.class);
	
    private static CommandFactory INSTANCE = new CommandFactory();
    
    /** Map [command name] to [CommandConfiguration] */
	private HashMap<String, CommandMapping> explicitMappings =
			new HashMap<String, CommandMapping> ();
    
	/** Map [URI pattern (as String)] to [CommandConfiguration] */
    private LinkedHashMap<String, CommandMapping> matcherMappings =
    		new LinkedHashMap<String, CommandMapping>();
    
    /** Map of [command name] to [command class name] */
    private HashMap<String, String> commandDefinitions = new HashMap<String, String>();
    
    /** Map [view name] to [view class name] */
    private HashMap<String, String> viewDefinitions = new HashMap<String, String>();
    
	private CommandFactory() {
        logger.debug("+");
        Document cfg = ResourceMap.getDom("APPLICATION_CONFIG");
        try {
            for (Element elCommand : Xquery.elems(cfg, "//components//command"))
                commandDefinitions.put(
                		elCommand.getAttribute("name"),
                		elCommand.getAttribute("class") );
            for (Element elView : Xquery.elems(cfg, "//components//view"))
            	viewDefinitions.put(
                		elView.getAttribute("name"),
                		elView.getAttribute("class") );
            configureCommands(
        		Xquery.nodes(cfg, "//controller/command").iterator(), false);
            configureCommands(
        		Xquery.nodes(cfg, "//controller/matcher").iterator(), true);
        } catch (Exception e) {
            logger.error("- JaxenException", e);
            throw new RuntimeException(e);
        }
        ShutdownNotifier.registerAppDestroyListener(this);
        logger.debug("-");
    }
    
    /** Returns Class of a command by its symbolic name. */
    public static Class getCommandClassForName(String friendlyName)
    throws ClassNotFoundException {    
        return Class.forName(getCommandClassNameForName(friendlyName));
    }
    
    public static String getCommandClassNameForName(String friendlyName)
    throws ClassNotFoundException{
    	logger.debug("+");
        String className = INSTANCE.commandDefinitions.get(friendlyName);
        if(className == null){
            throw new ClassNotFoundException(
                "Cannot find class by its friendly name " + friendlyName);
        }
        logger.debug("-");
        return className;
    }
    
    /** Returns Class of a view by its symbolic name; never null. */
    public static Class getViewClassForName(String friendlyName) {
    	logger.debug("+ -");
    	try {
			return Class.forName(INSTANCE.viewDefinitions.get(friendlyName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("No view by name: " + friendlyName, e);
		}
    }
    
    /**
	 * Finds a Command by its symbolic name.<br>
     * The resolution is done as follows:
     * <pre>
     * 1) command name in request context is searched
     * 2) if not found, servlet request URL is tested against attribute "url"
     *    of commands, in the order that they were inserted into the map of
     *    commands
     * 3) if still no matches, default command is picked from the map
     *    of commands, and executed
	 * </pre>
     * 
	 * @param request request context
	 */
	public static CommandMapping resolveCommand(RequestContext request) {
        logger.debug("+");
        
        // Can we resolve the command by name?
        CommandMapping cmdCfg =
        	INSTANCE.explicitMappings.get(request.getParameter("command"));
        if(cmdCfg != null){
            Map<String, String> cmdParams = cmdCfg.getCommandParameters();
            for (Map.Entry<String, String> name : cmdParams.entrySet())
            	request.setParameter(name.getKey(), name.getValue());
            logger.debug("- Command found by name");
            return cmdCfg;
        }
        
        // Can we resolve the command by URI?
        String uri = request.getParameter(RequestContext.REQUEST_URI);
        if(uri != null){
            Iterator iterator = INSTANCE.matcherMappings.values().iterator();
            while (iterator.hasNext()) {
                cmdCfg = (CommandMapping) iterator.next();
                if(cmdCfg.matches(uri)){
                    Map<String, String> cmdParams = cmdCfg.getCommandParameters();
                    cmdParams = cmdCfg.evaluateParameters(cmdParams, uri);
                    for (Map.Entry<String, String> name : cmdParams.entrySet())
                    	request.setParameter(name.getKey(), name.getValue());
                    logger.debug("-  Command found by URI");
                    return cmdCfg;
                }
            }
        }
        logger.warn("- cannot resolve the command");
        throw new RuntimeException("Cannot resolve the command");
	}
    
    private void configureCommands(
        Iterator commandsIterator, boolean isUriBased ) throws JaxenException
    {
        logger.debug("+");
        final DOMXPath xname = new DOMXPath("@name");
        final DOMXPath xcommandName = new DOMXPath("@command-name");
        final DOMXPath xuri = new DOMXPath("@uri");
        final DOMXPath xvalue = new DOMXPath("@value");
        final DOMXPath xparameter = new DOMXPath("parameter");
        final DOMXPath xbind = new DOMXPath("bind");
        final DOMXPath xview = new DOMXPath("@view");
        final DOMXPath xresult = new DOMXPath("@result");
        
        // Configure commands one by one, adding them to the appropriate map
        while (commandsIterator.hasNext()) {
            Object commandNode = commandsIterator.next();
            CommandMapping cmap = new CommandMapping();
            
            /* Configure command's parameters */
            for (Object param : xparameter.selectNodes(commandNode))
            	cmap.getCommandParameters()
            		.put(xname.valueOf(param), xvalue.valueOf(param));
            
            /* Configure command's bindings */
            for (Object elBind : xbind.selectNodes(commandNode)) {
                String resultName = xresult.valueOf(elBind);
                cmap.getBinds().put(resultName, xview.valueOf(elBind));
                Map<String, String> bindParams = new HashMap<String, String>(3);
                for (Object p : xparameter.selectNodes(elBind))
                    bindParams.put(xname.valueOf(p), xvalue.valueOf(p));
                cmap.addBindingParameters(resultName, bindParams);
            }
            
            /* Read command's attributes and resolve class */
            if(isUriBased){
                cmap.setCommandName(xcommandName.valueOf(commandNode));
                String uri = xuri.valueOf(commandNode);
                cmap.setPattern(uri);
                matcherMappings.put(uri, cmap);
            }else{
                cmap.setCommandName(xname.valueOf(commandNode));
                if(explicitMappings.containsKey(cmap.getCommandName())){
                    throw new RuntimeException(
                		"Duplicate command: " + cmap.getCommandName() );
                }
                explicitMappings.put(cmap.getCommandName(), cmap);
            }
        }
        logger.debug("-");
        return;
    }
    
    /** @see com.negeso.framework.event.AppDestroyListener#destroy(java.util.EventObject) */
    public void destroy(EventObject event) {
	    logger.info("Clearing command configuration cache...");
	    destroy();
	}
	
	
	public static void reloadCache() {
	    logger.info("Reloading command configuration cache...");
	    ShutdownNotifier.removeAppDestroyListener(INSTANCE);
	    destroy();
        CommandFactory.INSTANCE = new CommandFactory();
	}
	
	private static void destroy() {
		CommandFactory.INSTANCE.commandDefinitions.clear();
        CommandFactory.INSTANCE.viewDefinitions.clear();
	    CommandFactory.INSTANCE.explicitMappings.clear();
	    CommandFactory.INSTANCE.matcherMappings.clear();
	    CommandFactory.INSTANCE = null;
	}

	public static Command getCommandForName(String commandName, 
			RequestContext context) throws ClassNotFoundException,
											InstantiationException,
											IllegalAccessException
															
	{
		String className = getCommandClassNameForName(commandName);
		String beanName = StringUtils.uncapitalize(className.substring(className.lastIndexOf(".")+1));
		Command command = null;
		try{
			command = (Command)context.getService(beanName);
		} catch(NoSuchBeanDefinitionException e){
			logger.info("Command '" + className + "' not in spring application context. " +
					"It will not be protected with aspects");
		}
		if (command == null){
			command = (Command)Class.forName(className).newInstance();
		}
		return command;
	}
	
	public static void putMatchers (AbstractFriendlyUrlHandler urlHandler) {
		synchronized (INSTANCE.matcherMappings) {
			LinkedHashMap<String, CommandMapping> tempMap = new LinkedHashMap<String, CommandMapping>();
			tempMap.putAll(urlHandler.getUrlMatchers());
			tempMap.putAll(INSTANCE.matcherMappings);
			INSTANCE.matcherMappings.clear();
			INSTANCE.matcherMappings.putAll(tempMap);
		}
	}
	
	public static void removeMatchersByCommandName (String commandName) {
		synchronized (INSTANCE.matcherMappings) {
			LinkedHashMap<String, CommandMapping> tempMap = new LinkedHashMap<String, CommandMapping>();
			tempMap.putAll(INSTANCE.matcherMappings);
			for ( Entry<String, CommandMapping> entry: tempMap.entrySet()) {
				if (entry.getValue().getCommandName().equals(commandName)) {
					INSTANCE.matcherMappings.remove(entry.getKey());
				}
			}
		}
	}
    
}
