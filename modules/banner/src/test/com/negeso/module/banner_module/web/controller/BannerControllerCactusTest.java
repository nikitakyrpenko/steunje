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

import java.util.HashMap;
import java.util.Map;

import org.apache.cactus.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.BaseControllerTestCase;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.banner_module.bo.Banner;
import com.negeso.module.banner_module.dao.BannerDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class BannerControllerCactusTest extends BaseControllerTestCase{
	private BannerController controller;
	private BannerDao bannerDao;
	
	private static final String TEST_SQL_DELETE = "DELETE FROM bm_banner WHERE title = 'BANNER_CACTUS_TEST'";
		
	protected void setUp() throws Exception {
        super.setUp();
        controller = (BannerController) dc.getBean("banner_module", "bannerController");
        bannerDao = (BannerDao) dc.getBean("banner_module", "bannerDao");
    }
	
	protected void tearDown() {
    	controller = null;
    	bannerDao = null;
    }
	
	public void beginOnSubmit(WebRequest request){
		addParamsToRequest(request, getTestBannerMap());
	}
	
	public void testOnSubmit() throws Exception{
		try {
			ModelAndView modelAndView = controller.handleRequest(request, response);
			bannerDao.flush();
			assertEquals("bm_editbanner", modelAndView.getViewName());
			
			Banner banner = (Banner)modelAndView.getModel().get("banner");
			assertEquals("BANNER_CACTUS_TEST", banner.getTitle());
			assertEquals(true, banner.getActivated());
			assertEquals(new Long(2), banner.getCategoryId());
			assertEquals(new Long(1), banner.getBannerTypeId());
			assertEquals(new Long(1), banner.getImageType());
			assertEquals("TEST_URL", banner.getImageUrl());
			assertEquals("TEST_URL", banner.getUrl());
			assertEquals(new Long(7), banner.getPriority());
			assertEquals(new Long(7), banner.getMaxClicks());
			assertEquals(new Long(7), banner.getMaxViews());
			assertEquals(true, banner.getInNewWindow());
			
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}
	}
	
	public void beginOnSubmitEmptyTitle(WebRequest request){
		Map<String, String> map = getTestBannerMap();
		map.put("title", "");
		addParamsToRequest(request, map);
	}
	
	public void testOnSubmitEmptyTitle() throws Exception{
		try {
			ModelAndView modelAndView = controller.handleRequest(request, response);
			bannerDao.flush();
			assertEquals("bm_editbanner", modelAndView.getViewName());
			assertNotNull(request.getAttribute("errs"));
			
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}
	}
	
	private Map<String, String> getTestBannerMap(){
		return new HashMap<String, String>(){{
			put("title", "BANNER_CACTUS_TEST");
			put("categoryId", "2");
			put("bannerTypeId", "1");
			put("imageUrl", "TEST_URL");
			put("priority", "7");
			put("url", "TEST_URL");
			put("imageType", "1");
			put("maxClicks", "7");
			put("maxViews", "7");
			put("inNewWindow", "true");
			put("activated", "on");
		}};
	}
	
	private void addParamsToRequest(WebRequest request, Map<String, String> paramMap){
		for (String key : paramMap.keySet()) {
			request.addParameter(key, paramMap.get(key), WebRequest.POST_METHOD);
		}
	}
}

