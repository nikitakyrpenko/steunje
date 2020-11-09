/*
 * @(#)Id: GetAttributesCactusTest.java, 30.07.2007 16:22:26, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.wcmsattributes.command;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class GetAttributesCactusTest extends CommandTestCase{
	
	private GetAttributes command;
	
	public GetAttributesCactusTest(){
		super();
	}
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
		command = new GetAttributes();
		command.setRequestContext(requestContext);
	}
	
	/*
	 * test check - result failure if we don't specify WCMS_ATTRIBUTE_SET
	 */
	public void testResultFailure(){
		command.setRequestContext(requestContext);
		
		ResponseContext responseContext = command.execute();
		
		assertEquals(GetAttributes.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	/*
	 * test check - result success if we specify WCMS_ATTRIBUTE_SET
	 */
	public void beginResultSuccess(WebRequest request){
		request.addParameter(GetAttributes.WCMS_ATTRIBUTE_SET, "1");
	}
	
	public void testResultSuccess(){
		command.setRequestContext(requestContext);
		
		ResponseContext responseContext = command.execute();
		
		assertNotSame(GetAttributes.RESULT_FAILURE, responseContext.getResultName());
	}

}
