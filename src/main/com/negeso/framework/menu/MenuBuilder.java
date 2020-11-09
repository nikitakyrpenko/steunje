package com.negeso.framework.menu;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.menu.bo.JaxbMenuWraper;
import com.negeso.framework.menu.bo.Menu;
import com.negeso.framework.security.SecurityCache;
import com.negeso.framework.security.SecurityGuard;


public class MenuBuilder {
    
     /********************************************************************
     * Build menu XML tree with checking of authorization.
     * When truncating is on, menu will be truncated
     * if current date &gt; menuitem.expired_date
     * or current date &lt; menuitem.publish date
     * @param id
     * @param selectedMenuItem
     * @param user
     ********************************************************************/
   
	private static Logger logger = Logger.getLogger(SecurityCache.class);
	
    public Element buildMenuXML(Element parent, User user, List<Menu> topMenu, boolean truncate, String langCode){
    	JaxbMenuWraper menuWraper = new JaxbMenuWraper();
    	menuWraper.setTruncate(truncate);
    	menuWraper.setLangCode(langCode);
    	Element menuXml = Xbuilder.addBeanJAXB(parent, menuWraper);
    	for (Menu menu : topMenu) {    		
    		String role = SecurityGuard.resolveRole(user, menu.getContainerId());
    		if(role == null){
    			if(!menu.getForceVisibility()) { 
                    logger.debug("- access to the menu item is denied");
                    return menuXml;
                }
            }
    	    buildMenuItemXML(menuXml, user, menu);
		}    	
    	return menuXml;
    }
    
    public Element buildMenuItemXML(Element parent, User user, Menu topMenu){
    	Element menuXml = Xbuilder.addBeanJAXB(parent, topMenu);    	
    	for (Menu item : topMenu.getMenuItems()) {    		
    		String role = SecurityGuard.resolveRole(user, item.getContainerId());
    		if(role == null){
    			if(!item.getForceVisibility()) { 
                    logger.debug("- access to the menu item is denied");
                    continue;
                }
            }
    	    buildMenuItemXML(menuXml, user, item);    	    
		}    	
    	return menuXml;    	
    }
    
}
