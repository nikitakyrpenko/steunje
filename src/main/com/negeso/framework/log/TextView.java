/**
 * @(#)$Id: TextView.java,v 1.3, 2006-06-26 15:48:38Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.log;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.view.AbstractHttpView;

public class TextView extends AbstractHttpView{
	
    public static final String OUTPUT_DOCUMENT = "stringBuffer";
    
    private static final String RESULT_KEY = "result";
    
    private static final String MIME_KEY = "mime";
    
    private static final Logger logger = Logger.getLogger(TextView.class);
    
    public void process(RequestContext request, ResponseContext response) {
    	logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        httpResponse.setDateHeader(HEADER_EXPIRES, 0);
        String result = (String) response.get(TextView.RESULT_KEY);
        if(result.equals("text"))
        	httpResponse.setContentType("text/plain");
        else if(result.equals("file")) {
        	String mime = (String) response.get(TextView.MIME_KEY);
        	httpResponse.setContentType(mime != null ? mime : "text/file");
        }
        PrintWriter out = null;
        try {
			out = httpResponse.getWriter();
			StringBuffer buffer =
				(StringBuffer) response.get(TextView.OUTPUT_DOCUMENT);
            out.write(buffer.toString());
            out.flush();
            logger.debug("-");
        } catch (Exception e) {
            handleException(e);
        } finally {
        	IOUtils.closeQuietly(out);
        }
    }

}