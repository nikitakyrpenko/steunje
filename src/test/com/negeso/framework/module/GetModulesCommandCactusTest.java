/*
 * @(#)$Id: GetModulesCommandCactusTest.java,v 1.2, 2006-11-20 09:30:14Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.module;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.SharedValidator;
import com.negeso.TableRowCounter;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 3$
 *
 */
public class GetModulesCommandCactusTest extends CommandTestCase {
	
	private static final long TEST_ID = 1;
	
	private Command command; 
	
	public GetModulesCommandCactusTest(){
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new GetModulesCommand();
		command.setRequestContext(requestContext);
	}
	
	public void testAccesDenied() {
		// anonymous user 
		requestContext.getSessionData().setAttribute(SessionData.USER_ATTR_NAME, null);
		ResponseContext response = command.execute();
		//make sure result = failure and output xml is null for anonymous user
		assertEquals(AbstractCommand.RESULT_FAILURE, response.getResultName());
		assertNull(response.getResultMap().get(AbstractCommand.OUTPUT_XML));
	}

	private String formatTimeStamp(Timestamp timestamp) {
		return  timestamp.toString().substring(0, timestamp.toString().indexOf(' '));
	}
	
	private void validateXML(Document document) throws TransformerException, SQLException, XpathException {
		//make sure xml is not empty
		assertNotNull(document);
		
		XMLAssert.assertXpathExists("/*[local-name()='modules']", document);
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='modules'])", document);
		XMLAssert.assertXpathEvaluatesTo(TableRowCounter.getRowCount("module"), "count(/*[local-name()='modules']/*[local-name()='module'])", document);
		
		Connection conn = DBHelper.getConnection();
		try {
			Module module = Module.findById(conn, TEST_ID);
		
			XMLAssert.assertXpathExists("/*[local-name()='modules']/*[local-name()='module'][@id='1']", document);
			XMLAssert.assertXpathEvaluatesTo(module.getName(),
											"/*[local-name()='modules']/*[local-name()='module'][@id='1']/@name",
											document);
			if (module.getGolive() != null) {
				XMLAssert.assertXpathEvaluatesTo(formatTimeStamp(module.getGolive()),
												"/*[local-name()='modules']/*[local-name()='module'][@id='1']/@golive",
												document);
			}
			if (module.getExpiredDate()!= null) {
				XMLAssert.assertXpathEvaluatesTo(formatTimeStamp(module.getExpiredDate()),
												"/*[local-name()='modules']/*[local-name()='module'][@id='1']/@expired",
												document);
			}
			XMLAssert.assertXpathEvaluatesTo((module.getUrl()==null?"":module.getUrl()),
                							"/*[local-name()='modules']/*[local-name()='module'][@id='1']/@url",
                							document);
			XMLAssert.assertXpathEvaluatesTo(Boolean.toString(Module.isActive(module.getGolive(), module.getExpiredDate())),
											"/*[local-name()='modules']/*[local-name()='module'][@id='1']/@active",
											document);
		} finally {
			conn.close();
		}
	}
	
	public void testSuccessResult() throws SQLException, TransformerException, XpathException {
	    ResponseContext responseContext = command.execute();
		assertEquals(AbstractCommand.RESULT_SUCCESS,
				     responseContext.getResultName());
        validateXML((Document)responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML));
	}
	
	public void testHistoryStack() {
		SharedValidator.validateHistoryStack(command, requestContext);
	}
}

