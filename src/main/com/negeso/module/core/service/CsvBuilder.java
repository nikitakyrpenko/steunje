/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.service;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class CsvBuilder {
	
	private static Logger logger = Logger.getLogger(CsvBuilder.class);
	
	private static final String CSV_SEPARATOR = ";";
	
	public File getFile(String fileName, String[] header, String[][] data){
		
    	Folder folder = Repository.get().createFolder(
    		Repository.get().getRootFolder(),
    		"export"
    	);
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    	File file = new File(
    		folder.getFile(), 
    		formatter.format(new Date(System.currentTimeMillis())) + "-" + fileName + ".csv"
    	);
    	
    	PrintWriter writer = null;
    	
		try {
			file.createNewFile();
	    	writer = new PrintWriter(file);
	    	
	    	writer.println(buildLine(header));
			
	    	for (int i = 0; i < data.length; i++) {
	    		writer.println(buildLine(data[i]));
			}
		}
		catch(Exception e){
			logger.error("-error", e);
		}
		finally{			
			if (writer != null){
				writer.close();
			}
		}
		return file;
    }
	
	private String buildLine(String[] data) {
		StringBuffer line = new StringBuffer(200);
		for (int i = 0; i < data.length; i++) {
    		line.append(data[i] != null ? data[i] : StringUtils.EMPTY );
    		if (i <= data.length - 1 ) {
    			line.append(CSV_SEPARATOR);					    			
    		}
		}
		return line.toString();
	}

}

