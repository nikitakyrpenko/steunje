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
import com.negeso.module.banner_module.dao.*;
import com.negeso.module.banner_module.bo.*;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class BannerListControllerCactusTest extends BaseControllerTestCase {

    private BannerListController controller;
    private BannerDao bannerDao;

    private static final String TEST_SQL_INSERT = "INSERT INTO bm_banner "
	    + "	(id, title, category_id, type_id, image_url, priority, url, max_clicks, max_views, new_window, activated, image_type) "
	    + "	VALUES (0, 'BANNER_CACTUS_TEST', 2, 1, 'TEST_URL',7, 'TEST_URL', 7, 7, true, true, 1)";

    private static final String TEST_SQL_DELETE = "DELETE FROM bm_banner WHERE id = 0";

    protected void setUp() throws Exception {
	super.setUp();
	controller = (BannerListController) dc.getBean("banner_module",
		"bannerListController");
	bannerDao = (BannerDao) dc.getBean("banner_module", "bannerDao");
    }

    protected void tearDown() {
	controller = null;
	bannerDao = null;
    }

    public void beginAddBanner(WebRequest request) {
	request.addParameter("action", "addBanner", WebRequest.POST_METHOD);
	request.addParameter("parentId", "2", WebRequest.POST_METHOD);
    }

    public void testAddBanner() throws Exception {
	ModelAndView modelAndView = controller.handleRequest(request, response);
	assertEquals(2, ((Banner) modelAndView.getModel().get("banner"))
		.getCategoryId().longValue());
	assertEquals("bm_editbanner", modelAndView.getViewName());
	assertEquals(new Long(2), request.getAttribute("parentId"));
    }

    public void beginAddBannerNegativeParentId(WebRequest request) {
	request.addParameter("action", "addBanner", WebRequest.POST_METHOD);
	request.addParameter("parentId", "-2", WebRequest.POST_METHOD);
    }

    public void testAddBannerNegativeParentId() throws Exception {
	ModelAndView modelAndView = controller.handleRequest(request, response);
	assertEquals(-2, ((Banner) modelAndView.getModel().get("banner"))
		.getCategoryId().longValue());
	assertEquals("bm_editbanner", modelAndView.getViewName());
	assertEquals(new Long(-2), request.getAttribute("parentId"));
    }

    public void beginAddBannerEmptyParentId(WebRequest request) {
	request.addParameter("action", "addBanner", WebRequest.POST_METHOD);
    }

    public void testAddBannerEmptyParentId() throws Exception {
	validateOnFail();
    }

    public void beginDuplicateBanner(WebRequest request) {
	request.addParameter("action", "duplicateBanner",
		WebRequest.POST_METHOD);
	request.addParameter("parentId", "2", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "0", WebRequest.POST_METHOD);
    }

    public void testDuplicateBanner() throws Exception {
	try {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);

	    ModelAndView modelAndView = controller.handleRequest(request,
		    response);
	    assertEquals("bm_editbanner", modelAndView.getViewName());
	    assertEquals("2", request.getAttribute("parentId"));
	    assertEquals(true, request.getAttribute("duplicate"));

	    Banner banner = (Banner) modelAndView.getModel().get("banner");
	    assertEquals("BANNER_CACTUS_TEST(copy)", banner.getTitle());
	    assertEquals(false, banner.getActivated());
	    validateCommonFields(banner);

	} catch (Exception e) {
	    fail(FAIL);
	} finally {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
	}
    }

    public void beginDuplicateBannerEmptyParams(WebRequest request) {
	request.addParameter("action", "duplicateBanner",
		WebRequest.POST_METHOD);
    }

    public void testDuplicateBannerEmptyParams() throws Exception {
	controller.handleRequest(request, response);
	assertNull(request.getAttribute("duplicate"));
    }

    public void beginDuplicateBannerNegativeParams(WebRequest request) {
	request.addParameter("action", "duplicateBanner",
		WebRequest.POST_METHOD);
	request.addParameter("parentId", "-1", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "-1", WebRequest.POST_METHOD);
    }

    public void testDuplicateBannerNegativeParams() throws Exception {
	controller.handleRequest(request, response);
	assertNull(request.getAttribute("duplicate"));
    }

    public void beginEditBanner(WebRequest request) {
	request.addParameter("action", "editBanner", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "0", WebRequest.POST_METHOD);
    }

    public void testEditBanner() throws Exception {
	try {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);

	    ModelAndView modelAndView = controller.handleRequest(request,
		    response);
	    assertEquals("bm_editbanner", modelAndView.getViewName());

	    assertNotNull(request.getAttribute("bannertype"));
	    assertNotNull(request.getAttribute("bannertypes"));
	    assertNotNull(request.getAttribute("bannergroups"));
	    assertNotNull(request.getAttribute("bannerpages"));
	    assertNotNull(request.getAttribute("bannerclicks"));
	    assertNotNull(request.getAttribute("bannerviews"));

	    Banner banner = (Banner) modelAndView.getModel().get("banner");
	    assertEquals("BANNER_CACTUS_TEST", banner.getTitle());
	    validateCommonFields(banner);
	    assertEquals(true, banner.getActivated());
	} catch (Exception e) {
	    fail(FAIL);
	} finally {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
	}
    }

    public void beginEditBannerEmptyBannerId(WebRequest request) {
	request.addParameter("action", "editBanner", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "", WebRequest.POST_METHOD);
    }

    public void testEditBannerEmptyBannerId() throws Exception {
	validateOnFail();
    }

    public void beginEditBannerNegativeBannerId(WebRequest request) {
	request.addParameter("action", "editBanner", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "-1", WebRequest.POST_METHOD);
    }

    public void testEditBannerNegativeBannerId() throws Exception {
	ModelAndView modelAndView = controller.handleRequest(request, response);

	assertNull(modelAndView.getModel().get("banner"));
	assertNull(request.getAttribute("bannerclicks"));
	assertNull(request.getAttribute("bannerviews"));
    }

    public void beginDeleteBanner(WebRequest request) {
	request.addParameter("action", "deleteBanner", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "0", WebRequest.POST_METHOD);
    }

    public void testDeleteBanner() throws Exception {
	try {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);

	    ModelAndView modelAndView = controller.handleRequest(request,
		    response);
	    bannerDao.flush();

	    assertEquals("bm_bannerlist", modelAndView.getViewName());
	    assertEquals(new Long(2), request.getAttribute("parentId"));
	} catch (Exception e) {
	    fail(FAIL);
	} finally {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
	}
    }

    public void beginDeleteBannerEmptyBannerId(WebRequest request) {
	request.addParameter("action", "deleteBanner", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "", WebRequest.POST_METHOD);
    }

    public void testDeleteBannerEmptyBannerId() throws Exception {
	validateOnFail();
    }

    public void beginDeleteBannerNegativeBannerId(WebRequest request) {
	request.addParameter("action", "deleteBanner", WebRequest.POST_METHOD);
	request.addParameter("bannerId", "-5", WebRequest.POST_METHOD);
    }

    public void testDeleteBannerNegativeBannerId() throws Exception {
	validateOnFail();
    }

    public void beginShowStatistics(WebRequest request) {
	request
		.addParameter("action", "showStatistics",
			WebRequest.POST_METHOD);
	request.addParameter("bannerId", "0", WebRequest.POST_METHOD);
    }

    public void testShowStatistics() throws Exception {
	try {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_INSERT);

	    ModelAndView modelAndView = controller.handleRequest(request,
		    response);
	    assertEquals("bm_detailstat", modelAndView.getViewName());

	    Banner banner = (Banner) modelAndView.getModel().get("banner");
	    assertEquals("BANNER_CACTUS_TEST", banner.getTitle());
	    validateCommonFields(banner);
	    assertEquals(true, banner.getActivated());

	    assertEquals("week", request.getSession().getAttribute(
		    "bm_stat_period"));
	    assertNotNull(request.getSession().getAttribute("stat_start"));
	    assertNotNull(request.getSession().getAttribute("stat_finish"));
	} catch (Exception e) {
	    fail(FAIL);
	} finally {
	    DBHelper.getJdbcTemplate().execute(TEST_SQL_DELETE);
	}
    }

    public void beginShowStatisticsCorrectNegativeBannerId(WebRequest request) {
	request
		.addParameter("action", "showStatistics",
			WebRequest.POST_METHOD);
	request.addParameter("bannerId", "-1", WebRequest.POST_METHOD);
    }

    public void testShowStatisticsCorrectNegativeBannerId() throws Exception {
	ModelAndView modelAndView = controller.handleRequest(request, response);
	assertEquals("bm_bannerstat", modelAndView.getViewName());
	assertNotNull(request.getAttribute("bannerclicks"));
	assertNotNull(request.getAttribute("bannerviews"));
    }

    public void beginShowStatisticsNegativeBannerId(WebRequest request) {
	request
		.addParameter("action", "showStatistics",
			WebRequest.POST_METHOD);
	request.addParameter("bannerId", "-2", WebRequest.POST_METHOD);
    }

    public void testShowStatisticsNegativeBannerId() throws Exception {
	validateOnFail();
    }

    public void beginShowStatisticsEmptyBannerId(WebRequest request) {
	request
		.addParameter("action", "showStatistics",
			WebRequest.POST_METHOD);
    }

    public void testShowStatisticsEmptyBannerId() throws Exception {
	ModelAndView modelAndView = controller.handleRequest(request, response);
	assertEquals("bm_bannerstat", modelAndView.getViewName());
	assertNotNull(request.getAttribute("bannerclicks"));
	assertNotNull(request.getAttribute("bannerviews"));
    }

    private void validateOnFail() {
	try {
	    controller.handleRequest(request, response);
	    fail(FAIL);
	} catch (Exception e) {
	}
    }

    /**
     * @param ob
     * @author vmishchenko Seems that when you running test with Ant, JUnit can
     *         not recognize non Java classes. So if you pass to this method
     *         Banner class instance as parameter directly, you'll get exception
     *         that class loader can't find such class. At least with current
     *         configurations.
     */
    private void validateCommonFields(Object ob) {
	Banner banner = (Banner) ob;
	assertEquals(new Long(2), banner.getCategoryId());
	assertEquals(new Long(1), banner.getBannerTypeId());
	assertEquals(new Long(1), banner.getImageType());
	assertEquals("TEST_URL", banner.getImageUrl());
	assertEquals("TEST_URL", banner.getUrl());
	assertEquals(new Long(7), banner.getPriority());
	assertEquals(new Long(7), banner.getMaxClicks());
	assertEquals(new Long(7), banner.getMaxViews());
	assertEquals(true, banner.getInNewWindow());
    }

}
