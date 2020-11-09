/*
 * @(#)Id: ManageVisitorGroupsCommandCactusTest.java, 02.08.2007 11:51:22, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.user.command;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class ManageVisitorGroupsCommandCactusTest extends CommandTestCase{

	//user id - not administrator (access denied), must be in DB
	private static final int USER_ID_NOT_ADMIN = 3;

	//user id - administrator, must be in DB
	private static final int USER_ID_ADMIN = 1;


	private static final long TEST_GROUP_ID = 99999;
	
	private static final long TEST_GROUP_ID_WRONG = -1;
	
	private static final String TEST_GROUP_NAME = "TestGroup";
	
	private static final String TEST_GROUP_NAME2 = "TestGroup2";

	private static final String TEST_GROUP_ROLE_ID = "visitor";
	
	private static final long TEST_USER_ID = 99999;

	private static final String TEST_USER_USERNAME = "Dimaz";
	
	private static final String TEST_USER_LOGIN = "Dimaz";

	private static final String TEST_USER_PASS = "pass";
	
	private static final String TEST_USER_TYPE = "visitor";
	
	private static final Date TEST_USER_EXPIRED_DATE = new Date(2453454);

	private static final Date TEST_USER_PUBLISH_DATE = new Date(2341234);

	
	
	private ManageVisitorGroupsCommand command;
	private SessionData session;

	public ManageVisitorGroupsCommandCactusTest(){
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ManageVisitorGroupsCommand();
		command.setRequestContext(requestContext);
	}
	
	
	
	/*
	 * if session with user who is not administrator
	 */
	public void testAccessDenied() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_NOT_ADMIN);
		
		ResponseContext responseContext = command.execute();
		
		assertEquals(ManageVisitorsCommand.RESULT_ACCESS_DENIED, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with user who is administrator, but without action parameter(by default, it must list_groups())
	 * shows list of groups (from DB TABLE groups) in xml
	 * 
	 */
	public void beginNoActionParameter(WebRequest request) throws Exception {
//		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "wrong");
	}
	
	public void testNoActionParameter() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		createDBVisitorGroup();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("list","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']", document);
		

		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_GROUP_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_NAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@name", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_ROLE_ID,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@role-id", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBVisitorGroup();
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'add'
	 */
	public void beginAddActionParameter(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "add");
	}
	
	public void testAddActionParameter() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("add","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='containers']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='users']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='roles']", document);
		
		String XPATH_PREFIX = "/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='containers']";
		
		validateContainers(XPATH_PREFIX, document);
		
		XPATH_PREFIX = "/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='users']";
		
		validateUsers(XPATH_PREFIX, document);
		
		XPATH_PREFIX = "/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='roles']";
		
		validateRoles(XPATH_PREFIX, document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());

		deleteDBVisitorUser();
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'edit'
	 * but without group_id
	 */
	public void beginEditActionParameterNoGroupId(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "edit");
	}
	
	public void testEditActionParameterNoGroupId() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		//createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");

		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("edit","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		XMLAssert.assertXpathEvaluatesTo("Group id is not specified","/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']", document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'edit'
	 * but with any wrong id
	 */
	public void beginEditActionParameterWrongGroupId(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "edit");
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ID, String.valueOf(TEST_GROUP_ID_WRONG));
	}
	
	public void testEditActionParameterWrongGroupId() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		//createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("edit","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		XMLAssert.assertXpathEvaluatesTo("Failed to get group data","/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']", document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'edit'
	 * but with any wrong id
	 */
	public void beginEditActionParameterCorrectId(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "edit");
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ID, String.valueOf(TEST_GROUP_ID));
	}
	
	public void testEditActionParameterCorrectId() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		createDBVisitorGroup();
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("edit","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']", document);
		
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_GROUP_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_NAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/@name", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_ROLE_ID,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/@role", document);
		
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='containers']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='users']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='roles']", document);
		
		String XPATH_PREFIX = "/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='containers']";
		
		validateContainers(XPATH_PREFIX, document);
		
		XPATH_PREFIX = "/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='users']";
		
		validateUsers(XPATH_PREFIX, document);
		
		XPATH_PREFIX = "/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']/*[local-name()='roles']";
		
		validateRoles(XPATH_PREFIX, document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBVisitorGroup();
		deleteDBVisitorUser();
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'save'
	 * but with no name
	 */
	public void beginSaveActionParameterWithNoName(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorGroupsCommand.INPUT_NAME, "");
	}
	
	public void testSaveActionParameterWithNoName() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		//createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		XMLAssert.assertXpathEvaluatesTo("Group name cannot be empty","/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']", document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'save'
	 * also with Name parameter, but without id parameter
	 * new group must be created
	 */
	public void beginSaveActionParameterWithNoId(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorGroupsCommand.INPUT_NAME, TEST_GROUP_NAME);
	}
	
	public void testSaveActionParameterWithNoId() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		//createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
				
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']", document);
		
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_NAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@name", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_ROLE_ID,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@role-id", document);

		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBGroupByName(TEST_GROUP_NAME);
		
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'save'
	 * also with Name parameter and id parameter, but wrong id(in DB there is no group with such id)
	 * 
	 */
	public void beginSaveActionParameterUpdateNotExistGroup(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorGroupsCommand.INPUT_NAME, TEST_GROUP_NAME);
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ID, String.valueOf(TEST_GROUP_ID));
	}
	
	public void testSaveActionParameterUpdateNotExistGroup() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		//createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		XMLAssert.assertXpathEvaluatesTo("Failed to update the group","/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='group']", document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'save'
	 * also with Name parameter and id parameter
	 * new group must be created
	 */
	public void beginSaveActionParameterUpdateExistedGroup(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorGroupsCommand.INPUT_NAME, TEST_GROUP_NAME2);
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ID, String.valueOf(TEST_GROUP_ID));
	}
	
	public void testSaveActionParameterUpdateExistedGroup() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		createDBVisitorGroup();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']", document);
		
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_GROUP_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_NAME2,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@name", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_ROLE_ID,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']/*[local-name()='group']/@role-id", document);

		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBGroupByName(TEST_GROUP_NAME2);
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'delete'
	 * but id is specified (must return 'Group id is not specified')
	 */
	public void beginDeleteActionWithNoId(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "delete");
	}
	
	public void testDeleteActionWithNoId() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
