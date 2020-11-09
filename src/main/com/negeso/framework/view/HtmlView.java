/*
 * @(#)HtmlView.java  Created on 06.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.view;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
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
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.generators.Xquery;
import com.negeso.framework.util.replacing.ITokenResolver;
import com.negeso.framework.util.replacing.MapTokenResolver;
import com.negeso.framework.util.replacing.TokenReplacingReader;
import com.negeso.module.core.service.PlaceHolderService;


/**
 * Transforms an XML document with an XSL document (PARAMETER_XSL is id
 * of a stylesheet in the ResourceMap) and serializes the result
 * to the output stream. If XSL is not specified, xml is serialized without
 * transformation.<br>
 * Response mime-type header is set to 'text/html'.  
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class HtmlView extends AbstractHttpView{

    private static final Logger logger = Logger.getLogger(HtmlView.class);

    /** XML document to use for transformation (set in ResponseContext). */
    public static final String INPUT_XML = KEY_XML;

    /** XSL resource id (or null); expected in request context */
    public static final String INPUT_XSL = KEY_XSL;

    /** XSL resource id (or null); expected in request context */
    public static final String INPUT_XSL_LOCATION = KEY_XSL_LOCATION;

    private static final SimpleDateFormat DATE_TIME_ZONE =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    
    public void process(RequestContext request, ResponseContext response) {
        logger.debug("+");
        HttpServletResponse httpResponse = getServletResponse(request);
        setHeadersToDisableCaching(httpResponse);
        httpResponse.setContentType(AbstractHttpView.HTML_MIME_TYPE);
        Object xmlResult = response.get(INPUT_XML);
        if( !(xmlResult instanceof Document) ){
            xmlResult = Xbuilder.createTopEl("page").getOwnerDocument();
        }
        Document xml = (Document) xmlResult;
        Templates templates = (response.get(INPUT_XSL) != null)
        	? ResourceMap.getTemplates((String) response.get(INPUT_XSL))
			: ResourceMap.getTemplatesByLocation((String) response.get(INPUT_XSL_LOCATION));
        Element topEl = xml.getDocumentElement();
        topEl.setAttribute("interface-language", getInterfaceLanguage(request));
        Xbuilder.setAttr( topEl, "serverTime", DATE_TIME_ZONE.format((new Date())) );
        if (shouldSendStatus404(xml, request))
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        OutputStream out = null;
        try {
        	User user = request.getSession().getUser();
			if (user != null && User.ADMINISTRATOR_TYPE.equals(user.getType()) || PlaceHolderService.getPlaceHoldersMap(Env.getSiteId()).isEmpty()) {
				transform(out, httpResponse, templates, xml);
			} else {
				transformAndReplacePlaceHolders(out, httpResponse, templates, xml);
			}
            logger.debug("-");
        } catch (Exception e) {
        	handleException(e);
        } finally {
        	IOUtils.closeQuietly(out);
        }
    }
    
    private void transform(OutputStream out, HttpServletResponse httpResponse, Templates templates, Document xml) throws Exception {
    	out = httpResponse.getOutputStream();
        Transformer transformer = templates != null
            ? templates.newTransformer()
            : XmlHelper.newTransformer();
        transformer.setOutputProperty("method", "html");
        transformer.setOutputProperty("indent", "no");
        transformer.transform(new DOMSource(xml), new StreamResult(out));
        out.flush();
        out.close();
    }
    
    private void transformAndReplacePlaceHolders(OutputStream out, HttpServletResponse httpResponse, Templates templates, Document xml) throws Exception {
    	//out = httpResponse.getOutputStream();
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
        Transformer transformer = templates != null
            ? templates.newTransformer()
            : XmlHelper.newTransformer();
        transformer.setOutputProperty("method", "html");
        transformer.setOutputProperty("indent", "no");
        transformer.transform(new DOMSource(xml), new StreamResult(os));
        ITokenResolver resolver = new MapTokenResolver(PlaceHolderService.getPlaceHoldersMap(Env.getSiteId()));
        Reader reader = new TokenReplacingReader(
                new StringReader(os.toString("UTF-8")), resolver);
        int numRead=0;
        Writer writer = httpResponse.getWriter();
        while((numRead=reader.read()) != -1){
        	writer.write(numRead);
        }
        writer.flush();
        writer.close();
    }
    
	private boolean shouldSendStatus404(Document xml, RequestContext request) {
		logger.debug("+");
		if (xml == null) {
			logger.debug("- no XML in ResponseContext");
			return false;
		}
		if (!"not_found".equals(Xquery.val(xml, "/negeso:page/@category"))) {
			logger.debug("- it is not a 'Resource not found' page");
			return false;
		}
		String uriFilename = request.getParameter("filename");
		String pageFilename = Xquery.val(xml, "/negeso:page/negeso:filename/text()");
		if (uriFilename != null && uriFilename.equals(pageFilename)) {
			logger.debug("- User explicitely requested a 'Resource not found' page");
			return false;
		}
		logger.debug("- 'Resource not found' page, status 404 shall be sent");
		return true; 
		
	}
    
}