package com.negeso.framework.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebResponse;

import com.negeso.framework.controller.WebFrontController;

public class WebFrontControllerCactusTest extends ServletTestCase {

	WebFrontController servlet;
	
	@Override
	protected void setUp() throws Exception {
		servlet = new WebFrontController();
	}
	
	public void testWelcome() throws ServletException, UnsupportedEncodingException {
//		servlet.init(config);
		servlet.doGet(request, response);
	}
	
	public void endWelcome(WebResponse response) {
		String responseText = response.getText();
		assertNotNull(responseText);
	}

}
