/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.generators.XmlHelper;
import com.negeso.framework.module.Module;
import com.negeso.framework.module.domain.ModuleItem;
import com.negeso.framework.module.ws.CentralModulesProviderClient;
import com.negeso.module.core.dao.IModuleDao;
import com.negeso.module.custom_consts.service.CustomConstsService;
import com.negeso.module.user.command.ManageContainersCommand;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class ModuleService {
	
	private static Logger logger = Logger.getLogger(ModuleService.class);
	
	private IModuleDao moduleDao;
	private CustomConstsService customConstsService;
	private CentralModulesProviderClient centralModulesProviderClient;
	private String siteIdentifier;
	
	public List<Module> getAllModules() {
		return moduleDao.getAllModules();
	}
	
	public List<Module> getAll() {
		return moduleDao.readAll();
	}
	
	public Module getModuleById(Long id) {
		return moduleDao.getModuleById(id);
	}

	public Long getModuleByName(String moduleName) {
		return moduleDao.getModuleByName(moduleName);
	}

	public IModuleDao getModuleDao() {
		return moduleDao;
	}

	public void setModuleDao(IModuleDao moduleDao) {
		this.moduleDao = moduleDao;
	}
	

	
	public Document getDocument(boolean isCustomer, RequestContext requestContext) throws CriticalException {	       
        try {
            Document doc = Env.createDom();
            Element modulesElement = Env.createDomElement(doc, "modules");
            doc.appendChild(modulesElement);
            for (Module module: getAll()) {
                Element element = Env.createDomElement(doc, "module");
                setItemAttributes(element, module, isCustomer);
                //add properties for security module
                if (module.getName().equals("security_module")){
                	element.setAttribute("security.containers.management.enabled", 
                			ManageContainersCommand.MANAGEMENT_ENABLED?"true":"false");
                }
                modulesElement.appendChild(element);
            }
            try {
            	//centralModulesProviderClient.buildCentralModulesXml(modulesElement, siteIdentifier, requestContext.getSession().getInterfaceLanguageCode(), null);
			} catch (Exception e) {
				logger.error("Unable to get modules from the central store");
			}
            return doc;
        }
        catch (Exception ex) {
            throw new CriticalException(ex);
        }
    }
	    
    private void setItemAttributes(Element itemElement, Module module, boolean isCustomer)
        throws SQLException
    {
        Timestamp golive = module.getGolive();
        Timestamp expired = module.getExpiredDate();
        Xbuilder.setAttr(itemElement, "id", module.getId());
        Xbuilder.setAttr(itemElement, "name", module.getName());
        XmlHelper.setDateAttribute(itemElement, "golive", golive);
        XmlHelper.setDateAttribute(itemElement, "expired", expired);
        itemElement.setAttribute("url", module.getUrl());
        itemElement.setAttribute("active", String.valueOf(
            Module.isActive(golive, expired)));
        itemElement.setAttribute("title", module.getTitle());
        itemElement.setAttribute("image", module.getImage());
        Xbuilder.setAttr(itemElement, "parametersCount", isCustomer ? module.getVisibleParametersCount() : module.getParametersCount());
        Xbuilder.setAttr(itemElement, "constsCount", module.getConstsCount());
        itemElement.setAttribute("dict_key", module.getDictionaryKey());
        for (ModuleItem moduleItem : module.getItems()) {
        	Element moduleItemEl = Env.createDomElement(itemElement.getOwnerDocument(), "moduleItem");
        	moduleItemEl.setAttribute("dict_key", moduleItem.getDictionaryKey());
        	moduleItemEl.setAttribute("url", moduleItem.getUrl());
        	moduleItemEl.setAttribute("order_number", moduleItem.getOrderNumber() + "");
        	Xbuilder.setAttr(moduleItemEl, "hide_from_user", moduleItem.isHideFromUser());
        	itemElement.appendChild(moduleItemEl);
		}
    }

	public CustomConstsService getCustomConstsService() {
		return customConstsService;
	}

	public void setCustomConstsService(CustomConstsService customConstsService) {
		this.customConstsService = customConstsService;
	}

	public void setCentralModulesProviderClient(
			CentralModulesProviderClient centralModulesProviderClient) {
		this.centralModulesProviderClient = centralModulesProviderClient;
	}

	public String getSiteIdentifier() {
		return siteIdentifier;
	}

	public void setSiteIdentifier(String siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

public String getHelpLink(String moduleLink){
		return moduleDao.getHelpLink(moduleLink);
	}
}

