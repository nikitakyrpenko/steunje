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

import com.negeso.BaseControllerTestCase;
import com.negeso.TestUtil;
import com.negeso.framework.domain.Language;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class BannerProductPageChooserControllerCactusTest extends BaseControllerTestCase{
	private BannerProductPageChooserController controller;
		
	protected void setUp() throws Exception {
        super.setUp();
        controller = (BannerProductPageChooserController) dc.getBean("banner_module", "bannerProductPageChooserController");        
    }
	
	protected void tearDown() {
    	controller = null;
    }
	
	public void beginGetProductPages(WebRequest request){
		request.addParameter("action", "getProductPages", WebRequest.POST_METHOD);
		request.addParameter("id", "3", WebRequest.POST_METHOD);
		request.addParameter("banner_id", "1", WebRequest.POST_METHOD);
	}
	
	public void testGetProductPages() throws Exception{
		request.getSession().setAttribute("language_object", new Language(2L, "Dutch", "nl"));
		controller.handleRequest(request, response);
	}
	
	public void endGetProductPages(com.meterware.httpunit.WebResponse theResponse){
		assertEquals("text/xml", theResponse.getContentType());
		try {
			validateXML(TestUtil.parseToDom(theResponse.getText()), "forBannerProductPageChooserController.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void beginGetProductPagesNegativeId(WebRequest request){
		request.addParameter("action", "getProductPages", WebRequest.POST_METHOD);
		request.addParameter("id", "-3", WebRequest.POST_METHOD);
		request.addParameter("banner_id", "1", WebRequest.POST_METHOD);
	}
	
	public void testGetProductPagesNegativeId() throws Exception{
		request.getSession().setAttribute("language_object", new Language(2L, "Dutch", "nl"));
		controller.handleRequest(request, response);
	}
	
	public void endGetProductPagesNegativeId(com.meterware.httpunit.WebResponse theResponse){
		assertEquals("text/xml", theResponse.getContentType());	
		try {
			validateXML(TestUtil.parseToDom(theResponse.getText()), "forBannerProductPageChooserControllerNegativeId.xml");
		} catch (Exception e) {}
	}
	
}

