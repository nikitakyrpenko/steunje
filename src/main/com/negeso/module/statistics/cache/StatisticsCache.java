/**
 * @(#)$Id: StatisticsCache.java,v 1.2, 2006-02-16 10:03:39Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics.cache;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;



public class StatisticsCache {
   
    public static final String CACHE_FOLDER = "WEB-INF/statistics";
    private static Logger logger = Logger.getLogger(
                                   StatisticsCache.class);
   
    public static final StatisticsCache instance = 
                                            new StatisticsCache();
    
    public static StatisticsCache getInstance() {
        return instance;
    }

    private StatisticsCache(){}
   
   
    
    
    public String createHtmlFilename(String title, List<String> parameters) {
        if(    title==null      || title.equals("") 
            || parameters==null || parameters.isEmpty() )
        {
            return null;
        }
        else{
            StringBuffer filename = new StringBuffer(title);
            for( Iterator it=parameters.iterator(); it.hasNext(); ){
                String param = (String)it.next();
                filename.append("_" + param);
            }
            filename.append(".html");
            return filename.toString();
        }
    }
    
    public String createXmlFilename(String title, List<String> parameters) {
        if(    title==null      || title.equals("") 
                || parameters==null || parameters.isEmpty() )
        {
            return null;
        }
        else{
            StringBuffer filename = new StringBuffer(title);
            for( Iterator it=parameters.iterator(); it.hasNext(); ){
                String param = (String)it.next();
                filename.append("_" + param);
            }
            filename.append(".xml");
            return filename.toString();
        }
    }
    
    public void writeXmlFile(Document xml, String path){
            try{
                FileOutputStream fout = new FileOutputStream(path);
                String val = xml.getParentNode().getChildNodes().
                                                 item(0).getNodeValue();
              
                fout.write(val.getBytes());
                fout.flush();
                fout.close();
            }
            catch(Exception e){
                logger.error("write Xml Cache Exception, cache is " +path);
                logger.error(e.toString());
            }
    }

   
    
   
       

}
