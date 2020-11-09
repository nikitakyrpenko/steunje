/*
 * @(#)$Id: StreamView.java,v 1.0, 2007-02-07 18:15:59Z, Volodymyr Snigur$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.view;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.CopyUtils;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;

public class StreamView extends AbstractHttpView {
    
    private static Logger logger = Logger.getLogger(StreamView.class);
    
    /**
     * Accepted parameters:
     * <li> stream          - InputStream to send.
     * <li> stream-length   - number of bytes. Optional string.
     * <li> mime-type       - default is "application/octet-stream"). String.
     * <li> cache-private   - default is "false". String.
     * <li> cache-minutes   - "0" (default) is no cache. String.
     */
    public void process(RequestContext request, ResponseContext response) {
        logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        
        String mimeType = (String) response.get("mime-type");
        if (mimeType == null) mimeType = "application/octet-stream";
        httpResponse.setContentType(mimeType);
        
        if("true".equals(response.get("cache-private"))) {
            httpResponse.setHeader("Cache-Control", "private");
        }
        
        int minutes = 0;
        if (response.get("cache-minutes") != null) {
            minutes = Integer.parseInt("" + response.get("cache-minutes"));
        }
        if (minutes > 0) {
            long exp = System.currentTimeMillis() + minutes * 60 * 1000;
            httpResponse.setDateHeader(HEADER_EXPIRES, exp);
        } else {
            setHeadersToDisableCaching(httpResponse);
        }
        
        if (response.get("stream-length") != null) {
            int length = Integer.parseInt("" + response.get("stream-length"));
            httpResponse.setContentLength(length);
        }
        
        if (response.get("stream") == null) {
            logger.error("- throwing: stream not set");
            throw new IllegalArgumentException("Stream not set");
        }
        
        try {
            OutputStream out = httpResponse.getOutputStream();
            InputStream in = (InputStream) response.get("stream");
            CopyUtils.copy(in, out);
            out.flush();
            out.close();
            logger.debug("-");
            return;
        } catch (Exception e) {
        	logger.error("- Exception", e);
        }
    }
    
}

