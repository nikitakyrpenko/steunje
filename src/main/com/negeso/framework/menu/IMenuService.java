package com.negeso.framework.menu;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

//import org.springmodules.cache.annotations.CacheFlush;
//import org.springmodules.cache.annotations.Cacheable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.menu.bo.Menu;

public interface IMenuService {
	
//	@Cacheable(modelId="menuCaching")
	public Document getMenuXml(Language language, User user);
	
//	@Cacheable(modelId="menuCaching")
	public Long determinateSelectedMenuId(String curPage, Long curItemId);
	
	public void getMenuExplorerXml(Element doc, boolean truncate, String langCode, User user);
	
//	@CacheFlush(modelId = "menuFlushing")
	public void saveMenu(Menu menu);
	
	public Menu readMenu(Long id);	
	public List<Menu> getMain(Long langId, Long siteId);	
	public Long getNextOrder(Long parentId);
	/**
     * Changes order of a menu item so that it would occupy the place of another
     * menu item (defined by targetMIid).
     * 
     * @param targetMIid    menu item to be positioned
     * @param menuItem      id of menu item that owns target position now 
     * @throws RemoteException in case of failure
     */
//	@CacheFlush(modelId = "menuFlushing")
	public void moveMenu(Menu curMenu, Boolean up);	
	/**
     * Deletes menu item from table menu_item.
     * If the containing menu has no other items, it is deleted as well
     * (however, top menu is never deleted)
     * 
     * @param miId field <b>id</b> in table <b>menu_item</b>
     * @return true (success); if fails, an exception is thrown
     * @throws SQLException 
     */
//	@CacheFlush(modelId = "menuFlushing")
	public void deleteMenu(Long menuId);	
	
//	@CacheFlush(modelId = "menuFlushing")
	public void deleteMenuByPageId(Long pageId);
		
//	@CacheFlush(modelId = "menuFlushing")
	public void flush();

}
