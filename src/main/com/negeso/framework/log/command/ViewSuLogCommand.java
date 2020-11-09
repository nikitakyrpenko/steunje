/*
 * @(#)$Id: ViewSuLogCommand.java,v 1.0, 2006-11-14 15:00:06Z, Svetlana Bondar$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.log.command;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.ldap.LdapLogEntry;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.LdapLoginManager;

/**
 * 
 * @TODO
 * 
 * @author		Andrey V. Morskoy
 * @version		$Revision: 1$
 *
 */
public class ViewSuLogCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(ViewSuLogCommand.class);
    
    
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
        Element parentElement =   Xbuilder.createTopEl(
                "page"
        );
       
        Element wmLog = Xbuilder.addEl(parentElement, "WmLOG", null);
        User user = request.getSession().getUser();
      
        if (  !SecurityGuard.isSuperUser(user) ) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.debug("- Access denied");
            return response;
        }
        
        String curHost = request.getParameter("hostName");
        String loginFilter = request.getParameter("loginFilter");
        
        List<LdapLogEntry> log = LdapLoginManager.getDbLogCSV(curHost,loginFilter);
        
        buildHostList(wmLog,LdapLoginManager.getDbLogHosts(),curHost);
        for( LdapLogEntry entry: log){
        	buildElementFromBean(wmLog, entry);
        }
        
        Map<String,Object> resultMap = response.getResultMap();
        resultMap.put("xml", parentElement.getOwnerDocument());
        response.setResultName(RESULT_SUCCESS);
        logger.debug("-");
        
        return response;
    }
    
    
    
   
    
    
    
    private static void buildHostList(Element parentEl, List<String> list, String curHost){
    	logger.debug("+");
    	if( list==null ){
    		return ;
    	}
    	HashSet<String> hosts = new HashSet<String>();
    	Element hostsEl = Xbuilder.addEl(parentEl,"HostNames",null);
    	Element tmpEl;
    	for( String host : list ){
    		boolean unique = hosts.add(host);
    		if(unique){
    			tmpEl = Xbuilder.addEl(hostsEl,"Host",host);
    			if(host.equals(curHost)){
    				Xbuilder.setAttr(tmpEl,"isCurrent","true");
    			}
    		}
    	}
    	
    	logger.debug("-");
    }
    
    
    private static void buildElementFromBean(Element parentEl, Object bean) {
    	logger.debug("+");
    	BeanMap beanMap = new BeanMap(bean);
		Element element = Xbuilder.addEl(parentEl, bean.getClass().getSimpleName(), null);
		for (Object propName: beanMap.keySet()) {
			Object propVal = beanMap.get(propName);
			Xbuilder.setAttr( element, propName.toString(), propVal );
		}
		logger.debug("-");
	}
    
}
