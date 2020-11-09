/*
 * @(#)GetFileView.java  Created on 22.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;


/**
 * Writes a file into ServletOutputStream.
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class GetFileView extends AbstractHttpView {
    
    
    private static final String INPUT_MIME_TYPE =
        AbstractHttpView.HEADER_MIME_TYPE;
    
    
    private static final String INPUT_EXPIRES =
        AbstractHttpView.HEADER_EXPIRES;
    
    
    private static final String INPUT_FILE =
        AbstractHttpView.KEY_FILE;
    
    public static final String INPUT_LAST_MODIFIED =
        AbstractHttpView.HEADER_LAST_MODIFIED;
    
    private static final String INPUT_PRIVATE = "is-private";
    
    
    private static Logger logger = Logger.getLogger(GetFileView.class);
    
    
    public void process(RequestContext request, ResponseContext response)
    {
        logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        String mimeType = (String) response.get(INPUT_MIME_TYPE);
        if(mimeType != null){
            httpResponse.setContentType(mimeType);
        } else {
            logger.warn("mime type is not set");
        }
        if( "true".equals(response.get(INPUT_PRIVATE)) ) {
        	httpResponse.setHeader("Cache-Control", "private");
        }
        if (response.get(INPUT_LAST_MODIFIED) != null){
        	httpResponse.setDateHeader( 
        		HEADER_LAST_MODIFIED, 
				((Long) response.get(INPUT_LAST_MODIFIED)).longValue() );
        } else {
            long expires = ((Long) response.get(INPUT_EXPIRES)).longValue();
            if (expires > 0) {
		        httpResponse.setDateHeader(HEADER_EXPIRES, expires);
            } else {
            	setHeadersToDisableCaching(httpResponse);
            }
        }
        BufferedInputStream in = null;
        OutputStream out = null;
        try{
            File file = (File) response.get(INPUT_FILE);
            in = new BufferedInputStream(new FileInputStream(file));
            httpResponse.setContentLength((int) file.length());
			out = httpResponse.getOutputStream();
            byte[] buffer = new byte[4096];
            int position = 0; 
            while( (position = in.read(buffer)) != -1){
                out.write(buffer, 0, position);
            }
            out.flush();
            logger.debug("-");
            return;
        }catch(IOException e){
        	handleException(e);
        } finally{
        	IOUtils.closeQuietly(in);
        	IOUtils.closeQuietly(out);
        }
    }
}

