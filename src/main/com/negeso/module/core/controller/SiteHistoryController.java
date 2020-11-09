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
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.core.domain.ArticleRevision;
import com.negeso.module.core.service.ArticleRevisionService;
import com.negeso.module.core.service.CsvBuilder;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SiteHistoryController extends AbstractController {
	
	private static Logger logger = Logger.getLogger(SiteHistoryController.class);
	
	private static final String DEFAULT_VIEW = "site_history";
	private static final String ACTION = "act";
	private static final String ACTION_DELETE_HISTORY = "deleteHistory";
	private static final String ACTION_EXPORT = "export";
	private static final String DATE_FIELD = "dateField";
	private static final String SORT_BY = "sortBy";
	private static final String SORT_DIRECTION = "sortDirection";
	
	private static SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DecimalFormat formattedId = new DecimalFormat("000000");
	
	private static String [] header = {"Revision", "Date", "Author", "Type", "Title"};
	
	private static final String HQL_DELETE_REVISIONS = "delete from ArticleRevision where date < :date";
	
	private String view = DEFAULT_VIEW;
	
	private ArticleRevisionService articleRevisionService;
	private SessionFactory sessionFactory;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(view);
		
		String sortBy = ServletRequestUtils.getStringParameter(request, SORT_BY, "date");
		boolean sortDirection = ServletRequestUtils.getBooleanParameter(request, SORT_DIRECTION, false);
		modelAndView.addObject(SORT_BY, sortBy);
		modelAndView.addObject(SORT_DIRECTION, sortDirection);
		
		if (ACTION_DELETE_HISTORY.equals(request.getParameter(ACTION)) && StringUtils.isNotEmpty(request.getParameter(DATE_FIELD))) {
			Date date = format.parse(request.getParameter(DATE_FIELD));
			Session session = sessionFactory.getCurrentSession();
	        Query query = session.createQuery(HQL_DELETE_REVISIONS);
	        query.setTimestamp("date", date);
	        int result = query.executeUpdate();
	        session.flush();
	        modelAndView.addObject("result", result);
		} else if (ACTION_EXPORT.equals(request.getParameter(ACTION))) {
			try {
				export(response, sortBy, sortDirection);
				return null;
			} catch (Exception e) {
				logger.error("Error during export to CSV", e);
			}
		}
		modelAndView.addObject("revisions", articleRevisionService.listRevisions(sortBy, sortDirection));
		
		RequestUtil.getHistoryStack(request).push( new Link("CM_SITE_HISTORY", 
				"/admin/" + view, true));
		
		return modelAndView;
	}

	private void export(HttpServletResponse response, String sortBy, Boolean sortDirection) throws IOException {
		List<ArticleRevision> revisions = articleRevisionService.listRevisions(sortBy, sortDirection);
		String[][] data = new String[revisions.size()][5];
		int i = 0;
		for (ArticleRevision articleRevision : revisions) {
			data[i][0] = formattedId.format(articleRevision.getId());
			data[i][1] = format.format(articleRevision.getDate());
			data[i][2] = articleRevision.getAuthor();
			data[i][3] = articleRevision.getType();
			data[i++][4] = articleRevision.getTitle();
		}
		File file = (new CsvBuilder()).getFile("site-history", header, data);
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

	public void setView(String view) {
		this.view = view;
	}

	public ArticleRevisionService getArticleRevisionService() {
		return articleRevisionService;
	}

	public void setArticleRevisionService(
			ArticleRevisionService articleRevisionService) {
		this.articleRevisionService = articleRevisionService;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}

