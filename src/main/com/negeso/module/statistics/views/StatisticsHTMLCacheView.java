/**
 * @(#)$Id: StatisticsHTMLCacheView.java,v 1.2, 2007-01-09 18:59:56Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
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
import com.negeso.framework.view.AbstractHttpView;

public class StatisticsHTMLCacheView extends AbstractHttpView {
	
    /** XML document to use for transformation (set in ResponseContext). */
    public static final String INPUT_XML = KEY_XML;
    
    private static final String CACHE_XSL = "STATISTICS_CACHE_XSL";
    
    /** XSL resource id (or null); expected in request context */
    public static final String INPUT_XSL = KEY_XSL;
    
    private static final Logger logger =
    		Logger.getLogger(StatisticsHTMLCacheView.class);
    
    public void process(RequestContext request, ResponseContext response) {
        logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        httpResponse.setDateHeader(HEADER_EXPIRES, 0);
        httpResponse.setContentType(AbstractHttpView.HTML_MIME_TYPE);
        Object xmlResult = response.get(INPUT_XML);
        if( !(xmlResult instanceof Document) ){
            xmlResult = Xbuilder.createTopEl("page").getOwnerDocument();
        }
        Document xml = (Document) xmlResult;
        Element elTop = xml.getDocumentElement();
		String buildCache = elTop.getAttribute("buildCache");
        String cacheAttr = elTop.getAttribute("cahedFilename");
        String cacheXmlAttr = elTop.getAttribute("cahedXmlFilename");
        if(!Boolean.parseBoolean(buildCache)) {
        	sendFromCache(httpResponse, cacheAttr);
            logger.debug("- returning cache");
            return;
        }
        String xslId = (String) response.get(INPUT_XSL);
        Templates templates = ResourceMap.getTemplates(xslId);
        elTop.setAttribute("interface-language", getInterfaceLanguage(request));
        OutputStream out1 = null;
        OutputStream out2 = null;
        OutputStream out3 = null;
        try {
        	String useCache = Env.getProperty("statistics.useCache","false");
        	out1 = httpResponse.getOutputStream();
        	
        	Transformer transformer = templates != null
        	? templates.newTransformer()
        			: XmlHelper.newTransformer();
        	transformer.setOutputProperty("method", "html");
        	transformer.setOutputProperty("indent", "yes");
        	transformer.transform(new DOMSource(xml), new StreamResult(out1));
        	out1.flush();
        	if( useCache.equals("true") ){
        		out2 = new FileOutputStream(cacheAttr);
        		transformer.transform(new DOMSource(xml), new StreamResult(out2));
        		out2.flush();
        	}
        	templates = ResourceMap.getTemplates(CACHE_XSL);
        	transformer = templates != null
        	? templates.newTransformer()
        			: XmlHelper.newTransformer();
        	transformer.setOutputProperty("indent", "yes");
        	if( useCache.equals("true") ){
        		out3 = new FileOutputStream(cacheXmlAttr);
        		transformer.transform(new DOMSource(xml), new StreamResult(out3));
        		out3.flush();
        	}
        } catch (Exception e) {
        	handleException(e);
        } finally {
        	IOUtils.closeQuietly(out1);
        	IOUtils.closeQuietly(out2);
        	IOUtils.closeQuietly(out3);
        }
        logger.debug("- cache bypassed");
        return;
    }

	private void sendFromCache(HttpServletResponse response, String file) {
		logger.debug("+");
		FileInputStream in = null;
		PrintWriter writer = null;
        try {
			String cnt = FileUtils.readFileToString(new File(file), "UTF-8");
			writer = response.getWriter();
			writer.print(cnt);
			writer.flush();
        } catch(Exception e) {
        	handleException(e);
        } finally {
        	IOUtils.closeQuietly(in);
        	IOUtils.closeQuietly(writer);
        }
        logger.debug("-");
	}
  
}