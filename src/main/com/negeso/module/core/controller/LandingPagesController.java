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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.page.PageH;
import com.negeso.module.core.PreparedModelAndView;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class LandingPagesController extends AbstractController{

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection cn = null;
		Statement st = null;
		ResultSet rs = null;
		List<PageH> pages = new ArrayList<PageH>();
		try {
			cn = DBHelper.getConnection();
			st = cn.createStatement();
			rs = st.executeQuery(
					" SELECT * FROM page " +
					" WHERE category IN ('page', 'frontpage', 'contentpage', 'simple') " +					
					" ORDER BY RANDOM() ");
			while (rs.next()) {
				pages.add(PageH.load(rs));
			}
		} catch (SQLException e) {
			logger.error("- Unsupported structure of table 'page'", e);
		} finally {
			DBHelper.close(rs, st, cn);
		}
		return new PreparedModelAndView("landing_pages")
					.addToModel("pages", pages)
					.addToModel("url", Env.getHostName())
					.get();
	}

}

