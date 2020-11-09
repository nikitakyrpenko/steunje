/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.media_catalog.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;


/**
 * 
 * @TODO
 * 
 * @author		Vyacheslav Zapadnyuk
 * @version		$Revision: $
 *
 */
public class StreamingBandwidthUpdater extends TimerTask {
	private static Logger logger = Logger.getLogger(StreamingBandwidthUpdater.class);
	
	private static ParameterService parameterService;
	
	public void run() {
		logger.debug("-");
		updateBandwidthStatistics();
		logger.debug("+");
	}
	
    public void updateBandwidthStatistics() {
    	logger.debug("-");
    	ConfigurationParameter parameter = parameterService.findParameterByName("media.catalog.streaming.statistics"); 
		if( parameter!=null ) {
			parameter.setValue(
					readBandwidthStatistics(
							parameterService.findParameterByName("media.catalog.streaming.url").getValue()
					)
			);
			parameterService.update(parameter, false);
		}
		logger.debug("+");
    }

	private String readBandwidthStatistics(String url) {
		logger.debug("+");
		String statisticsMb = "0";
		if(url==null) {
			url = Env.getSmtpHost();
		}
		String filepath = parameterService.findParameterByName("media.catalog.streaming.file").getValue();
		if(filepath!=null) {
	        try{
				File file = new File(filepath);
		        if( (!file.canRead()) || (!file.isFile()) ){
		            logger.warn(filepath+" - the streaming bandwidth statistics file can not be found.");
		            logger.debug("-");
		    		return statisticsMb;
		        }

			    BufferedReader reader = new BufferedReader(new FileReader(file));
			    String line = null;
			    boolean not_found = true;
			    while (
			    		( line=reader.readLine() )!=null 
			    		&& not_found
			    	){
			    	if(line.indexOf(url)!=-1) {
			    		statisticsMb = line.substring(url.length()+1);
			    		not_found = false;
			    	}
			    }
		    }
		    catch(FileNotFoundException e){
		    	logger.error(e);
		    }
		    catch(IOException e){
		    	logger.error(e);
		    }
	        
		} else {
			logger.warn(" - the streaming bandwidth statistics file can not be found.");
		}
		logger.debug("-");
		return statisticsMb;
	}

	public static ParameterService getParameterService() {
		return parameterService;
	}

	public static void setParameterService(ParameterService parameterService) {
		StreamingBandwidthUpdater.parameterService = parameterService;
	}
	
}