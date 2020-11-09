/*
 * @(#)$Id: ComponentManager.java,v 1.6, 2007-01-31 11:35:32Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.controller.ModuleEngineDispatcherServlet;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.Xquery;
import com.negeso.framework.module.engine.RuntimeModule;
import com.negeso.framework.util.Timer;

/**
 * 
 * Page component manager. Responsible for: 
 * - bulding components xml;
 * - component configuration management;
 * - caching;    
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 7$
 *
 */
public class ComponentManager {

	/** Key to pass page mode (end-user or admin) to page element  */
	public static final String RUNTIME_PARAM_VISITOR_MODE = "runtime-visitor-mode";
	
	/** Key to pass filename of the page being built to page element */
	public static final String RUNTIME_PARAM_FILENAME = "runtime-filename";
	
	/** Map: [component logical name => component class name] */
	private Map<String, String> component2ClassMap = null; 

	private Map<Long, PageComponentRecord[]> pageComponentCache = null; 
	
	private static Logger logger = Logger.getLogger(ComponentManager.class);
	
	private static ComponentManager INSTANCE = new ComponentManager();
	
	public static ComponentManager get() { return INSTANCE; }
	
	public String resolveComponentClassName(String componentName) {
		logger.debug("+");
		if (component2ClassMap == null) {
			logger.info("loading cache of page components configuraion");
			component2ClassMap = new HashMap<String, String>();

			//read page components from application.xconf 
			List<Element> elems = Xquery.elems(
					ResourceMap.getDom("APPLICATION_CONFIG"), "//page-components/page-component");
			for (Element el : elems)
				component2ClassMap.put(el.getAttribute("name"), el.getAttribute("class"));

			// add page components from runtime module
			component2ClassMap.putAll(RuntimeModule.getModule().getPageComponents());
		}
		logger.debug("-");
		return component2ClassMap.get(componentName);
	}

	public synchronized void addPageComponents(PageH page, RequestContext req, Element elPage, boolean isVisitorMode) {
        logger.debug("+");
        Document doc = elPage.getOwnerDocument();
        Element contents = Xbuilder.addEl(elPage, "contents", null);
            
        Connection conn = null;
        try {
//		    PageComponentRecord[] components = getPageComponentCache().get(page.getId());
		    PageComponentRecord[] components = null;
		    conn = DBHelper.getConnection();
		    if (components == null) {
		        components = PageComponentRecord.getPageComponents(conn, page.getId());
		        //getPageComponentCache().put(page.getId(), components);
		    }
            for(int i = 0; components != null && i < components.length; i++) {
                String componentName = components[i].getName();
                PageComponent component = getComponent(req, componentName);
                if (component == null) {
                	continue;
                }
                Map params = components[i].getParameters(conn);
                params.put(RUNTIME_PARAM_VISITOR_MODE, Boolean.toString(isVisitorMode));
                contents.appendChild(component.getElement(doc, req, params));
            }
            logger.debug("-");
            return;
        } catch(Exception e) {
            logger.error("- Cannot get page components", e);
            return;
        } finally {
            DBHelper.close(conn);
        }
    }
	
	public PageComponent getComponent(RequestContext request, String componentName) {
		PageComponent component = null;
		String className = resolveComponentClassName(componentName);
		if (className != null) {
			try {
				component = (PageComponent) Class.forName(className).newInstance();
				return component;
			} catch (Exception e) {
				logger.error(e);
			}
		}
		try {
			component = (PageComponent)WebApplicationContextUtils.getWebApplicationContext(request.getSessionData().getServletContext()).getBean(componentName);
			return component;
		} catch (Exception e) {
			try {
				int endIndex = componentName.indexOf('-');
				if (endIndex == -1)
					throw new IllegalArgumentException("Not a spring module");
				String module = componentName.substring(0, endIndex);
				component = (PageComponent) DispatchersContainer.getInstance().getBean(module, componentName);
				return component;
			}catch (IllegalArgumentException ex){
				logger.warn("Component not found by name: " + componentName);
			}catch (Exception ex){
				logger.error("Component not found by name: " + componentName, ex);
			}
		}

		return null;
	}
	
	public synchronized void cleanCache() {
		component2ClassMap = null;
		pageComponentCache = null;
	}

	private Map<Long, PageComponentRecord[]> getPageComponentCache() {
		if (pageComponentCache == null) {
			pageComponentCache = new HashMap<Long, PageComponentRecord[]>();
		}
		return pageComponentCache;
	}

	public Map<String, String> getComponent2ClassMap() {
		return component2ClassMap;
	}
}
