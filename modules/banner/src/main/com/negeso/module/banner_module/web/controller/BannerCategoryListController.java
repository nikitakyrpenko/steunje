/*
 * @(#)Id: CategoriesController.java, 13.12.2007 13:08:08, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.HistoryStack;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.banner_module.bo.BannerCategory;
import com.negeso.module.banner_module.service.BannerService;
import com.negeso.module.banner_module.service.BannerCategoryService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerCategoryListController extends MultiActionController {
	
	public static final String FILTER_PRIORITY = "filter_priority";
	public static final String FILTER_EXPIRED = "filter_expired";
	public static final String FILTER_ORDER = "order_field";
	public static final String FILTER_DIRECTION = "order_direction";
	
	private static Logger logger = Logger.getLogger(BannerCategoryListController.class);
	
	private BannerCategoryService bannerCategoryService;
	private BannerService bannerService;

	public ModelAndView addCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		BannerCategory category = new BannerCategory();
		String linkUrl = "/admin/bm_categorylist.html?action=addCategory";
	
		long pid = ServletRequestUtils.getLongParameter(request, "parentId", -1);
		if (pid > 0) {
			category.setParentId(pid);
			request.setAttribute("parentId", pid);
			linkUrl += "&parentId=" + pid;  
		}
			
		RequestUtil.getHistoryStack(request).push( new Link("New category",
				linkUrl, false) );
			
		logger.debug("-");
		return new ModelAndView("bm_editcategory", "category", category);	
	}
	
	public ModelAndView editCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		Long catId = ServletRequestUtils.getLongParameter(request, "categoryId");
		BannerCategory category = bannerCategoryService.findCategoryById(catId);
	
		RequestUtil.getHistoryStack(request).push( new Link(category.getTitle(), 
				"/admin/bm_categorylist.html?action=editCategory&categoryId="
				+ category.getId(), false) );
			
		logger.debug("-");
		return new ModelAndView("bm_editcategory", "category", category);
	}
	
	public ModelAndView deleteCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		Long catId = ServletRequestUtils.getLongParameter(request, "categoryId");
		BannerCategory category = bannerCategoryService.findCategoryById(catId);

		if (!bannerCategoryService.isContainBanners(catId)
				&& !bannerCategoryService.isContainCategories(catId)) {
			bannerCategoryService.deleteCategory(catId);
		} else {
			logger.debug("- cannot be deleted, not emptry");
			request.setAttribute("error_message", "BM.CATEGORY_CANNOT_BE_DELETED");
		}
		request.setAttribute("parentId", String.valueOf(category.getParentId()));
		logger.debug("-");
		return new ModelAndView("bm_categorylist", "categorylist",
				bannerCategoryService.findCategoriesByParentId(category.getParentId()));
	}
	
	public ModelAndView categoryContent(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("+");

		putParamsFromRequestToSession(request);

		long pid = ServletRequestUtils.getLongParameter(request, "parentId", -1);
		Map<String, Object> params = getSessionViewParams(request);
			
		if (pid == -1) {
			RequestUtil.getHistoryStack(request).push(new Link("Top category",
					"/admin/bm_categorylist.html", false, -1));
			return new ModelAndView("bm_categorylist", "categorylist",
					bannerCategoryService.findCategoriesByParentId(null));
		}

		request.setAttribute("parentId", String.valueOf(pid));
		BannerCategory category = bannerCategoryService.findCategoryById(pid);
		
		if (category.getIsLeaf()) {
			RequestUtil.getHistoryStack(request).push(new Link(category.getTitle(),
					"/admin/bm_categorylist.html?action=categoryContent&parentId="
					+ pid, false));
			request.setAttribute("bannerclicks", bannerService.getBannerClicksByCategoryId(pid));
			request.setAttribute("bannerviews", bannerService.getBannerViewsByCategoryId(pid));
			logger.debug("- is leaf, return banner list");
			return new ModelAndView("bm_bannerlist", "bannerlist",
					bannerService.findParametrizedBannerList(pid, params));
		}
		
		RequestUtil.getHistoryStack(request).push(
				new Link(category.getTitle(),
						"/admin/bm_categorylist.html?action=categoryContent&parentId="
						+ pid, false));
		logger.debug("- is parentcategory");
		return new ModelAndView("bm_categorylist", "categorylist",
				bannerCategoryService.findCategoriesByParentId(pid));
	}

	private void putParamsFromRequestToSession(HttpServletRequest request){
		request.getSession().setAttribute(FILTER_PRIORITY, request.getParameter(FILTER_PRIORITY));
		request.getSession().setAttribute(FILTER_EXPIRED, request.getParameter(FILTER_EXPIRED));
		request.getSession().setAttribute(FILTER_ORDER, request.getParameter(FILTER_ORDER));
		request.getSession().setAttribute(FILTER_DIRECTION, request.getParameter(FILTER_DIRECTION));
	}
	
	public static Map<String, Object> getSessionViewParams(HttpServletRequest request){
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		try{
			String filterPriority = (String)request.getSession().getAttribute(FILTER_PRIORITY);
			try{
				if (filterPriority != null && filterPriority.length() > 0){
					params.put(FILTER_PRIORITY, Long.parseLong(filterPriority));
                }
			}catch(Exception e){
				logger.debug("wrong priority: " +  e.getMessage());
			}
			
			String filterExpired = (String)request.getSession().getAttribute(FILTER_EXPIRED);
			if (filterExpired != null){
				if (filterExpired.trim().toLowerCase().equals("yes")){
					params.put(FILTER_EXPIRED, Boolean.TRUE);
				}else
				if (filterExpired.trim().toLowerCase().equals("no")){
					params.put(FILTER_EXPIRED, Boolean.FALSE);
				}
			}
			
			String orderBy = (String)request.getSession().getAttribute(FILTER_ORDER);
			if (orderBy != null && orderBy.length() > 0){
				params.put(FILTER_ORDER, orderBy.trim().toLowerCase());
			}
			
			String orderDirection = (String)request.getSession().getAttribute(FILTER_DIRECTION);
			if (orderDirection != null && orderDirection.length() > 0){
				if (orderDirection.trim().toLowerCase().equals("asc")){
					params.put(FILTER_DIRECTION, Boolean.TRUE);
				}else{
					params.put(FILTER_DIRECTION, Boolean.FALSE);
				}
			}
		}catch(Exception e){
			logger.error("error getting sorting params from session: " + e.getMessage());
		}
		return params;
	}
	
	public ModelAndView changeDirection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		Long pid = ServletRequestUtils.getLongParameter(request, "parentId");
		long cid = ServletRequestUtils.getRequiredLongParameter(request, "categoryId");
		
		String direction = request.getParameter("direction");
		boolean direct = "up".equals(direction);
		
		BannerCategory category = bannerCategoryService.findCategoryById(cid);
		logger.debug("cat=" + category);
		bannerCategoryService.changeDirection(category, direct);

		List<BannerCategory> categories = bannerCategoryService.findCategoriesByParentId(pid);
		request.setAttribute("parentId", pid);
		logger.debug("-");
		return new ModelAndView("bm_categorylist", "categorylist", categories);
	}

	public void setBannerCategoryService(BannerCategoryService bannerCategoryService) {
		this.bannerCategoryService = bannerCategoryService;
	}	
	
	public void setBannerService(BannerService bannerService) {
		this.bannerService = bannerService;
	}
}