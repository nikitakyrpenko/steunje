package com.negeso.framework.menu.command;

import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.menu.IMenuService;
import com.negeso.framework.menu.bo.Menu;
import com.negeso.framework.menu.service.MenuService;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;

public class DeleteMenuItemCommand extends AbstractCommand {
	
	private static Logger logger = Logger.getLogger(DeleteMenuItemCommand.class);
		
	@SuppressWarnings("unchecked")
	public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_FAILURE);
        Map responseMap = response.getResultMap();
        User user = getRequestContext().getSession().getUser();                
        Boolean isDeletePage = Boolean.valueOf(getRequestContext().getString("deletePage", "false"));
        try {
        	deleteMenuItem(user, getRequestContext().getLong("id"), isDeletePage);
            response.setResultName(RESULT_SUCCESS);
        } catch(Exception e){
            logger.error("", e);
            responseMap.put(
        		OUTPUT_ERROR, "Cannot delete the menu item. " + e.getMessage());
        }
        logger.debug("-");
        return response;
	}
	
    /**
     * Deletes menu item from table menu_item.
     * If the containing menu has no other items, it is deleted as well
     * (however, top menu is never deleted)
     * 
     * @param miId field <b>id</b> in table <b>menu_item</b>
     * @return true (success); if fails, an exception is thrown
     * @throws SQLException 
     */
    public void deleteMenuItem(User user, Long mId, Boolean isDeletePage) throws Exception {
        logger.debug("+");
        IMenuService menuService = MenuService.getSpringInstance();
        Menu menu = menuService.readMenu(mId);        
        Long pageId = menu.getPageId();
        Long cid = menu.getContainerId();        
        PageH page = null;
        PageService pageService = PageService.getInstance(); 
        if(pageId != null) {
        	page = pageService.findById(pageId);                        
        }
        if(!SecurityGuard.canManage(user, cid)) {
            throw new Exception("You are not authorized for this operation");
        }    
        if (page != null && "nodelete".equals(page.getProtected_()) && isDeletePage) {
            throw new Exception("Page is protected from deletion");
        }
        menuService.deleteMenu(mId);
        if (isDeletePage && pageId != null){
        	pageService.delete(pageId);
        	menuService.deleteMenuByPageId(pageId);
        }
        logger.debug("-");
    }

}