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
package com.negeso.module.core.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.BriefPage;
import com.negeso.module.core.service.CsvBuilder;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SiteOverviesController extends AbstractController {
	
	private static Logger logger = Logger.getLogger(SiteOverviesController.class);
	
	private static final String VIEW = "site_overvies"; 
	private static final String ACTION = "act";
	private static final String ACTION_EXPORT = "export";
	private static final String SORT_BY = "sortBy";
	private static final String SORT_DIRECTION = "sortDirection";
	private static final String FILTER_LANG = "filterLangId";
	private static final String DEFAULT_SORT = "filename";
	
	private static String[] header = {"Page name", "Edit user", "Edit date", "Publish date", "Expired date", "Page template", "Page group"};
	
	private static final String selectSQL = 
			" SELECT filename, edit_user, edit_date, publish_date, expired_date, class, name as group, is_sitemap, sitemap_prior, sitemap_freq " +
			" FROM page " +
			" LEFT JOIN containers ON containers.id = page.container_id ";
			

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if (ACTION_EXPORT.equals(request.getParameter(ACTION))) {
			export(request,response);
			return null;
		}
		
		ModelAndView modelAndView = new ModelAndView(VIEW);
		
		String sortBy = ServletRequestUtils.getStringParameter(request, SORT_BY, DEFAULT_SORT);
		boolean sortDirection = ServletRequestUtils.getBooleanParameter(request, SORT_DIRECTION, true);
		Long filterlangId = ServletRequestUtils.getLongParameter(request, FILTER_LANG,0L);
		modelAndView.addObject(SORT_BY, sortBy);
		modelAndView.addObject(SORT_DIRECTION, sortDirection);
		modelAndView.addObject(FILTER_LANG, filterlangId);
		modelAndView.addObject("languages", Language.getItems());
		
		
		RequestUtil.getHistoryStack(request).push( new Link("CM_SITE_OVERVIES", 
				"/admin/" + VIEW, true));
		
		modelAndView.addObject("pages", listPages(sortBy,sortDirection,filterlangId));
		
		return modelAndView;
	}
	
	private List<BriefPage>  listPages(String sortBy, boolean sortDirection, Long filterlangId) {
		
		List<BriefPage> list = new ArrayList<BriefPage>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			String sql = selectSQL ;
			if(filterlangId != null && filterlangId > 0L){
				sql += " WHERE page.lang_id = ?"; 
			}
			stmt = conn.prepareStatement(sql + " ORDER BY " + sortBy + (sortDirection ? " ASC" : " DESC"));
			
			if(filterlangId != null && filterlangId > 0L){
				stmt.setLong(1, filterlangId);
			}
			rs = stmt.executeQuery();
			while (rs.next()) {
				BriefPage briefPage = new BriefPage();
				briefPage.setName(rs.getString("filename"));
				briefPage.setEditUser(rs.getString("edit_user"));
				briefPage.setEditDate(rs.getTimestamp("edit_date"));
				briefPage.setPublishDate(rs.getTimestamp("publish_date"));
				briefPage.setTemplate(rs.getString("class"));
				briefPage.setPageGroup(rs.getString("group"));
				briefPage.setSitemap(rs.getBoolean("is_sitemap"));
				briefPage.setSitemapPrior(rs.getString("sitemap_prior"));
				briefPage.setSitemapFreq(rs.getString("sitemap_freq"));
				list.add(briefPage);
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			DBHelper.close(rs, stmt, conn);
		}
		return list;
	} 
	
	public void export(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String sortBy = ServletRequestUtils.getStringParameter(request, SORT_BY, DEFAULT_SORT);
		boolean sortDirection = ServletRequestUtils.getBooleanParameter(request, SORT_DIRECTION, true);
		Long FilterlangId = ServletRequestUtils.getLongParameter(request, FILTER_LANG,0L);
		
		List<BriefPage> pages = listPages(sortBy,sortDirection,FilterlangId);
		String[][] data = new String[pages.size()][7];
		int i = 0;
		for (BriefPage page : pages) {
			data[i][0] = page.getName();
			data[i][1] = page.getEditUser();
			data[i][2] = Env.formatRoundDate(page.getEditDate());
			data[i][3] = Env.formatRoundDate(page.getPublishDate());
			data[i][4] = Env.formatRoundDate(page.getEditDate());
			data[i][5] = page.getTemplate();
			data[i++][6] = page.getPageGroup();
		}
		File file = new CsvBuilder().getFile("pages", header, data);
		int fSize = (int) file.length();
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));		

		response.setBufferSize((int)file.length());
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		response.setContentLength(fSize);

		FileCopyUtils.copy(in, response.getOutputStream());
		in.close();
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}		

}

