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
package com.negeso.module.form_manager.web.controller;

import java.util.List;

import org.apache.cactus.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.BaseControllerTestCase;
import com.negeso.module.form_manager.domain.FormField;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FormArchiveControllerCactusTest extends BaseControllerTestCase{
	FormArchiveController controller = null;
	
	 protected void setUp() throws Exception {
	        // needed to initialize a user
	        super.setUp();
	        controller = (FormArchiveController) dc.getBean("form_manager", "formArchiveController");
	    }

	    protected void tearDown() {
	    	controller = null;
	    }
	    
	    public void beginViewArchiveWithEmptyFormId(WebRequest request){
	    	request.addParameter("todo", "viewArchive", WebRequest.POST_METHOD);
	    	request.addParameter("form_id", "", WebRequest.POST_METHOD);
	    }
	    
	    public void testViewArchiveWithEmptyFormId() throws Exception{
	    	ModelAndView modelAndView = null;
	    	try{
	    		modelAndView = controller.handleRequest(request, response);
	    	}catch (Exception e) {
				// TODO: handle exception
			}
	    	assertNull(modelAndView);
	    }
	    
	    public void beginViewArchive(WebRequest request){
	    	request.addParameter("todo", "viewArchive", WebRequest.POST_METHOD);
	    	request.addParameter("form_id", "5", WebRequest.POST_METHOD);
	    }
	    
	    public void testViewArchive() throws Exception{
	    	ModelAndView modelAndView = controller.handleRequest(request, response);
	    	validateModelAndView(modelAndView);
	    }
	    
	    public void beginExportArchive(WebRequest request){
	    	request.addParameter("todo", "exportArchive", WebRequest.POST_METHOD);
	    	request.addParameter("form_id", "5", WebRequest.POST_METHOD);
	    }
	    
	    public void testExportArchive() throws Exception{
	    	ModelAndView modelAndView = controller.handleRequest(request, response);
	    	assertNull(modelAndView);
	    }
	    
	    public void endExportArchive(com.meterware.httpunit.WebResponse theResponse){
	        assertEquals("text/plain", theResponse.getContentType());
	    }
	    
	    public void beginSaveConfigAllInvisble(WebRequest request){
	    	request.addParameter("todo", "saveConfig", WebRequest.POST_METHOD);
	    	request.addParameter("form_id", "5", WebRequest.POST_METHOD);
	    }
	    	    
	    public void testSaveConfigAllInvisble() throws Exception{
	    	ModelAndView modelAndView = controller.handleRequest(request, response);
	    	validateModelAndView(modelAndView);
	    	validateFormsFieldQuantity(0, (List<FormField>)modelAndView.getModel().get("formFields"));
	    }
	    
	    public void beginSaveConfig5Visible(WebRequest request){
	    	request.addParameter("todo", "saveConfig", WebRequest.POST_METHOD);
	    	request.addParameter("form_id", "5", WebRequest.POST_METHOD);
	    	request.addParameter("_fieldId", "1", WebRequest.POST_METHOD);
	    	request.addParameter("_fieldId", "2", WebRequest.POST_METHOD);
	    	request.addParameter("_fieldId", "3", WebRequest.POST_METHOD);
	    	request.addParameter("_fieldId", "4", WebRequest.POST_METHOD);
	    	request.addParameter("_fieldId", "5", WebRequest.POST_METHOD);
	    }
	    	    
	    public void testSaveConfig5Visible() throws Exception{
	    	ModelAndView modelAndView = controller.handleRequest(request, response);
	    	validateModelAndView(modelAndView);
	    	validateFormsFieldQuantity(5, (List<FormField>)modelAndView.getModel().get("formFields"));
	    }
	    
	    private void validateModelAndView(ModelAndView modelAndView){
	    	assertNotNull(modelAndView.getModel());
	    	assertNotNull(modelAndView.getModel().get("formId"));
	    	assertNotNull(modelAndView.getModel().get("formFields"));
	    	assertEquals("forms_archive", modelAndView.getViewName());
	    }
	    
	    private void validateFormsFieldQuantity(int quantity, List<FormField> list){	    	
	    	int counter = 0;
	    	for (FormField formField : list) {
	    		if (formField.isVisible()) {
	    			 counter++;
				}
	    	}
	    	assertEquals(quantity, counter);
	    }
}

