/*
 * @(#)XmlView.java  Created on 01.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.view;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;


/**
 * Serializes XML to the output stream.
 * If PARAMETER_XSL is set (id of a stylesheet in the ResourceMap),
 * an additional transformation is made while serializing.<br>
 * Response mime-type header is set to 'text/xml'.
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */
public class XmlView extends AbstractHttpView {
    
    
    private static Logger logger = Logger.getLogger(XmlView.class);
    
    
    /** XML document to use for transformation (set in ResponseContext) */
    private static final String INPUT_XML = KEY_XML;
    
    
    /** XSL resource id (or null); expected in request context */
    private static final String PARAMETER_XSL = KEY_XSL;
    
    private static final SimpleDateFormat DATE_TIME_ZONE =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");    
    
    public void process(RequestContext request, ResponseContext response) {
        logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        setHeadersToDisableCaching(httpResponse);
        httpResponse.setContentType("text/xml; charset=UTF-8");
        Object xmlResult = response.get(INPUT_XML);
        if( !(xmlResult instanceof Document) ){
            xmlResult = Xbuilder.createTopEl("page").getOwnerDocument();
        }
        Document xml = (Document) xmlResult;
        String xslId = (String) response.get(PARAMETER_XSL);
        Templates templates = ResourceMap.getTemplates(xslId);
        
        // Addition the interface language attribute for localization
        
	    String interfaceLanguage = 
	          (String)request.getSession().getAttribute("interface-language");         
	    if (interfaceLanguage == null)
	         interfaceLanguage = "en";
	    Element topEl = xml.getDocumentElement();
	    topEl.setAttribute("interface-language", interfaceLanguage);
	        
	    Xbuilder.setAttr(
	            topEl, "serverTime", DATE_TIME_ZONE.format((new Date())));
	       
        OutputStream out = null;
        try {
			out = httpResponse.getOutputStream();
            Transformer transformer = templates != null
                ? templates.newTransformer()
                : XmlHelper.newTransformer();
            transformer.transform(new DOMSource(xml), new StreamResult(out));
            out.flush();
            logger.debug("-");
            return;
        } catch (Exception e) {
        	handleException(e);
        } finally {
        	IOUtils.closeQuietly(out);
        }
    }    
    
    
}