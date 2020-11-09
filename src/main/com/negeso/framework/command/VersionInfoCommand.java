/*
 * @(#)$Id: VersionInfoCommand.java,v 1.11, 2007-04-12 08:57:32Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.command;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.negeso.framework.Env;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.module.core.service.ModuleService;

/**
 *
 * Version info command
 * 
 * @version		$Revision: 12$
 * @author		Olexiy Strashko
 * 
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class VersionInfoCommand extends AbstractCommand {

    private static Logger logger = Logger.getLogger(VersionInfoCommand.class);
    private static final String NEGESO_CMS_LIB_PATH = "WEB-INF" + File.separator + "lib";
    private static final String NEGESO_CMS_JAR_PREFIX = "negeso-wcms";
    
    private ResponseContext response;
    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    @Override
    @SuperuserRequired
    public ResponseContext execute() {
    	response = new ResponseContext();
    	response.setResultName(RESULT_SUCCESS);
		
    	String version = getVersion();
		Map<String, Object> resultMap = response.getResultMap();
		resultMap.put("versionInfo", version);
		addModulesToResultMap();
		return response;
    }
    
    public String getVersion(){
    	// locale jar name by prefix 
		File jarDir = new File(Env.getRealPath(NEGESO_CMS_LIB_PATH));
		String[] files = jarDir.list();
		String jarName = null; 
		for (int i = 0; i < files.length; i++){
		    if (files[i].matches("^" + NEGESO_CMS_JAR_PREFIX + "-[0-9].+")){
		        jarName = files[i]; 
		    }
		}
		
	    // get version from jar manifest
		String version = null;
		if (jarName != null){
		    
		    version = this.readVersionFromJar(jarName);
		    if ( version == null) {
		    	version = this.readVersionFromJarName(jarName);
		    }
		    
		    if ( version == null) {
				version = "VersionInfo is unsupported";
		    }
		    
		    logger.debug("version:" + version);
		}
		return version;
    }

	private void addModulesToResultMap() {
		try {
			ModuleService moduleService = (ModuleService)getRequestContext().getService("moduleService");
			Writer streamWriter = new StringWriter();
			Source source = new DOMSource(moduleService.getDocument(false, getRequestContext()));
			StreamResult result = new StreamResult(streamWriter);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(source, result);
			response.getResultMap().put("modulesXml", streamWriter.toString());
		} catch (TransformerException e) {
			logger.error("Exception - can not get modules xml", e);
			response.setResultName(RESULT_FAILURE);
		} catch (Exception e) {
			logger.error("Exception - can not get modules xml", e);
			response.setResultName(RESULT_FAILURE);
		}
	}

	private String readVersionFromJar(String jarName){
    	if (jarName == null) return null;
    	File jarFile = new File(Env.getRealPath(NEGESO_CMS_LIB_PATH), jarName); 
	    try{
		    JarFile negesoCmsJar = new JarFile(jarFile);
		    if ( negesoCmsJar.getManifest() != null ){ 
				Attributes attrs = negesoCmsJar.getManifest().getMainAttributes();
				if ( attrs.getValue("Specification-Version") != null ){
					return attrs.getValue("Specification-Version");
				}
		    }
	    }
	    catch (IOException e){
	    	logger.error("Error reading jar file: " + jarName + " " + e.getMessage());
	    }
	    return null;
    }
    
    private String readVersionFromJarName(String jarName){
    	if ( jarName == null ) return null;
    	
    	jarName = jarName.replaceAll(NEGESO_CMS_JAR_PREFIX + "-", "");
    	return jarName.replaceAll(".jar", "");
    }


}
