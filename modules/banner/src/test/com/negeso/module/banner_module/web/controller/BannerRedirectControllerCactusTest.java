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
import org.springframework.web.servlet.view.RedirectView;

import com.negeso.BaseControllerTestCase;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.banner_module.dao.BannerStatisticsDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class BannerRedirectControllerCactusTest extends BaseControllerTestCase{
	
	private BannerRedirectController controller;
	private BannerStatisticsDao bannerStatisticsDao;
	
	private static final String TEST_SQL_INSERT = 
		"INSERT INTO bm_banner " +
			"	(id, title, category_id, type_id, image_url, priority, url, max_clicks, max_views, new_window, activated, image_type) " +
			"	VALUES (99999, 'BANNER_CACTUS_TEST', 2, 1, 'TEST_URL',7, 'TEST_URL', 7, 7, true, true, 1)";
	
	private static final String TEST_SQL_DELETE = "DELETE FROM bm_banner WHERE id = 99999"; 
	
	protected void setUp() throws Exception {
        super.setUp();
        controller = (BannerRedirectController) dc.getBean("banner_module", "bannerRedirectController");
        bannerStatisticsDao = (BannerStatisticsDao) dc.getBean("banner_module", "bannerStatisticsDao");
    }
	
	protected void tearDown() {
    	controller = null;
    	bannerStatisticsDao = null;
    }
	
	public void beginRedirect(WebRequest request){
		request.addParameter("action", "redirect", WebRequest.POST_METHOD);
		request.addParameter("id", "99999", WebRequest.POST_METHOD);
	}
	
	public void testRedirect() throws Exception{
		try {
			DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);
			
			ModelAndView modelAndView = controller.handleRequest(request, response);
			bannerStatisticsDao.flush();
			
			RedirectView red = (RedirectView)modelAndView.getView();
			assertEquals("TEST_URL", red.getUrl());
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}
	}
	
	public void beginRedirectEmptyId(WebRequest request){
		request.addParameter("action", "redirect", WebRequest.POST_METHOD);
	}
	
	public void testRedirectEmptyId() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		assertEquals("redirect:", modelAndView.getViewName());
	}
	
	public void beginRedirectNotExistentId(WebRequest request){
		request.addParameter("action", "redirect", WebRequest.POST_METHOD);
		request.addParameter("id", "999999", WebRequest.POST_METHOD);
	}
	
	public void testRedirectNotExistentId() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		assertEquals("redirect:", modelAndView.getViewName());
	}
	
}

