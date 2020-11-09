/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.module.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class CentralModulesProviderClient {
	
	private static Logger logger = Logger.getLogger(CentralModulesProviderClient.class);
	
	
	
	public static final String NEGESO_NAMESPACE = "http://negeso.com/2003/Framework";
	
	private String wsUri = null;
	
	public CentralModulesProviderClient(String wsUri) {
		this.wsUri = wsUri;
	}
	
	public CentralModulesProviderClient() {}

	public static void main(String[] args) {
//	      try {
//	         CentralModulesProvider port = new CentralModulesProviderService().getCentralModulesProviderPort();
//	         List<CentralModule> modulesList = port.getModules(null, "en", null);
//	         System.out.println("Got central modules = " + modulesList.size());
//	      } catch(Exception e) {
//	         e.printStackTrace();
//	      }
	   }
	
	public void buildCentralModulesXml(Element parent, String site, String langCode, String reserved) {
		try {
			CentralModulesProviderService service = init();
			CentralModulesProvider port = service.getCentralModulesProviderPort();
			List<CentralModule> modulesList = port.getModules(site, "en", null);
			for (CentralModule centralModule : modulesList) {
				buildModuleElemet(parent, centralModule);
			}
		} catch (Exception e) {
			logger.error("Exeption durring getting central modules from: " + wsUri, e);
		}
	}
	
	private CentralModulesProviderService init() {
		URL url = null;
		try {
			url = new URL(wsUri);
		} catch (MalformedURLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return new CentralModulesProviderService(url);
	}
	
	protected Element buildModuleElemet(Element parent, CentralModule centralModule) {
		Element moduleEl = createEl(parent, "centralModule");
		moduleEl.setAttribute("title", centralModule.getTitle());
		moduleEl.setAttribute("url", centralModule.getUrl());
		moduleEl.setAttribute("image", centralModule.getImage());
		moduleEl.setAttribute("orderNumber", String.valueOf(centralModule.getOrderNumber()));
		parent.appendChild(moduleEl);
				
		for (CentralModuleItem centralModuleItem : centralModule.getItems()) {
			Element moduleItemEl = createEl(parent, "centralModuleItem");
			moduleItemEl.setAttribute("title", centralModuleItem.getTitle());
			moduleItemEl.setAttribute("url", centralModuleItem.getUrl());
			moduleEl.appendChild(moduleItemEl);
		}
		return moduleEl;
	}
	
	public static Element createEl(Node parent, String name)
    {
        Document doc;
        if(parent instanceof Document){
        	doc = (Document) parent;
        }else{
        	doc = parent.getOwnerDocument();
        }
        Element child =
            doc.createElementNS(NEGESO_NAMESPACE, "negeso:" + name);
        return child;
	}

	public String getWsUri() {
		return wsUri;
	}

	public void setWsUri(String wsUri) {
		this.wsUri = wsUri;
	}
}

