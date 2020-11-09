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

import java.util.List;

import org.apache.cactus.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.BaseControllerTestCase;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.dao.BannerTypeDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class TypeControllerCactusTest extends BaseControllerTestCase{
	private TypeController controller;
	private BannerTypeDao  bannerTypeDao;
	
	private static final String CACTUS_TEST_BANNER_TYPE = "CACTUS_TEST_BANNER_TYPE";
	
	private static final String TEST_SQL_INSERT = "INSERT into bm_type (id, title, width, height) VALUES (99999, " +
			"'" + CACTUS_TEST_BANNER_TYPE + "_before_update" + "', 10, 10)";
	
	private static final String TEST_SQL_DELETE = "DELETE FROM bm_type WHERE title like '%" + CACTUS_TEST_BANNER_TYPE + "%'";
	
	protected void setUp() throws Exception {
        super.setUp();
        controller = (TypeController) dc.getBean("banner_module", "typeController");
        bannerTypeDao = (BannerTypeDao) dc.getBean("banner_module", "bannerTypeDao");
    }
	
	protected void tearDown() {
    	controller = null;
    	bannerTypeDao = null;
    }
	
	public void beginOnSubmit(WebRequest request){
		request.addParameter("title", CACTUS_TEST_BANNER_TYPE, WebRequest.POST_METHOD);
		request.addParameter("width", "50", WebRequest.POST_METHOD);
		request.addParameter("height", "50", WebRequest.POST_METHOD);
	}
	
	public void testOnSubmit() throws Exception{
		try {
			ModelAndView modelAndView = controller.handleRequest(request, response);
			bannerTypeDao.flush();
			
			validateModelAndView(modelAndView);
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}
	}
	
	public void beginOnSubmitForExistentType(WebRequest request){
		request.addParameter("id", "99999", WebRequest.POST_METHOD);
		request.addParameter("title", CACTUS_TEST_BANNER_TYPE , WebRequest.POST_METHOD);
		request.addParameter("width", "50", WebRequest.POST_METHOD);
		request.addParameter("height", "50", WebRequest.POST_METHOD);
	}
	
	public void testOnSubmitForExistentType() throws Exception{
		try {
			DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);
			
			ModelAndView modelAndView = controller.handleRequest(request, response);
			bannerTypeDao.flush();
			validateModelAndView(modelAndView);
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}
	}
	
	private void validateModelAndView(ModelAndView modelAndView){
		List<BannerType> bannerTypeList = (List<BannerType>) modelAndView.getModel().get("typelist");
		assertNotNull(modelAndView);
		assertEquals("bm_typelist", modelAndView.getViewName());
		assertNotNull(bannerTypeList);
		
		boolean isTestBannerTypeInList = false;
		BannerType bannerT = null;
		for (BannerType bannerType : bannerTypeList) {
			if (bannerType.getTitle().equals(CACTUS_TEST_BANNER_TYPE)){
				isTestBannerTypeInList = true;
				bannerT = bannerType;
			}
		}
		assertEquals(true, isTestBannerTypeInList);
		assertEquals(50, bannerT.getWidth());
		assertEquals(50, bannerT.getHeight());
	}

}

