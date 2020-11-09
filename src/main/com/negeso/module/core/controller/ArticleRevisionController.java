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

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.User;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.ArticleRevision;
import com.negeso.module.core.service.ArticleRevisionService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ArticleRevisionController extends AbstractController{
	
	private static final String ACTION = "action";
	private static final String ACTION_DETAILS = "details";
	private static final String ARTICLE = "article";
	private static final String ID = "id";
	
	private ArticleRevisionService articleRevisionService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(request, ID, -1L);
		if (ACTION_DETAILS.equals(request.getParameter(ACTION))) {
			String text = StringUtils.EMPTY;
			if (id > 0) {
				text = articleRevisionService.find(id).getText();
			}
			response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
	        out.print(text);
	        out.close();
	        return null;
		} else if(ARTICLE.equals(request.getParameter(ACTION))) {
			Article article = Article.findById(id);
			response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
	        out.print(article.getText());
	        out.close();
	        return null;
		}
		List<ArticleRevision> articleRevisions = articleRevisionService.listByArticleId(id);
		if (articleRevisions.isEmpty()) {
			Article article = Article.findById(id);
			
			ArticleRevision articleRevision = new ArticleRevision(article.getId(), article.getText());
			articleRevision.setAuthor(getUserLogin(request));
			articleRevisionService.create(articleRevision);
			
			articleRevisions.add(articleRevision);
		}
		return new PreparedModelAndView("rte_articleRevisions")
				.addToModel("articleRevisions", articleRevisions)
				.get();
	}

	public ArticleRevisionService getArticleRevisionService() {
		return articleRevisionService;
	}

	public void setArticleRevisionService(
			ArticleRevisionService articleRevisionService) {
		this.articleRevisionService = articleRevisionService;
	}
	
	private String getUserLogin (HttpServletRequest request) {
		User user = getUser(request);
		if (user != null) {
			if (user.isSuperUser()) {
				return user.getSuperuserLogin();
			}
			return user.getLogin();
		}
		return null;
	}
	
	private User getUser(HttpServletRequest request) {
		logger.debug("+");
		Object user = request.getSession().getAttribute(SessionData.USER_ATTR_NAME);
		if (!(user instanceof User)) {
			logger.debug("- user is not set");
			return null;
		}
		logger.debug("- user is set");
		return (User) user;
	}

}

