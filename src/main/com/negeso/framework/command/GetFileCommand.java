/*
 * @(#)CheckFileExistanceCommand.java  Created on 21.01.2004
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.media_catalog.Repository;


/**
 * Tries to find and read a publicly accessilbe file (which requires no
 * authorization).
 * This command expect that files are requested by virtual path and
 * uses {@link Env#getRealPath(String)} to resolve it.
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class GetFileCommand extends AbstractCommand {


    private static Logger logger = Logger.getLogger(GetFileCommand.class);
    
    
    /** filename, including virtual path (e.g. pics/photo1.jpg */
    public static final String INPUT_FILE = "file";

    /** filename, including virtual path (e.g. pics/photo1.jpg */
    public static final String INPUT_IS_MEDIA_FLAG = "is_media_file";
    
    
    /** Object of class java.io.File */
    public static final String OUTPUT_FILE = AbstractHttpView.KEY_FILE;
    
    
    public static final String OUTPUT_MIME_TYPE = AbstractHttpView.HEADER_MIME_TYPE;
    
    
    public static final String OUTPUT_EXPIRES = AbstractHttpView.HEADER_EXPIRES;
    
    
    /** Static resources expiration period, millis (1 week by default) */
    private static long EXPIRATION_PERIOD = 1000 * 60 * 60 * 24 * 7;
    
    
    {
        try{
            String conf = Env.getProperty("static.files.expiration");
            // The value in conf is measured in minutes
            EXPIRATION_PERIOD = Long.parseLong(conf) * 60 * 1000;
        }catch(Exception e){
            logger.error("Incorrect value of static files expiration", e);
        }
    }
    
    
    public ResponseContext execute()
    {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        
        String path = getRequestContext().getParameter(INPUT_FILE);
        try {
        	path = new String(getRequestContext().
        			getParameter(INPUT_FILE)
        			.getBytes("ISO-8859-1"), 
        			"UTF-8");
        	path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		String fileName = null;
		
        if ( "true".equals(this.getRequestContext().getParameter(INPUT_IS_MEDIA_FLAG) )){
            fileName = Repository.get().getRealPath(path);
        }
        else{
            if ( "true".equals(this.getRequestContext().getParameter("is_site_file") )){
                try{
                    fileName = Env.getSite().getTemplateRealPath(path);
                }
                catch(CriticalException e){
                    logger.error("Error for file:" + path, e);
                    response.setResultName(RESULT_FAILURE);
                    return response;
                }
            }
            else{
                fileName = Env.getRealPath(path);
            }
        }
        if ( logger.isInfoEnabled() ){
            logger.info("fileName:" + fileName);
        }
        
        if(fileName == null){
            response.setResultName(RESULT_FAILURE);
            logger.debug("- file name is not specified");
            return response;
        }
        File file = new File(fileName);
        if( (!file.canRead()) || (!file.isFile()) ){
            response.setResultName(RESULT_FAILURE);
            logger.debug("- not a file or cannot be read");
            return response;
        }
        response.getResultMap().put(OUTPUT_FILE, file);
        
        String mimeType = Env.getMimeType(file.getName().toLowerCase());
        if(mimeType != null) {
            if(mimeType.startsWith("text/")) {
                mimeType += "; charset=UTF-8";
            }
        } else {
            mimeType = "application/octet-stream";
        }
        
        response.getResultMap().put(OUTPUT_MIME_TYPE, mimeType);
        response.getResultMap().put(
            OUTPUT_EXPIRES,
            new Long(System.currentTimeMillis() + EXPIRATION_PERIOD)
            );
        response.setResultName(RESULT_SUCCESS);
        logger.debug("-");
        return response;
    }


}

