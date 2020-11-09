package com.negeso.module.statistics;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StatisticsExportBuilder{
	private static Logger logger = Logger.getLogger(StatisticsExportBuilder.class);	
	
	
	
	public static StringBuffer buildStatistics(
            String title, 
            String head, 
            Document xml)
    {
        //HashMap map = new HashMap();
        StringBuffer buf = new StringBuffer();
        Element xmlEl = xml.getDocumentElement();
        NodeList nodes =  xmlEl.getElementsByTagName("negeso:StatisticsItem");
        NodeList nodesPage =  xmlEl.getElementsByTagName("negeso:StatisticsItemName");
        NodeList nodesHit =  xmlEl.getElementsByTagName("negeso:StatisticsItemNameQty");
        NodeList pageNames =  xmlEl.getElementsByTagName("negeso:PageName");
        NodeList pageTitles =  xmlEl.getElementsByTagName("negeso:PageTitle");
        //StatisticsList stats = new StatisticsList();
        buf.append(title+"\n\n\n");
        buf.append(head+"\n");
        String page = "";
        String hit = "";
        if( nodes!=null){
                for( int i=0; i<nodes.getLength(); i++ ){
	                Node node = nodes.item(i);
	                //NodeList items = node.getChildNodes();
	                //StatisticsItem stItem = new StatisticsItem();
	                page = nodesPage.item(i).getChildNodes().item(0).getNodeValue();   
	                hit = nodesHit.item(i).getChildNodes().item(0).getNodeValue();   
	                String pageTitle = pageTitles.item(i).getChildNodes().item(0).getNodeValue();   
                    String pageName = pageNames.item(i).getChildNodes().item(0).getNodeValue();   
	                if( page!=null && hit!=null ){
	                    buf.append(pageTitle + ";" + pageName + ";" + hit + "\n");
	                }
                } 
        }
        
        return buf;
        
    }
    
    
  

}





