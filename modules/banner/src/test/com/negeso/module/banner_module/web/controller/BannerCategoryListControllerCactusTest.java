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
package com.negeso.module.banner_module.web.controller;

import java.util.Map;

import org.apache.cactus.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.BaseControllerTestCase;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.banner_module.bo.BannerCategory;
import com.negeso.module.banner_module.dao.BannerCategoryDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class BannerCategoryListControllerCactusTest extends BaseControllerTestCase{
	
	private BannerCategoryListController controller;
	private BannerCategoryDao bannerCategoryDao;
		
	protected void setUp() throws Exception {
        super.setUp();
        controller = (BannerCategoryListController) dc.getBean("banner_module", "bannerCategoryListController");
        bannerCategoryDao = (BannerCategoryDao) dc.getBean("banner_module", "bannerCategoryDao");
    }
	
	protected void tearDown() {
    	controller = null;
    	bannerCategoryDao = null;
    }
	
	private void addActionAndCatId(WebRequest request, String action, String categoryId){
		request.addParameter("action", action, WebRequest.POST_METHOD);
    	request.addParameter("categoryId", categoryId, WebRequest.POST_METHOD);
	}
	
	private void addActionAndParentCatId(WebRequest request, String action, String categoryId){
		request.addParameter("action", action, WebRequest.POST_METHOD);
    	request.addParameter("parentId", categoryId, WebRequest.POST_METHOD);
	}
	
	public void beginAddCategory(WebRequest request){
		addActionAndParentCatId(request, "addCategory", "1");
	}
	
	public void testAddCategory() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		assertEquals("bm_editcategory", modelAndView.getViewName());
		assertEquals(1, ((BannerCategory)modelAndView.getModel().get("category")).getParentId().longValue());
	}
	
	public void beginEditCategory(WebRequest request){
		addActionAndCatId(request, "editCategory", "1");
    }
	
	public void testEditCategory() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		assertEquals("bm_editcategory", modelAndView.getViewName());
		assertEquals(1, ((BannerCategory)modelAndView.getModel().get("category")).getId().longValue());
	}
	
	public void beginEditCategoryEmptyId(WebRequest request){
		addActionAndCatId(request, "editCategory", "");
    }
	
	public void testEditCategoryEmptyId() throws Exception{
		try {
			controller.handleRequest(request, response);
			fail(FAIL);
		} catch (Exception e) {}
	}
	
	public void beginEditCategoryNegativeId(WebRequest request){
		addActionAndCatId(request, "editCategory", "-1");
    }
	
	public void testEditCategoryNegativeId() throws Exception{
		try {
			controller.handleRequest(request, response);
			fail(FAIL);
		} catch (Exception e) {}
	}
	
	public void beginNOTEmptyDeleteCategory(WebRequest request){
		addActionAndCatId(request, "deleteCategory", "1");
    }
	
	public void testNOTEmptyDeleteCategory() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		assertEquals("BM.CATEGORY_CANNOT_BE_DELETED",request.getAttribute("error_message"));
		assertEquals("bm_categorylist", modelAndView.getViewName());
		assertNotNull(modelAndView.getModel().get("categorylist"));
	}
	
	public void beginDeleteCategory(WebRequest request){
		addActionAndCatId(request, "deleteCategory", "0");
    }
	
	public void testDeleteCategory() throws Exception{
		ModelAndView modelAndView = null;
		
		DBHelper.getJdbcTemplate().execute("INSERT INTO bm_category (id,  title, is_leaf, site_id) VALUES (0, 'TEST CATEGORY', true, 1)");
		try {
			modelAndView = controller.handleRequest(request, response);
			bannerCategoryDao.flush();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		DBHelper.getJdbcTemplate().execute("DELETE FROM bm_category where id = 0");
		
		assertNull(request.getAttribute("error_message"));
		assertEquals("bm_categorylist", modelAndView.getViewName());
		assertNotNull(modelAndView.getModel().get("categorylist"));
	}
	
	public void beginDeleteCategoryEmptyId(WebRequest request){
		addActionAndCatId(request, "deleteCategory", "");
    }
	
	public void testDeleteCategoryEmptyId() throws Exception{
		try {
			controller.handleRequest(request, response);
			fail("This part of code must not be reached");
		} catch (Exception e) {}
	}
	
	public void beginDeleteCategoryNegativeId(WebRequest request){
		addActionAndCatId(request, "deleteCategory", "-1");
    }
	
	public void testDeleteCategoryNegativeId() throws Exception{
		try {
			controller.handleRequest(request, response);
			fail(FAIL);
		} catch (Exception e) {}
	}
	
	public void testDeleteForGetSessionViewParams() throws Exception{
		request.getSession().setAttribute("filter_priority", "1");
		request.getSession().setAttribute("filter_expired", "no");
		request.getSession().setAttribute("order_field", "priority");
		request.getSession().setAttribute("order_direction", "desc");
		
		Map<String, Object> params = controller.getSessionViewParams(request);
		
		assertEquals(1L, ((Long)params.get("filter_priority")).longValue());
		assertEquals(false, ((Boolean)params.get("filter_expired")).booleanValue());
		assertEquals("priority", params.get("order_field"));
		assertEquals(false, ((Boolean)params.get("order_direction")).booleanValue());
	}
	
	public void beginForCategoryContentNegativeParentId (WebRequest request){
		addActionAndParentCatId(request, "categoryContent", "-1");
	}
	
	public void testForCategoryContentNegativeParentId() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		assertEquals("bm_categorylist", modelAndView.getViewName());
		assertNotNull("categorylist", modelAndView.getModel().get("categorylist"));
	}
	
	public void beginForCategoryContent (WebRequest request){
		addActionAndParentCatId(request, "categoryContent", "6");
	}
	
	public void testForCategoryContent() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		assertEquals("bm_bannerlist", modelAndView.getViewName());
		assertNotNull(modelAndView.getModel().get("bannerlist"));
		assertNotNull(request.getAttribute("bannerclicks"));
		assertNotNull(request.getAttribute("bannerviews"));
	}
	
	public void beginForCategoryContentEmptyId (WebRequest request){
		addActionAndParentCatId(request, "categoryContent", "-100");
	}
	
	public void testForCategoryContentEmptyId() throws Exception{
		try {
			controller.handleRequest(request, response);
			fail(FAIL);
		} catch (Exception e) {}
	}
	
}

