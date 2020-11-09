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

import org.apache.cactus.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.BaseControllerTestCase;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.banner_module.bo.BannerCategory;
import com.negeso.module.banner_module.dao.BannerCategoryDao;
import com.negeso.module.banner_module.service.BannerCategoryService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class BannerCategoryControllerCactusTest extends BaseControllerTestCase{
	
	private BannerCategoryController controller;
	private BannerCategoryService bannerCategoryService;
	private BannerCategoryDao bannerCategoryDao;
	private static final String TEST_CATEGORY_TITLE = "LyhozhonCategory";
	
	protected void setUp() throws Exception {
        super.setUp();
        controller = (BannerCategoryController) dc.getBean("banner_module", "bannerCategoryController");
        bannerCategoryService = (BannerCategoryService) dc.getBean("banner_module", "bannerCategoryService");
        bannerCategoryDao = (BannerCategoryDao) dc.getBean("banner_module", "bannerCategoryDao");
    }
	
	protected void tearDown() {		
    	controller = null;
    	bannerCategoryService = null;
    }
    
    public void beginOnSubmit(WebRequest request){
    	request.addParameter("id", "-1", WebRequest.POST_METHOD);
    	request.addParameter("title", TEST_CATEGORY_TITLE, WebRequest.POST_METHOD);
    	request.addParameter("isLeaf", "on", WebRequest.POST_METHOD);
    }
    
    public void testOnSubmit() throws Exception{
    	try{
    		ModelAndView modelAndView = controller.handleRequest(request, response);
    		bannerCategoryDao.flush();
    		assertNotNull(modelAndView);
    		
        	assertEquals("bm_categorylist", modelAndView.getViewName());
        	assertNotNull(modelAndView.getModel().get("categorylist"));
        	assertNull(request.getAttribute("errors"));
        	
    		BannerCategory bannerCategory = bannerCategoryService.findCategoryByTitleParentId(TEST_CATEGORY_TITLE, null);
        	assertEquals(TEST_CATEGORY_TITLE, bannerCategory.getTitle());
        	assertEquals(true, bannerCategory.getIsLeaf());
        	
    		bannerCategoryService.deleteCategory(bannerCategory.getId());
    		bannerCategoryDao.flush();
    	}catch (Exception e) {
			fail(e.getMessage());
		}finally{
			DBHelper.getJdbcTemplate().execute("DELETE FROM bm_category WHERE title = '" + TEST_CATEGORY_TITLE +"'");
		}
    	
    }

}

