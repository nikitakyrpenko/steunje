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

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class TypeListControllerCactusTest extends BaseControllerTestCase{
	
	private TypeListController controller;
	
	private static final String CACTUS_TEST_BANNER_TYPE = "CACTUS_TEST_BANNER_TYPE";
	
	private static final String TEST_SQL_INSERT = "INSERT into bm_type (id, title, width, height) VALUES (99999, " +
			"'" + CACTUS_TEST_BANNER_TYPE + "', 10, 10)";
	
	private static final String TEST_SQL_DELETE = "DELETE FROM bm_type WHERE title like '%" + CACTUS_TEST_BANNER_TYPE + "%'";
	
	private static final String TEST_SQL_INSERT_BANNER = 
		"INSERT INTO bm_banner " +
			"	(id, title, category_id, type_id, image_url, priority, url, max_clicks, max_views, new_window, activated, image_type) " +
			"	VALUES (0, 'BANNER_CACTUS_TEST', 2, 99999, 'TEST_URL',7, 'TEST_URL', 7, 7, true, true, 1)";
	
	private static final String TEST_SQL_DELETE_BANNER = "DELETE FROM bm_banner WHERE id = 0";
	
	protected void setUp() throws Exception {
        super.setUp();
        controller = (TypeListController) dc.getBean("banner_module", "typeListController");
    }
	
	protected void tearDown() {
    	controller = null;
    }
	
	public void beginAddType(WebRequest request){
		request.addParameter("action", "addType", WebRequest.POST_METHOD);
	}
	
	public void testAddType() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		BannerType bannerType = (BannerType)modelAndView.getModel().get("type");
		assertNotNull(bannerType);
		assertEquals(new Long(-1), bannerType.getId());
		assertEquals("bm_edittype", modelAndView.getViewName());
	}
	
	public void beginEditType(WebRequest request){
		request.addParameter("action", "editType", WebRequest.POST_METHOD);
		request.addParameter("typeId", "99999", WebRequest.POST_METHOD);
	}
	
	public void testEditType() throws Exception{
		try {
			DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);
			ModelAndView modelAndView = controller.handleRequest(request, response);
			
			BannerType bannerType = (BannerType)modelAndView.getModel().get("type");
			assertNotNull(bannerType);
			assertEquals(new Long(99999), bannerType.getId());
			assertEquals(10, bannerType.getHeight());
			assertEquals(10, bannerType.getWidth());
			assertEquals(CACTUS_TEST_BANNER_TYPE, bannerType.getTitle());
			assertEquals("bm_edittype", modelAndView.getViewName());
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}
	}
	
	public void beginEditTypeEmptyTypeId(WebRequest request){
		request.addParameter("action", "editType", WebRequest.POST_METHOD);
	}
	
	public void testEditTypeEmptyTypeId() throws Exception{
		try {
			controller.handleRequest(request, response);
			fail(FAIL);
		} catch (Exception e) {}
	}
	
	public void beginDeleteType(WebRequest request){
		request.addParameter("action", "deleteType", WebRequest.POST_METHOD);
		request.addParameter("typeId", "99999", WebRequest.POST_METHOD);
	}
	
	public void testDeleteType() throws Exception{
		try {
			DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);
			ModelAndView modelAndView = controller.handleRequest(request, response);
			
			List<BannerType> bannerTypeList = (List<BannerType>) modelAndView.getModel().get("typelist");
			assertNotNull(modelAndView);
			assertEquals("bm_typelist", modelAndView.getViewName());
			assertNotNull(bannerTypeList);
			
			boolean isTestBannerTypeInList = false;
			for (BannerType bannerType : bannerTypeList) {
				if (bannerType.getTitle().equals(CACTUS_TEST_BANNER_TYPE)){
					isTestBannerTypeInList = true;
				}
			}
			assertEquals(false, isTestBannerTypeInList);
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}		
	}
	
	public void beginDeleteTypeWithConnectedBanner(WebRequest request){
		request.addParameter("action", "deleteType", WebRequest.POST_METHOD);
		request.addParameter("typeId", "99999", WebRequest.POST_METHOD);
	}
	
	public void testDeleteTypeWithConnectedBanner() throws Exception{
		try {
			DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);
			DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT_BANNER);
			ModelAndView modelAndView = controller.handleRequest(request, response);
			
			assertEquals("BM.TYPE_CANNOT_BE_DELETED", request.getAttribute("error_message"));
			assertEquals("bm_typelist", modelAndView.getViewName());
			assertNotNull(modelAndView.getModel().get("typelist"));
		} catch (Exception e) {
			fail(FAIL);
		}finally{
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE_BANNER);
			DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
		}
	}
	
	public void beginShowTypeList(WebRequest request){
		request.addParameter("action", "showTypeList", WebRequest.POST_METHOD);
	}
	
	public void testShowTypeList() throws Exception{
		ModelAndView modelAndView = controller.handleRequest(request, response);
		
		assertNotNull(modelAndView);
		assertEquals("bm_typelist", modelAndView.getViewName());
		assertNotNull(modelAndView.getModel().get("typelist"));
	}

}

