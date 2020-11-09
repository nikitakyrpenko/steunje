/**
 * @(#)$Id: LogXmlBuilder.java,v 1.4, 2007-01-09 18:27:48Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;


public class LogXmlBuilder {
    private static final Logger logger = Logger.getLogger(LogXmlBuilder.class);
    private static final String logFile = "/WEB-INF/generated/logs/main_framework.log";
   
    public static StringBuffer buildLogBuffer(String filename){
        StringBuffer buffer = new StringBuffer();        
       
        try{
            File logF = new File( Env.getRealPath(filename) );
            if (!logF.exists()) {
            	logF = new File( Env.getRealPath(logFile) );
            }
            logger.info("Loaded file: " + logF.getAbsolutePath());
            
            BufferedReader br = new BufferedReader(new FileReader(logF));
            
            String tmpS;
         
            while( (tmpS = br.readLine())!=null ){
                buffer.append(tmpS).append("\r\n");
            }
  
            br.close();
        }
        catch (Exception nullExc) {
           logger.error("Can't load "+logFile);
           return new StringBuffer(" ");
        }
       
        return buffer;
    }
}
