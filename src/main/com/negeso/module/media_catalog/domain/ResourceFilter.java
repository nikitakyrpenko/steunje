/*
 * @(#)Id: ResorceFilter.java, 02.11.2007 19:01:00, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.media_catalog.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class ResourceFilter {
	
	private static Logger logger = Logger.getLogger(ResourceFilter.class);

	private static String IGNORE_FILE_ATTRIBUTE = "ignore.file.list";
	private static String IGNORE_FOLDER_ATTRIBUTE = "ignore.folder.list";
	
	private ArrayList<Pattern> file_masks = new ArrayList<Pattern>();
	private ArrayList<Pattern> folder_masks = new ArrayList<Pattern>();
	
	private static ResourceFilter instance;
	
	private ResourceFilter() {
		init();
	}
	
	private void init(){
		file_masks = getPatterns(IGNORE_FILE_ATTRIBUTE);
		folder_masks = getPatterns(IGNORE_FOLDER_ATTRIBUTE);
	}
	
	private ArrayList<Pattern> getPatterns(String property){
		String buffer = Env.getProperty(property);
		String[] st_array = buffer.split(";");
		ArrayList<Pattern> ret_value = new ArrayList<Pattern>();
		for (String s : st_array){
			ret_value.add(Pattern.compile(s.trim().toLowerCase().replace("*", ".*")));
		}
		return ret_value;
	}
	
	public static ResourceFilter get(){
		if (instance == null){
			instance = new ResourceFilter();
		}
		return instance;
	}
	
	public boolean isAllow(Resource resourse, String showMode){
		logger.debug("+");
		if (showMode == null || showMode.equals("true"))
			return true;
		ArrayList<Pattern> pattern = (resourse.isFolder()?folder_masks:file_masks);
		for (Pattern p : pattern){
			if (p.matcher(resourse.getName().toLowerCase()).matches()){
				logger.debug("- resource is not allowed");
				return false;
			}
		}
		logger.debug("- resourse filter passed");
		return true;
	}
	
	public boolean isAllow(File file, String showMode){
		logger.debug("+");
		Resource res = null;
		if (file.isDirectory()){
			res = new Folder(file);
		}else{
			res = new FileResource(file);
		}
		logger.debug("-");
		return isAllow(res, showMode);
	}
}