//		createDBVisitorGroup();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("delete","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		XMLAssert.assertXpathEvaluatesTo("Group id is not specified","/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']", document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with user who is administrator, with action parameter = 'delete'
	 * but id is specified (must return 'Group id is not specified')
	 */
	public void beginDeleteActionCorrect(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ACTION, "delete");
		request.addParameter(ManageVisitorGroupsCommand.INPUT_ID, String.valueOf(TEST_GROUP_ID));
	}
	
	public void testDeleteActionCorrect() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create test visitor group
		createDBVisitorGroup();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("delete","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='groups']", document);
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		deleteDBVisitorGroup();
	}
	
	/*
	 * -------------------------------------------------
	 */
	
	
	private void createDBVisitorGroup() throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "INSERT INTO groups " +
							"(id,name,role_id,site_id)"+
							"VALUES (?,?,?,1);";
		
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setLong(1, TEST_GROUP_ID);
		ps.setString(2, TEST_GROUP_NAME);
		ps.setString(3, TEST_GROUP_ROLE_ID);
		
		
		ps.execute();
		ps.close();
		conn.close();
	}

	private void deleteDBVisitorGroup() throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "DELETE FROM groups WHERE groups.id = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setLong(1, TEST_GROUP_ID);
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	private void deleteDBVisitorUser() throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "DELETE FROM user_list WHERE user_list.id = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setLong(1, TEST_USER_ID);
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	private void createDBVisitorUser() throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "INSERT INTO user_list " +
							"(id,username,login,password,type,publish_date,expired_date,site_id)"+
							"VALUES (?,?,?,?,?,?,?,1);";
		
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setLong(1, TEST_USER_ID);
		ps.setString(2, TEST_USER_USERNAME);
		ps.setString(3, TEST_USER_LOGIN);
		ps.setString(4, TEST_USER_PASS);
		ps.setString(5, TEST_USER_TYPE);
		ps.setDate(6, TEST_USER_PUBLISH_DATE);
		ps.setDate(7, TEST_USER_EXPIRED_DATE);
		
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	private void deleteDBGroupByName(String groupName) throws Exception{
		Connection conn = DBHelper.getConnection();
		String query = "DELETE " +
							"FROM groups " +
							"WHERE groups.name = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, groupName);
		
		ps.execute();
		
		ps.close();
		conn.close();
	}
	
	private void validateContainers(String PREFIX, Document document) throws Exception{
		
		String[] ids = {"1","2","3"};
		String[] name = {"Default","Extranet","Intranet"};
		
		int number_of_containers = 3;
		
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(number_of_containers), "count(" + PREFIX + "/*[local-name()='container'])", document);
		
		for (int i = 0 ;i < number_of_containers; i++){
			
			XMLAssert.assertXpathEvaluatesTo(ids[i], PREFIX + "/*[local-name()='container'][" + (i + 1) + "]/@id", document);
			XMLAssert.assertXpathEvaluatesTo(name[i], PREFIX + "/*[local-name()='container'][" + (i + 1) + "]/@name", document);
			
		}
	}
	
	private void validateUsers(String PREFIX, Document document) throws Exception{
		
		String[] ids = {String.valueOf(TEST_USER_ID)};
		String[] name = {TEST_USER_USERNAME};
		
		int number_of_containers = 1;
		
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(number_of_containers), "count(" + PREFIX + "/*[local-name()='user'])", document);
		
		for (int i = 0 ;i < number_of_containers; i++){
			
			XMLAssert.assertXpathEvaluatesTo(ids[i], PREFIX + "/*[local-name()='user'][" + (i + 1) + "]/@id", document);
			XMLAssert.assertXpathEvaluatesTo(name[i], PREFIX + "/*[local-name()='user'][" + (i + 1) + "]/@name", document);
			
		}
	}
	
	private void validateRoles(String PREFIX, Document document) throws Exception{
		
		String[] ids = {"visitor"};
		String[] name = {"visitor"};
		
		int number_of_containers = 1;
		
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(number_of_containers), "count(" + PREFIX + "/*[local-name()='role'])", document);
		
		for (int i = 0 ;i < number_of_containers; i++){
			
			XMLAssert.assertXpathEvaluatesTo(ids[i], PREFIX + "/*[local-name()='role'][" + (i + 1) + "]/@id", document);
			XMLAssert.assertXpathEvaluatesTo(name[i], PREFIX + "/*[local-name()='role'][" + (i + 1) + "]/@name", document);
			
		}
	}
	
	
}
