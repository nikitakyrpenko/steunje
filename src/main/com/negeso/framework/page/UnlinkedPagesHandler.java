/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.page;

import java.awt.MenuItem;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.site_map.PageDescriptor;
import com.negeso.framework.site_map.PagesHandler;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class UnlinkedPagesHandler implements PagesHandler {
	
	Logger logger = Logger.getLogger(UnlinkedPagesHandler.class);

	@Override
	public PageDescriptor getPages(Long langId, Object... args) {
		logger.debug("+");
		PageDescriptor root = null;
		Connection conn = null; 
		try{
			conn = DBHelper.getConnection();
			root = new PageDescriptor(null, "Unlinked pages", false, false, null ,null, null, null);
			addUnlinkedPagesToDescriptor(conn, root, langId);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBHelper.close(conn);
		}
		
		logger.debug("-");
		return root;
	}
	
	private void addUnlinkedPagesToDescriptor(Connection conn, PageDescriptor page, Long langId){
		logger.debug("+");		
		
		page.setLeaf(false);
		List<PageDescriptor> pages = new ArrayList<PageDescriptor>();
		List<PageH> p = PageService.getInstance().listUnlinkedPages(langId);
		if (p != null && p.size() > 0){
			for (PageH pageH : p) {
				PageDescriptor pageDescriptor = new PageDescriptor(
						pageH.getId(), 
						pageH.getTitle(),
						true,
						!pageH.isDateOk(),
						pageH.getId(),
						null,
						pageH.getFilename(),
						null);
				//addPagesToDescriptor(conn, pageDescriptor, langId);
				pages.add(pageDescriptor);
			}
			page.setContent(pages);			
		}else{
			page.setLeaf(true);
		}
		logger.debug("-");
	}

	@Override
	public String getPage(Long langId) {
		return null;
	}

}

