/**
 * @(#)$Id: StatisticsCSVView.java,v 1.0, 2006-01-25 15:33:59Z, Andrey Morskoy$
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
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.view.AbstractHttpView;
import com.negeso.module.statistics.StatisticsExportBuilder;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

public class StatisticsCSVView extends AbstractHttpView {
    private static final Logger logger = Logger.getLogger(StatisticsCSVView.class);
    private static final String CSV_MIME_TYPE = "text/csv";
    public static final String INPUT_XML = KEY_XML;
    
    
    public void process(RequestContext request, ResponseContext response) {
        HttpServletResponse httpResponse = getServletResponse(request);
        httpResponse.setDateHeader(HEADER_EXPIRES, 0);
        httpResponse.setContentType(StatisticsCSVView.CSV_MIME_TYPE);
        Object xmlResult = response.get(INPUT_XML);
        if( !(xmlResult instanceof Document) ){
            xmlResult =
                 Xbuilder.createTopEl("page").getOwnerDocument();
        }
        Document xml = (Document) xmlResult;
       
        String title = request.getParameter("csv_title");
        String head = request.getParameter("csv_head");
        String cacheAttr = xml.getDocumentElement().
                                getAttribute("cahedFilename");
        String cacheXmlAttr = xml.getDocumentElement().
                                  getAttribute("cahedXmlFilename");

        if( title!=null && head!=null ){
            try{
                OutputStream out = httpResponse.getOutputStream();
                
                StringBuffer buf = StatisticsExportBuilder.buildStatistics(
                                title,
                                head,
                                xml                       
                             );
                
                String outString = buf.substring(0,buf.length());
              
                out.write(outString.getBytes());
               
                out.flush();
                out.close();
                
            }
            catch(Exception e){
                logger.error("Error while generating CSV view");
                logger.error("Title is "+title);
               
                logger.error("Exception: ",e);
            }
        }
        else{
            logger.error("CSV title or CSV head is null");
            logger.error("CSV title is " + title);
            logger.error("CSV head is "  + head);
        }
        
        
        
    }
    

}
